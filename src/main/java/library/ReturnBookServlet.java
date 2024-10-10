package library;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/returnBookProcess")
public class ReturnBookServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Declare database credentials and connection URLs
        String url = "jdbc:mysql://localhost:3306/Library";
        String userName = "root";
        String password = "root";

        int bookId = Integer.parseInt(req.getParameter("bookId"));
        String studentId = req.getParameter("studentId");
        int librarianId = Integer.parseInt(req.getParameter("LibId"));
        String librarianPass = req.getParameter("pass");

        PrintWriter out = res.getWriter();

        try {
            // Load MySQL driver and establish connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, userName, password);

            // Check librarian credentials
            String librarianQuery = "SELECT id FROM librarian WHERE id = ? AND pass = ?";
            PreparedStatement pstLibrarian = con.prepareStatement(librarianQuery);
            pstLibrarian.setInt(1, librarianId);
            pstLibrarian.setString(2, librarianPass);
            ResultSet rsLibrarian = pstLibrarian.executeQuery();

            if (!rsLibrarian.next()) {
                out.println("Invalid librarian credentials. Please try again.");
                return;
            }

            // Check if the student has borrowed the book
            String borrowCheckQuery = "SELECT borrowDate FROM student_book WHERE studentId = ? AND bookId = ?";
            PreparedStatement pstBorrowCheck = con.prepareStatement(borrowCheckQuery);
            pstBorrowCheck.setInt(1, Integer.parseInt(studentId));
            pstBorrowCheck.setInt(2, bookId);
            ResultSet rsBorrowCheck = pstBorrowCheck.executeQuery();

            if (rsBorrowCheck.next()) {
                Date borrowDate = rsBorrowCheck.getDate("borrowDate");

                // Calculate the fine if the return is after 15 days
                LocalDate borrowedDate = borrowDate.toLocalDate();
                LocalDate currentDate = LocalDate.now();
                long daysElapsed = ChronoUnit.DAYS.between(borrowedDate, currentDate);
                int fineAmount = 0;

                if (daysElapsed > 15) {
                    fineAmount = (int) (daysElapsed - 15) * 10;
                }

                // Remove the entry from `student_book` table and increase book count in `books` table
                String deleteBorrowEntry = "DELETE FROM student_book WHERE studentId = ? AND bookId = ?";
                PreparedStatement pstDeleteEntry = con.prepareStatement(deleteBorrowEntry);
                pstDeleteEntry.setInt(1, Integer.parseInt(studentId));
                pstDeleteEntry.setInt(2, bookId);
                pstDeleteEntry.executeUpdate();

                String updateBookCount = "UPDATE books SET count = count + 1 WHERE id = ?";
                PreparedStatement pstUpdateBookCount = con.prepareStatement(updateBookCount);
                pstUpdateBookCount.setInt(1, bookId);
                pstUpdateBookCount.executeUpdate();

                // Update fine amount in student table
                String updateFineQuery = "UPDATE student SET fine = fine + ? WHERE regNO = ?";
                PreparedStatement pstUpdateFine = con.prepareStatement(updateFineQuery);
                pstUpdateFine.setInt(1, fineAmount);
                pstUpdateFine.setInt(2, Integer.parseInt(studentId));
                pstUpdateFine.executeUpdate();

                out.println("Book returned successfully. Fine incurred: Rs " + fineAmount);
            } else {
                out.println("Book not found in the student's borrowed list. Please check the details.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            out.println("An error occurred: " + e.getMessage());
            e.printStackTrace(out);
        }
    }
}

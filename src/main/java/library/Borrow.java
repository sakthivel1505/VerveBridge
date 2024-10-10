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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/BorrowBookServlet")
public class Borrow extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Database credentials and connection details
        String url = "jdbc:mysql://localhost:3306/Library";
        String userName = "root";
        String password = "root";

        int bookId = Integer.parseInt(req.getParameter("bookId"));
        int studentId = Integer.parseInt(req.getParameter("studentId"));
        int librarianId = Integer.parseInt(req.getParameter("LibId"));
        String librarianPass = req.getParameter("LibPs");

        PrintWriter out = res.getWriter();

        try {
            // Load MySQL driver and establish a connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, userName, password);

            // Verify librarian's credentials
            String librarianQuery = "SELECT id, pass FROM librarian WHERE id = ? AND pass = ?";
            PreparedStatement pstLibrarian = con.prepareStatement(librarianQuery);
            pstLibrarian.setInt(1, librarianId);
            pstLibrarian.setString(2, librarianPass);
            ResultSet rsLibrarian = pstLibrarian.executeQuery();

            if (!rsLibrarian.next()) {
                out.println("Invalid librarian credentials. Please try again.");
                return;
            }

            // Check if the student has already borrowed the book
            String borrowCheckQuery = "SELECT * FROM student_book WHERE studentId = ? AND bookId = ?";
            PreparedStatement pstBorrowCheck = con.prepareStatement(borrowCheckQuery);
            pstBorrowCheck.setInt(1, studentId);
            pstBorrowCheck.setInt(2, bookId);
            ResultSet rsBorrowCheck = pstBorrowCheck.executeQuery();

            if (rsBorrowCheck.next()) {
                out.println("This book is already borrowed by the student.");
                return;
            }

            // Check book availability
            String bookQuery = "SELECT count FROM books WHERE id = ?";
            PreparedStatement pstBook = con.prepareStatement(bookQuery);
            pstBook.setInt(1, bookId);
            ResultSet rsBook = pstBook.executeQuery();

            if (rsBook.next()) {
                int bookCount = rsBook.getInt("count");

                if (bookCount > 0) {
                    // Insert borrow information into `borrowed_books`
                    String borrowQuery = "INSERT INTO student_book (studentId, bookId, borrowDate) VALUES (?, ?, ?)";
                    PreparedStatement pstBorrow = con.prepareStatement(borrowQuery);
                    pstBorrow.setInt(1, studentId);
                    pstBorrow.setInt(2, bookId);
                    pstBorrow.setDate(3, Date.valueOf(LocalDate.now()));

                    int rowsInserted = pstBorrow.executeUpdate();

                    if (rowsInserted > 0) {
                        // Update book count in `books` table
                        String updateBookQuery = "UPDATE books SET count = ? WHERE id = ?";
                        PreparedStatement pstUpdateBook = con.prepareStatement(updateBookQuery);
                        pstUpdateBook.setInt(1, bookCount - 1);
                        pstUpdateBook.setInt(2, bookId);
                        pstUpdateBook.executeUpdate();

                        out.println("Book borrowed successfully!");
                    } else {
                        out.println("Failed to record the borrowing. Please try again.");
                    }
                } else {
                    out.println("The book is currently out of stock.");
                }
            } else {
                out.println("Book not found. Please check the book ID.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            out.println("An error occurred: " + e.getMessage());
            e.printStackTrace(out);
        }
    }
}

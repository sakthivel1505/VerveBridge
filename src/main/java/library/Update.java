package library;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/update")
public class Update extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Retrieve book details from request parameters
        String title = req.getParameter("title");
        String author = req.getParameter("author");
        String category = req.getParameter("category");
        int id = Integer.parseInt(req.getParameter("bookId"));
        int year = Integer.parseInt(req.getParameter("year"));
        int copies = Integer.parseInt(req.getParameter("copies"));

        PrintWriter out = res.getWriter();

        // Database connection details
        String url = "jdbc:mysql://localhost:3306/Library";
        String userName = "root";
        String password = "root";

        // SQL query to update the book record in the books table
        String query = "UPDATE books SET bname = ?, author = ?, categery = ?, pubYear = ?, count = ? WHERE id = ?";

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish database connection
            Connection con = DriverManager.getConnection(url, userName, password);

            // Prepare the SQL update query
            PreparedStatement pts = con.prepareStatement(query);
            pts.setString(1, title);     // Set book title
            pts.setString(2, author);    // Set author name
            pts.setString(3, category);  // Set category
            pts.setInt(4, year);         // Set publication year
            pts.setInt(5, copies);       // Set number of copies
            pts.setInt(6, id);           // Set book ID for where clause

            // Execute the update query
            int rowsUpdated = pts.executeUpdate();

            // Check if the update was successful
            if (rowsUpdated > 0) {
                out.println("Successfully updated the book record with ID: " + id);
            } else {
                out.println("No book found with ID: " + id + ". Update failed.");
            }

            // Close the resources
            pts.close();
            con.close();
        } catch (SQLException e) {
            out.println("An error occurred while connecting to the database.");
            e.printStackTrace(out);
        } catch (ClassNotFoundException e) {
            out.println("JDBC Driver not found.");
            e.printStackTrace(out);
        }
    }
}

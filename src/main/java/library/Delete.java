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

@WebServlet("/delete")
public class Delete extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        
        // Get the book ID from the request
        int id = Integer.parseInt(req.getParameter("bookId"));

        PrintWriter out = res.getWriter();

        // Database credentials and connection URL
        String url = "jdbc:mysql://localhost:3306/Library";
        String userName = "root";
        String password = "root";

        // SQL query to delete a book based on its ID
        String query = "DELETE FROM books WHERE id = ?";

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish database connection
            Connection con = DriverManager.getConnection(url, userName, password);

            // Prepare and execute the DELETE query
            PreparedStatement pts = con.prepareStatement(query);
            pts.setInt(1, id);
            int rowsDeleted = pts.executeUpdate();

            if (rowsDeleted > 0) {
                out.println("Book with ID " + id + " was successfully deleted from the database.");
            } else {
                out.println("No book found with ID " + id + ". Please check the ID and try again.");
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

package library;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/searchBookProcess")
public class SearchBookProcess extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Set the content type of the response
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        // Get the book name and author name from the request parameters
        String bookName = req.getParameter("bName");
        String authorName = req.getParameter("aName");

        // Database connection details
        String url = "jdbc:mysql://localhost:3306/Library";
        String userName = "root";
        String password = "root";

        // SQL query to search for books based on book name or author name
        String query = "SELECT id, bname, author FROM books WHERE bname LIKE ? OR author LIKE ?";

        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            Connection con = DriverManager.getConnection(url, userName, password);

            // Create a PreparedStatement
            PreparedStatement pstmt = con.prepareStatement(query);

            // Set the values for the placeholders in the SQL query
            pstmt.setString(1, "%" + bookName + "%");  // Search for books containing the book name
            pstmt.setString(2, "%" + authorName + "%");  // Search for books containing the author name

            // Execute the query and get the result set
            ResultSet rs = pstmt.executeQuery();

            // Display the search results
            out.println("<h2>Search Results:</h2>");
            out.println("<table border='1'><tr><th>Book ID</th><th>Book Name</th><th>Author Name</th></tr>");
            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                int id = rs.getInt("id");
                String bname = rs.getString("bname");
                String author = rs.getString("author");

                // Display each record in a table row
                out.println("<tr><td>" + id + "</td><td>" + bname + "</td><td>" + author + "</td></tr>");
            }
            out.println("</table>");

            // If no results are found, display a message
            if (!hasResults) {
                out.println("<p>No books found matching the search criteria.</p>");
            }

            // Close the resources
            rs.close();
            pstmt.close();
            con.close();
        } catch (ClassNotFoundException e) {
            out.println("Error: JDBC Driver not found.");
            e.printStackTrace(out);
        } catch (SQLException e) {
            out.println("Error: Unable to connect to the database.");
            e.printStackTrace(out);
        }
    }
}

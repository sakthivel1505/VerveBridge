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

@WebServlet("/add")
public class AddBook extends HttpServlet {
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Retrieve the input parameters from the form
        String title = req.getParameter("title");
        String author = req.getParameter("author");
        String category = req.getParameter("category");
        int id = Integer.parseInt(req.getParameter("bookId"));
        int year = Integer.parseInt(req.getParameter("year"));
        int copies = Integer.parseInt(req.getParameter("copies"));
        
        // Set up the response writer
        PrintWriter out = res.getWriter();
        res.setContentType("text/html");

        // Database connection details
        String url = "jdbc:mysql://localhost:3306/Library";
        String username = "root";
        String password = "root";
        
        // SQL query to insert a new book into the 'books' table
        String query = "INSERT INTO books (bname, author, categery, id, pubYear, count) VALUES (?, ?, ?, ?, ?, ?)";
        
        try {
            // Load the JDBC driver and establish a connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, username, password);
            
            // Prepare the SQL statement with parameters
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, title);
            pst.setString(2, author);
            pst.setString(3, category);
            pst.setInt(4, id);
            pst.setInt(5, year);
            pst.setInt(6, copies);
            
            // Execute the update and check if the record was inserted
            int rowsInserted = pst.executeUpdate();
            if (rowsInserted > 0) {
                out.println("<h3>Book added successfully!</h3>");
            } else {
                out.println("<h3>Failed to add the book. Please try again.</h3>");
            }
            
            // Close the statement and connection
            pst.close();
            con.close();
            
        } catch (SQLException | ClassNotFoundException e) {
            // Handle exceptions and provide feedback to the user
            out.println("<h3>An error occurred while adding the book: " + e.getMessage() + "</h3>");
            e.printStackTrace(out);
        }
    }
}

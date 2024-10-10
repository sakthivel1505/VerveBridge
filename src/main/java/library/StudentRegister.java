package library;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/StuRegister")
public class StudentRegister extends HttpServlet {
    
    // Override doPost method to handle POST requests
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Set response content type
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        // Database connection details
        String url = "jdbc:mysql://localhost:3306/Library";
        String userName = "root";
        String passWord = "root";

        // Retrieving form data from request parameters
        String firstName = req.getParameter("f_name");
        String lastName = req.getParameter("l_name");
        int regNo = Integer.parseInt(req.getParameter("Reg_no"));
        String dept = req.getParameter("dept");
        String degree = req.getParameter("degree");
        String college = req.getParameter("college");
        String password = req.getParameter("password");

        // Initialize book and fine details to default values
        int fine = 0;

        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish database connection
            Connection con = DriverManager.getConnection(url, userName, passWord);

            // SQL query to insert student data
            String query = "INSERT INTO student (fName, lName, regNO, dept, degree, college, pass, fine) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            // Prepare the SQL statement
            PreparedStatement pstmt = con.prepareStatement(query);

            // Set values for each column in the student table
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setInt(3, regNo);
            pstmt.setString(4, dept);
            pstmt.setString(5, degree);
            pstmt.setString(6, college);
            pstmt.setString(7, password);  // Password for student login
            pstmt.setInt(8, fine);  // Fine amount (default value)

            // Execute the query
            int rowsInserted = pstmt.executeUpdate();

            // Check if the row was successfully inserted
            if (rowsInserted > 0) {
                out.println("<h2>Registration Successful!</h2>");
                out.println("<p><a href='index.html'>Go to Home</a></p>");
            } else {
                out.println("<h2>Registration Failed. Please try again.</h2>");
            }

            // Close the prepared statement and database connection
            pstmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<h2>Database Connection Failed: " + e.getMessage() + "</h2>");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            out.println("<h2>JDBC Driver Not Found: " + e.getMessage() + "</h2>");
        }
    }
}

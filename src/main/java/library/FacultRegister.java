package library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/facult_register")
public class FacultRegister extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		String name1=req.getParameter("f_name");
		String name2=req.getParameter("l_name");
		String regNo=req.getParameter("Reg_no");
		String dept=req.getParameter("dept");
		String college=req.getParameter("college");
		String pass=req.getParameter("password");
		
		String url="jdbc:mysql://localhost:3306/Library";
		String userName="root";
		String password="root";
		
		String query="INSERT INTO faculty (fname, lname, id, dept, college, pass) VALUES (?, ?, ?, ?, ?, ?);";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection(url,userName,password);
			PreparedStatement pstmt = con.prepareStatement(query);

            pstmt.setString(1, name1);
            pstmt.setString(2, name2);
            pstmt.setString(3, regNo);
            pstmt.setString(4, dept);
            pstmt.setString(5, college);
            pstmt.setString(6, pass);
            
            int rs=pstmt.executeUpdate();
			
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}

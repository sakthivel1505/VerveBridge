package library;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@WebServlet("/BookManagementServlet")
public class Book extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		String action=req.getParameter("action");
		if(action.equals("add")) {
			RequestDispatcher rd=req.getRequestDispatcher("add");
			rd.forward(req, res);
		}
		else if(action.equals("update")) {
			RequestDispatcher rd=req.getRequestDispatcher("update");
			rd.forward(req, res);
		}
		else if(action.equals("delete")) {
			RequestDispatcher rd=req.getRequestDispatcher("delete");
			rd.forward(req, res);
		}
		
	}

}

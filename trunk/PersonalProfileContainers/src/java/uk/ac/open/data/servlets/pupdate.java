package uk.ac.open.data.servlets;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class pupdate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public pupdate() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			FileWriter fstream = new FileWriter("/web/lucero.open.ac.uk/oucu-updates.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			String oucu = request.getParameter("oucu");
			out.write(oucu+"\n");
			out.close();
		}catch (Exception e){
			System.err.println(e);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

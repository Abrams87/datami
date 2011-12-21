package uk.ac.open.data.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.open.data.common.HeaderFooter;
import uk.ac.open.data.common.Querying;

public class resource extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public resource() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String uri = request.getParameter("uri");
		if (uri==null){
			response.setContentType("text/html");
			out.write(HeaderFooter.getHeader("Error"));
			out.write("You need to specify an entity to inspect.");
			out.write(HeaderFooter.getFooter());
		}
		else {
			Querying q = new Querying();
			response.setContentType("application/rdf+xml");
			out.write(q.getRDFCode(uri)); 
			q.shutdown();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
		
	}

}

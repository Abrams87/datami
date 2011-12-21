package uk.ac.open.data.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.open.data.common.HeaderFooter;
import uk.ac.open.data.common.Querying;

public class page extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public page() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String uri = request.getParameter("uri");
		response.setContentType("text/html");
		if (uri==null){
			out.write(HeaderFooter.getHeader("Error"));
			out.write("You need to specify an entity to inspect.");
			out.write(HeaderFooter.getFooter());
		}
		else if (RedirectRoutes.getInstance().shouldRedirect(uri)){
			RedirectRoutes.getInstance().redirect(response, uri);
		}
		else {
			if (uri.endsWith(".html")) uri = uri.substring(0, uri.length()-5);
			if (uri.endsWith(".rdf")) RedirectRoutes.getInstance().redirectToRDF(response, uri.substring(0, uri.length()-4));
			Querying q = new Querying();
			String label = q.getLabel(uri).replaceAll("\"", "").replaceAll("\\^\\^", "");
			out.write(HeaderFooter.getHeader(label));
			out.write("<h1>"+label+"</h1>\n");
			out.write("<div><em>"+q.getComment(uri).replaceAll("\"", "").replaceAll("\\^\\^", "")+"</div>\n");
			out.write("<div><table>\n");
			Vector<String[]> rf = q.getRelationsFrom(uri);
			String currentPURI = "";
			boolean first = true;
			for (String[] rel : rf){
				if (!currentPURI.equals(rel[0])){ 
					if (!first) out.write("</td></tr>\n");
					currentPURI = rel[0];
					first = false;
					out.write("<tr><td>");
					out.write("<a href=\""+rel[0]+"\">"+q.getLabel(rel[0]).replaceAll("\"", "").replaceAll("\\^\\^", "")+"</a>");
					out.write("</td><td>"); 
				}
				if (rel[1].startsWith("http://")) out.write("<a href=\""+rel[1]+"\">"+q.getLabel(rel[1]).replaceAll("\"", "").replaceAll("\\^\\^", "")+"</a><br/>");
				else out.write(rel[1].replaceAll("\"", "").replaceAll("\\^\\^", "")+"<br/>"); 
			}
			if (!first) out.write("</td></tr>");
			out.write("</table></div>\n");
			
			out.write("<div><table>\n");
			Vector<String[]> rt = q.getRelationsTo(uri);
			String currentSURI = "";
			first = true;
			for (String[] rel : rt){
				if (!currentSURI.equals(rel[1])){ 
					if (!first) {
						out.write("</td><td>");
						if (currentSURI.startsWith("http://")) out.write("<a href=\""+currentSURI+"\">"+q.getLabel(currentSURI).replaceAll("\"", "").replaceAll("\\^\\^", "")+"</a>");
						else out.write(currentSURI); 
						out.write("</td></tr>\n");
					}
					currentSURI = rel[1];
					first = false;
					out.write("<tr><td>"); 
				}
				if (rel[0].startsWith("http://")) out.write("<a href=\""+rel[0]+"\">"+q.getLabel(rel[0]).replaceAll("\"", "").replaceAll("\\^\\^", "")+"</a><br/>");
			}
			if (!first) {
				out.write("</td><td>");
				if (currentSURI.startsWith("http://")) out.write("<a href=\""+currentSURI+"\">"+q.getLabel(currentSURI).replaceAll("\"", "").replaceAll("\\^\\^", "")+"</a>");
				else out.write(currentSURI.replaceAll("\"", "").replaceAll("\\^\\^", "")); 
				out.write("</td></tr>\n");
			}
			out.write("</table></div>\n");
			
			out.write(HeaderFooter.getFooter());
			q.shutdown(); 
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

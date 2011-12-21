package uk.ac.open.data.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.codehaus.jackson.JsonFactory;

public class query2 extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public query2() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String query = request.getParameter("query");
		if (query != null) {
			String url = "http://data.open.ac.uk/query?query="+URLEncoder.encode(query);
		    HttpClient client = new HttpClient();
		    GetMethod method = new GetMethod(url);
		    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
		    		new DefaultHttpMethodRetryHandler(3, false));
		    try {
		      int statusCode = client.executeMethod(method);
		      if (statusCode != HttpStatus.SC_OK) {
		        System.err.println("Method failed: " + method.getStatusLine());
		        return ;
		      }
		      response.setContentType("application/sparql-results+xml");
		      InputStream is = method.getResponseBodyAsStream();
		      // to JSON!!! would be very useful...
		      ServletOutputStream sos = response.getOutputStream();
		      
		      byte[] buffer = new byte[1024];
		      int lenght = 0;
		      while ((lenght = is.read(buffer))>0){
		    	  sos.write(buffer, 0, lenght);
		      }
		      is.close();
		      sos.close();
		    } catch (HttpException e) {
		      System.err.println("Fatal protocol violation: " + e.getMessage());
		      e.printStackTrace();
		    } catch (IOException e) {
		      System.err.println("Fatal transport error: " + e.getMessage());
		      e.printStackTrace();
		    } finally {
		      method.releaseConnection();
		    }  
		  }
		}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

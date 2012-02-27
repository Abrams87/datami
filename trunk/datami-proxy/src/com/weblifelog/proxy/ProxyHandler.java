package com.weblifelog.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class ProxyHandler extends AbstractHandler {

    private final static HttpLogger logger = new HttpLogger();

    private String proxyServer = null;
    private int proxyPort = 80;

    // missing refereer 
    // can it also do streamed post content?
    // cookies don't seem well handled

    // hascode text on the other side so that it does not need to be reprocessed

    public ProxyHandler(){
	Properties configFile = new Properties();
	try {
	    configFile.load(new FileInputStream(new File("wllproxy.properties")));
	    proxyServer = configFile.getProperty("PROXY_SERVER");
	    String pp = configFile.getProperty("PROXY_PORT");
	    if (pp!=null && !pp.trim().equals("")){
		proxyPort = Integer.parseInt(pp.trim());
	    }
	    System.out.println(" WLL: will use proxy "+proxyServer+":"+proxyPort);	       
	} catch(Exception e){
	    e.printStackTrace();
	}
    }


	@Override
	public void handle(String target, Request req, HttpServletRequest sreq,
			HttpServletResponse resp) throws IOException, ServletException {

	    HTTPRequestLog log = new HTTPRequestLog();	
	    log.time = System.currentTimeMillis()+"";
	    HttpMethodBase creq = transformRequest(req, log);
	    if (creq==null){
		resp.setStatus(502);
		resp.getWriter().write(Messages.proxy_error);
		req.setHandled(true);
		return ;
	    }
   
	    InputStream is = executeRequest(creq, resp, log);
	    if (is==null) {
		// 		System.out.println("   WLL:: No output...");
		// return;
	    }
	    		
	    if (is!=null){
		ServletOutputStream sos = resp.getOutputStream();	      
		byte[] buffer = new byte[1024];
		int lenght = 0;
		String contentType = resp.getContentType();
		boolean record = contentType == null || (contentType.trim().startsWith("text") && (contentType.contains("html") || contentType.contains("xml")));
		boolean compressed = (log.response.header.get("Content-Encoding") !=null && log.response.header.get("Content-Encoding").toLowerCase().trim().equals("gzip"));
		StringBuffer content = new StringBuffer("");
		String tmpFileName = null;
		FileOutputStream fos = null; 
		if (compressed && record) {
		    tmpFileName = "tmp/"+System.currentTimeMillis()+"-"+Math.random();
		    fos = new FileOutputStream(new File(tmpFileName));
		}
		while ((lenght = is.read(buffer))>0){
		    sos.write(buffer, 0, lenght);
		    if (record && !compressed) {       
			content.append(new String(buffer, 0, lenght));
		    } else if (compressed && record){
			fos.write(buffer, 0, lenght);
			// System.out.println("Stream from "+log.url+" is compressed");		    	
		    }
		}
		if (record && !compressed) {       
		    log.response.content = content.toString();
		} else if (record && compressed){		    
		    fos.close();
		    GZIPInputStream gis = new GZIPInputStream(new FileInputStream(new File(tmpFileName)));
		    buffer = new byte[1024];
		    lenght = 0;
		    while ((lenght = gis.read(buffer))>0){
			content.append(new String(buffer, 0, lenght));
		    }
		    log.response.content = content.toString();
		    gis.close();
		    new File(tmpFileName).delete();
		}
		sos.close();
		is.close();
	    }
	    
	    req.setHandled(true);
	    logger.log(log);
	    //  resp.getWriter().println("calling "+req.getRequestURL());
	}
    
    private InputStream executeRequest(HttpMethodBase creq, HttpServletResponse resp, HTTPRequestLog log) {
	HttpClient client = new HttpClient();
	if (proxyServer!=null) {
	    client.getHostConfiguration().setProxy(proxyServer, proxyPort);
	}
	
	creq.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
				      new DefaultHttpMethodRetryHandler(3, false));
	try {
	    int statusCode = client.executeMethod(creq);
	    //		      System.out.println("   WLL:: status code = "+statusCode);
	    resp.setStatus(statusCode);
	    log.response.status=statusCode;
	    log.response.time=System.currentTimeMillis()+"";
	    //   if (statusCode == HttpStatus.SC_OK) {
	    InputStream is = creq.getResponseBodyAsStream();
	    String contentType = resp.getContentType();
	    boolean record = contentType == null || (contentType.trim().startsWith("text") && (contentType.contains("html") || contentType.contains("xml")));
	    // if (record) log.response.content = creq.getResponseBodyAsString(); 
	    Header[] headers = creq.getResponseHeaders();
	    for (Header h : headers){
		resp.setHeader(h.getName(), h.getValue()); // shoudl log;
		// cooky??
		log.response.header.put(h.getName(), h.getValue());
	    }
	    return is;
	    // 	}
	} catch (Exception e){
	    System.out.println("   WLL:: Exception occured, returning 502 for "+log.url);
	    resp.setStatus(502);
	    log.response.status=502;
	    e.printStackTrace();
	}
	return null;
    }

    private HttpMethodBase transformRequest(Request req, HTTPRequestLog log){

	HttpMethodBase res = null;		
	log.url=req.getRequestURL().toString();
	log.queryString = req.getQueryString();
	if (req.getMethod().toUpperCase().equals("GET")){
	    // System.out.println(" WLL:: treating GET request "+req.getRequestURL()+" "+log.queryString);
	    log.method = "GET";
	    res = new GetMethod(req.getRequestURL().toString());	 
	} else if (req.getMethod().toUpperCase().equals("POST")){
	    // System.out.println(" WLL:: treating POST request "+req.getRequestURL());
	    log.method = "POST";
	    res = new PostMethod(req.getRequestURL().toString());
	    Enumeration params = req.getParameterNames();	    
	    while (params.hasMoreElements()){
		String name = params.nextElement().toString();;
		// System.out.println("parameter :: "+name+" = "+req.getParameter(name));
		((PostMethod)res).addParameter(name, req.getParameter(name));
	    }
	    // content...
	    /* try {
		BufferedReader r = new BufferedReader(req.getReader());
		log.content = r.readLine();
		// ((PostMethod)res). // don't see how to add content.
	    } catch (IOException e) {
		e.printStackTrace();
		} */
	    
	    // } else if (req.getMethod().toUpperCase().equals("POST")){ 
	    //res = new ConnectMethod();
	} else if (req.getMethod().toUpperCase().equals("CONNECT")){
	    log.method = req.getMethod();
	    // we know we can't do, but it is annoying to get it all the time in the log...
	    return null;
	} else {
	    log.method = req.getMethod();
	    System.err.println(" WLL: Unsuppoted method "+req.getMethod()+" for request "+req.getRequestURL());
	    return null;
	}
	if (log.queryString!=null) res.setQueryString(log.queryString);
	Enumeration<String> header = (Enumeration<String>)req.getHeaderNames();
	while(header.hasMoreElements()){
	    String name = header.nextElement();
	    String value = req.getHeader(name);
	    res.addRequestHeader(name, value); // need to treat the response better first: compressed asked - compressed obttained...
	    log.header.put(name, value);
	}
	return res;
    }
    

}

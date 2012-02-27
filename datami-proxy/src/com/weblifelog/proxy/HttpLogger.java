package com.weblifelog.proxy;

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringEscapeUtils;


// localhost:3031/wll/query?query=select ?x where {[] <http://weblifelog.com/ontology/hasContent> ?x}
// kmi-dev04:3031/wll/query?query=select ?x where {[] <http://weblifelog.com/ontology/hasContent> ?x}
public class HttpLogger {

    private String user = null;
    private String updateendpoint = null;
    private String proxyServer = null;
    private int proxyPort = 80;

    private static final String WLLNS = "http://weblifelog.com/ontology/";
    private static final String XSDNS = "http://www.w3.org/2001/XMLSchema#";

    public HttpLogger(){
	Properties configFile = new Properties();
	try {
	    configFile.load(new FileInputStream(new File("wllproxy.properties")));
	    user = configFile.getProperty("USER");
	    updateendpoint = configFile.getProperty("UPDATE_ENDPOINT");
	    proxyServer = configFile.getProperty("PROXY_SERVER");
	    String pp = configFile.getProperty("PROXY_PORT");
	    if (pp!=null && !pp.trim().equals("")){
		proxyPort = Integer.parseInt(pp.trim());
	    }

	} catch(Exception e){
	    e.printStackTrace();
	}
    }

    public void log(HTTPRequestLog l){
	// sepcial DATAMI... shoudl specialise the class
	if (l.response.content == null || l.response.content.trim().equals("")) return;
	String query = generateUpdateQuery(l);
	sendQuery(query);
    }

    private void sendQuery(String query){
	HttpClient client = new HttpClient();
	//	if (proxyServer!=null) {
	//	    client.getHostConfiguration().setProxy(proxyServer, proxyPort);
	//	}
	PostMethod res = new PostMethod(updateendpoint);
	res.addParameter("update", query);
	try{
	    int statusCode = client.executeMethod(res);
	    if (statusCode != 200 && statusCode != 204){
		System.out.println(" WLL:: Something went wrong with updating the store");
	    }
	} catch (Exception e){
	    e.printStackTrace();
	}
    }
    
    private String generateUpdateQuery(HTTPRequestLog l){
	StringBuffer query = new StringBuffer("INSERT {");
	String requestURI = generateRequestURI(l);
	String responseURI =  generateResponseURI(l);
	query.append("<"+requestURI+"> <"+WLLNS+"atTime> \""+l.time+"\"^^<"+XSDNS+"long>. ");
	query.append("<"+requestURI+"> <"+WLLNS+"toSite> <"+l.url+">. ");
	query.append("<"+requestURI+"> <"+WLLNS+"usingMethod> <"+WLLNS+l.method+"_METHOD>. ");
	if (l.queryString!=null) query.append("<"+requestURI+"> <"+WLLNS+"withQueryString> \""+l.queryString+"\"^^<"+XSDNS+"string>. ");
	// content
	// header
	query.append("<"+requestURI+"> <"+WLLNS+"hasResponse> <"+responseURI+">. ");
	query.append("<"+responseURI+"> <"+WLLNS+"atTime> \""+l.response.time+"\"^^<"+XSDNS+"long>. ");	
	if (l.response.content!=null) {
	    l.response.content = StringEscapeUtils.escapeHtml3(l.response.content);
	    l.response.content = java.net.URLEncoder.encode(l.response.content);
	    // System.out.println("sending "+l.response.content);
	    query.append("<"+responseURI+"> <"+WLLNS+"hasContent> \""+l.response.content+"\"^^<"+XSDNS+"string>. ");
	}
	query.append("<"+responseURI+"> <"+WLLNS+"status> <"+WLLNS+l.response.status+"_STATUS>. ");
	// header		
	query.append("} WHERE {}");
	//	System.out.println(query);
	return query.toString();
    }

    private String generateRequestURI(HTTPRequestLog l){
	return "http://weblifelog.com/"+user+"/request/"+l.time+"-"+l.url.hashCode();
    }

    private String generateResponseURI(HTTPRequestLog l){
	return "http://weblifelog.com/"+user+"/request/"+l.response.time+"-"+l.url.hashCode();
    }

    
	
}

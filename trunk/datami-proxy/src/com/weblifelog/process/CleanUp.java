package com.weblifelog.process;

import java.util.Properties;
import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;

import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

public class CleanUp {

    private String updateEndpoint;
    private String queryEndpoint;

    private static final String WLLNS = "http://weblifelog.com/ontology/";
    private static final String XSDNS = "http://www.w3.org/2001/XMLSchema#";

    public CleanUp(){
	Properties configFile = new Properties();
	try {
	    configFile.load(new FileInputStream(new File("wllproxy.properties")));
	    updateEndpoint = configFile.getProperty("UPDATE_ENDPOINT");
	    queryEndpoint = configFile.getProperty("QUERY_ENDPOINT");
	} catch(Exception e){
	    e.printStackTrace();
	}
    }

    public boolean doCleanUp(){
	String query ="delete {?x ?p ?y. ?z ?q ?t} where {?x <http://weblifelog.com/ontology/hasResponse> ?z. ?x ?p ?y. ?z ?q ?t. ?z <http://weblifelog.com/ontology/hasContent> ?c. FILTER ( str(?c) = \"\" )}";
	if (!sendUpdateQuery(query)){
	    System.out.println("Clean up failed"); return false;
	} else System.out.println("Clean up successful");
	return true;
    }

    private boolean sendUpdateQuery(String query){
	HttpClient client = new HttpClient();
	//	if (proxyServer!=null) {
	//	    client.getHostConfiguration().setProxy(proxyServer, proxyPort);
	//	}
	PostMethod res = new PostMethod(updateEndpoint);
	res.addParameter("update", query);
	try{
	    int statusCode = client.executeMethod(res);
	    if (statusCode != 200 && statusCode != 204){
		return false;
	    }
	} catch (Exception e){
	    e.printStackTrace();
	    return false;
	}
	return true;
    }
    
    public static void main (String [] args){
	CleanUp app = new CleanUp();
	app.doCleanUp();
    }

}



package com.weblifelog.process;

import java.util.Properties;
import java.util.HashMap;
import java.util.Vector;
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

public class Sync {

    private String syncEndpoint;
    private String queryEndpoint;
    private String updateEndpoint;

    private static final String WLLNS = "http://weblifelog.com/ontology/";
    private static final String XSDNS = "http://www.w3.org/2001/XMLSchema#";

    public Sync(){
	Properties configFile = new Properties();
	try {
	    configFile.load(new FileInputStream(new File("wllproxy.properties")));
	    syncEndpoint = configFile.getProperty("SYNC_ENDPOINT");
	    queryEndpoint = configFile.getProperty("QUERY_ENDPOINT");
	    updateEndpoint = configFile.getProperty("UPDATE_ENDPOINT");
	} catch(Exception e){
	    e.printStackTrace();
	}
    }

    public boolean doSync(){
	Query query = QueryFactory.create("select distinct ?x where {?x <"+WLLNS+"toSite> ?y}");
	QueryExecution qe = QueryExecutionFactory.sparqlService(queryEndpoint, query);
	ResultSet rs = qe.execSelect();
	int count = 0;
	Vector<String> requests = new Vector<String>();
	while(rs.hasNext()){
	    requests.add(rs.next().getResource("x").getURI());
	    count++;
	}
	qe.close();
	System.out.println("Found "+count+" objects");
	count=0;
	for (String reqURI : requests){
	    boolean res = doSync(reqURI);
	    if (!res) {
		System.out.println("Synced "+count+" objects");
		return false;
	    }
	    count++;
	}
	System.out.println("Synced "+count+" objects");
	return true;
    }

    private boolean doSync(String rURI){
	rURI = "<"+rURI+">";
	System.out.println("syncing "+rURI);
	Query query = QueryFactory.create("select distinct ?p ?y where {"+rURI+" ?p ?y}");
	QueryExecution qe = QueryExecutionFactory.sparqlService(queryEndpoint, query);
	ResultSet rs = qe.execSelect();       
	HashMap<String, String> reqP = new HashMap<String, String>();
	while(rs.hasNext()){
	    QuerySolution qs = rs.next();
	    String prop = qs.getResource("p").getURI();
	    RDFNode valn = qs.get("y");
	    String val = "";
	    if (valn.isResource()){
		val = "<"+valn.asResource().getURI()+">";
	    } else {
		val = "\""+valn.asLiteral().getString()+"\"^^<"+valn.asLiteral().getDatatypeURI()+">";
	    }
	    reqP.put(prop, val);
	}
	qe.close();
	String respURI = reqP.get("http://weblifelog.com/ontology/hasResponse");
	query = QueryFactory.create("select distinct ?p ?y where {"+respURI+" ?p ?y}");
	qe = QueryExecutionFactory.sparqlService(queryEndpoint, query);
	rs = qe.execSelect();       
	HashMap<String, String> respP = new HashMap<String, String>();
	while(rs.hasNext()){
	    QuerySolution qs = rs.next();
	    String prop = qs.getResource("p").getURI();
	    RDFNode valn = qs.get("y");
	    String val = "";
	    if (valn.isResource()){
		val = "<"+valn.asResource().getURI()+">";
	    } else {
		val = "\""+valn.asLiteral().getString()+"\"^^<"+valn.asLiteral().getDatatypeURI()+">";
	    }
	    respP.put(prop, val);
	}
	qe.close();
	String insertQuery = createInsertQuery(reqP, respP, rURI, respURI);
	//System.out.println(insertQuery);
	boolean updated = sendUpdateQuery(insertQuery);
	if (!updated){
	    System.out.println("Could not access sync endpoint. Aborting");
	    return false;
	}
	String deleteQuery = createDeleteQuery(reqP, respP, rURI, respURI);
	boolean deleted = sendUpdateQuery(deleteQuery, updateEndpoint);
	//	recordAsToBeDeleted(rURI, respURI);
	return true;
    }

    private boolean sendUpdateQuery(String query, String service){
	HttpClient client = new HttpClient();
	//	if (proxyServer!=null) {
	//	    client.getHostConfiguration().setProxy(proxyServer, proxyPort);
	//	}
	PostMethod res = new PostMethod(service);
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

    private boolean sendUpdateQuery(String query){
	return sendUpdateQuery(query, syncEndpoint);
    }

    // private String createDeleteQuery(HashMap<String, String> reqP, HashMap<String, String> respP, String reqURI, String respURI){
    //    return "delete {"+reqURI+" ?p ?y. "+respURI+" ?q ?t} where {"+reqURI+" ?p ?y. "+respURI+" ?q ?t}";
    // }


    private String createDeleteQuery(HashMap<String, String> reqP, HashMap<String, String> respP, String reqURI, String respURI){
	StringBuffer res = new StringBuffer("");
	res.append("delete {");
	for (String key : reqP.keySet()){
	    res.append(reqURI+" <"+key+"> "+reqP.get(key)+".");
	}
	for (String key : respP.keySet()){
	    res.append(respURI+" <"+key+"> "+respP.get(key)+".");
	}
	res.append("} where { }");
	return res.toString();
    }

    private String createInsertQuery(HashMap<String, String> reqP, HashMap<String, String> respP, String reqURI, String respURI){
	StringBuffer res = new StringBuffer("");
	res.append("insert {");
	for (String key : reqP.keySet()){
	    res.append(reqURI+" <"+key+"> "+reqP.get(key)+".");
	}
	for (String key : respP.keySet()){
	    res.append(respURI+" <"+key+"> "+respP.get(key)+".");
	}
	res.append("} where { }");
	return res.toString();
    }

    
    public static void main (String [] args){
	Sync app = new Sync();
	app.doSync();
    }

}



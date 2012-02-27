package com.weblifelog.process;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Vector;

public class SPARQLClient {

    private String endpoint;
	
    public SPARQLClient(String endpoint){
	this.endpoint = endpoint;
    }

    // should use HTTP client...
    public String runQuery(String query){
	String res = HTTPUtils.getFrom(endpoint+"?query="+URLEncoder.encode(query));
	return res;
    }
    


}

package com.weblifelog.process;

import java.io.*;
import java.net.*;

public class HTTPUtils {

    public static String postTo(String URL, String query){
	System.out.println("getting from "+URL+" query = "+query);
	StringBuffer res = new StringBuffer("");
	try {
	    URL url = new URL(URL);
	    URLConnection postUrlConnection = url.openConnection();
	    postUrlConnection.setDoOutput(true);
	    postUrlConnection.setDoInput(true);
	    postUrlConnection.setUseCaches(false);
	    postUrlConnection.setAllowUserInteraction(false);
	    postUrlConnection.setRequestProperty("Accept",
						 "application/xml");

	    DataOutputStream dos = new DataOutputStream(postUrlConnection.getOutputStream());
	    dos.writeBytes(query);
	    dos.close();
	    
	    InputStream postInputStream = postUrlConnection.getInputStream();
	    BufferedReader in = new BufferedReader(new InputStreamReader(postInputStream));
	    String str;
	    char[] buff = new char[1024*100];
	    int n;
	    int i = 0;
	    do {
	    	n = in.read(buff);
	    	if (n!=-1) res.append(new String(buff,0,n));
	    	i += n;
			System.out.print((i/1024)+"K("+n+") ");	
			// res.append(str);
	    } while(n!=-1);
	    // while ((str = in.readLine()) != null) {
		// System.out.println(str);
		// res += str+"\n";
	    // }	    
	    in.close();
	} 
	catch (Exception e){
	    e.printStackTrace();
	}	
	return res.toString();
    }
    
    
    public static String getFrom(String URL){
    	// System.out.println("getting from "+URL);
    	StringBuffer res = new StringBuffer("");
    	try {
    	    URL url = new URL(URL);
    	    URLConnection postUrlConnection = url.openConnection();
    	    postUrlConnection.setDoOutput(true);
    	    postUrlConnection.setUseCaches(false);
    	    postUrlConnection.setRequestProperty("Accept",
    						 "application/xml");


    	    InputStream postInputStream = postUrlConnection.getInputStream();
    	    BufferedReader in = new BufferedReader(new InputStreamReader(postInputStream));
    	    String str;
    	    char[] buff = new char[1024*100];
    	    int n;
    	    int i = 0;
    	    do {
    	    	n = in.read(buff);
    	    	if (n!=-1) res.append(new String(buff,0,n));
    	    	i += n;
    			// System.out.print((i/1024)+"K("+n+") ");	
    			// res.append(str);
    	    } while(n!=-1);
    	    // while ((str = in.readLine()) != null) {
    		// System.out.println(str);
    		// res += str+"\n";
    	    // }	    
    	    in.close();
    	} 
    	catch (Exception e){
    	    e.printStackTrace();
    	}	
    	return res.toString();
        }

}

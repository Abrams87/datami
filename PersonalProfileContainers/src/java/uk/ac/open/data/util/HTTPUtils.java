package uk.ac.open.data.util;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HTTPUtils {

//    public static void main (String args[])
//    {
//        try {
//            System.getProperties().put("proxySet","true");
//            System.getProperties().put("proxyHost", "wwwcache.open.ac.uk");
//            System.getProperties().put("proxyPort", "80");
//            String str = "<http://uciad.info/data/web06_04-May-2011>" +
//                                             "WHERE {<http://uciad.info/actorsetting/dcee009f1e6511ef58e1bc11ea991ea4> ?p ?o}";
//            String encoded = URLEncoder.encode(str, "UTF-8");
//            String sparqlQuery = "http://kmi-web01.open.ac.uk:8080/openrdf-workbench/repositories/UCIAD/query?query=SELECT * FROM " + encoded;
//            String test = getFrom(sparqlQuery);
//            System.out.println(test);
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(HTTPUtils.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
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
    
    
    public static String getFrom(String URL, String acceptHeader){
    	System.out.println("posting to "+URL);
    	StringBuffer res = new StringBuffer("");
    	try {
            System.setProperty("proxySet", "true");
            System.setProperty("proxyHost", "wwwcache.open.ac.uk");
            System.setProperty("proxyPort", "80");
        
    	    URL url = new URL(URL);
    	    URLConnection postUrlConnection = url.openConnection();
    	    postUrlConnection.setDoOutput(true);
    	    postUrlConnection.setUseCaches(false);
            if (acceptHeader != null)
            {
                postUrlConnection.setRequestProperty("Accept", acceptHeader);
            }
    	    //postUrlConnection.setRequestProperty("Accept", "application/rdf+xml");
            //postUrlConnection.setRequestProperty("Accept", "application/text+html");


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
    			//System.out.print((i/1024)+"K("+n+") ");	
    			// res.append(str);
    	    } while(n!=-1);
    	    // while ((str = in.readLine()) != null) {
    		// System.out.println(str);
    		// res += str+"\n";
    	    // }	    
    	    in.close();
    	} 
    	catch (Exception e){
            System.out.println(e);
    	    e.printStackTrace();
    	}	
    	return res.toString();
        }
    public static String detectBrowser(String userAgentStr) {
        String userAgent = null;
        try {            
            String httpQueryStr = "http://id.furud.net/identify.php?uagent="+ URLEncoder.encode(userAgentStr, "UTF-8");
            userAgent = HTTPUtils.getFrom(httpQueryStr, "text/html");

            BufferedReader in = null;
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("browerDetector.txt"));
                writer.write(userAgent);
                writer.close();
                
                in = new BufferedReader(new FileReader("browerDetector.txt"));
                String str;
                while ((str = in.readLine()) != null) {
                    if (str.trim().equalsIgnoreCase("<td>"))
                    {
                       userAgent = in.readLine();                       
                       in.close();
                    }
                }
            } catch (IOException e) {
            } finally {
                if (in != null)
                {
                    in.close();
                }
            }
            
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return userAgent = userAgent.trim().replace("<i>", "").replace("</i>", "");
    }
}

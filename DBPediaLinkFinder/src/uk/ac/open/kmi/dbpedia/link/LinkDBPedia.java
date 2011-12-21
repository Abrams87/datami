/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.open.kmi.dbpedia.link;

/**
 *
 * @author se3535
 */
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class LinkDBPedia {

	private static double urlDBPediaResultThreshHold = 0.0;
	private static double refDBPediaResultThreshHold = 0.0;
	private static double homepageDBPediaResultThreshHold = 0.0;	
	private static final int general = 0;
	private static final int fromRef = 1;
	private static final int fromHomepage = 2;
	
	//private static Map<String, String> dbpediaLinks = new HashMap<String, String>();
	
    public static void main (String args[]) {
    	System.out.println(LetterPairSimilarity.compareStrings("nsm.dell", "Dell"));
    	//System.out.println(getLevenshteinDistance("FRANCE", "FRENCH"));
        try {
            //System.out.println(getFrom("http://data.open.ac.uk/oro/21488"));
        	System.getProperties().put("proxySet","true");
            System.getProperties().put("proxyHost", "wwwcache.open.ac.uk");
            System.getProperties().put("proxyPort", "80");
            Connection con = null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                String url = "jdbc:mysql://localhost/pimst";
                con = DriverManager.getConnection(url, "root", "");
                
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT distinct h.host FROM hosts h, attribmatches a " +
                		                         "WHERE h.attribute = a.dattrib");
                while (rs.next()){
                	queryDBPedia(rs.getString(1));
                }                
                rs.close();
                stmt.close();
                
            } catch (Exception ex) {
                System.out.println(ex);
            }           

        } catch (Exception ex) {
        	System.out.println(ex);
        }
    }    

    public static String getFrom(String URL, String acceptHeader) throws Exception {
        StringBuffer res = new StringBuffer("");
        try {
        	
            URL url = new URL(URL);
            //System.out.println(URL);
            URLConnection postUrlConnection = url.openConnection();
            
            postUrlConnection.setDoOutput(true);
            postUrlConnection.setUseCaches(false);
            if (acceptHeader != null){
            	postUrlConnection.setRequestProperty("Accept", acceptHeader);
            } else {
            	postUrlConnection.setRequestProperty("Accept", "application/RDF+XML");
            }
            
            //postUrlConnection.setRequestProperty("Accept","application/RDF+XML");
            //postUrlConnection.setRequestProperty("Accept","text/html");

            InputStream postInputStream = postUrlConnection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(postInputStream));
            
            char[] buff = new char[1024 * 100];
            int n;
            int i = 0;
            do {
                n = in.read(buff);
                if (n != -1) {
                    res.append(new String(buff, 0, n));
                }
                i += n;
            } while (n != -1);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res.toString();
    }

    // This approach is slightly different from the above method because it reads and writes zip files which
    // does not contain lines so doesn't have to check for new line characters etc which causes zip file to corrupt as well.
    
    @SuppressWarnings("finally")
	public static boolean downloadZipFile(String URL, String indexNumber, String indexArchivePath) throws Exception {
        boolean success = false;
        try {
            URL url = new URL(URL+"/"+indexNumber+"-index.zip");
            URLConnection postUrlConnection = url.openConnection();
            postUrlConnection.setDoOutput(true);
            postUrlConnection.setUseCaches(false);
            postUrlConnection.setRequestProperty("content-type","binary/data");

            InputStream in = postUrlConnection.getInputStream();
            FileOutputStream fout = new FileOutputStream(indexArchivePath+"/"+indexNumber+"-index.zip");
            
            byte[] buff = new byte[8192]; // 1024 * 8
            int n;
            while ((n = in.read(buff))>0) {
                      fout.write(buff, 0, n);
            }
            in.close();
            fout.close();
            success = true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return success;
        }
    }
    
    public static String queryDBPedia (String domain) {
    	String response = "";
    	String query = "";
    	String URL = "http://dbpedia.org/sparql";    	
    	String query1 = "?default-graph-uri=http://dbpedia.org&query=";    	
    	String query2 = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>" +"PREFIX dbpprop: <http://dbpedia.org/property/>" +
		   				"PREFIX foaf: <http://xmlns.com/foaf/0.1/>";		
    	String query3 = "";
    	String origDomain = domain;
    	try {    		
    		boolean result = false;
    		System.err.println(origDomain);
    		refDBPediaResultThreshHold = 0.0;
    		urlDBPediaResultThreshHold = 0.0;
    		homepageDBPediaResultThreshHold = 0.0;
    		
    		//dbpprop:url + dbpedia-owl:Website
    		query3 = "ASK {{?p dbpprop:url <"+origDomain+"> . ?p a dbpedia-owl:Website} " +
    				 "UNION {?p dbpprop:url <"+origDomain+"/> . ?p a dbpedia-owl:Website} " +
    				 "UNION {?p dbpprop:url <http://"+origDomain+"> . ?p a dbpedia-owl:Website} " +
    				 "UNION {?p dbpprop:url <http://"+origDomain+"/> . ?p a dbpedia-owl:Website} " +
    				 "UNION {?p dbpprop:url <http://www."+origDomain+"> . ?p a dbpedia-owl:Website} " +
    				 "UNION {?p dbpprop:url <http://www."+origDomain+"/> . ?p a dbpedia-owl:Website}" +
    				 
    				 "UNION {?p dbpprop:url \""+origDomain+"\"@en . ?p a dbpedia-owl:Website} " +
    				 "UNION {?p dbpprop:url \""+origDomain+"/\"@en . ?p a dbpedia-owl:Website} " +
    				 "UNION {?p dbpprop:url \"http://"+origDomain+"\"@en . ?p a dbpedia-owl:Website} " +
    				 "UNION {?p dbpprop:url \"http://"+origDomain+"/\"@en . ?p a dbpedia-owl:Website} " +
    				 "UNION {?p dbpprop:url \"http://www."+origDomain+"\"@en . ?p a dbpedia-owl:Website} " +
    				 "UNION {?p dbpprop:url \"http://www."+origDomain+"/\"@en . ?p a dbpedia-owl:Website}" +
    				 "}";
    		query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
    		if (getResource(query)) {
    			result = true;
    			printResource(origDomain, origDomain, URL, query1, query2, query3, general);
    		}

    		
    		if (!result){
    			domain = origDomain.substring(origDomain.indexOf(".")+1);
    			query3 = "ASK {{?p dbpprop:url <"+domain+"> . ?p a dbpedia-owl:Website} " +
    					 "UNION {?p dbpprop:url <"+domain+"/> . ?p a dbpedia-owl:Website} " +
    					 "UNION {?p dbpprop:url <http://"+domain+"> . ?p a dbpedia-owl:Website} " + 
    					 "UNION {?p dbpprop:url <http://"+domain+"/> . ?p a dbpedia-owl:Website} " +
    					 "UNION {?p dbpprop:url <http://www."+domain+"> . ?p a dbpedia-owl:Website} " + 
    					 "UNION {?p dbpprop:url <http://www."+domain+"/> . ?p a dbpedia-owl:Website}" +
    					 
    					 "UNION {?p dbpprop:url \""+domain+"\"@en . ?p a dbpedia-owl:Website} " +
        				 "UNION {?p dbpprop:url \""+domain+"/\"@en . ?p a dbpedia-owl:Website} " +
        				 "UNION {?p dbpprop:url \"http://"+domain+"\"@en . ?p a dbpedia-owl:Website} " +
        				 "UNION {?p dbpprop:url \"http://"+domain+"/\"@en . ?p a dbpedia-owl:Website} " +
        				 "UNION {?p dbpprop:url \"http://www."+domain+"\"@en . ?p a dbpedia-owl:Website} " +
        				 "UNION {?p dbpprop:url \"http://www."+domain+"/\"@en . ?p a dbpedia-owl:Website}" +
        				 "}";
        		query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
        		if (getResource(query)) {
        			result = true;
        			printResource(origDomain, domain, URL, query1, query2, query3, general);
        		} 
    		}
    		
    		//foaf:homepage + dbpedia-owl:Organization
    		
    		if (!result){
    			query3 = "ASK {{?p foaf:homepage <"+origDomain+"> . ?p a dbpedia-owl:Organisation} " +
			     "UNION {?p foaf:homepage <"+origDomain+"/> . ?p a dbpedia-owl:Organisation} " +        
			     "UNION {?p foaf:homepage <http://"+origDomain+"> . ?p a dbpedia-owl:Organisation} " +
			     "UNION {?p foaf:homepage <http://"+origDomain+"/> . ?p a dbpedia-owl:Organisation} " +
			     "UNION {?p foaf:homepage <http://www."+origDomain+"> . ?p a dbpedia-owl:Organisation} " +
			     "UNION {?p foaf:homepage <http://www."+origDomain+"/> . ?p a dbpedia-owl:Organisation} " +
			     
			     "UNION {?p foaf:homepage \""+origDomain+"\"@en . ?p a dbpedia-owl:Organisation} " +
			     "UNION {?p foaf:homepage \""+origDomain+"/\"@en . ?p a dbpedia-owl:Organisation} " +        
			     "UNION {?p foaf:homepage \"http://"+origDomain+"\"@en . ?p a dbpedia-owl:Organisation} " +
			     "UNION {?p foaf:homepage \"http://"+origDomain+"/\"@en . ?p a dbpedia-owl:Organisation} " +
			     "UNION {?p foaf:homepage \"http://www."+origDomain+"\"@en . ?p a dbpedia-owl:Organisation} " +
			     "UNION {?p foaf:homepage \"http://www."+origDomain+"/\"@en . ?p a dbpedia-owl:Organisation} " +
				 "}";
				query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
				if (getResource(query)) {
					result = true;
					printResource(origDomain, origDomain, URL, query1, query2, query3, general);
				}
    		}   		

    		
    		if (!result){
    			domain = origDomain.substring(origDomain.indexOf(".")+1);
    			query3 = "ASK {{?p foaf:homepage <"+domain+"> . ?p a dbpedia-owl:Organisation} " +
    					 "UNION {?p foaf:homepage <"+domain+"/> . ?p a dbpedia-owl:Organisation} " + 	
    					 "UNION {?p foaf:homepage <http://"+domain+"> . ?p a dbpedia-owl:Organisation} " +
    					 "UNION {?p foaf:homepage <http://"+domain+"/> . ?p a dbpedia-owl:Organisation} " +
    					 "UNION {?p foaf:homepage <http://www."+domain+"> . ?p a dbpedia-owl:Organisation} " +
    					 "UNION {?p foaf:homepage <http://www."+domain+"/> . ?p a dbpedia-owl:Organisation} " +

    					 "UNION {?p foaf:homepage \""+domain+"\"@en . ?p a dbpedia-owl:Organisation} " +
    				     "UNION {?p foaf:homepage \""+domain+"/\"@en . ?p a dbpedia-owl:Organisation} " +        
    				     "UNION {?p foaf:homepage \"http://"+domain+"\"@en . ?p a dbpedia-owl:Organisation} " +
    				     "UNION {?p foaf:homepage \"http://"+domain+"/\"@en . ?p a dbpedia-owl:Organisation} " +
    				     "UNION {?p foaf:homepage \"http://www."+domain+"\"@en . ?p a dbpedia-owl:Organisation} " +
    				     "UNION {?p foaf:homepage \"http://www."+domain+"/\"@en . ?p a dbpedia-owl:Organisation} " +
    					 "}";
    			
        		query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
        		if (getResource(query)) {
        			result = true;
        			printResource(origDomain, domain, URL, query1, query2, query3, general);
        		} 
    		}
    		
    		
    		//dbpprop:reference + dbpedia-owl:Website    		
    		if (!result) {    			
    			query3 = "ASK {{?p dbpprop:reference <"+origDomain+"> . ?p a dbpedia-owl:Website} " +
    			 		 "UNION {?p dbpprop:reference <"+origDomain+"/> . ?p a dbpedia-owl:Website} " +
    			 		 "UNION {?p dbpprop:reference <http://"+origDomain+"> . ?p a dbpedia-owl:Website} " +
    			 		 "UNION {?p dbpprop:reference <http://"+origDomain+"/> . ?p a dbpedia-owl:Website} " +
    			 		 "UNION {?p dbpprop:reference <http://www."+origDomain+"> . ?p a dbpedia-owl:Website} " +
    			 		 "UNION {?p dbpprop:reference <http://www."+origDomain+"/> . ?p a dbpedia-owl:Website} " +
    			 		 
    			 		 "UNION {?p dbpprop:reference \""+origDomain+"\"@en . ?p a dbpedia-owl:Website} " +
    			 		 "UNION {?p dbpprop:reference \""+origDomain+"/\"@en . ?p a dbpedia-owl:Website} " +
    			 		 "UNION {?p dbpprop:reference \"http://"+origDomain+"\"@en . ?p a dbpedia-owl:Website} " +
    			 		 "UNION {?p dbpprop:reference \"http://"+origDomain+"/\"@en . ?p a dbpedia-owl:Website} " +
    			 		 "UNION {?p dbpprop:reference \"http://www."+origDomain+"\"@en . ?p a dbpedia-owl:Website} " +
    			 		 "UNION {?p dbpprop:reference \"http://www."+origDomain+"/\"@en . ?p a dbpedia-owl:Website} " +    			 		 
    					 "}";
    			query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
    			if (getResource(query)) {
    				result = true;
    				printResource(origDomain, origDomain, URL, query1, query2, query3, general);
    			}
    		}

    		
    		
    		if (!result){
				domain = origDomain.substring(origDomain.indexOf(".")+1);
				query3 = "ASK {{?p dbpprop:reference <"+domain+"> . ?p a dbpedia-owl:Website} " +
						 "UNION {?p dbpprop:reference <"+domain+"/> . ?p a dbpedia-owl:Website} " +
						 "UNION {?p dbpprop:reference <http://"+domain+"> . ?p a dbpedia-owl:Website} " +
						 "UNION {?p dbpprop:reference <http://"+domain+"/> . ?p a dbpedia-owl:Website} " +
						 "UNION {?p dbpprop:reference <http://www."+domain+"> . ?p a dbpedia-owl:Website} " +
						 "UNION {?p dbpprop:reference <http://www."+domain+"/> . ?p a dbpedia-owl:Website} " +
						 
						 "UNION {?p dbpprop:reference \""+domain+"\"@en . ?p a dbpedia-owl:Website} " +
    			 		 "UNION {?p dbpprop:reference \""+domain+"/\"@en . ?p a dbpedia-owl:Website} " +
    			 		 "UNION {?p dbpprop:reference \"http://"+domain+"\"@en . ?p a dbpedia-owl:Website} " +
    			 		 "UNION {?p dbpprop:reference \"http://"+domain+"/\"@en . ?p a dbpedia-owl:Website} " +
    			 		 "UNION {?p dbpprop:reference \"http://www."+domain+"\"@en . ?p a dbpedia-owl:Website} " +
    			 		 "UNION {?p dbpprop:reference \"http://www."+domain+"/\"@en . ?p a dbpedia-owl:Website} " +    			 		 
    					 "}";
	    		query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
	    		if (getResource(query)) {
	    			result = true;
	    			printResource(origDomain, domain, URL, query1, query2, query3, general);
	    		}
			}
    		
    		
    		//dbpprop:reference + dbpedia-owl:Organisation
    		
    		if (!result) {    			
    			query3 = "ASK {{?p dbpprop:reference <"+origDomain+"> . ?p a dbpedia-owl:Organisation} " +
    				     "UNION {?p dbpprop:reference <"+origDomain+"/> . ?p a dbpedia-owl:Organisation} " +
    				     "UNION {?p dbpprop:reference <http://"+origDomain+"> . ?p a dbpedia-owl:Organisation} " +
    				     "UNION {?p dbpprop:reference <http://"+origDomain+"/> . ?p a dbpedia-owl:Organisation} " +
    				     "UNION {?p dbpprop:reference <http://www."+origDomain+"> . ?p a dbpedia-owl:Organisation} " +
    				     "UNION {?p dbpprop:reference <http://www."+origDomain+"/> . ?p a dbpedia-owl:Organisation} " +
    				     
    				     "UNION {?p dbpprop:reference \""+origDomain+"\"@en . ?p a dbpedia-owl:Organisation} " +
    				     "UNION {?p dbpprop:reference \""+origDomain+"/\"@en . ?p a dbpedia-owl:Organisation} " +
    				     "UNION {?p dbpprop:reference \"http://"+origDomain+"\"@en . ?p a dbpedia-owl:Organisation} " +
    				     "UNION {?p dbpprop:reference \"http://"+origDomain+"/\"@en . ?p a dbpedia-owl:Organisation} " +
    				     "UNION {?p dbpprop:reference \"http://www."+origDomain+"\"@en . ?p a dbpedia-owl:Organisation} " +
    				     "UNION {?p dbpprop:reference \"http://www."+origDomain+"/\"@en . ?p a dbpedia-owl:Organisation} " +
    					 "}";
        		query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
        		if (getResource(query)) {
        			result = true;
        			printResource(origDomain, origDomain, URL, query1, query2, query3, general);
        		}
    		}    		

    		
    		if (!result){
				domain = origDomain.substring(origDomain.indexOf(".")+1);
				query3 = "ASK {{?p dbpprop:reference <"+domain+"> . ?p a dbpedia-owl:Organisation} " +
						 "UNION {?p dbpprop:reference <"+domain+"/> . ?p a dbpedia-owl:Organisation} " +
						 "UNION {?p dbpprop:reference <http://"+domain+"> . ?p a dbpedia-owl:Organisation} " +
						 "UNION {?p dbpprop:reference <http://"+domain+"/> . ?p a dbpedia-owl:Organisation} " +
						 "UNION {?p dbpprop:reference <http://www."+domain+"> . ?p a dbpedia-owl:Organisation} " +
						 "UNION {?p dbpprop:reference <http://www."+domain+"/> . ?p a dbpedia-owl:Organisation} " +
						 
						 "UNION {?p dbpprop:reference \""+domain+"\"@en . ?p a dbpedia-owl:Organisation} " +
    				     "UNION {?p dbpprop:reference \""+domain+"/\"@en . ?p a dbpedia-owl:Organisation} " +
    				     "UNION {?p dbpprop:reference \"http://"+domain+"\"@en . ?p a dbpedia-owl:Organisation} " +
    				     "UNION {?p dbpprop:reference \"http://"+domain+"/\"@en . ?p a dbpedia-owl:Organisation} " +
    				     "UNION {?p dbpprop:reference \"http://www."+domain+"\"@en . ?p a dbpedia-owl:Organisation} " +
    				     "UNION {?p dbpprop:reference \"http://www."+domain+"/\"@en . ?p a dbpedia-owl:Organisation} " +
    					 "}";
        		query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
        		if (getResource(query)) {
        			result = true;
        			printResource(origDomain, domain, URL, query1, query2, query3, general);
        		}
			}
    		
    		//dbpprop:url    		
    		if (!result) {    			
    			query3 = "ASK {{?p dbpprop:url <"+origDomain+">} " +
    					 "UNION {?p dbpprop:url <"+origDomain+"/>} " +
    					 "UNION {?p dbpprop:url <http://"+origDomain+">} " +
    					 "UNION {?p dbpprop:url <http://"+origDomain+"/>} " +
    					 "UNION {?p dbpprop:url <http://www."+origDomain+">} " +
    					 "UNION {?p dbpprop:url <http://www."+origDomain+"/>} " +
    					 
    					 "UNION {?p dbpprop:url \""+origDomain+"\"@en} " +
    					 "UNION {?p dbpprop:url \""+origDomain+"/\"@en} " +
    					 "UNION {?p dbpprop:url \"http://"+origDomain+"\"@en} " +
    					 "UNION {?p dbpprop:url \"http://"+origDomain+"/\"@en} " +
    					 "UNION {?p dbpprop:url \"http://www."+origDomain+"\"@en} " +
    					 "UNION {?p dbpprop:url \"http://www."+origDomain+"/\"@en} " +
    					 "}";
        		query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
        		if (getResource(query)) {
        			result = true;
        			printResource(origDomain, origDomain, URL, query1, query2, query3, general);
        		}
    		}    		

    		
    		if (!result){
				domain = origDomain.substring(origDomain.indexOf(".")+1);
    			query3 = "ASK {{?p dbpprop:url <"+domain+">} " +
    					 "UNION {?p dbpprop:url <"+domain+"/>} " +
    					 "UNION {?p dbpprop:url <http://"+domain+">} " +
    					 "UNION {?p dbpprop:url <htpp://"+domain+"/>} " +
    					 "UNION {?p dbpprop:url <http://www."+domain+">} " +
    					 "UNION {?p dbpprop:url <http://www."+domain+"/>} " +
    					 
    					 "UNION {?p dbpprop:url \""+domain+"\"@en} " +
    					 "UNION {?p dbpprop:url \""+domain+"/\"@en} " +
    					 "UNION {?p dbpprop:url \"http://"+domain+"\"@en} " +
    					 "UNION {?p dbpprop:url \"http://"+domain+"/\"@en} " +
    					 "UNION {?p dbpprop:url \"http://www."+domain+"\"@en} " +
    					 "UNION {?p dbpprop:url \"http://www."+domain+"/\"@en} " +
    					 "}";
        		query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
        		if (getResource(query)) {
        			result = true;
        			printResource(origDomain, domain, URL, query1, query2, query3, general);
        		}
			}  
    		
    		//foaf:homepage
    		if (!result) {    			
    			query3 = "ASK {{?p foaf:homepage <"+origDomain+">} " +
    					 "UNION {?p foaf:homepage <"+origDomain+"/>} " +
    					 "UNION {?p foaf:homepage <http://"+origDomain+">} " +
    					 "UNION {?p foaf:homepage <http://"+origDomain+"/>} " +
    					 "UNION {?p foaf:homepage <http://www."+origDomain+">} " +
    					 "UNION {?p foaf:homepage <http://www."+origDomain+"/>} " +
    					 
    					 "UNION {?p foaf:homepage \""+origDomain+"\"@en} " +
    					 "UNION {?p foaf:homepage \""+origDomain+"/\"@en} " +
    					 "UNION {?p foaf:homepage \"http://"+origDomain+"\"@en} " +
    					 "UNION {?p foaf:homepage \"http://"+origDomain+"/\"@en} " +
    					 "UNION {?p foaf:homepage \"http://www."+origDomain+"\"@en} " +
    					 "UNION {?p foaf:homepage \"http://www."+origDomain+"/\"@en} " +
    					 "}";
        		query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
        		if (getResource(query)) {
        			result = true;
        			printResource(origDomain, origDomain, URL, query1, query2, query3, general);
        		}
    		}    		

    		
    		if (!result){
				domain = origDomain.substring(origDomain.indexOf(".")+1);
				query3 = "ASK {{?p foaf:homepage <"+domain+">} " +
						 "UNION {?p foaf:homepage <"+domain+"/>} " +
						 "UNION {?p foaf:homepage <http://"+domain+">} " +
						 "UNION {?p foaf:homepage <http://"+domain+"/>} " +
						 "UNION {?p foaf:homepage <http://www."+domain+">} " +
						 "UNION {?p foaf:homepage <http://www."+domain+"/>} " +

						 "UNION {?p foaf:homepage \""+domain+"\"@en} " +
    					 "UNION {?p foaf:homepage \""+domain+"/\"@en} " +
    					 "UNION {?p foaf:homepage \"http://"+domain+"\"@en} " +
    					 "UNION {?p foaf:homepage \"http://"+domain+"/\"@en} " +
    					 "UNION {?p foaf:homepage \"http://www."+domain+"\"@en} " +
    					 "UNION {?p foaf:homepage \"http://www."+domain+"/\"@en} " +
    					 "}";
        		query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
        		if (getResource(query)) {
        			result = true;
        			printResource(origDomain, domain, URL, query1, query2, query3, general);
        		}
			}
    		
    		// dbpprop:reference    		
    		if (!result) {    			
    			query3 = "ASK {{?p dbpprop:reference <"+origDomain+">} " +
    			 		 "UNION {?p dbpprop:reference <"+origDomain+"/>} " +
    			 		 "UNION {?p dbpprop:reference <http://"+origDomain+">} " +
    			 		 "UNION {?p dbpprop:reference <http://"+origDomain+"/>} " +
    			 		 "UNION {?p dbpprop:reference <http://www."+origDomain+">} " +
    			 		 "UNION {?p dbpprop:reference <http://www."+origDomain+"/>} " +
    			 		 
    			 		 "UNION {?p dbpprop:reference \""+origDomain+"\"@en} " +
    			 		 "UNION {?p dbpprop:reference \""+origDomain+"/\"@en} " +
    			 		 "UNION {?p dbpprop:reference \"http://"+origDomain+"\"@en} " +
    			 		 "UNION {?p dbpprop:reference \"http://"+origDomain+"/\"@en} " +
    			 		 "UNION {?p dbpprop:reference \"http://www."+origDomain+"\"@en} " +
    			 		 "UNION {?p dbpprop:reference \"http://www."+origDomain+"/\"@en} " +
    					 "}";
        		query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
        		if (getResource(query)) {
        			result = true;
        			printResource(origDomain, origDomain, URL, query1, query2, query3, general);
        		}
    		}   		

    		
    		if (!result){
				domain = origDomain.substring(origDomain.indexOf(".")+1);
				query3 = "ASK {{?p dbpprop:reference <"+domain+">} " +
						 "UNION {?p dbpprop:reference <"+domain+"/>} " +
						 "UNION {?p dbpprop:reference <http://"+domain+">} " +
						 "UNION {?p dbpprop:reference <http://"+domain+"/>} " +
						 "UNION {?p dbpprop:reference <http://www."+domain+">} " +
						 "UNION {?p dbpprop:reference <http://www."+domain+"/>} " +

    			 		 "UNION {?p dbpprop:reference \""+domain+"\"@en} " +
    			 		 "UNION {?p dbpprop:reference \""+domain+"/\"@en} " +
    			 		 "UNION {?p dbpprop:reference \"http://"+domain+"\"@en} " +
    			 		 "UNION {?p dbpprop:reference \"http://"+domain+"/\"@en} " +
    			 		 "UNION {?p dbpprop:reference \"http://www."+domain+"\"@en} " +
    			 		 "UNION {?p dbpprop:reference \"http://www."+domain+"/\"@en} " +
    					 "}";
        		query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
        		if (getResource(query)) {
        			result = true;
        			printResource(origDomain, domain, URL, query1, query2, query3, general);
        		}
			}
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return response;
    }
    
    
	private static boolean queryDBPediaWithHomepage(String URL, String query1,
			String query2, String origDomain, boolean result)
			throws UnsupportedEncodingException, Exception {
		String domain;
		String query;
		String query3;
		//foaf:homepage + dbpedia-owl:Organization
		
		if (!result){
			query3 = "ASK {{?p foaf:homepage <"+origDomain+"> . ?p a dbpedia-owl:Organisation} " +
				     "UNION {?p foaf:homepage <"+origDomain+"/> . ?p a dbpedia-owl:Organisation} " +        
				     "UNION {?p foaf:homepage <http://"+origDomain+"> . ?p a dbpedia-owl:Organisation} " +
				     "UNION {?p foaf:homepage <http://"+origDomain+"/> . ?p a dbpedia-owl:Organisation} " +
				     "UNION {?p foaf:homepage <http://www."+origDomain+"> . ?p a dbpedia-owl:Organisation} " +
				     "UNION {?p foaf:homepage <http://www."+origDomain+"/> . ?p a dbpedia-owl:Organisation} " +
				     
				     "UNION {?p foaf:homepage \""+origDomain+"\"@en . ?p a dbpedia-owl:Organisation} " +
				     "UNION {?p foaf:homepage \""+origDomain+"/\"@en . ?p a dbpedia-owl:Organisation} " +        
				     "UNION {?p foaf:homepage \"http://"+origDomain+"\"@en . ?p a dbpedia-owl:Organisation} " +
				     "UNION {?p foaf:homepage \"http://"+origDomain+"/\"@en . ?p a dbpedia-owl:Organisation} " +
				     "UNION {?p foaf:homepage \"http://www."+origDomain+"\"@en . ?p a dbpedia-owl:Organisation} " +
				     "UNION {?p foaf:homepage \"http://www."+origDomain+"/\"@en . ?p a dbpedia-owl:Organisation} " +
					 "}";
			query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
			if (getResource(query)) {
				result = true;
				printResource(origDomain, origDomain, URL, query1, query2, query3, fromHomepage);
			} 
//			else {
//				if (!result){
//					query3 = "ASK {{?p foaf:homepage <"+origDomain+">} " +
//						     "UNION {?p foaf:homepage <"+origDomain+"/>} " +        
//						     "UNION {?p foaf:homepage <http://"+origDomain+">} " +
//						     "UNION {?p foaf:homepage <http://"+origDomain+"/>} " +
//						     "UNION {?p foaf:homepage <http://www."+origDomain+">} " +
//						     "UNION {?p foaf:homepage <http://www."+origDomain+"/>} " +
//						     
//						     "UNION {?p foaf:homepage \""+origDomain+"\"@en} " +
//						     "UNION {?p foaf:homepage \""+origDomain+"/\"@en} " +        
//						     "UNION {?p foaf:homepage \"http://"+origDomain+"\"@en} " +
//						     "UNION {?p foaf:homepage \"http://"+origDomain+"/\"@en} " +
//						     "UNION {?p foaf:homepage \"http://www."+origDomain+"\"@en} " +
//						     "UNION {?p foaf:homepage \"http://www."+origDomain+"/\"@en} " +
//							 "}";
//					query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
//					if (getResource(query)) {
//						result = true;
//						printResource(origDomain, origDomain, URL, query1, query2, query3, fromHomepage);
//					}
//				} 
//			}
		}   		

		
		if (!result){
			domain = origDomain.substring(origDomain.indexOf(".")+1);
			query3 = "ASK {{?p foaf:homepage <"+domain+"> . ?p a dbpedia-owl:Organisation} " +
					 "UNION {?p foaf:homepage <"+domain+"/> . ?p a dbpedia-owl:Organisation} " + 	
					 "UNION {?p foaf:homepage <http://"+domain+"> . ?p a dbpedia-owl:Organisation} " +
					 "UNION {?p foaf:homepage <http://"+domain+"/> . ?p a dbpedia-owl:Organisation} " +
					 "UNION {?p foaf:homepage <http://www."+domain+"> . ?p a dbpedia-owl:Organisation} " +
					 "UNION {?p foaf:homepage <http://www."+domain+"/> . ?p a dbpedia-owl:Organisation} " +
		
					 "UNION {?p foaf:homepage \""+domain+"\"@en . ?p a dbpedia-owl:Organisation} " +
				     "UNION {?p foaf:homepage \""+domain+"/\"@en . ?p a dbpedia-owl:Organisation} " +        
				     "UNION {?p foaf:homepage \"http://"+domain+"\"@en . ?p a dbpedia-owl:Organisation} " +
				     "UNION {?p foaf:homepage \"http://"+domain+"/\"@en . ?p a dbpedia-owl:Organisation} " +
				     "UNION {?p foaf:homepage \"http://www."+domain+"\"@en . ?p a dbpedia-owl:Organisation} " +
				     "UNION {?p foaf:homepage \"http://www."+domain+"/\"@en . ?p a dbpedia-owl:Organisation} " +
					 "}";
			
			query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
			if (getResource(query)) {
				result = true;
				printResource(origDomain, domain, URL, query1, query2, query3, fromHomepage);
			} 
//			else {
//				if (!result){
//					query3 = "ASK {{?p foaf:homepage <"+domain+">} " +
//						     "UNION {?p foaf:homepage <"+domain+"/>} " +        
//						     "UNION {?p foaf:homepage <http://"+domain+">} " +
//						     "UNION {?p foaf:homepage <http://"+domain+"/>} " +
//						     "UNION {?p foaf:homepage <http://www."+domain+">} " +
//						     "UNION {?p foaf:homepage <http://www."+domain+"/>} " +
//						     
//						     "UNION {?p foaf:homepage \""+domain+"\"@en} " +
//						     "UNION {?p foaf:homepage \""+domain+"/\"@en} " +        
//						     "UNION {?p foaf:homepage \"http://"+domain+"\"@en} " +
//						     "UNION {?p foaf:homepage \"http://"+domain+"/\"@en} " +
//						     "UNION {?p foaf:homepage \"http://www."+domain+"\"@en} " +
//						     "UNION {?p foaf:homepage \"http://www."+domain+"/\"@en} " +
//							 "}";
//					query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
//					if (getResource(query)) {
//						result = true;
//						printResource(origDomain, origDomain, URL, query1, query2, query3, fromHomepage);
//					}
//				} 
//			}
		}
		return result;
	}
    
    
	private static boolean queryDBPediaWithRef(String URL, String query1,
			String query2, String origDomain, boolean result)
			throws UnsupportedEncodingException, Exception {
		String domain;
		String query;
		String query3;
		
		//dbpprop:reference + dbpedia-owl:Website    		
		if (!result) {    			
			query3 = "ASK {{?p dbpprop:reference <"+origDomain+"> . ?p a dbpedia-owl:Website} " +
			 		 "UNION {?p dbpprop:reference <"+origDomain+"/> . ?p a dbpedia-owl:Website} " +
			 		 "UNION {?p dbpprop:reference <http://"+origDomain+"> . ?p a dbpedia-owl:Website} " +
			 		 "UNION {?p dbpprop:reference <http://"+origDomain+"/> . ?p a dbpedia-owl:Website} " +
			 		 "UNION {?p dbpprop:reference <http://www."+origDomain+"> . ?p a dbpedia-owl:Website} " +
			 		 "UNION {?p dbpprop:reference <http://www."+origDomain+"/> . ?p a dbpedia-owl:Website} " +
			 		 
			 		 "UNION {?p dbpprop:reference \""+origDomain+"\"@en . ?p a dbpedia-owl:Website} " +
			 		 "UNION {?p dbpprop:reference \""+origDomain+"/\"@en . ?p a dbpedia-owl:Website} " +
			 		 "UNION {?p dbpprop:reference \"http://"+origDomain+"\"@en . ?p a dbpedia-owl:Website} " +
			 		 "UNION {?p dbpprop:reference \"http://"+origDomain+"/\"@en . ?p a dbpedia-owl:Website} " +
			 		 "UNION {?p dbpprop:reference \"http://www."+origDomain+"\"@en . ?p a dbpedia-owl:Website} " +
			 		 "UNION {?p dbpprop:reference \"http://www."+origDomain+"/\"@en . ?p a dbpedia-owl:Website} " +    			 		 
					 "}";
			query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
			if (getResource(query)) {
				result = true;
				printResource(origDomain, origDomain, URL, query1, query2, query3, fromRef);
			} 
//			else {
//				if (!result) {    			
//					query3 = "ASK {{?p dbpprop:reference <"+origDomain+">} " +
//					 		 "UNION {?p dbpprop:reference <"+origDomain+"/>} " +
//					 		 "UNION {?p dbpprop:reference <http://"+origDomain+">} " +
//					 		 "UNION {?p dbpprop:reference <http://"+origDomain+"/>} " +
//					 		 "UNION {?p dbpprop:reference <http://www."+origDomain+">} " +
//					 		 "UNION {?p dbpprop:reference <http://www."+origDomain+"/>} " +
//					 		 
//					 		 "UNION {?p dbpprop:reference \""+origDomain+"\"@en} " +
//					 		 "UNION {?p dbpprop:reference \""+origDomain+"/\"@en} " +
//					 		 "UNION {?p dbpprop:reference \"http://"+origDomain+"\"@en} " +
//					 		 "UNION {?p dbpprop:reference \"http://"+origDomain+"/\"@en} " +
//					 		 "UNION {?p dbpprop:reference \"http://www."+origDomain+"\"@en} " +
//					 		 "UNION {?p dbpprop:reference \"http://www."+origDomain+"/\"@en} " +
//							 "}";
//					query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
//					if (getResource(query)) {
//						result = true;
//						printResource(origDomain, origDomain, URL, query1, query2, query3, fromRef);
//					} 
//				}
//			}
		}
		
		if (!result){
			domain = origDomain.substring(origDomain.indexOf(".")+1);
			query3 = "ASK {{?p dbpprop:reference <"+domain+"> . ?p a dbpedia-owl:Website} " +
					 "UNION {?p dbpprop:reference <"+domain+"/> . ?p a dbpedia-owl:Website} " +
					 "UNION {?p dbpprop:reference <http://"+domain+"> . ?p a dbpedia-owl:Website} " +
					 "UNION {?p dbpprop:reference <http://"+domain+"/> . ?p a dbpedia-owl:Website} " +
					 "UNION {?p dbpprop:reference <http://www."+domain+"> . ?p a dbpedia-owl:Website} " +
					 "UNION {?p dbpprop:reference <http://www."+domain+"/> . ?p a dbpedia-owl:Website} " +
		
			 		 "UNION {?p dbpprop:reference \""+domain+"\"@en . ?p a dbpedia-owl:Website} " +
			 		 "UNION {?p dbpprop:reference \""+domain+"/\"@en . ?p a dbpedia-owl:Website} " +
			 		 "UNION {?p dbpprop:reference \"http://"+domain+"\"@en . ?p a dbpedia-owl:Website} " +
			 		 "UNION {?p dbpprop:reference \"http://"+domain+"/\"@en . ?p a dbpedia-owl:Website} " +
			 		 "UNION {?p dbpprop:reference \"http://www."+domain+"\"@en . ?p a dbpedia-owl:Website} " +
			 		 "UNION {?p dbpprop:reference \"http://www."+domain+"/\"@en . ?p a dbpedia-owl:Website} " +
					 "}";
    		query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
    		if (getResource(query)) {
    			result = true;
    			//dbpediaLinks.put(domain, query3);
    			printResource(origDomain, domain, URL, query1, query2, query3, fromRef);
    		} 
    		
//    		else {
//    			if (!result) {    			
//					query3 = "ASK {{?p dbpprop:reference <"+domain+">} " +
//					 		 "UNION {?p dbpprop:reference <"+domain+"/>} " +
//					 		 "UNION {?p dbpprop:reference <http://"+domain+">} " +
//					 		 "UNION {?p dbpprop:reference <http://"+domain+"/>} " +
//					 		 "UNION {?p dbpprop:reference <http://www."+domain+">} " +
//					 		 "UNION {?p dbpprop:reference <http://www."+domain+"/>} " +
//					 		 
//					 		 "UNION {?p dbpprop:reference \""+domain+"\"@en} " +
//					 		 "UNION {?p dbpprop:reference \""+domain+"/\"@en} " +
//					 		 "UNION {?p dbpprop:reference \"http://"+domain+"\"@en} " +
//					 		 "UNION {?p dbpprop:reference \"http://"+domain+"/\"@en} " +
//					 		 "UNION {?p dbpprop:reference \"http://www."+domain+"\"@en} " +
//					 		 "UNION {?p dbpprop:reference \"http://www."+domain+"/\"@en} " +
//							 "}";
//					query = URL+query1+URLEncoder.encode(query2+query3, "UTF-8")+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8");
//					if (getResource(query)) {
//						result = true;
//						printResource(origDomain, origDomain, URL, query1, query2, query3, fromRef);
//					} 
//				}
//    		}
		}
		
		return result;
	}
	
	/**
	 * had to define static variables to handle recursive behaviour of this function.
	 * @param orginDomain
	 * @param transfDomain
	 * @param URL
	 * @param query1
	 * @param query2
	 * @param query3
	 * @param fromRef
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	private static void printResource(String orginDomain, String transfDomain, String URL, String query1, String query2, String query3, int type)
		throws UnsupportedEncodingException, Exception {
		boolean result = false;		
		if (query3.contains("dbpprop:url") && type != 1) {
			urlDBPediaResultThreshHold = filterDBPediaResults(orginDomain, transfDomain, URL, query1, query2, query3, false);
			result = queryDBPediaWithRef(URL, query1, query2, orginDomain, result);
			if (result) {
				if (urlDBPediaResultThreshHold > refDBPediaResultThreshHold){
					filterDBPediaResults(orginDomain, transfDomain, URL, query1, query2, query3, true);
				}
			}
		}
		else if (query3.contains("dbpprop:url") && (type == 1)){
			refDBPediaResultThreshHold = filterDBPediaResults(orginDomain, transfDomain, URL, query1, query2, query3, false);
			if (refDBPediaResultThreshHold > urlDBPediaResultThreshHold){
				filterDBPediaResults(orginDomain, transfDomain, URL, query1, query2, query3, true);
			}			
		} else if (query3.contains("dbpprop:reference") && (type != 2)){
			refDBPediaResultThreshHold = filterDBPediaResults(orginDomain, transfDomain, URL, query1, query2, query3, false);
			result = queryDBPediaWithHomepage(URL, query1, query2, orginDomain, result);
			if (result) {
				if (refDBPediaResultThreshHold > homepageDBPediaResultThreshHold){
					filterDBPediaResults(orginDomain, transfDomain, URL, query1, query2, query3, true);
				}
			} else {
				if (query3.contains("dbpprop:reference") && ((type == 0) || (type == 1))){
					filterDBPediaResults(orginDomain, transfDomain, URL, query1, query2, query3, true);
				}
			}
			
		} else if (query3.contains("foaf:homepage") && (type == 2)){
			homepageDBPediaResultThreshHold = filterDBPediaResults(orginDomain, transfDomain, URL, query1, query2, query3, false);
			if (homepageDBPediaResultThreshHold >= refDBPediaResultThreshHold){
				filterDBPediaResults(orginDomain, transfDomain, URL, query1, query2, query3, true);
			}	
		} else {
			refDBPediaResultThreshHold = filterDBPediaResults(orginDomain, transfDomain, URL, query1, query2, query3, false);
			if (refDBPediaResultThreshHold > urlDBPediaResultThreshHold){
				filterDBPediaResults(orginDomain, transfDomain, URL, query1, query2, query3, true);
			}			
		} 
		
	}
	private static double filterDBPediaResults(String orginDomain,
			String transfDomain, String URL, String query1, String query2,
			String query3, boolean addToDB) throws UnsupportedEncodingException, Exception {
		String query;
		query3 = query3.replace("ASK ", "SELECT distinct ?p WHERE ");
		query = URL + query1 + URLEncoder.encode(query2 + query3, "UTF-8")+"&format="+URLEncoder.encode("application/sparql-results+xml","UTF-8");
		Map<String, Vector<String>> dbpediaResults = parseXML(getFrom(query, "application/sparql-results+xml"));
		
		Vector<String> dbpediaURLs = dbpediaResults.get("dbpediaURLs");
		Vector<String> numberOfResults = dbpediaResults.get("numberOfResults");
		
		
		Iterator<String> itr = dbpediaURLs.iterator();
		String websiteName = "";
		if (!orginDomain.startsWith("www.")) {
			websiteName = orginDomain.substring(0, orginDomain.lastIndexOf("."));
		} else {
			websiteName = orginDomain.substring(4, orginDomain.lastIndexOf("."));
		}
		
		Map<String, Double> noisePercentage = new HashMap<String, Double>();
		String dbpediaURL = "";
		String dbpediaIdentifier = "";
		while (itr.hasNext()){        	
			dbpediaURL = itr.next();
			dbpediaIdentifier = dbpediaURL.substring(dbpediaURL.lastIndexOf("/")+1);
			if (Integer.valueOf(numberOfResults.firstElement()) > 1) {
				System.out.println(websiteName);
				System.out.println(dbpediaIdentifier);
				System.out.println(filterNoiseData(websiteName,	dbpediaIdentifier));
				noisePercentage.put(dbpediaURL, filterNoiseData(websiteName, dbpediaIdentifier));
			}			
		}
		
		Map<String, Double> noisePercentageSortedByValue = new HashMap<String, Double>();
		noisePercentageSortedByValue = sortByValue(noisePercentage);
		String dbpediaURLToUse = null;
		double similarityThreshold = 0.0;
		for (String key : noisePercentageSortedByValue.keySet()) {
			System.out.println("key/value: " + key + "/"+noisePercentageSortedByValue.get(key));
		}
		for (String key : noisePercentageSortedByValue.keySet()) {
			dbpediaURLToUse = key;
			similarityThreshold = noisePercentageSortedByValue.get(key);
			break;
		}
		if (dbpediaURLToUse != null && similarityThreshold > 0.20 ) {
			System.out.println("final choice: "+dbpediaURLToUse);
			if (addToDB && !alreadyExist(orginDomain)) {
				addToDB(orginDomain, transfDomain, query3, dbpediaURLToUse, Integer.valueOf(numberOfResults.firstElement()));
			}			
//			finalDBPediaURLToUse = dbpediaURLToUse;
		} else {
			similarityThreshold = filterNoiseData(websiteName, dbpediaIdentifier);
			if (similarityThreshold > 0.20){
				if (addToDB && !alreadyExist(orginDomain)) {
					addToDB(orginDomain, transfDomain, query3, dbpediaURL, Integer.valueOf(numberOfResults.firstElement()));
				}
//				finalDBPediaURLToUse = dbpediaURL;
			}			
		}
		return similarityThreshold;
	}
	
	private static void addToDB (String host, String transfHost, String query, String dbpediaURL, int numberOfResults) {
		Connection con = null;
		Statement stmt = null;
        try {        	 
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost/pimst";
            con = DriverManager.getConnection(url, "root", "");            
            stmt = con.createStatement();
            stmt.executeUpdate("INSERT into dbplinks VALUES (\'"+host+"\',\'"+transfHost+"\',\'"+query+"\'," +
            												"\'"+dbpediaURL+"\',"+numberOfResults+")");           
                       
            
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        	try {        		
				stmt.close();
				con.close(); 
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }
	}
	
	private static boolean alreadyExist (String host) {
		boolean result = false;
		Connection con = null;
		Statement stmt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost/pimst";
            con = DriverManager.getConnection(url, "root", "");            
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT host FROM dbplinks WHERE host = '"+host+"'");
            if (rs.next()) {
            	result = true;
            }          
            
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        	try {        		
				stmt.close();
				con.close(); 
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }
        return result;
	}
    
	private static double filterNoiseData (String strFirst, String strSec) {
		return LetterPairSimilarity.compareStrings(strFirst, strSec);
	}
    
	private static boolean getResource(String query)
			throws Exception {
		boolean result = false;
		
			if (getFrom(query, "text/html").equalsIgnoreCase("true")) {
				result = true;
			} 

		return result;
	} 
	
	private static Map<String, Double> sortByValue(Map map) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o2, Object o1) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});
		// logger.info(list);
		Map result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	
	private static Map<String, Vector<String>> parseXML(String sparqlResults) {
		Map <String, Vector<String>> dbpediaResults= new HashMap<String, Vector<String>>();
		Vector<String> dbpediaURLs = new Vector<String>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(sparqlResults));

		Document doc = null;
		try {
			doc = db.parse(is);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		NodeList nodes = doc.getElementsByTagName("result");
		// iterate the results
        for (int i = 0; i < nodes.getLength(); i++) {
           Element element = (Element) nodes.item(i);

           NodeList name = element.getElementsByTagName("uri");
           Element line = (Element) name.item(0);
           System.out.println("uri: " + getCharacterDataFromElement(line));
           dbpediaURLs.add(getCharacterDataFromElement(line));
        }
        Vector<String> numberOfResults = new Vector<String>();
        numberOfResults.add(String.valueOf(nodes.getLength()));
        dbpediaResults.put("numberOfResults", numberOfResults);
        dbpediaResults.put("dbpediaURLs", dbpediaURLs);
        return dbpediaResults;
	}
	
	private static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "?";
	}
	
	public static int getLevenshteinDistance(String s, String t) {
	      if (s == null || t == null) {
	          throw new IllegalArgumentException("Strings must not be null");
	      }

	      int n = s.length(); // length of s
	      int m = t.length(); // length of t

	      if (n == 0) {
	          return m;
	      } else if (m == 0) {
	          return n;
	      }

	      if (n > m) {
	          // swap the input strings to consume less memory
	          String tmp = s;
	          s = t;
	          t = tmp;
	          n = m;
	          m = t.length();
	      }

	      int p[] = new int[n+1]; //'previous' cost array, horizontally
	      int d[] = new int[n+1]; // cost array, horizontally
	      int _d[]; //placeholder to assist in swapping p and d

	      // indexes into strings s and t
	      int i; // iterates through s
	      int j; // iterates through t

	      char t_j; // jth character of t

	      int cost; // cost

	      for (i = 0; i<=n; i++) {
	          p[i] = i;
	      }

	      for (j = 1; j<=m; j++) {
	          t_j = t.charAt(j-1);
	          d[0] = j;

	          for (i=1; i<=n; i++) {
	              cost = s.charAt(i-1)==t_j ? 0 : 1;
	              // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
	              d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);
	          }

	          // copy current distance counts to 'previous row' distance counts
	          _d = p;
	          p = d;
	          d = _d;
	      }

	      // our last action in the above loop was to switch d and p, so p now 
	      // actually has the most recent cost counts
	      return p[n];
	  }
    
}

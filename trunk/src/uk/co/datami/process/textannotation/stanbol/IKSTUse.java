package uk.co.datami.process.textannotation.stanbol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class IKSTUse {
		
    String stanbolEnhancer=""; //"http://localhost:8080/enhancer"
    
    public IKSTUse(String stanbolEnhancer){
	this.stanbolEnhancer=stanbolEnhancer;
    }
    
    public ArrayList<String> fromTextToRDF(String content, String RDFFilePath){		
	try {
	    URL url = new URL(this.stanbolEnhancer);
	    URLConnection   urlConn;
	    DataOutputStream    printout;
	    DataInputStream     input;
	    
		    try {
		    	// URL connection channel.
			urlConn = url.openConnection();
			((HttpURLConnection)urlConn).setRequestMethod("POST");
			// Let the run-time system (RTS) know that we want input.
			urlConn.setDoInput (true);
			
			// Let the RTS know that we want to do output.
			urlConn.setDoOutput (true);
			
			// No caching, we want the real thing.
			urlConn.setUseCaches (false);
			
			// Specify the content type.
			urlConn.setRequestProperty("Content-type", "text/plain");
			// urlConn.setRequestProperty("Accept", "text/turtle");
			urlConn.setRequestProperty("Accept", "application/rdf+xml");
			    
			// Send POST output.
			printout = new DataOutputStream (urlConn.getOutputStream ());
			
			printout.writeBytes (content);
			    
			printout.flush ();
			printout.close ();
						    
			// Get response data.
			input = new DataInputStream (urlConn.getInputStream ());
		  	
			BufferedReader d
			    = new BufferedReader(new InputStreamReader(input));
			
			//File f=new File(homeDirectory+System.getProperty("file.separator")+"m"+i+".rdf"); //i++;
			FileWriter fstream = new FileWriter(RDFFilePath);
			// File rdfFile=new File(RDFFilePath); //i++;
			
			BufferedWriter out = new BufferedWriter(fstream);
			String str;
			while (null != ((str = d.readLine())))
			    {
				// System.out.println (str);
				out.write(str);
				out.write("\n"); 	    
			    }
			    input.close ();
			    out.close(); 				
			} catch (IOException e) {
				e.printStackTrace();
			}		    		    		    
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}		
		ArrayList<String> eas = getEntityAnnotation(RDFFilePath);
		return eas;
	}
	
	private ArrayList<String> getEntityAnnotation(String RDFFilePath){		
		ArrayList<String> entityAnnotationList=null;		
		String rdfFilePath="file://"+RDFFilePath;		
		if(!(RDFFilePath==null)){						
			Model model = ModelFactory.createDefaultModel();
			model.read(rdfFilePath);			
			String queryString=					
			    "SELECT ?x WHERE {"+
			    "	?x a <http://fise.iks-project.eu/ontology/EntityAnnotation> ."+
			    "	}";			
			com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString); 
			QueryExecution qexec = QueryExecutionFactory.create(query, model);
			ResultSet rs=qexec.execSelect();		    
			entityAnnotationList =new ArrayList<String>();			
			while(rs.hasNext()){
			    entityAnnotationList.add(rs.next().getResource("x").getURI());
			 }			
			return entityAnnotationList; 
		}		
		return null;
	}				
	
}

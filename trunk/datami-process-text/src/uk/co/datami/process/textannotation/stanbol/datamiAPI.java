package uk.co.datami.process.textannotation.stanbol;

import java.io.UnsupportedEncodingException;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class datamiAPI {

	String indexerPath="";
	String stanbolEnhancer="";
	String fromTextToRDF=""; 
	String rdfReporisoty=""; 
	
    public datamiAPI(String indexPath, String stanbolEnhancer, String fromTextToRDF, String rdfReporisoty){
	this.indexerPath=indexPath;
	this.stanbolEnhancer=stanbolEnhancer;
	this.fromTextToRDF=fromTextToRDF;
	this.rdfReporisoty=rdfReporisoty;
    }
		
    public ArrayList<String> annotate(String freeText){	
	if(freeText!=null && !freeText.trim().equals("")){
	    MD5Generator md5= new MD5Generator();	
	    try {
		//create MD5 from the text
		String s1=md5.getMD5(freeText);		
		ArrayList<String> descriptorsResult1=null;
		if(descriptorsResult1!=null){
		    return descriptorsResult1;
		}
		else{
		    // from Text to RDF file
		    IKSTUse ikstUse=new IKSTUse(this.stanbolEnhancer);		    
		    String rdfFilepath=this.fromTextToRDF+System.getProperty("file.separator")
			+ s1+".rdf";

		    ArrayList<String> descriptions=ikstUse.fromTextToRDF(freeText, rdfFilepath);		    
		    FusekiUse fu = new FusekiUse(this.rdfReporisoty);
		    fu.saveFile(rdfFilepath);
		    
		    // delete the file
		    File f = new File(rdfFilepath);
		    f.delete();
		    
		    return descriptions;
		}		
	    } catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
	    } catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	    }	    	    
	}	
	return new ArrayList<String>();	
    }            	    	
	
}

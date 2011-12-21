package uk.ac.open.kmi.dbpedia.link;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class OneFilePIMSTRepositoryPopulator {
	
	public static void main (String args[]) {
    	System.getProperties().put("proxySet","true");
        System.getProperties().put("proxyHost", "wwwcache.open.ac.uk");
        System.getProperties().put("proxyPort", "80");
		populatePIMSTDBPediaLinks();
		populatePIMSTAttribRep();
        populatePIMSTAttribMatchRep();
	}
	
	private static void populatePIMSTAttribMatchRep () {
		try {            
            Connection con = null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                String url = "jdbc:mysql://localhost/pimst";
                con = DriverManager.getConnection(url, "root", "");
                
                java.sql.Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT h.id, h.host, h.attribute, a.pattrib, h.value FROM hosts as h, attribmatches as a " +
                								 "WHERE h.status = 'Match' AND h.id = a.did order by attribute");
                Model dModel = ModelFactory.createDefaultModel();
                String defaultBase = "http://kmi.open.ac.uk/pimst/ontology/";
                String subjectURI = "";
                String host = "";
                String profileAttribute = "";
                String strValue = "";
                String hostURI = "";
                while (rs.next()){
                	host = rs.getString(2);
                	subjectURI = rs.getString(3);
                	profileAttribute = rs.getString(4);
                	strValue = rs.getString(5);
                	if (!host.contains("http://")){
                    	hostURI = "http://"+host;
                    }
                	System.out.println(subjectURI);
                    dModel.createResource(subjectURI).addProperty(dModel.createProperty(defaultBase+"wasSentTo"), 
                    		dModel.createResource(hostURI));                    
                    dModel.createResource(subjectURI).addProperty(dModel.createProperty(defaultBase+"hasValue"), 
                    		dModel.createTypedLiteral(strValue.trim()));                    
                    dModel.createResource(subjectURI).addProperty(dModel.createProperty(defaultBase+"hasAttribute"), 
                    		dModel.createTypedLiteral(profileAttribute));
                }                
                rs.close();
                stmt.close();
                
                dModel.setNsPrefix("rdf", NameSpace.RDF);
                dModel.setNsPrefix("rdfs", NameSpace.RDFS);
                dModel.setNsPrefix("pimst", "http://kmi.open.ac.uk/pimst/ontology/");
                
                OutputStream out = null;
                File tempFile = new File("PIMSTAttribData/attribMatches.rdf");
                try {
        			out = new FileOutputStream(tempFile);
        		} catch (FileNotFoundException e) {
        			e.printStackTrace();
        		}
                dModel.write(out, "RDF/XML");
                try {
        			out.flush();
        			out.close();
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
                
            } catch (Exception ex) {
                System.out.println(ex);
            }           

        } catch (Exception ex) {
        	System.out.println(ex);
        }
	}
	
	private static void populatePIMSTAttribRep () {
		try {            
            Connection con = null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                String url = "jdbc:mysql://localhost/pimst";
                con = DriverManager.getConnection(url, "root", "");
                
                java.sql.Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT distinct dbpediaurl, host FROM dbplinks");
                
             // create an empty model
                Model dModel = ModelFactory.createDefaultModel();
                String defaultBase = "http://kmi.open.ac.uk/pimst/ontology/";
                String subjectURI = "";
                String host = "";
                
                while (rs.next()){
                	subjectURI = rs.getString(1);
                	host = rs.getString(2);
                	System.out.println(subjectURI);
                	dModel.createResource(subjectURI).addProperty(dModel.createProperty(defaultBase+"hasWebsite"), 
                    		dModel.createResource("http://"+host));
                }                
                rs.close();
                stmt.close();
                
                dModel.setNsPrefix("rdf", NameSpace.RDF);
                dModel.setNsPrefix("rdfs", NameSpace.RDFS);
                dModel.setNsPrefix("pimst", "http://kmi.open.ac.uk/pimst/ontology/");
                
                OutputStream out = null;
                File tempFile = new File("PIMSTAttribData/attribs.rdf");
                try {
        			out = new FileOutputStream(tempFile);
        		} catch (FileNotFoundException e) {
        			e.printStackTrace();
        		}
                dModel.write(out, "RDF/XML");
                try {
        			out.flush();
        			out.close();
        		} catch (IOException e) {
        			e.printStackTrace();
        		} 
                
            } catch (Exception ex) {
                System.out.println(ex);
            }           

        } catch (Exception ex) {
        	System.out.println(ex);
        }
	}	
	
	private static void populatePIMSTDBPediaLinks () {		
		try {            
            Connection con = null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                String url = "jdbc:mysql://localhost/pimst";
                con = DriverManager.getConnection(url, "root", "");
                
                java.sql.Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT distinct dbpediaurl FROM dbplinks");
                OutputStream out = null;
                
                File tempFile = new File("PIMSTData/pmist.rdf");
                try {
        			out = new FileOutputStream(tempFile);
        		} catch (FileNotFoundException e) {
        			e.printStackTrace();
        		}
        		Model dModel = ModelFactory.createDefaultModel();
                 
                while (rs.next()){
                	//wirteDBPLinksRDFModel((rs.getString(1).replace("resource", "data"))+".rdf");
                	System.out.println((rs.getString(1).replace("resource", "data"))+".rdf");
                    dModel.read((rs.getString(1).replace("resource", "data"))+".rdf", "RDF/XML");                    
                    System.out.println((rs.getString(1).replace("resource", "data"))+".rdf");
                }                
                rs.close();
                stmt.close();
                dModel.setNsPrefix("dbpedia-owl", "http://dbpedia.org/ontology/");
                dModel.setNsPrefix("dbpprop", "http://dbpedia.org/property/");
                dModel.setNsPrefix("dbpedia", "http://dbpedia.org/resource/");
                dModel.setNsPrefix("fbase", "http://rdf.freebase.com/ns/");
                dModel.setNsPrefix("category", "http://dbpedia.org/resource/Category/");
                dModel.setNsPrefix("yago", "http://dbpedia.org/class/yago/");
                dModel.setNsPrefix("yago-res", "http://mpii.de/yago/resource/");
                dModel.setNsPrefix("skos", NameSpace.SKOS);
                dModel.setNsPrefix("foaf", NameSpace.FOAF);
                dModel.setNsPrefix("owl", NameSpace.OWL);
                
                dModel.write(out, "RDF/XML");                
                
            } catch (Exception ex) {
                System.out.println(ex);
            }           

        } catch (Exception ex) {
        	System.out.println(ex);
        }	           
	}

}

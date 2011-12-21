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

public class PIMSTRepositoryPopulator {
	
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
                while (rs.next()){
                	wirtePIMSTDBPAttribMatchRDFModel(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
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
	
	private static void wirtePIMSTDBPAttribMatchRDFModel (String strId, String host, String attribute, String profileAttribute, String strValue) {
		// create an empty model
        Model dModel = ModelFactory.createDefaultModel();
        String defaultBase = "http://kmi.open.ac.uk/pimst/ontology/";
        String subjectURI = attribute;
        String hostURI = null;
        if (!host.contains("http://")){
        	hostURI = "http://"+host;
        }
        System.out.println(attribute);
        dModel.createResource(subjectURI).addProperty(dModel.createProperty(defaultBase+"wasSentTo"), 
        		dModel.createResource(hostURI));
        
        dModel.createResource(subjectURI).addProperty(dModel.createProperty(defaultBase+"hasValue"), 
        		dModel.createTypedLiteral(strValue.trim()));
        
        dModel.createResource(subjectURI).addProperty(dModel.createProperty(defaultBase+"hasAttribute"), 
        		dModel.createTypedLiteral(profileAttribute));
        
        dModel.setNsPrefix("rdf", NameSpace.RDF);
        dModel.setNsPrefix("rdfs", NameSpace.RDFS);
        dModel.setNsPrefix("pimst", "http://kmi.open.ac.uk/pimst/ontology/");
        
        OutputStream out = null;
        File tempFile = new File("PIMSTAttribData/"+strId+".rdf");
        try {
			out = new FileOutputStream(tempFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        dModel.write(out, "RDF/XML");
        add(tempFile.getAbsolutePath(), "http://kmi.open.ac.uk/pimst/attribs/matches");
        try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
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
                while (rs.next()){
                	wirtePIMSTDBPAttribRDFModel(rs.getString(1), rs.getString(2));
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
	
	private static void wirtePIMSTDBPAttribRDFModel (String dbpediaURL, String host) {
		// create an empty model
        Model dModel = ModelFactory.createDefaultModel();
        String defaultBase = "http://kmi.open.ac.uk/pimst/ontology/";
        String subjectURI = dbpediaURL;
        System.out.println(dbpediaURL);
        dModel.createResource(subjectURI).addProperty(dModel.createProperty(defaultBase+"hasWebsite"), 
        		dModel.createResource("http://"+host));
        
        
        dModel.setNsPrefix("rdf", NameSpace.RDF);
        dModel.setNsPrefix("rdfs", NameSpace.RDFS);
        dModel.setNsPrefix("pimst", "http://kmi.open.ac.uk/pimst/ontology/");
        
        OutputStream out = null;
        File tempFile = new File("PIMSTAttribData/" + dbpediaURL.substring(dbpediaURL.lastIndexOf("/")+1)+".rdf");
        try {
			out = new FileOutputStream(tempFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        dModel.write(out, "RDF/XML");
        add(tempFile.getAbsolutePath(), "http://kmi.open.ac.uk/pimst/attribs");
        try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
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
                while (rs.next()){
                	wirteDBPLinksRDFModel((rs.getString(1).replace("resource", "data"))+".rdf");
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
	
	private static void wirteDBPLinksRDFModel (String strURL) {
		// create an empty model
        Model dModel = ModelFactory.createDefaultModel();
        System.out.println(strURL);
        dModel.read(strURL, "RDF/XML");
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
        
        OutputStream out = null;
        
        File tempFile = new File("PIMSTData/" + strURL.substring(strURL.lastIndexOf("/")+1));
        try {
			out = new FileOutputStream(tempFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        dModel.write(out, "RDF/XML");
        add(tempFile.getAbsolutePath(), "http://kmi.open.ac.uk/pimst");
        try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
     * This method adds RDF Statements from the file provided into the triple store
     * under the provided context.
     * @param filePath
     * @param superContextStr
     */
    public static void add(String filePath, String superContextStr) {
        try {
            Repository luceroRepository = new HTTPRepository("http://localhost:8081/openrdf-sesame", "PIMST");
            luceroRepository.initialize();
            RepositoryConnection con = luceroRepository.getConnection();
            try {                
                File file = new File(filePath);
                ValueFactory valFactory = luceroRepository.getValueFactory();

                URI superContext = valFactory.createURI(superContextStr);
                con.add(file, NameSpace.OP, RDFFormat.RDFXML, superContext);                

            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (RDFParseException ex) {
                System.out.println(filePath);
                ex.printStackTrace();
            } catch (RepositoryException ex) {
                System.out.println(filePath);
                ex.printStackTrace();
            } finally {
                con.close();
            }
        } catch (OpenRDFException ex) {
        	ex.printStackTrace();
        }

    }

}

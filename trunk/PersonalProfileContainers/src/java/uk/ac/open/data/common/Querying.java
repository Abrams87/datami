package uk.ac.open.data.common;

import java.io.StringWriter;
import java.util.Vector;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

public class Querying {

	Repository repository;
	RepositoryConnection repositoryConn;
	RemoteRepositoryManager man;
	
	public Querying(){
		 try {
	    	 man = new RemoteRepositoryManager("http://kmi-web01.open.ac.uk:8080/openrdf-sesame");
	     	 man.initialize();
	    	 repository = man.getRepository("UCIAD");
	    	 repositoryConn = repository.getConnection();
			repositoryConn.setAutoCommit(false);
			System.out.println("Connected to the repository");
	    } catch (Exception ex) {
	      throw new RuntimeException(ex);
	    }
	}
	
	public String getLabel(String uri){
		
		String query = "select distinct ?lab where { <"+uri+"> <http://www.w3.org/2000/01/rdf-schema#label> ?lab }";
		try {
			TupleQueryResult res = evaluateSPARQLQuery(query);
			if (res.hasNext()) {
				BindingSet bs = res.next();
				Value v = bs.getValue("lab");
				return v.toString();
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		if (uri.contains("#")) return uri.substring(uri.lastIndexOf("#")+1);
		if (uri.endsWith("/")) {
			String uri2 = uri.substring(0, uri.length()-2);
			return uri.substring(uri2.lastIndexOf("/")+1);
		}
		return uri.substring(uri.lastIndexOf("/")+1);
	}
	
	public String getComment(String uri){
		String query = "select distinct ?com where { <"+uri+"> <http://www.w3.org/2000/01/rdf-schema#comment> ?lab }";
		try {
			TupleQueryResult res = evaluateSPARQLQuery(query);
			if (res.hasNext()) {
				BindingSet bs = res.next();
				Value v = bs.getValue("com");
				return v.toString();
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return uri;
	}
	
	public String getRDFCode(String uri){
		String query = "describe <"+uri+">";
		try {
			GraphQuery gq = repositoryConn.prepareGraphQuery(QueryLanguage.SPARQL, query);
			StringWriter stringout = new StringWriter();
			RDFWriter w = Rio.createWriter(RDFFormat.RDFXML, stringout);
			gq.evaluate(w);
			return stringout.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public Vector<String[]> getRelationsFrom(String uri){
		String query = "select distinct ?p ?x where { <"+uri+"> ?p ?x } order by ?p";
		Vector<String[]> result = new Vector<String[]>();
		try {
		TupleQueryResult tqr = evaluateSPARQLQuery(query);
		while (tqr.hasNext()) {
			BindingSet bs = tqr.next();
			String[] elem = new String[2];
			elem[0] = bs.getValue("p").toString();
			elem[1] = bs.getValue("x").toString();
			result.add(elem);
		}
		} catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public Vector<String[]> getRelationsTo(String uri){
		String query = "select distinct ?x ?p where { ?x ?p <"+uri+"> } order by ?p";
		Vector<String[]> result = new Vector<String[]>();
		try {
		TupleQueryResult tqr = evaluateSPARQLQuery(query);
		while (tqr.hasNext()) {
			BindingSet bs = tqr.next();
			String[] elem = new String[2];
			elem[0] = bs.getValue("x").toString();
			elem[1] = bs.getValue("p").toString();
			result.add(elem);
		}
		} catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public TupleQueryResult evaluateSPARQLQuery(String query) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		  TupleQuery tupleQuery = repositoryConn.prepareTupleQuery(QueryLanguage.SPARQL, query);
		  TupleQueryResult result = tupleQuery.evaluate();
		  return result; 
	  }
	
	public void shutdown(){
		 System.out.println("\n===== Shutting down ==========\n");
		    if (repository != null) {
		      try {
		    	  repositoryConn.close();
		          man.shutDown();
		          repositoryConn = null;
		      } catch (Exception ex) {
		        throw new RuntimeException(ex);
		      }
		    }
	}
}

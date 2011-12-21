/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.open.data.util;

import info.aduna.iteration.Iterations;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * Manages all the interactions with the underlying triple store.
 * @author se3535
 */
public class UCIADRepositoryManager {
    
    Model userActorModel = ModelFactory.createDefaultModel();
    /**
     * This method adds RDF Statements from the file provided into the triple store
     * under the provided context.
     * @param filePath
     * @param contextURI
     */
    public static void add(String filePath, String contextURI) {
		try {
//			Repository uciadRepository = new HTTPRepository(
//					System.getProperty("repURI"), System.getProperty("repID"));
                        Repository uciadRepository = new HTTPRepository("http://kmi-dev04.open.ac.uk:8080/openrdf-sesame", "UCIADAll");
			uciadRepository.initialize();
			RepositoryConnection con = null;
			con = uciadRepository.getConnection();
			try {
                                con.setAutoCommit(false);
				File file = new File(filePath);
				ValueFactory valFactory = uciadRepository.getValueFactory();

				URI superContext = valFactory.createURI(contextURI);
				con.add(file, NameSpace.UCIAD, RDFFormat.RDFXML, superContext);                                
                                con.commit();

			} catch (IOException ex) {
				ex.printStackTrace();
				Logger.getLogger(UCIADRepositoryManager.class.getName()).log(
						Level.ERROR, null, ex);
			} catch (RDFParseException ex) {
				System.out.println(filePath);
				Logger.getLogger(UCIADRepositoryManager.class.getName()).log(
						Level.ERROR, null, ex);
			} catch (RepositoryException ex) {
				System.out.println(filePath);
				Logger.getLogger(UCIADRepositoryManager.class.getName()).log(
						Level.ERROR, null, ex);
			} finally {
				con.close();
			}
		} catch (OpenRDFException ex) {
            Logger.getLogger(UCIADRepositoryManager.class.getName()).log(Level.ERROR, null, ex);
        } 

    }    

    /**
     * This method removes RDF statements from the triple store contained
     * in the file provided. It uses a temporary repository to list all the
     * statements and then remove them from the actual repository.
     * @param filePath
     * @param superContextStr
     */
    public static void remove(String filePath, String superContextStr) {
        try {

            Repository luceroRepository = new HTTPRepository(System.getProperty("repURI"), System.getProperty("repID"));
            luceroRepository.initialize();
            RepositoryConnection con = luceroRepository.getConnection();
            ValueFactory valFactory = luceroRepository.getValueFactory();

            Repository tempRep = new SailRepository(new MemoryStore());
            tempRep.initialize();
            RepositoryConnection tempRepCon  = tempRep.getConnection();
            URI tempContext = valFactory.createURI(superContextStr+ filePath.substring(filePath.lastIndexOf("/")));           

            try {
                URI superContext = valFactory.createURI(superContextStr);
                
                tempRepCon.add(new File(filePath), null, RDFFormat.RDFXML, tempContext);
                RepositoryResult<Statement> stmtsToRemove = tempRepCon.getStatements(null, null, null, true, tempContext);
                List<Statement> stmtsToRemoveList = Iterations.addAll(stmtsToRemove, new ArrayList<Statement>());
                con.remove(stmtsToRemoveList, superContext);
                tempRepCon.clear(tempContext);

            } catch (RepositoryException ex) {
                Logger.getLogger(UCIADRepositoryManager.class.getName()).log(Level.ERROR, null, ex);
            } finally {
                con.close();
                tempRepCon.close();
            }

        } catch (IOException ex) {
            Logger.getLogger(UCIADRepositoryManager.class.getName()).log(Level.ERROR, null, ex);
        } catch (RDFParseException ex) {
            Logger.getLogger(UCIADRepositoryManager.class.getName()).log(Level.ERROR, null, ex);
        } catch (RepositoryException ex) {
            Logger.getLogger(UCIADRepositoryManager.class.getName()).log(Level.ERROR, null, ex);
        } 

    }
    
    public static TupleQueryResult evaluateSPARQLQuery(String query, String repID)
			throws RepositoryException, MalformedQueryException,
			QueryEvaluationException {
//		Repository uciadRepository = new HTTPRepository(
//				System.getProperty("repURI"), repID);
                Repository uciadRepository = new HTTPRepository("http://kmi-dev04.open.ac.uk:8080/openrdf-sesame", repID);
		uciadRepository.initialize();
		
		RepositoryConnection con = null;
		con = uciadRepository.getConnection();
		TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
		TupleQueryResult result = tupleQuery.evaluate();
                		
		return result;
	}
    /**
     * This function clears the directory containing previously rendered RDF files.
     * @param destDirPath 
     */        
    public static void clearDestDir(String destDirPath) {
        File sourceDir = new File(destDirPath);
        File[] listOfFiles = sourceDir.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            File file = new File(destDirPath + listOfFiles[i].getName());
            if (file.exists()) {
                file.delete();
            }
        }
    }
        
    
    public void createActorWithExistingSetting(String actorSettingURI, String actorUsername) {
        
            userActorModel.createResource(NameSpace.TRACEACTORBASE+actorUsername)
                .addProperty(userActorModel.createProperty(NameSpace.TRACEACTOR, "knownSetting"),
                    userActorModel.createResource(actorSettingURI));        
        writeTheModel(actorUsername);
    }
    
      
    public void createActorWithNewSetting(String actorUsername, String actorAgentId, String IPAddress, boolean newSetting) {
        
            userActorModel.createResource(NameSpace.TRACEACTORBASE+actorUsername)
                .addProperty(userActorModel.createProperty(NameSpace.TRACEACTOR, "knownSetting"), 
                                                                           renderActorAgentSetting(actorAgentId, IPAddress));        
        writeTheModel(actorUsername);
    }

    public Resource renderActorAgentSetting(String actorAgentId, String IPAddress) {
            Resource resource = null;
            try {

                    resource = userActorModel.createResource(NameSpace.ACTORSETTINGBASE+MD5Generator.getMD5(IPAddress+actorAgentId))
                            .addProperty(userActorModel.createProperty(NameSpace.TRACEACTOR, "hasAgent"), 
                                                                                                                            renderActorAgent(actorAgentId));

                    resource.addProperty(userActorModel.createProperty(NameSpace.TRACEACTOR, "fromComputer"),	renderComputer(IPAddress));

                    resource.addProperty(RDF.type, userActorModel.createResource(NameSpace.TRACEACTOR+"ActorSetting"));


            } catch (NoSuchAlgorithmException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
            return resource;
    }

	
	public Resource renderActorAgent(String actorAgentId) {
		Resource resource = null;
		try {
			resource = userActorModel.createResource(NameSpace.TRACEACTORBASE+MD5Generator.getMD5(actorAgentId))
											.addProperty(userActorModel.createProperty(NameSpace.TRACEACTOR,"agentId"), 
														 userActorModel.createTypedLiteral(actorAgentId));
			resource.addProperty(RDF.type, userActorModel.createResource(NameSpace.TRACEACTOR+"ActorAgent"));
			//System.out.println("actorAgent: "+resource.toString());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resource;
	}
	
	
	public Resource renderComputer(String IPAddress) {
		Resource resource = null;
		try {
			resource = userActorModel.createResource(NameSpace.COMPUTERBASE+MD5Generator.getMD5(IPAddress))
											.addProperty(userActorModel.createProperty(NameSpace.TRACEACTOR, "hasIPAddress"),
														 userActorModel.createTypedLiteral(IPAddress));
			
			resource.addProperty(RDF.type, userActorModel.createResource(NameSpace.TRACEACTOR+"Computer"));
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	    return resource;
	}
        
        
        private void writeTheModel(String username) {
            //FINAL MODEL
            userActorModel.setNsPrefix("trace", NameSpace.TRACE);
            userActorModel.setNsPrefix("traceactor", NameSpace.TRACEACTOR);
            userActorModel.setNsPrefix("rdf", NameSpace.RDF);

            String fileName = ("userModel.rdf");            
            OutputStream out = null;
            File tempFile = new File(fileName);
            String path = tempFile.getAbsolutePath();
            try {
                out = new FileOutputStream(tempFile);
                userActorModel.write(out, "RDF/XML");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.flush();
                    out.close();
                    //TODO
                    //UCIADRepositoryManager.add(fileName, System.getProperty("context"));
                    UCIADRepositoryManager.add(fileName, "http://uciad.info/users/"+username);
                    userActorModel.close();
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    
    
}

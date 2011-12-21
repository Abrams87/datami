/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.open.data.servlets;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import uk.ac.open.data.util.Commons;
import uk.ac.open.data.util.DBConnection;
import uk.ac.open.data.util.HTTPUtils;
import uk.ac.open.data.util.UCIADRepositoryManager;

/**
 *
 * @author se3535
 */
public class Register extends HttpServlet {
   
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    boolean regSuccessfull = false;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        try {

            String userAgent = HTTPUtils.detectBrowser(req.getHeader("User-Agent"));
            //String userAgent = "unknown browser";
            if (userAgent.equalsIgnoreCase("unknown browser"))
            {
                userAgent = req.getHeader("User-Agent");
            }
            
            //String userAgentId = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3)";
            String IPAddress =   req.getRemoteAddr();
            if (IPAddress.equals("127.0.0.1"))
            {
                IPAddress = "137.108.24.198";
            }
            
            resp.setContentType("text/html");
            out.println(Commons.header("UCIAD"));            
            if (req.getParameter("cmd") != null && req.getParameter("cmd").equalsIgnoreCase("exist"))
            {
                out.println("<p style=\"color:red;\">This username \""+req.getParameter("username") +"\" already exist, please try anotherone.</p><br/>" );
            }
            else if (req.getParameter("cmd") != null && req.getParameter("cmd").equalsIgnoreCase("mismatch"))
            {
                out.println("<p style=\"color:red;\">Your passwords do not match, please try again.</p><br/>" );
            }
            
            out.println("<form name=\"form\" action=\"register\" method=\"POST\">");            
            out.println("Username: <input type=\"text\" name=\"username\" size=\"30\" /> <br/>");
            out.println("Password: <input type=\"password\" name=\"password\" size=\"30\" /> <br/><br/>");
            out.println("ReEnter Password: <input type=\"password\" name=\"password2\" size=\"30\" /> <br/><br/>");
            out.println("<p>You seem to use this browser: "+ userAgent +" <br/> Your IP address is: "+ IPAddress +" </p><br/>" );
            
            String query = " PREFIX traceactor:<http://uciad.info/ontology/actor/> "
                         + " PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> "
                         + " SELECT * WHERE "
                         + " {?actorSetting traceactor:fromComputer ?computer . "
                         + " ?computer traceactor:hasIPAddress \"" + IPAddress + "\"^^xsd:string . "
                         + " ?actorSetting traceactor:hasAgent ?actorAgent ."
                         + " ?actorAgent traceactor:agentId ?agentIdString"
                         + " }";
            
            TupleQueryResult tqr = UCIADRepositoryManager.evaluateSPARQLQuery(query, "UCIADAll");
            
            int index = 1;
            if (tqr.hasNext())
            {
                out.println("<p>We also found following broswers being used againt this IP address: <br/><br/>" );
            }
            while (tqr.hasNext()) {
                    BindingSet bs = tqr.next();                    
                    out.println("<input type=\"checkbox\" name=\"userBrowser"+index+"\" value=\"\" /> "+ 
                            HTTPUtils.detectBrowser(bs.getValue("agentIdString").toString().replaceAll("\"", "").substring(0, 
                                            (bs.getValue("agentIdString").toString().indexOf("^"))-2)) +" <br/>");
                    
                    index++;
            }
            out.println("Please select the browsers you want to be added into you profile<br/><br/>");
            out.println("(These settings will be used to indentify you in future)<br/>");
            out.println("<input type=\"checkbox\" name=\"userSettingAgreement\" value=\"\" onclick=\"checkBoxClick()\"/> Yes I agree <br/><br/>");
            out.println("<input type=\"hidden\" name=\"totalCheckBoxes\" value=\""+index+"\" /> <br/>");
            out.println("<input type=\"submit\" disabled=\"true\" value=\"Register\" name=\"registerBtn\" />");
            out.println("</form>");
            out.println(Commons.footer()); //mainDiv

        } catch (RepositoryException ex) {
            Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedQueryException ex) {
            Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
        } catch (QueryEvaluationException ex) {
            Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {        
        if (DBConnection.userExists(req.getParameter("username")))
        {
            try {                
                    resp.sendRedirect("register?cmd=exist&username="+req.getParameter("username"));
                } catch (IOException ex) {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        else if (!req.getParameter("password").equals(req.getParameter("password2")))
        {
            try {                
                    resp.sendRedirect("register?cmd=mismatch");
                } catch (IOException ex) {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        else {
            try {

                UCIADRepositoryManager repManager = null;
                //String userAgentId = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3)";
                String userAgentId = req.getHeader("User-Agent");
                String IPAddress = req.getRemoteAddr();
                if (IPAddress.equals("127.0.0.1")) {
                    IPAddress = "137.108.24.198";
                }

                PrintWriter out = resp.getWriter();
//            String query = " PREFIX traceactor:<http://uciad.info/ontology/actor/> "
//                         + " PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> "
//                         + " SELECT * WHERE "
//                         + " {?actorSetting traceactor:fromComputer ?computer . "
//                         + " ?computer traceactor:hasIPAddress \"" + IPAddress + "\"^^xsd:string . "
//                         + " ?actorSetting traceactor:hasAgent ?actorAgent ."
//                         + " ?actorAgent traceactor:agentId \"" + userAgentId + "\"^^xsd:string"
//                         + " }";

                String query = " PREFIX traceactor:<http://uciad.info/ontology/actor/> "
                        + " PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> "
                        + " SELECT * WHERE "
                        + " {?actorSetting traceactor:fromComputer ?computer . "
                        + " ?computer traceactor:hasIPAddress \"" + IPAddress + "\"^^xsd:string . "
                        + " ?actorSetting traceactor:hasAgent ?actorAgent ."
                        + " ?actorAgent traceactor:agentId ?agentIdString"
                        + " }";

                TupleQueryResult tqr = UCIADRepositoryManager.evaluateSPARQLQuery(query, "UCIADAll");
                List<String> agentIds = new ArrayList<String>();
                if (tqr.hasNext()) {
                    String agentIdString = "";
                    int index = 1;
                    while (tqr.hasNext()) {
                        BindingSet bs = tqr.next();
                        agentIdString = bs.getValue("agentIdString").toString().replaceAll("\"", "").substring(0,
                                (bs.getValue("agentIdString").toString().indexOf("^")) - 2);
                        agentIds.add(agentIdString);
                        if (req.getParameter("userBrowser" + index) != null) {
                            repManager = new UCIADRepositoryManager();
                            repManager.createActorWithExistingSetting(bs.getValue("actorSetting").toString(), req.getParameter("username"));
                        }
                        index++;
                    }
                    if (!agentIds.contains(userAgentId)) {
                        repManager = new UCIADRepositoryManager();
                        repManager.createActorWithNewSetting(req.getParameter("username"), userAgentId, IPAddress, true);
                    }
                    DBConnection.createUser(req.getParameter("username"), req.getParameter("password"));
                } else {
                    repManager = new UCIADRepositoryManager();
                    repManager.createActorWithNewSetting(req.getParameter("username"), userAgentId, IPAddress, true);
                    DBConnection.createUser(req.getParameter("username"), req.getParameter("password"));
                }
                populateUserGraph(req.getParameter("username"));
                //out.println("Thank you for registering with us.");
                try {
//                HttpSession session = req.getSession(true);
//                session.setAttribute("validUser", 1);
//                session.setAttribute("username", req.getParameter("username"));
                    resp.sendRedirect("login");
                } catch (IOException ex) {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                }


            } catch (RepositoryException ex) {
                ex.printStackTrace();
            } catch (MalformedQueryException ex) {
                ex.printStackTrace();
            } catch (QueryEvaluationException ex) {
                PrintWriter out = resp.getWriter();
                out.println(ex.toString());
                ex.printStackTrace();
            }
        }
             
        
    }
    
    private void populateUserGraph(String username)
    {
        //PrintWriter out = null;
        try {
            //out = resp.getWriter();
            String contructQuery = " PREFIX tr:<http://uciad.info/ontology/trace/> "
                       + " PREFIX actor:<http://uciad.info/ontology/actor/> "
                       + " construct { "
                       + " ?trace ?p ?x. "
                       + "   ?x ?p2 ?x2 . "
                       + "   ?x2 ?p3 ?x3 "
//                       + "  ?x3 ?p4 ?x4 "
                       + " } where { "
                       + "  <http://uciad.info/actor/"+username+"> actor:knownSetting ?set . "
                       + "  ?trace tr:hasSetting ?set . "
                       + "  ?trace ?p ?x. "
                       + "  OPTIONAL {?x ?p2 ?x2 OPTIONAL {?x2 ?p3 ?x3}} "
                       //+ "  OPTIONAL {?x ?p2 ?x2 OPTIONAL  { ?x2 ?p3 ?x3  OPTIONAL {?x3 ?p4 ?x4}}} "
                       + "}";
//            com.hp.hpl.jena.query.Query query = QueryFactory.create(contructQuery);
//            QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("http://kmi-dev04.open.ac.uk:8080/openrdf-workbench/repositories/UCIADAll/query", query);            
//            Model results = qexec.execConstruct();
            String result = HTTPUtils.getFrom("http://137.108.24.63:8080/openrdf-workbench/repositories/UCIADAll/query?query="+URLEncoder.encode(contructQuery, "UTF-8")+"&Accept="+URLEncoder.encode("application/rdf+xml","UTF-8"), "application/rdf+xml");
            File file = new File("ConstructGraph.rdf");            
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(result);
            writer.close();
            UCIADRepositoryManager.add(file.getAbsolutePath(), "http://uciad.info/users/"+username);

                        
        } catch (IOException ex) {
            Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
        }
//        finally {
//            out.close();
//        }
    }

    

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

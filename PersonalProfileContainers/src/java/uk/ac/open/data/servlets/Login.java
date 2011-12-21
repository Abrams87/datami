/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.open.data.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Session;
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
public class Login extends HttpServlet {
   
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        PrintWriter out = resp.getWriter();
        Integer validUser = (Integer)session.getAttribute("validUser");
        if (validUser != null && validUser == 1)
        {
            if (req.getParameter("cmd") != null && req.getParameter("cmd").equalsIgnoreCase("logout"))
            {
                session.invalidate();
                try {
                    resp.sendRedirect("login");
                } catch (IOException ex) {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                resp.setContentType("text/html");
                out.println(Commons.header("UCIAD"));    
                out.println("Welcome to UCIAD!");
                out.println("&nbsp;&nbsp;<a href=login?cmd=logout>Logout</a>");
                out.println(Commons.footer()); //mainDiv
            }
            
        }
        else
        {
            resp.setContentType("text/html");
            out.println(Commons.header("UCIAD"));            
            out.println("<form name=\"register\" action=\"login\" method=\"POST\">");
            //out.println("<input type=\"hidden\" name=\"userAgent\" /> <br/>");
            out.println("Username: <input type=\"text\" name=\"username\" size=\"30\" /> <br/>");
            out.println("Password: <input type=\"password\" name=\"password\" size=\"30\" /> <br/><br/>");
            out.println("<input type=\"submit\" value=\"Log In\" name=\"registerBtn\" />");
            out.println("</form>");
            out.println(Commons.footer()); //mainDiv
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
        //doGet(request, response);
        HttpSession session = req.getSession(true);
        UCIADRepositoryManager repManager = new UCIADRepositoryManager();
        PrintWriter out = resp.getWriter();
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String userAgentId = req.getHeader("User-Agent");
        String IPAddress =   req.getRemoteAddr();
        if (IPAddress.equals("127.0.0.1"))
        {
            IPAddress = "137.108.24.198";
        } 
//        if (req.getParameter("newUserAgent") != null)
//        {
//            repManager.createActorWithNewSetting(req.getParameter("username"), userAgentId, IPAddress, true);
//        }
        
        String requestType = req.getParameter("RequestType");  
        if(requestType != null) {  
            if (requestType.equalsIgnoreCase("Register")) {
                registerNewAgent(req, resp);
                try {
                    resp.sendRedirect("login");
                } catch (IOException ex) {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (requestType.equalsIgnoreCase("Ignore It!") || requestType.equalsIgnoreCase("Ignore This Setting!")) {
                ignoreAgent(req, resp);
                try {
                    resp.sendRedirect("login");
                } catch (IOException ex) {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                }
            }  
        }
        else if (DBConnection.authenticateUser(username, password)) {            
            session.setAttribute("validUser", 1);
            session.setAttribute("username", username);
            try {       

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
                if (tqr.hasNext())
                {                
                    String agentIdString = "";
                    while (tqr.hasNext()) {
                        BindingSet bs = tqr.next();                    
                        agentIdString = bs.getValue("agentIdString").toString().replaceAll("\"", "").substring(0, 
                                                (bs.getValue("agentIdString").toString().indexOf("^"))-2);
                        agentIds.add(agentIdString);
                    }
                    ArrayList<String> ignoredAgentList = DBConnection.getIgnoredAgents(username, IPAddress);
                    if (!agentIds.contains(userAgentId) && !ignoredAgentList.contains(userAgentId))
                    {
                        resp.setContentType("text/html");
                        out.println(Commons.header("UCIAD"));    
                        out.println("Welcome to UCIAD!");
                        out.println("<form name=\"form\" action=\"login\" method=\"POST\">");
                        out.println("<input type=\"hidden\" name=\"newUserAgent\" /> <br/>");
                        out.println("<p>We don't have this browser: \""+ HTTPUtils.detectBrowser(userAgentId) +"\" registered with your profile,<br/>" );
                        out.println("would you like to register it as well? <br/></p>");
                        out.println("<input type=\"checkbox\" name=\"userSettingAgreement\" value=\"\" onclick=\"checkBoxClick()\"/> Yes Please <br/><br/>");
                        out.println("<input type=\"submit\" value=\"Register\"   name=\"RequestType\" />" +"&nbsp;&nbsp;");
                        out.println("<input type=\"submit\" value=\"Ignore It!\" name=\"RequestType\"  />");
                        out.println("</form>");
                        out.println(Commons.footer()); //mainDiv
                        
                    }
                    else
                    {
                        resp.setContentType("text/html");
                        out.println(Commons.header("UCIAD"));    
                        out.println("Welcome to UCIAD!");
                        out.println("&nbsp;&nbsp;<a href=login?cmd=logout>Logout</a>");
                        out.println(Commons.footer()); //mainDiv
                    }

                }
                else
                {
                    ArrayList<String> ignoredIPAddressList = DBConnection.getIgnoredIPAddresses(username);
                    if (!ignoredIPAddressList.contains(IPAddress))
                    {
                        resp.setContentType("text/html");
                        out.println(Commons.header("UCIAD"));    
                        out.println("<form name=\"form\" action=\"login\" method=\"POST\">");
                        out.println("<input type=\"hidden\" name=\"newUserAgent\" /> <br/>");
                        out.println("<p>We don't have a record of this setting:" +"\"" +IPAddress +"\" & "+ "\""+ HTTPUtils.detectBrowser(userAgentId) +"\" registered with your profile,<br/>" );
                        out.println("would you like to register it as well? <br/></p>");
                        out.println("<input type=\"checkbox\" name=\"userSettingAgreement\" value=\"\" onclick=\"checkBoxClick()\"/> Yes Please <br/><br/>");
                        out.println("<input type=\"submit\" value=\"Register\"   name=\"RequestType\" />" +"&nbsp;&nbsp;");
                        out.println("<input type=\"submit\" value=\"Ignore This Setting!\" name=\"RequestType\"  />");
                        out.println("</form>");
                        out.println(Commons.footer()); //mainDiv
                    }
                    else
                    {
                        resp.setContentType("text/html");
                        out.println(Commons.header("UCIAD"));    
                        out.println("Welcome to UCIAD!");
                        out.println("&nbsp;&nbsp;<a href=login?cmd=logout>Logout</a>");
                        out.println(Commons.footer()); //mainDiv
                    }
                    
                }
                
             
            } catch (RepositoryException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedQueryException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            } catch (QueryEvaluationException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            } finally { 
                out.close();
            }       
        } else
        {
            resp.setContentType("text/html");
            out.println(Commons.header("UCIAD"));    
            out.println("Either password or username provided is wrong, please try again. <a href=\"login\">Login</a>");
            out.println(Commons.footer()); //mainDiv
        }
    }
    
    private void registerNewAgent(HttpServletRequest req, HttpServletResponse resp)
    {
        HttpSession session = req.getSession(true);
        String username = (String)session.getAttribute("username");
        UCIADRepositoryManager repManager = new UCIADRepositoryManager();
        String userAgentId = req.getHeader("User-Agent");
        String IPAddress =   req.getRemoteAddr();
        if (IPAddress.equals("127.0.0.1"))
        {
            IPAddress = "137.108.24.198";
        }
        repManager.createActorWithNewSetting(username, userAgentId, IPAddress, true);        
    }
    
    private void ignoreAgent(HttpServletRequest req, HttpServletResponse resp)
    {
        HttpSession session = req.getSession(true);
        String username = (String)session.getAttribute("username");
        String userAgentId = req.getHeader("User-Agent");
        String IPAddress =   req.getRemoteAddr();
        if (IPAddress.equals("127.0.0.1"))
        {
            IPAddress = "137.108.24.198";
        }        
        DBConnection.addIgnoredAgent(username, IPAddress, userAgentId);
        
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

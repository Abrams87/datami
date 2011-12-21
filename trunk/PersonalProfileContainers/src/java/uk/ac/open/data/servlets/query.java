/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.open.data.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import uk.ac.open.data.common.HeaderFooter;
import uk.ac.open.data.util.DBConnection;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author se3535
 */
public class query extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet query</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet query at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
             */
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Principal principal = request.getUserPrincipal();
        String username = principal.getName();

        String query = request.getParameter("query");
        if (query != null && principal != null) {
            System.getProperties().put("proxySet", "true");
            System.getProperties().put("proxyHost", "wwwcache.open.ac.uk");
            System.getProperties().put("proxyPort", "80");
            query = contextualizeQuery(query, username);
            if (query.contains("Sorry you don't have")) {
                PrintWriter out = response.getWriter();
                query = query.replace("<", "\"");
                query = query.replace(">", "\"");
                out.println(query);
                return;
            }
            String url = "http://kmi-dev04.open.ac.uk:8080/openrdf-workbench/repositories/UCIAD/query?query=" + URLEncoder.encode(query);

            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(url);
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler(3, false));
            try {
                int statusCode = client.executeMethod(method);
                if (statusCode != HttpStatus.SC_OK) {
                    System.err.println("Method failed: " + method.getStatusLine());
                    return;
                }
                response.setContentType("application/sparql-results+xml");
                InputStream is = method.getResponseBodyAsStream();
                // to JSON!!! would be very useful...
                ServletOutputStream sos = response.getOutputStream();

                byte[] buffer = new byte[1024];
                int lenght = 0;
                while ((lenght = is.read(buffer)) > 0) {
                    sos.write(buffer, 0, lenght);
                }
                is.close();
                sos.close();
            } catch (HttpException e) {
                System.err.println("Fatal protocol violation: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Fatal transport error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                method.releaseConnection();
            }
        } else {
            PrintWriter out = response.getWriter();
            out.println(HeaderFooter.getHeader("Home"));

            out.println("<form method=\"post\" action=\"http://localhost:8080/PersonalProfileContainers/query\">");
            out.println("<textarea name=\"sparqlQuery\" cols=\"100\" rows=\"25\"></textarea><br/>");
            out.println("<input type=\"submit\" value=\"Submit\" />");
            out.println("</form>");

            out.println(HeaderFooter.getFooter());
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String[] paramValues = request.getParameterValues("sparqlQuery");
        if (paramValues.length > 0) {
            String paramValue = paramValues[0];
            response.sendRedirect("http://localhost:8080/PersonalProfileContainers/query?query=" + paramValue);
        }

    }

    private static String contextualizeQuery(String queryStr, String username) {
        String[] userContexts = DBConnection.getUserContext(username);
        if (StringUtils.containsIgnoreCase(queryStr, "from") && StringUtils.containsIgnoreCase(queryStr, "where")) {
            int indexOfFromClause = com.mysql.jdbc.StringUtils.indexOfIgnoreCase(queryStr, "from");
            int indexOfWhereClause = com.mysql.jdbc.StringUtils.indexOfIgnoreCase(queryStr, "where");

            String fromClause = queryStr.substring(indexOfFromClause, indexOfWhereClause - 1);

            String REGEX = "(from)";
            Pattern p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
            String[] fromClauseItems = p.split(fromClause);
            if (fromClauseItems.length > 2) {

                for (String clause : fromClauseItems) {
                    if (!hasAccessRightsOn(clause.trim(), userContexts) && clause.contains("uciad.info")) {
                        queryStr = "Sorry you don't have access rights on this context: " + clause;
                        break;
                    }
                }
            } else {
                String[] fromClausesplits = fromClause.split(" ");
                if (!hasAccessRightsOn(fromClausesplits[1], DBConnection.getUserContext(username)[0])) {
                    queryStr = "Sorry you don't have access rights on this context: " + fromClausesplits[1];
                }

            }
        } else if (StringUtils.containsIgnoreCase(queryStr, "where")) {
            String REGEX = "(where)";
            Pattern p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
            String[] queryStrSplits = p.split(queryStr);
            String fromClause = "";
            for (String userContext : userContexts) {
                fromClause += "FROM " + userContext + " ";
            }
            queryStr = queryStrSplits[0] + fromClause + " WHERE " + queryStrSplits[1];

        }
        return queryStr;
    }

    private static boolean hasAccessRightsOn(String context, String userContexts[]) {
        boolean result = false;
        for (String userContext : userContexts) {
            if (userContext.equalsIgnoreCase(context)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private static boolean hasAccessRightsOn(String context, String userContext) {
        boolean result = false;

        if (userContext.equalsIgnoreCase(context)) {
            result = true;
        }
        return result;
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.open.data.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.open.data.util.Commons;

/**
 *
 * @author se3535
 */
public class Home extends HttpServlet {
   
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
        PrintWriter out = resp.getWriter();
        //Commons.assignProperties();
        try {
             out.println(Commons.header("UCIAD Web"));
             out.println("<a href=\"login\">Login</a> <br/>");
             out.println("<a href=\"register\">Register</a>");
             out.println(Commons.footer());
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        doGet(request, response);
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

<%--
    Document   : index
    Created on : 11-Jan-2011, 00:14:52
    Author     : se3535
--%>

<%@page import="uk.ac.open.kmi.dbpedia.link.LinkfinderWS"%>
<%@page import="uk.ac.open.kmi.dbpedia.link.LinkfinderWSService"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>DBPedia Link Finder</title>
    </head>
    <body>
        <h1>DBPedia Link Finder</h1>
        <form method="post">
            <p>Please enter a domain name without http e.g. www.google.com, google.com, news.bbc.co.uk</p>
            <input type="text" name="queryURL" size="50"/>
            <input type="submit" name="submitBtn" size="20" value="Search"/>
        </form>
        <%
        LinkfinderWSService service = new LinkfinderWSService();
        //System.out.println(service.getWSDLDocumentLocation().toString());
        LinkfinderWS  dbpClient = service.getLinkfinderWSPort();
        //System.out.println(dbpClient.getClass());
        if (request.getParameter("queryURL") != null) {
            if (request.getParameter("queryURL").equals("")) {
                out.println("<br />Please enter a domain name!");
            } else {
                String result = dbpClient.getLink(request.getParameter("queryURL"));
                if(result != null) {
                    out.println("<br /><a href=\""+result+"\" target=\"_blank\">"+result+"</a>");
                } else {
                    out.println("<br />Either there does not exist a record for the domain you have entered or you have not entered a valid domain.");
                }
            }
        }
        %>

    </body>


</html>
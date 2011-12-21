package uk.ac.open.data.util;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Class FeedItemDBConnection is used to connect to the MySQL Database.
 */
public class DBConnection {

    /**
     * Creates a Connection object.
     * @return Connection
     */
    private static Connection getConnection() {

        Connection con = null;
        try {
//            System.setProperty("proxySet", "true");
//            System.setProperty("proxyHost", "wwwcache.open.ac.uk");
//            System.setProperty("proxyPort", "80");
            
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://" + "localhost" + "/" + "profilecontainer";
            //con = DriverManager.getConnection(url, "root", "Furl0ng_5860");
            con = DriverManager.getConnection(url, "root", "");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
        return con;
    }

    public static String[] getUserContext(String username) {
        ArrayList<String> userContextsList = new ArrayList<String>();

        try {

            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT context FROM usercontexts WHERE username = \"" + username + "\"");
            while (resultSet.next()) {
                userContextsList.add(resultSet.getString(1));
            }

            resultSet.close();
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        String userContexts[] = new String[userContextsList.size()];
        return userContextsList.toArray(userContexts);
    }

    public static boolean userExists(String username)
    {
        boolean result = false;
        int count = 0;
        try {

            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT count(*) FROM users WHERE username = '"+username+"'");
            while (resultSet.next())
            {
                count = resultSet.getInt(1);
            }
            if (count == 1)
            {
                result = true;
            }
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public static void createUser(String username, String password) {
        try {

            Connection con = getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate("INSERT INTO users VALUES('" + username + "', '" + password + "')");
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean authenticateUser(String username, String password) {
        boolean result = false;
        int count = 0;
        try {

            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT count(*) FROM users WHERE username = '"+username+"' AND password = '"+password+"'");
            while (resultSet.next())
            {
                count = resultSet.getInt(1);
            }
            if (count == 1)
            {
                result = true;
            }
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public static void addIgnoredAgent (String username, String IPAddress, String agentId)
    {
        try {

            Connection con = getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate("INSERT INTO ignoredagents VALUES('" + username + "', '" + IPAddress + "', '"+agentId+"')");
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static ArrayList<String> getIgnoredAgents (String username, String IPAddress)
    {
        ArrayList<String> ignoredAgentsList = new ArrayList<String>();
        
        try {
            
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT agentid FROM ignoredagents WHERE username = \"" + username + "\" AND ipaddress = \""+IPAddress+"\"");
            while (resultSet.next()) {
                ignoredAgentsList.add(resultSet.getString(1));
            }

            resultSet.close();
            stmt.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
               
        return ignoredAgentsList;
    }
    
    public static ArrayList<String> getIgnoredIPAddresses (String username)
    {
        ArrayList<String> ignoredAgentsList = new ArrayList<String>();
        
        try {
            
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT distinct ipaddress FROM ignoredagents WHERE username = \"" + username + "\"");
            while (resultSet.next()) {
                ignoredAgentsList.add(resultSet.getString(1));
            }

            resultSet.close();
            stmt.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
               
        return ignoredAgentsList;
    }
            
}
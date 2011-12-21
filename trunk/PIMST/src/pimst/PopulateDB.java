/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pimst;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author se3535
 */
public class PopulateDB {

    private static Object hosts [][];

    public static void main(String [] args){
        populateDB();
    }
    public static String strQuery = new String();
    private static void populateDB() {
        try {
            Connection con = null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                String url = "jdbc:mysql://localhost/pimst";
                con = DriverManager.getConnection(url, "root", "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            Statement stmt = con.createStatement();

            BufferedReader in = new BufferedReader(new FileReader("dataSent-rp.txt"));
            String str;
            String strHost = new String();
            String strAttribute = new String();
            String strValue = new String();


            //Vector<String> strVector = new Vector<String>();
            while ((str = in.readLine()) != null) {
                strHost = str.substring(0, str.indexOf('\u0009'));
                strAttribute = str.substring(str.indexOf('\u0009')+1, str.lastIndexOf('\u0009'));
                strValue = str.substring(str.lastIndexOf('\u0009'));
                if ((!(strValue.contains("\""))) && (!(strValue.length()>1000))) {
                    strQuery = "INSERT into hosts VALUES(\"" + strHost + "\",\"" + strAttribute + "\",\"" + strValue + "\",'Unknown')";
                    stmt.executeUpdate(strQuery);
                }
               

//                if (!(strVector.contains(strHost))) {
//                    strVector.add(strHost);
//                }
            }
            in.close();
            stmt.close();
            con.close();
//            hosts = new Object[strVector.size()][1];
//
//            Iterator vecIt = strVector.iterator();
//            int i = 0;
//            while (vecIt.hasNext()) {
//                hosts[i][0] = (String) vecIt.next();
//                i++;
//            }
        } catch (Exception e) {
            System.out.println(strQuery);
            e.printStackTrace();
        }
    }

}

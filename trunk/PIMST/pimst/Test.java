/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pimst;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author se3535
 */
public class Test {

    public static void main(String[] args) {
        populateHostsTable();
    }
    private static Object hosts [][];
    private static void populateHostsTable() {
        try {
            BufferedReader in = new BufferedReader(new FileReader("C:/dataSent-rp.txt"));
            String str;
            String strHost;
            Vector<String> strVector = new Vector<String>();
            while ((str = in.readLine()) != null) {
                strHost = str.substring(0, str.indexOf('\u0009'));
                if (!(strVector.contains(strHost))) {
                    strVector.add(strHost);
                }
            }
            in.close();
            hosts = new Object[strVector.size()][1];

            Iterator vecIt = strVector.iterator();
            int i = 0;
            while (vecIt.hasNext()) {
                hosts[i][0] = (String) vecIt.next();
                i++;
            }
        } catch (IOException e) {
        }
    }
}

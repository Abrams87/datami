/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.open.kmi.dbpedia.link;


import java.io.File;
import jonelo.jacksum.JacksumAPI;
import jonelo.jacksum.algorithm.AbstractChecksum;

/**
 *
 * @author se3535
 */
public class CheckSumGenerator {
//    public static void main (String args[]) {
//        try {
//            File file = new File("/data/4c256ffe-bcaa-4404-8f48-33035a87ff38");
//            BufferedReader reader = new BufferedReader(new FileReader(file));
//            String in = null;
//            while ((in = reader.readLine())!=null ){
//
//            }
//            System.out.println(getFingerPrint(file));
//            CheckSumGenerator tester = new CheckSumGenerator();
//            tester.moveFile("/data",file, getFingerPrint(file));
//        } catch (Exception ex) {
//            Logger.getLogger(CheckSumGenerator.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    /**
     * Generates a checksum value of the file.
     * @param file
     * @return String
     * @throws Exception
     */
    public static String getFingerPrint(File file) throws Exception {
        AbstractChecksum checksum = null;
        checksum = JacksumAPI.getChecksumInstance("sha1");
        checksum.reset();
        checksum.setEncoding(AbstractChecksum.HEX);
        checksum.readFile(file.getAbsolutePath());
        return checksum.format("urn:sha1:#CHECKSUM");
    }

    /**
     * Moves a file from one location to another location. Also takes
     * care of parent directories if not already existing.
     * @param basePath
     * @param file
     * @param contentFP
     */
    public static void moveFile(String basePath ,File file, String contentFP) {
        try {
            File filePath = new File(basePath, getPath(contentFP));
            if (!filePath.exists()){
                filePath.getParentFile().mkdirs();
                if (file.renameTo(filePath)) {
                    System.out.println("success");
                } else {
                    System.out.println("failure");
                }

            } else {
            	// File already exist, delete it
//            	file.delete();
            }
        }
    	catch (Exception e) {
            e.printStackTrace();
    	}
    }

    /**
     * Extracts a directory hierarchy path from the checksum hash
     * value of a file.
     * @param contentURI
     * @return String
     */
    public static String getPath(String contentURI) {
        //urn:sha1:fc75445068463f261e4da336a8b1c99d75e84ff0
        //01234567890123456789012345678901234567890123456789
        //---------^-^--^---^----^---------^
        //9,11,14,18,23,33
        return contentURI.substring(9, 10)
        + "/"
                + contentURI.substring(10, 13)
                + "/"
                + contentURI.substring(13, 17)
                + "/"
                + contentURI.substring(17, 22)
                + "/"
                + contentURI.substring(22, 32)
                + "/"
                + contentURI.substring(32);
    }
}

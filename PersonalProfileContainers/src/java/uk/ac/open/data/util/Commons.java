package uk.ac.open.data.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class Commons {

    public static String header(String titlePart){
	String toPrint = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
	toPrint += "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n";
	toPrint += "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n";
	toPrint += "<head>\n";
	toPrint += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n";
    
	toPrint += "<title>Cupboard - "+titlePart+"</title>\n";	
//        toPrint += "<link rel=\"stylesheet\" media=\"screen\" href=\""+getResourceURL()+"/js/style.css\" />";
//        toPrint += "<script type=\"text/javascript\" language=\"javascript\" src=\""+getResourceURL()+"/js/rating.js\"></script>";
//        toPrint += "<script type=\"text/javascript\" language=\"javascript\" src=\""+getResourceURL()+"/js/ajaxScripts.js\"></script>";    
        toPrint +="<script type=\"text/javascript\"> \n";
        toPrint +="function checkBoxClick(){\n";
        toPrint +="if (document.form.userSettingAgreement.checked==false) \n "
                + "{ \n "
                + "document.form.registerBtn.disabled=true; "
                + "\n } \n "
                + "else \n {"
                + "\n document.form.registerBtn.disabled=false; "
                + "\n} \n";
        toPrint +="}\n";
        toPrint +="</script>\n";
	toPrint += "</head><body>\n";   
	toPrint += "<div id=\"main\" >\n";
	return toPrint;
    }

    public static String footer(){
    //String toPrint = "</div>\n"; //wsmain div
    String    toPrint = "</div>\n"; //wsmain div
    toPrint += "<div id=\"footer\">";
//    toPrint += "<a href=\""+Commons.getSearchURL()+"\">Search</a> | \n";
//    toPrint += "<a href=\""+Commons.getBaseURL()+"/about\">About</a> | \n";
//    toPrint += "<a href=\""+Commons.getBaseURL()+"/contactus\">Contact Us</a>\n";
    
    toPrint += "</div>\n";
//    toPrint += "</div>\n"; //wsmain div
    toPrint += "</body></html>\n";    
	return toPrint;
    }


//     public static String footer(){
//	String toPrint = "</div>\n";
//	toPrint += "</body></html>\n";
//	return toPrint;
//    }
    private static String getEnv(String var){
	Map map = System.getenv();
	String result = (String) map.get(var);
	return result;
    }

    public static String getTmpDir(){
	String res = getEnv("WS_TMP_DIR");
	if (res==null) 
	    res = "/data/cb_tmp";
	return res;
    }

    public static String getBaseURL(){
	String res = getEnv("WS_BASE_URL");
	return res;
    }
    
    public static String getBaseURL(String url){
    	if (url.startsWith("http://kmi-web06")) return "http://kmi-web06.open.ac.uk:8081/cupboard";
    	String res = getEnv("WS_BASE_URL");
    	return res;
        }

    public static String getSearchURL(){
	String res = getEnv("WS_SEARCH_URL");
	return res;
    }

    public static String getResourceURL(){
	String res = getEnv("WS_RES_URL");
	return res;
    }

    public static String getOSNameFronURL(String URL){
    	String ss1 = URL.substring((getBaseURL(URL)+"/ontology/").length());
    	ss1 = ss1.substring(0, ss1.indexOf("/"));
    	return ss1;
    }
    
    public static String getOntoNameFronURL(String URL){
    	String ss1 = URL.substring(URL.lastIndexOf("/")+1);
    	return ss1;
    }
    
    public static void assignProperties()
    {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("uciadweb.conf"));
            for (Object key : properties.keySet().toArray()) {
                System.setProperty((String) key, properties.getProperty((String) key));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}

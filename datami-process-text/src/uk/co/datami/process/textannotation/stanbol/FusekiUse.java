package uk.co.datami.process.textannotation.stanbol;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;

public class FusekiUse {
	
	String fusekiUploadURI=""; //"http://localhost:3030/datami/upload"
	
	public FusekiUse(String fusekiUploadURI){
		this.fusekiUploadURI=fusekiUploadURI;
	}
		
	public void saveFile(String fileName){
		
	    HttpClient httpclient = new DefaultHttpClient();
	    httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

	    HttpPost httppost = new HttpPost(fusekiUploadURI); // the url of the fuseki upload endpoint 
	    File file = new File(fileName);
	    
	    MultipartEntity mpEntity = new MultipartEntity();
	    //MultipartEntity mpEntity  = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
	    
	    ContentBody cbFile = new FileBody(file, "application/rdf+xml", "UTF-8");
	    mpEntity.addPart("filename.rdf", cbFile); 
	    
	    httppost.setEntity(mpEntity);
	    System.out.println("executing request " + httppost.getRequestLine());
	    try {
		HttpResponse hresponse = httpclient.execute(httppost);
		HttpEntity resEntity = hresponse.getEntity();
		System.out.println(hresponse.getStatusLine());
		if (resEntity != null) {
		    System.out.println(EntityUtils.toString(resEntity));
		}
		if (resEntity != null) {
		    resEntity.consumeContent();//.consumeContent();
		}
		httpclient.getConnectionManager().shutdown();
		
	    } catch (ClientProtocolException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }	    	    	     

	}		
	
}

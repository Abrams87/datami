package uk.ac.open.data.servlets;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

public class RedirectRoutes {

	private static RedirectRoutes instance = new RedirectRoutes();
	
	private RedirectRoutes(){};
	
	public static RedirectRoutes getInstance(){
		return instance;
	}

	public boolean shouldRedirect(String uri) {
		if (uri.startsWith("http://data.open.ac.uk/course")){
			String url = accessibleCourseURL(uri);
			if (url != null) return true;
		}
		return false;
	}

	private String accessibleCourseURL(String uri) {
		String pg_pref = "http://www3.open.ac.uk/study/postgraduate/course/";
		String ug_pref = "http://www3.open.ac.uk/study/undergraduate/course/";
		String code = uri.substring(new String("http://data.open.ac.uk/course/").length());
		String url = pg_pref+code+".htm";
		if (testURL(url)) return url;
		url = ug_pref+code+".htm";
		if (testURL(url)) return url;
		return null;
	}

	private boolean testURL(String url) {
		URL urlurl;
		try {
			urlurl = new URL(url);
			int code = ((HttpURLConnection) urlurl.openConnection()).getResponseCode();
			if (code == 200) return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void redirect(HttpServletResponse res, String uri) {
		if (uri.startsWith("http://data.open.ac.uk/course")){
			String url = accessibleCourseURL(uri);
			if (url!=null){
				res.setContentType("text/plain");
				res.setStatus(res.SC_MOVED_PERMANENTLY);
				res.setHeader("Location", url);
			}
		}
	}
	
	// for tests
	public static void main (String[] args){
		System.out.println(RedirectRoutes.getInstance().shouldRedirect("http://data.open.ac.uk/course/aa100"));
	}

	public void redirectToRDF(HttpServletResponse res, String uri) {
		String url = uri.replace("ac.uk/", "ac.uk/resource/");
		res.setContentType("text/plain");
		res.setStatus(res.SC_MOVED_PERMANENTLY);
		res.setHeader("Location", url);
	}
	
	
}

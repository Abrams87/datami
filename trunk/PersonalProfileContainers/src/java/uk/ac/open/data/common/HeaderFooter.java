package uk.ac.open.data.common;

public class HeaderFooter {

	public static String getHeader(String title){
		String result = 
		"<!DOCTYPE html>"
		+ "<html lang=\"en\">\n"
		+"<head>\n"
		+"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n"
		+"<title>"+title+" - The Open University</title>\n"
		+"<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.open.ac.uk/includes/ouice/screen.css\" media=\"screen, projection\" />\n"
		+"<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.open.ac.uk/includes/ouice/print.css\" media=\"print\" />\n"

		+"<!-- insert OU Header CSS -->\n"
		+"<!-- interim version here: http://www.open.ac.uk/webstandards/v2/inc/org-header.css -->\n"
		+"<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.open.ac.uk/includes/headers-footers/header-public-centre-gradient.css\" />\n"

		+"<!--[if lt IE 9]>\n"
		+"<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.open.ac.uk/includes/ouice/ie.css\" />\n"
		+"<![endif]-->\n"

		+"<!--[if lt IE 7]>\n"
		+"<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.open.ac.uk/includes/ouice/ie6.css\" />\n"
		+"<![endif]-->\n"

		+"<!-- OU_ICE v2 | gdc53 2010.07.04 -->\n"

		+"</head>\n"

		+"<body>\n"

		+"<div id=\"org\">\n"

		+"<!-- insert OU Header -->\n"
		+"<!-- interim version here: http://www.open.ac.uk/webstandards/v2/inc/org-header.php -->\n"

		+"<div id=\"site\">\n"
		+"<div id=\"site-header\">\n"

		+"<div id=\"org-header\">\n"
		+"<a id=\"ou-skip\" href=\"#ou-content\">Skip to content</a>\n"

		+"<div class=\"ou-hswrap\">\n"
		+"<div id=\"hsheader\">\n"
		+"<a href=\"http://www.open.ac.uk/\" id=\"hslogo\"><img alt=\"The Open University\" src=\"http://www.open.ac.uk/includes/headers-footers/oulogo-56.jpg\" width=\"83\" height=\"56\" /></a>\n"
		+"<div id=\"sbhstools\">\n"
		+"<ul>\n"
		+"<li id=\"ou-accessibility\"><a href=\"http://www.open.ac.uk/accessibility/\">Accessibility</a></li>\n"
		+"<li class=\"wrapper\">\n"
		+"<ul>\n"
		+"<li id=\"ou-person\" class=\"hide\"></li>\n"
		+"<li id=\"ou-signin1\"><a href=\"https://msds.open.ac.uk/signon/sams001.aspx\" id=\"ou-signin2\">Sign in</a></li>\n"
		+"<li id=\"ou-signout\" class=\"hide\"><a href=\"https://msds.open.ac.uk/signon/samsoff.aspx\">&nbsp;/ Sign out</a></li>\n"
		+"<li id=\"ou-studenthome\" class=\"hide\"><a href=\"http://www.open.ac.uk/students/\" id=\"ou-studenthome2\">StudentHome</a></li>\n"
		+"<li id=\"ou-tutorhome\" class=\"hide\"><a href=\"http://www.open.ac.uk/tutorhome/\">TutorHome</a></li>\n"

		+"<li id=\"ou-intranet\" class=\"hide\"><a href=\"http://intranet.open.ac.uk/\">IntranetHome</a></li>\n"
		+"<li id=\"ou-sponsor\" class=\"hide\"><a href=\"http://www.open.ac.uk/employers/\">SponsorHome</a></li>\n"
		+"</ul>\n"
		+"</li>\n"
		+"<li id=\"ou-contact\"><a href=\"http://www3.open.ac.uk/contact/\">Contact</a></li>\n"
		+"<li id=\"ou-search\"><a href=\"http://www.open.ac.uk/search\">Search the OU</a></li>\n"
		+"</ul>\n"
		+"</div>\n"
		+"<ul id=\"sbhsnavigation\">\n"
		+"<li class=\"first\"><a href=\"http://www.open.ac.uk/\">The Open University</a></li>\n"
		+"<li><a href=\"http://www.open.ac.uk/study/\">Study at the OU</a></li>\n"

		+"<li><a href=\"http://www.open.ac.uk/research/\">Research at the OU</a></li>\n"
		+"<li class=\"current\"><a href=\"http://www.open.ac.uk/community/\">OU Community</a></li>\n"
		+"<li><a href=\"http://www.open.ac.uk/about/\">About the OU</a></li>\n"
		+"</ul>\n"
		+"</div>\n"
		+"</div>\n"
		+"<div class=\"ou-clear\"></div>\n"
		+"</div>\n"

		+"</div>\n"

		+"<div id=\"site-body\">\n"

		+"<div id=\"page\">\n"

		+"<div id=\"region0\">\n"
		+"</div>\n"

		+"<div id=\"region1\">\n";

		return result;
	}
	
	public static String getFooter(){
		String result =
			"</div>\n"

		+"<!-- TODO: something on the URI Scheme -->\n"

		+"<div id=\"region2\">\n"
		
		+"</div>\n"

		+"</div>\n"

		+"<div id=\"org-footer\">\n"
		+"<div class=\"ou-clear\"></div>\n"
		+"<div class=\"ou-hswrap\">\n"
		+"<div id=\"hsdeepfooter\">\n"
		+"<div class=\"hsfooterGrid\">\n"
		+"<div class=\"hsfooterRegion1\">\n"
		+"<div class=\"sbc1of4\">\n"
		+"<h3>The Open University</h3>\n"
		+"<p>&copy; Copyright <span id=\"sbyear\"></span>. All rights reserved.</p>\n"
		+"<h4>+44 (0) 845 300 60 90<br />\n"

		+"<a href=\"http://www.open.ac.uk/email/\">Email us</a></h4>\n"
		+"</div>\n"
		+"<div class=\"sbc2of4\">\n"
		+"<ul>\n"
		+"<li><a href=\"http://www.open.ac.uk/privacy\">Website privacy</a></li>\n"
		+"<li><a href=\"http://www.open.ac.uk/copyright\">Copyright</a></li>\n"
		+"<li><a href=\"http://www.open.ac.uk/conditions\">Conditions of use</a></li>\n"
		+"<li><a href=\"http://www.open.ac.uk/cymraeg\">Cymraeg</a></li>\n"
		+"</ul>\n"
		+"</div>\n"
		+"<div class=\"sbc3of4\">\n"
		+"<ul>\n"

		+"<li><a href=\"http://www.open.ac.uk/platform\">Platform</a></li>\n"
		+"<li><a href=\"http://www.open.ac.uk/openlearn\">Openlearn</a></li>\n"
		+"<li><a href=\"http://www.open.ac.uk/facebook\">Facebook</a></li>\n"
		+"<li><a href=\"http://www.open.ac.uk/twitter\">Twitter</a></li>\n"
		+"<li><a href=\"http://www.open.ac.uk/youtube\">YouTube</a></li>\n"
		+"</ul>\n"
		+"</div>\n"
		+"<div class=\"clear\"></div>\n"
		+"</div>\n"
		+"</div>\n"
		+"</div>\n"
		+"</div></div>   \n"


		+"<script type=\"text/javascript\" src=\"/includes/headers-footers/header.js\"></script>\n"

		+"<!-- Insert OU Footer -->\n"
		+"<!-- interim version here: http://www.open.ac.uk/webstandards/v2/inc/org-footer.php -->\n"

		+"<!--\n"
		+"gdc53\n"
		+"2010.06.17 15:57\n"
		+"Unofficial org-footer pending IA work.\n"
		+"-->\n"


		+"</div>\n"

		+"<!-- [ if jQuery not alread in use -->\n"
		+"<script type=\"text/javascript\" src=\"/ouice/library.js\"></script>\n"
		+"<!-- ] -->\n"

		+"<script type=\"text/javascript\" src=\"/ouice/scripts.js\"></script>\n"

		+"<!-- Insert OU Header JS -->\n"
		+"<!-- interim version here: http://www.open.ac.uk/webstandards/v2/inc/org-header.js -->\n"

//		+"<!-- Piwik -->\n"
//		+"<script type=\"text/javascript\">\n"
//		+"var pkBaseURL = ((\"https:\" == document.location.protocol) ? \"https://lucero-project.info/piwik/\" : \"http://lucero-project.info/piwik/\");\n"
//		+"document.write(unescape(\"%3Cscript src='\" + pkBaseURL + \"piwik.js' type='text/javascript'%3E%3C/script%3E\"));\n"
//		+"</script><script type=\"text/javascript\">\n"
//		+"try {\n"
//		+"var piwikTracker = Piwik.getTracker(pkBaseURL + \"piwik.php\", 2);\n"
//		+"piwikTracker.trackPageView();\n"
//		+"piwikTracker.enableLinkTracking();\n"
//		+"} catch( err ) {}\n"
//		+"</script><noscript><p><img src=\"http://lucero-project.info/piwik/piwik.php?idsite=2\" style=\"border:0\" alt=\"\" /></p></noscript>\n"
//		+"<!-- End Piwik Tag -->\n"

		+"</body>\n"
		+"</html>\n";

		return result;
	}
	
	
}

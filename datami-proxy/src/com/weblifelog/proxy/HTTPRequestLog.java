package com.weblifelog.proxy;

import java.util.HashMap;

public class HTTPRequestLog {
    
    String url;
    String method;
    String queryString;
    String content;
    String time;

    HashMap<String, String> header = new HashMap<String,String>();

    HTTPResponseLog response = new HTTPResponseLog();
    
}

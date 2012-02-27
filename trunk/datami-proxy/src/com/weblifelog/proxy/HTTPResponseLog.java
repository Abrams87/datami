package com.weblifelog.proxy;

import java.util.HashMap;

public class HTTPResponseLog {

    String content;
    String time;
    int status;

    HashMap<String, String> header = new HashMap<String,String>();
}

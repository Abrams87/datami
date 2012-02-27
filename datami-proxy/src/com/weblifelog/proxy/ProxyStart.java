package com.weblifelog.proxy;

import java.util.Timer;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;

import org.eclipse.jetty.server.Server;
import com.weblifelog.process.SyncTask;

// TODO: 
//  have a proper logging thing for messages

public class ProxyStart {
	
	public static void main (String [] args){
	    Properties configFile = new Properties();
	    int syncTime = 1800;
	    try {
		configFile.load(new FileInputStream(new File("wllproxy.properties")));
		syncTime = Integer.parseInt(configFile.getProperty("SYNC_TIME"));
	    } catch(Exception e){
		e.printStackTrace();
	    }
	    Timer timer = new Timer(); 
	    timer.schedule(new SyncTask(), 0, syncTime*1000);

	    int port = 80;
	    if (args.length == 1) {
		try {
		    port = Integer.parseInt(args[0]);
		}catch(Exception e){
		    System.out.println("The first agrument (port) should be a number");
		    System.exit(0);
		}
	    }
	    Server server = new Server(port);
	    server.setHandler(new ProxyHandler());
	    try {
		System.out.println(" WLL:: Starting on port "+port);
		System.out.println(" WLL:: Will synchronise every "+syncTime+" seconds");		
		server.start();
		server.join();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	
	
}

package com.weblifelog.process;

import java.util.TimerTask;

public class SyncTask extends TimerTask {

    public SyncTask(){

    }

    public void run(){
	Sync app = new Sync();
	app.doSync();
    }

}

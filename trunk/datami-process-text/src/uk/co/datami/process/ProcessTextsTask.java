package uk.co.datami.process;

import java.util.TimerTask;

public class ProcessTextsTask extends TimerTask {

    public ProcessTextsTask(){

    }

    public void run(){
	ProcessTextsFromProxy app = new ProcessTextsFromProxy();
	app.doSync();
    }

}

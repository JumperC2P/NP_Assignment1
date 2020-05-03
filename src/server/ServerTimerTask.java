package server;

import java.io.PrintWriter;
import java.util.TimerTask;

/**
 * @author Chih-Hsuan Lee <s3714761>
 *
 */
public class ServerTimerTask extends TimerTask{
	
	private String message;
	private PrintWriter printWriter;
	
	public ServerTimerTask(PrintWriter printWriter, String message) {
		this.message = message;
		this.printWriter = printWriter;
	}
	
	public void run() {
		
		try {
			printWriter.println(message);
		} catch (Exception e) {}
		
	}

}

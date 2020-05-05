package server;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.TimerTask;

/**
 * @author Chih-Hsuan Lee <s3714761>
 *
 */
public class ServerTimerTask extends TimerTask{
	
	private String message;
	private PrintWriter printWriter;
	private Long startTime;
	private Socket connection;
	private final int LIMIT_DURATION = 60*5;
	
	public ServerTimerTask(Socket connection, PrintWriter printWriter, String message) {
		this.message = message;
		this.printWriter = printWriter;
		this.startTime = System.currentTimeMillis();
		this.connection = connection;
	}
	
	public void run() {
		
		Long endTime = System.currentTimeMillis();
		int duration = (int) ((endTime - startTime)/1000);
		System.out.println("duration: " + duration);
		
		try {
			if (duration >= LIMIT_DURATION) {
				printWriter.println("As you have not responsed for 5 minutes, the connection is closed.");
				connection.close();
			}else if (duration < LIMIT_DURATION && duration >= (LIMIT_DURATION-30)){
				printWriter.println("The connection will be closed after " + (LIMIT_DURATION-duration) + " seconds.");
			}else {
				printWriter.println(message);
			}
		} catch (Exception e) {}
		
	}

}

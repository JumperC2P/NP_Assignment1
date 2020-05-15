package server;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.TimerTask;

/**
 * ServerTimerTask is used to send keep-alive message to client, 
 * and also close the connection if it waits too long.
 * @author Chih-Hsuan Lee <s3714761>
 *
 */
public class ServerTimerTask extends TimerTask{
	
	private Boolean demo = false;
	private String message;
	private PrintWriter printWriter;
	private Long startTime;
	private Socket connection;
	private int LIMIT_DURATION = demo?30:60*5;
	
	public ServerTimerTask(Socket connection, PrintWriter printWriter, String message, Boolean demo) {
		this.message = message;
		this.printWriter = printWriter;
		this.startTime = System.currentTimeMillis();
		this.connection = connection;
		this.demo = demo;
		LIMIT_DURATION = demo?30:60*5;
	}
	
	/**
	 * send keep-alive message to client
	 */
	public void run() {
		
		Long endTime = System.currentTimeMillis();
		int duration = (int) ((endTime - startTime)/1000);
		System.out.println("duration: " + duration);
		
		try {
			// if the server is waiting too long, close the connection
			if (duration >= LIMIT_DURATION) {
				printWriter.println("As you have not responsed for 5 minutes, the connection is closed.");
				connection.close();
			// send notification about closing connection every 30 seconds 
			}else if (duration < LIMIT_DURATION && duration >= (LIMIT_DURATION-(demo?15:60))){
				printWriter.println("The connection will be closed after " + (LIMIT_DURATION-duration) + " seconds.");
			// otherwise, just send a normal keep-alive message
			}else {
				printWriter.println(message);
			}
		} catch (Exception e) {}
		
	}

}

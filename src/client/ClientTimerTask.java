package client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.TimerTask;

/**
 * ClientTimerTask is used to receive and to print the message from server.
 * @author Chih-Hsuan Lee <s3714761>
 *
 */
public class ClientTimerTask extends TimerTask {

	private Scanner scanner;
	private Socket connection;
	
	public ClientTimerTask(Socket connection) throws IOException {
		this.scanner = new Scanner(connection.getInputStream());
		this.connection = connection;
	}
	
	/**
	 * catch keep-alive message from server
	 */
	public void run() {
		try {
			
			if (connection.isClosed()) {
				System.out.println("The server closed the connection");
				System.exit(-1);
			}else {
				System.out.println(scanner.nextLine());
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}

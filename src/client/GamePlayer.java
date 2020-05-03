package client;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Timer;

/**
 * @author Chih-Hsuan Lee <s3714761>
 *
 */
public class GamePlayer {
	
	// declare for the host information
	private final static String ADDRESS = "localhost";
    private final static int PORT = 61618;

    private Socket connection;

    private Scanner scanner, input;
    private PrintWriter printWriter;
    private long delay = 1000*5;
	private long period = 1000*5;
    
    public GamePlayer() {
    	
    	try {
			// generate a new socket object with the address and port.
            connection = new Socket(ADDRESS, PORT);

			// use the socket connection to get InputStream and OutputStream
			// and convert to different object to operate them.
            printWriter = new PrintWriter(connection.getOutputStream(), true);
            scanner = new Scanner(connection.getInputStream());
            
			String serverMessage = null;
			// print welcome message from server.
			printMessageFromServer();
			
//			// give player name
//			printMessageFromServer();
//			input = new Scanner(System.in);
//			printWriter.println(input.nextLine()); 
//			
//			// print waiting message from server.
//			printMessageFromServer();
			
			// use while loop to listen feedbacks from server.
            while (true) {
            	
            	try {
            		
            		serverMessage = printMessageFromServer();
					
					// check whether the message matches the end condition or not.
                	if (serverMessage.startsWith("You are right") || serverMessage.startsWith("Wrong numbers")) {
                		
                		printMessageFromServer();
                		printMessageFromServer();
                		input = new Scanner(System.in);
                		String option = input.nextLine();
    					printWriter.println(option); 
    					
    					printMessageFromServer();
    					
    					if ("q".equals(option))
    						break;
    					else
    						continue;
                	}
                	
                	ClientTimerTask receiver = new ClientTimerTask(new Scanner(connection.getInputStream()));
                	Timer timer = new Timer("Receiver");
                	timer.scheduleAtFixedRate(receiver, delay, period);
					
					// get input from console and use PrintWriter to send the input to server.
                	input = new Scanner(System.in);
					printWriter.println(input.nextLine()); 
					
					timer.cancel();
					
				// If the server is shut down, end the while loop.
            	}catch (NoSuchElementException nsee) {
            		System.out.println("The server is out of service. No more games.");
            		break;
            	}
            }
            

        } catch (IOException e) {
            e.printStackTrace();
        }
    	
    }

	private String printMessageFromServer() {
		// read the message from server
		String serverMessage = scanner.nextLine();  
		
//		// print the message from server.
		System.out.println(serverMessage);
		return serverMessage;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new GamePlayer();

	}

}

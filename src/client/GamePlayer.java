package client;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Timer;

/**
 * GamePlayer is the main program for client.
 * @author Chih-Hsuan Lee <s3714761>
 *
 */
public class GamePlayer {
	
	// declare for the host information
	private static Boolean demo = false;
	private final static String ADDRESS = "localhost";
    private final static int PORT = 61618;

    private Socket connection;

    private Scanner scanner, input;
    private PrintWriter printWriter;
    private long delay = demo?1000*5:1000*30;
	private long period = demo?1000*5:1000*30;
    
    public GamePlayer() {
    	
    	delay = demo?1000*5:1000*30;
    	period = demo?1000*5:1000*30;
    	
    	try {
			// generate a new socket object with the address and port.
            connection = new Socket(ADDRESS, PORT);

			// use the socket connection to get InputStream and OutputStream
			// and convert to different object to operate them.
            printWriter = new PrintWriter(connection.getOutputStream(), true);
            scanner = new Scanner(connection.getInputStream());
            
            printMessageFromServer();
            
			String serverMessage = null;
			
			// use while loop to listen feedbacks from server.
            while (true) {
            	
            	try {
            		
            		// get the message from server and print it.
            		serverMessage = printMessageFromServer();
					
					// check whether the message matches the end condition or not.
                	if (serverMessage.startsWith("You are right") || serverMessage.startsWith("Wrong numbers")) {
                		
                		printMessageFromServer();
                		printMessageFromServer();
                		// create a timer and timertask to receive keep-alive message from server
                    	ClientTimerTask receiver = new ClientTimerTask(connection);
                    	Timer timer = new Timer("ClientTimer");
                    	timer.scheduleAtFixedRate(receiver, delay, period);
                		input = new Scanner(System.in);
                		String option = input.nextLine();
    					printWriter.println(option); 
    					timer.cancel();
    					
    					printMessageFromServer();
    					
    					// if player enter a q, and the program, otherwise continue the while loop.
    					if ("q".equals(option.toLowerCase()))
    						break;
    					else
    						continue;
    				// if the message starts with "Thank you", it's time to end the program.
                	} else if (serverMessage.startsWith("Thank you")) {
                		printMessageFromServer();
                		break;
                	// if the message starts with "Hi,", it just a infromation, no need to enter anything at this time.
                	} else if (serverMessage.startsWith("Hi,")) {
                		continue;
                	}
                	
                	// create a timer and timertask to receive keep-alive message from server
                	ClientTimerTask receiver = new ClientTimerTask(connection);
                	Timer timer = new Timer("ClientTimer");
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
            System.out.println(e.getMessage());
        }
    }

    /**
     * print the message from server
     * @return
     */
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
		
		try {
			demo = "demo".equals(args[0].toLowerCase());
			System.out.println("Game Player is started in Demo mode.");
		}catch (Exception e){}
		
		new GamePlayer();
	}
}

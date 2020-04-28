package client;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * @author Chih-Hsuan Lee <s3714761>
 *
 */
public class GamePlayer {
	
	// declare for the host information
	private final static String ADDRESS = "netprog1.csit.rmit.edu.au";
    private final static int PORT = 61618;

    private Socket connection;

    private Scanner scanner, input;
    private PrintWriter printWriter;
    
    public GamePlayer() {
    	
    	try {
			// generate a new socket object with the address and port.
            connection = new Socket(ADDRESS, PORT);

			// use the socket connection to get InputStream and OutputStream
			// and convert to different object to operate them.
            printWriter = new PrintWriter(connection.getOutputStream(), true);
            scanner = new Scanner(connection.getInputStream());
            
			String serverMessage = null;
			
			// use while loop to listen feedbacks from server.
            while (true) {
            	
            	try {
					// read the message from server
            		serverMessage = scanner.nextLine();  
					
					// check whether or not the message is not empty.
					// if the message is empty, continue the while loop.
                	if (serverMessage == null || serverMessage.trim().length() == 0) {
                		continue;
                	}
					
					// print the message from server.
					System.out.println(serverMessage);
					
					// check whether the message matches the end condition or not.
                	if (serverMessage.startsWith("C") || serverMessage.startsWith("Sorry")) {
                		break;
                	}
					
					// get input from console and use PrintWriter to send the input to server.
                	input = new Scanner(System.in);
					printWriter.println(input.nextLine()); 
					
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new GamePlayer();

	}

}

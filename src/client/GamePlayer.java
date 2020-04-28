package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author Chih-Hsuan Lee <s3714761>
 *
 */
public class GamePlayer {
	
	private final static String ADDRESS = "localhost";
    private final static int PORT = 61618;

    private Socket connection;

    private Scanner scanner, input;
    private PrintWriter printWriter;
    
    public GamePlayer() {
    	
    	try {
            connection = new Socket(ADDRESS, PORT);

            printWriter = new PrintWriter(connection.getOutputStream(), true);
            scanner = new Scanner(connection.getInputStream());
            
            String serverMessage = null;
            while (true) {
            	
            	serverMessage = scanner.nextLine();  
            	
            	if (serverMessage == null || serverMessage.trim().length() == 0) {
            		continue;
            	}
            	
            	System.out.println(serverMessage);
            	if (serverMessage.startsWith("C") || serverMessage.startsWith("Sorry")) {
            		break;
            	}
            	
            	input = new Scanner(System.in);
            	printWriter.println(input.nextLine());  
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

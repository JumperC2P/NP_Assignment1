package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;

/**
 * @author Chih-Hsuan Lee <s3714761>
 *
 */
public class GameServer {
	
	// declare some default parameters
	private final static int PORT = 61618;
	
	private final Integer MAX_CHANCES = 4;
	
	private Socket connection;

	private Scanner connInput;
    private PrintWriter printWriter;
    
    private String playerName;
    
    private Integer randomNumber = null;

	public GameServer() {
		
		ServerSocket server = null;

        try {
			// start the server
            server = new ServerSocket(PORT);
            System.out.println("Game server is started.");
			
			// use while loop to wait for player joining.
            while(true) {
            	
            	System.out.println("Waiting for new player...");
				
				// accept a connection from client
                connection = server.accept();
                System.out.println("A new player has joined the game.");
				
				// use the socket connection to get InputStream and OutputStream
				// and convert to different object to operate them.
                printWriter = new PrintWriter(connection.getOutputStream(),true);
                connInput = new Scanner(connection.getInputStream());
                
                try {
					// get player's name if it's a new player.
                	if (playerName == null) {
                    	setPlayerName();
                    }
					
					// setup random number
                    if (randomNumber == null) {
                    	setRandomNumber();
                    }
                    
                    printWriter.println("Let's start the game. Please guess a number between 0 and 12: ");
                    
                    String str = null;
					Integer guessChance = 1;
					Timer timer = new Timer("Sender");
					// use while loop to communicate with client continuously.
                    while (true) {
                    	try {
                    		ServerTimer sender = new ServerTimer(printWriter, "Please guess a number between 0 and 12: ");
                    		timer = new Timer("Sender");
                    		timer.scheduleAtFixedRate(sender, 1000*5, 1000*5);
                    		
							// get the message from client
							str = connInput.nextLine(); 
							
							timer.cancel();
							
							// check whether the input is a number or not.
                            Integer guessingNumber = null;
                            try {
								guessingNumber = Integer.valueOf(str);
								
							// if it is not a number, return the message to client.
                            }catch (NumberFormatException nfe) {
                            	printWriter.println("The input you entered is not a \"NUMBER\". Please give us a number: ");
                            	continue;
                            }
							
							// check whether the input number is in the range.
							// if not, return the message to client.
                            if (guessingNumber < 0 || guessingNumber > 12) {
                            	printWriter.println("The number you entered is out of range. Please give us a number between 0 to 12: ");
                            	continue;
                            }
							
							// check whether the client is correct.
							// if yes, return the message and end the while loop.
                            if (guessingNumber == randomNumber) {
                            	printWriter.println("Congratulations. You are right. The number is " + randomNumber + ".");
                            	reset();
                            	break;
                            }
							
							// check whether the client has other chances to guess.
							// if not, return the message and end the while loop.
                            if ((MAX_CHANCES-guessChance) == 0) {
                            	printWriter.println("Sorry. The number you guess is " + guessingNumber + ", which is wrong. The target number is " + randomNumber + ". Thanks for joining the game.");
                            	reset();
                            	break;
                            }
							
							// returning different messages depends on different situations.
                            if (guessingNumber < randomNumber)
                            	printWriter.println("The number you guess is " + guessingNumber + ", which is smaller than the generated number. Please guess again (chances left: " + (MAX_CHANCES-guessChance) + "): ");
                            else
                            	printWriter.println("The number you guess is " + guessingNumber + ", which is bigger than the generated number. Please guess again (chances left: " + (MAX_CHANCES-guessChance) + "): ");
							
							guessChance ++;

						// if the client quits the game during game, reset all the parameters
						// and wait for another player joining the game.
                    	}catch (NoSuchElementException nsee) {
                    		reset();
                    		break;
                    	}
                        
                    }
                }catch (NoSuchElementException ne) {
                	System.out.println("Client quits the game.");
                	reset();
                	continue;
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                server.close();
            } catch (IOException e) { }
        }
		
	}

	/**
	 * reset the parameters to default values.
	 */
	private void reset() {
		
		playerName = null;
		randomNumber = null;
	}

	/**
	 * set random number to guess.
	 */
	private void setRandomNumber() {
		randomNumber = new Random().nextInt(12);
		System.out.println("The random number is " + randomNumber + ".");
	}

	/**
	 * get player name from client
	 */
	private void setPlayerName() {
		
		printWriter.println("Welcome to the guessing game. Please give us your name: ");
		Timer timer = new Timer("Sender");
		while(true) {
			ServerTimer sender = new ServerTimer(printWriter, "Please give us your name: ");
    		timer = new Timer("Sender");
    		timer.scheduleAtFixedRate(sender, 1000*60*3, 1000*60*3);
			String playerInput = connInput.nextLine();
			timer.cancel();
			if (playerInput == null || playerInput.trim().length() == 0) {
				printWriter.println("The name you gave is unvalid. Please try again: ");
			}else {
				playerName = playerInput;
				break;
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new GameServer();

	}

}

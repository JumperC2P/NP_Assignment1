package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * @author Chih-Hsuan Lee <s3714761>
 *
 */
public class PlayerHandler extends Thread {

	private String playerName;
	private Integer MAX_CHANCES;
	private Scanner connInput;
	private PrintWriter printWriter;
	private GameServer server;
	private Socket connection;

	public PlayerHandler(Socket connection, GameServer server, String playerName) {
		
		this.playerName = playerName;
		this.connection = connection;
		this.server = server;
		MAX_CHANCES = server.getMaxChance();
		try {
			connInput = new Scanner(connection.getInputStream());
			printWriter = new PrintWriter(connection.getOutputStream(), true);
			if (playerName == null)
				sendMessage("Please wait for other players. The Game will start in 3 minutes no matter how many players join in 3 minutes.");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void sendMessage(String message) {
		printWriter.println(message);
	}
	
	@Override
	public void run() {
		
		if (playerName == null)
			setPlayerName();

		Integer randomNumber = server.getRandomNumber();
		sendMessage("Let's start the game. Please guess a number between 0 and 12: ");
		
		
		try {
            String str = null;
			Integer guessChance = 1;
			// use while loop to communicate with client continuously.
            while (true) {
            	try {
					// get the message from client
					str = connInput.nextLine();  
					
					// check whether the input is a number or not.
                    Integer guessingNumber = null;
                    try {
						guessingNumber = Integer.valueOf(str);
						
					// if it is not a number, return the message to client.
                    }catch (NumberFormatException nfe) {
                    	sendMessage("The input you entered is not a \"NUMBER\". Please give us a number: ");
                    	continue;
                    }
					
					// check whether the input number is in the range.
					// if not, return the message to client.
                    if (guessingNumber < 0 || guessingNumber > 12) {
                    	sendMessage("The number you entered is out of range. Please give us a number between 0 to 12: ");
                    	continue;
                    }
					
					// check whether the client is correct.
					// if yes, return the message and end the while loop.
                    if (guessingNumber == randomNumber) {
                    	sendMessage("You are right. The number is " + randomNumber + ". Please wait for result.");
                    	server.setResultMap(this, (MAX_CHANCES-guessChance));
                    	break;
                    }
					
					// check whether the client has other chances to guess.
					// if not, return the message and end the while loop.
                    if ((MAX_CHANCES-guessChance) == 0) {
                    	sendMessage("Wrong numbers. You are out of chances. Please wait for result.");
                    	server.setResultMap(this, 0);
                    	break;
                    }
					
					// returning different messages depends on different situations.
                    if (guessingNumber < randomNumber)
                    	sendMessage("The number you guess is " + guessingNumber + ", which is smaller than the generated number. Please guess again (chances left: " + (MAX_CHANCES-guessChance) + "): ");
                    else
                    	sendMessage("The number you guess is " + guessingNumber + ", which is bigger than the generated number. Please guess again (chances left: " + (MAX_CHANCES-guessChance) + "): ");
					
					guessChance ++;

				// if the client quits the game during game
				// return the handler object and minus 1 which means client quits the game.    
            	}catch (NoSuchElementException nsee) {
            		System.out.println(playerName + " quits the game.");
            		server.setResultMap(this, -1);
            		break;
            	}
                
            }
            System.out.println(playerName + " end the game.");
        // if the client quits the game during game
        // return the handler object and minus 1 which means client quits the game.    
        }catch (NoSuchElementException ne) {
        	server.setResultMap(this, -1);
        	System.out.println(playerName + " quits the game.");
        }
//		System.out.println("End the Thread.");
	}

	/**
	 * get player name from client
	 */
	private void setPlayerName() {

		printWriter.println("Welcome to the guessing game. Please give us your name: ");

		while (true) {

			String playerInput = connInput.nextLine();
			if (playerInput == null || playerInput.trim().length() == 0) {
				printWriter.println("The name you gave is unvalid. Please try again: ");
			} else {
				playerName = playerInput;
				break;
			}
		}
	}
	
	public Socket getConnection() {
		return connection;
	}
	
	public String getPlayerName() {
		return playerName;
	}

}

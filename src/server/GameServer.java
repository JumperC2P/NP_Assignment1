package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Chih-Hsuan Lee <s3714761>
 *
 */
public class GameServer {
	
	private final static int PORT = 61618;
	
	private final Integer MAX_CHANCES = 4;
	
	private Socket connection;

	private Scanner connInput;
    private PrintWriter printWriter;
    
    private String playerName;
    
    private Integer randomNumber = null;

	public GameServer() {
		
		ServerSocket server = null;

        try
        {
            server = new ServerSocket(PORT);
            System.out.println("Game server is started.");
            
            while(true) {
            	
            	System.out.println("Waiting for new player...");
                
                connection = server.accept();
                System.out.println("A new player has joined the game.");
                
                printWriter = new PrintWriter(connection.getOutputStream(),true);
                connInput = new Scanner(connection.getInputStream());
                
                if (playerName == null) {
                	setPlayerName();
                }
                
                if (randomNumber == null) {
                	setRandomNumber();
                }
                
                printWriter.println("Let's start the game. Please guess a number between 0 and 12: ");
                
                String str = null;
                Integer guessChance = 1;
                while (true) {
                	try {
                		str = connInput.nextLine();  
                        Integer guessingNumber = null;
                        try {
                        	guessingNumber = Integer.valueOf(str);
                        }catch (NumberFormatException nfe) {
                        	printWriter.println("The input you entered is not a \"NUMBER\". Please give us a number: ");
                        	continue;
                        }
                        
                        if (guessingNumber < 0 || guessingNumber > 12) {
                        	printWriter.println("The number you entered is out of range. Please give us a number between 0 to 12: ");
                        	continue;
                        }
                        
                        if (guessingNumber == randomNumber) {
                        	printWriter.println("Congratulations. You are right. The number is " + randomNumber + ".");
                        	reset();
                        	break;
                        }
                        
                        if ((MAX_CHANCES-guessChance) == 0) {
                        	printWriter.println("Sorry. The number you guess is " + guessingNumber + ", which is wrong. The target number is " + randomNumber + ". Thanks for joining the game.");
                        	reset();
                        	break;
                        }
                        
                        if (guessingNumber < randomNumber)
                        	printWriter.println("The number you guess is " + guessingNumber + ", which is smaller than the generated number. Please guess again (chances left: " + (MAX_CHANCES-guessChance) + "): ");
                        else
                        	printWriter.println("The number you guess is " + guessingNumber + ", which is bigger than the generated number. Please guess again (chances left: " + (MAX_CHANCES-guessChance) + "): ");
                        guessChance ++;
                	}catch (NoSuchElementException nsee) {
                		reset();
                		break;
                	}
                    
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

	private void reset() {
		
		playerName = null;
		randomNumber = null;
	}

	private void setRandomNumber() {
		randomNumber = new Random().nextInt(12);
		System.out.println("The random number is " + randomNumber + ".");
	}

	private void setPlayerName() {
		
		printWriter.println("Welcome to the guessing game. Please give us your name: ");
		
		while(true) {
			
			String playerInput = connInput.nextLine();
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

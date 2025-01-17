package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import tools.GameLogger;

/**
 * The PlayerHandler will handle the main game process.
 * @author Chih-Hsuan Lee <s3714761>
 *
 */
public class PlayerHandler extends Thread {
	
	private Boolean demo = false;
	private long delay = demo?1000*5:1000*30;
	private long period = demo?1000*5:1000*30;
	private Boolean isGame = false;
	private String playerName;
	private Integer MAX_CHANCES;
	private Scanner connInput;
	private PrintWriter printWriter;
	private GameServer server;
	private Socket connection;
	private final Logger LOGGER = GameLogger.getGameLogger();

	public PlayerHandler(Socket connection, GameServer server, String playerName, Boolean demo) throws IOException {
		
		this.playerName = playerName;
		this.connection = connection;
		this.server = server;
		MAX_CHANCES = server.getMaxChance();
		this.demo = demo;
		delay = demo?1000*5:1000*30;
		period = demo?1000*5:1000*30;
		try {
			connInput = new Scanner(connection.getInputStream());
			printWriter = new PrintWriter(connection.getOutputStream(), true);
			// if the player name is exist already, it means that the player has played at least one round.
			if (playerName == null)
				sendMessage("Please wait for other players. The game will start in 3 minutes no matter how many players join in 3 minutes.");
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw e;
		}

	}

	/**
	 * Send message to client
	 * @param message message to send
	 */
	public void sendMessage(String message) {
		printWriter.println(message);
	}
	
	/**
	 * Set the flag for playing the game
	 */
	public void setToGameTime() {
		this.isGame = true;
	}
	
	@Override
	public void run() {

		// if the flag is true, it means it's time to play.
		if (isGame) {
			playGame();
		// if it is false, the player needs to give us his or her name.
		}else {
			if (this.playerName == null) {
				setPlayerName();
				setToGameTime();
			}
		}
	}

	/**
	 * The playing game method
	 */
	private void playGame() {
		// create a new Timer object for keep-alive message
		Timer timer = null;
		try {
			
			// check whether or not the player has provided his or her name.
			if (this.playerName == null) {
				setPlayerName();
			}
			
			// get the number to guess from server
			Integer randomNumber = server.getRandomNumber();
			sendMessage("Let's start the game. If you want to quit the game, enter \"e\". Now, please guess a number between 0 and 12 (chances left: " + (MAX_CHANCES) + "): " );
			
			
            String str = null;
			Integer guessChance = 1;
			// use while loop to communicate with client continuously.
            while (true) {
            	try {
            		// create a new TimerTask and schedule it
            		// once receiving the response from client, the timer will be stoped.
            		ServerTimerTask sender = new ServerTimerTask(connection, printWriter, "Please guess a number between 0 and 12: ", demo);
            		timer = new Timer("ServerTimer");
            		timer.scheduleAtFixedRate(sender, delay, period);
            		
					// get the message from client
					str = connInput.nextLine();  
					
					timer.cancel();
					
					LOGGER.log(Level.INFO, playerName + " enters: " + str);
					// check whether the input is a number or not.
                    Integer guessingNumber = null;
                    try {
						guessingNumber = Integer.valueOf(str);
					// if it is not a number, check whether the input is "e".
                    }catch (NumberFormatException nfe) {
                    	
                    	if ("e".equals(str)) {
                    		LOGGER.log(Level.INFO, playerName + " quits during the game.");
                    		sendMessage("Thank you for joining the game.");
                    		server.setResultMap(this, -1);
                    		break;
                    	}else {
                    		sendMessage("The input you entered is not a \"NUMBER\". Please give us a number: ");
                    		continue;
                    	}
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
                    	LOGGER.log(Level.INFO, playerName + " guesses the right number with " + (MAX_CHANCES-guessChance) + " chances remaining.");
                    	sendMessage("You are right. The number is " + randomNumber + ". Please wait for result.");
                    	server.setResultMap(this, (MAX_CHANCES-guessChance));
                    	break;
                    }
					
					// check whether the client has other chances to guess.
					// if not, return the message and end the while loop.
                    if ((MAX_CHANCES-guessChance) == 0) {
                    	LOGGER.log(Level.INFO, playerName + " is out of chance and waiting for result.");
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
            		timer.cancel();
            		System.out.println((playerName==null?"Someone":playerName) + " quits the game.");
            		LOGGER.log(Level.INFO, (playerName==null?"Someone":playerName) + " quits the game.");
            		server.setResultMap(this, -1);
            		break;
            	}
                
            }
            LOGGER.log(Level.INFO, (playerName==null?"Someone":playerName) + " end the game.");
            System.out.println((playerName==null?"Someone":playerName) + " end the game.");
        // if the client quits the game during game
        // return the handler object and minus 1 which means client quits the game.    
        }catch (NoSuchElementException ne) {
        	timer.cancel();
        	server.setResultMap(this, -1);
        	LOGGER.log(Level.INFO, (playerName==null?"Someone":playerName) + " quits the game.");
        	System.out.println((playerName==null?"Someone":playerName) + " quits the game.");
        }
		
	}

	/**
	 * get player name from client
	 */
	private void setPlayerName() {
		Timer timer = null;
		try {
			printWriter.println("Welcome to the guessing game. Please give us your name: ");

			// the while loop is used to ask for player name
			while (true) {
				timer = new Timer("ServerTimer");
				ServerTimerTask sender = new ServerTimerTask(connection, printWriter, "Please give us your name: ", demo);
				timer.scheduleAtFixedRate(sender, delay, period);
				String playerInput = connInput.nextLine();
				timer.cancel();
				if (playerInput == null || playerInput.trim().length() == 0) {
					printWriter.println("The name you gave is unvalid. Please try again: ");
				} else {
					playerName = playerInput;
					LOGGER.log(Level.INFO, "One of players is " + playerName);
					sendMessage("Hi, "+ this.playerName+ ". Please wait until telling you to start the game.");
					break;
				}
			}
		}catch (Exception e) {
			timer.cancel();
			LOGGER.log(Level.INFO, "Player quits the game before giving name.");
			throw e;
		}
	}
	
	/**
	 * Return the connection of the playerhandler
	 * @return
	 */
	public Socket getConnection() {
		return connection;
	}
	
	/**
	 * Return the player name recorded in the playerhandler
	 * @return
	 */
	public String getPlayerName() {
		return playerName;
	}

}

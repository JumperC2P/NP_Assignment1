package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import tools.CalculateResult;
import tools.GameLogger;

/**
 * It is the main program of server.
 * @author Chih-Hsuan Lee <s3714761>
 *
 */
public class GameServer {
	
	// declare some default parameters
	private static Boolean demo = false;
	private final static int PORT = 61618;
	private static final Integer MAX_CHANCES = 4;
	private Socket connection;
    private static Integer randomNumber = null;
    private static Map<PlayerHandler, Integer> resultMap = new HashMap<>();
    static List<PlayerHandler> connectedPlayers = new LinkedList<>();
    static List<PlayerHandler> waitingPlayers = new LinkedList<>();
    static List<PlayerHandler> players = new LinkedList<>();
    private int requeueTimes = 0;
    private int SERVER_WAITING_TIME = demo?1000*10:1000*60*3;
    private static final Logger LOGGER = GameLogger.getGameLogger();

    
    public GameServer() {
    	SERVER_WAITING_TIME = demo?1000*10:1000*60*3;
		ServerSocket server = null;

        try {
        	
			while (true) {
				// start the server
				server = new ServerSocket(PORT);
				server.setSoTimeout(SERVER_WAITING_TIME);
	            System.out.println("Waiting for players for "+(demo?"10 seconds...":"3 minutes..."));
	            LOGGER.log(Level.INFO, "Waiting for players for "+(demo?"10 seconds...":"3 minutes..."));
				
				// use while loop to wait for player joining.
	            while(true) {
	            	try {
	            		// accept a connection from client
	            		connection = server.accept();

	            		connectedPlayers.add(new PlayerHandler(connection, this, null, demo));
	            		connectedPlayers.get(connectedPlayers.size()-1).start();
	            		
	            		if (connectedPlayers.size() >= 6) {
	            			LOGGER.log(Level.INFO, "The lobby is full. Prepare to play.");
		            		System.out.println("The lobby is full. Prepare to play.");
	            		}
	            		
	            	}catch (SocketTimeoutException ste) {
	            		LOGGER.log(Level.INFO, "Time's out. Prepare to play.");
	            		System.out.println("Time's out. Prepare to play.");
	            		break;
	            	}
	            }
	            
	            for (PlayerHandler connectedPlayer : connectedPlayers) {
	            	connectedPlayer.join();
	            	System.out.println(connectedPlayer.getPlayerName() + " is joined the game from " + connectedPlayer.getConnection().getRemoteSocketAddress().toString() + ".");
            		LOGGER.log(Level.INFO, connectedPlayer.getPlayerName() + " is joined the game from " + connectedPlayer.getConnection().getRemoteSocketAddress().toString() + ".");
	            
            		// add the player to waiting line
            		PlayerHandler player = new PlayerHandler(connectedPlayer.getConnection(), this, connectedPlayer.getPlayerName(), demo);
            		player.setToGameTime();
            		waitingPlayers.add(player);
	            }
	            
	            // remove old thread object to save memory
	            connectedPlayers.removeAll(connectedPlayers);
	            
	            // play the guessing game until no player in queue.
	            while (true) {
	            	
	            	// reset all the settings for new game
	            	reset();

	                this.decidePlayers();
	                
	                // Check whether or not there are players in the waiting list.
	                // If there is no player, end the program.
	                if (players.size() == 0) {
	                	LOGGER.log(Level.INFO, "No player in lobby. Game over.");
	                	break;
	                }
	                
	                // generate a random number to guess.
	                this.setRandomNumber();
	                
	                System.out.println(players.size() + " players are ready to start.");
	                LOGGER.log(Level.INFO, players.size() + " players are ready to start.");
	                
	                // start the game for players
	                players.forEach((player)-> {
	                	try {
	            			player.start();
	                	}catch (IllegalThreadStateException e) {
	                		System.out.println("Thread has already started.");
	                	}
	                });
	                System.out.println(players.size() + " players have started");
	                LOGGER.log(Level.INFO, players.size() + " players have started");

	                // stop the while loop until all players finish their guessing.
	                while(true) {
	                	TimeUnit.SECONDS.sleep(2);
	                	if (resultMap.size() == players.size())
	                		break;
	                }
	                
	                LOGGER.log(Level.INFO, players.size() + " players have finished their guessing.");
	                // send message to each players.
	                sendResultToClient();
	                
	                if (waitingPlayers.size() == 0)
	                	break;
	                
	            }
	            server.close();
			}
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            try{
                server.close();
            } catch (IOException e) { }
        }
		
	}
    
    /**
     * reset to initial state to restart a game.
     */
    private void reset() {
    	players.removeAll(players);
    	resultMap.clear();
    	requeueTimes = 0;
	}

    /**
     * Add the first three waiting players in the queue to the player list.
     */
	private void decidePlayers() {
    	
		// if there are more than 3 waiting players
		// take first 3 waiting players to the player list
    	if (waitingPlayers.size() > 3) {
        	for (int i = 0; i < 3; i++) {
        		players.add(waitingPlayers.get(0));
        		waitingPlayers.remove(0);
        	}
        	
        // if less than 3 waiting players, add all of them to the player list.
        }else {
        	players.addAll(waitingPlayers);
        	waitingPlayers.removeAll(waitingPlayers);
        }
    	
    	// Logging
    	StringBuffer sb = new StringBuffer();
    	for (PlayerHandler player : players) {
    		String name = player.getPlayerName();
    		sb.append(name==null?"Unknown":name).append(" ");
    	}
    	LOGGER.log(Level.INFO, "These players start the game: " + sb.toString());
    	
    	if (waitingPlayers.size() != 0) {
    		StringBuffer sb1 = new StringBuffer();
    		for (PlayerHandler player : waitingPlayers) {
    			String name = player.getPlayerName();
    			sb1.append(name==null?"Unknown":name).append(" ");
    		}
    		LOGGER.log(Level.INFO, "These players wait for another game: " + sb.toString());
    	}else {
    		LOGGER.log(Level.INFO, "No player is the waiting line.");
    	}
	}

	/**
	 * Requeue players to the waiting queue
	 * @param player : player thread
	 * @param isAgain : whether or not the player wants to play again
	 * @throws IOException 
	 */
	public synchronized void requeue(PlayerHandler player, Boolean isAgain) throws IOException {
		
		LOGGER.log(Level.INFO, player.getPlayerName() + (isAgain?" wants to play again. Requeue the player.":" ends the game."));
		
		// if the player wants to play again, re-add to the lobby with new thread.
		if (isAgain) {
			PlayerHandler renewPlayer = new PlayerHandler(player.getConnection(), this, player.getPlayerName(), demo);
			renewPlayer.setToGameTime();
			waitingPlayers.add(renewPlayer);
		}
		
		requeueTimes++;
    }
    
	/**
	 * send results to each players. In the method, it will calculate the rank based on the resultMap and decides the messages for each players.
	 * @throws InterruptedException
	 */
    private void sendResultToClient() throws InterruptedException {
    	if (resultMap.size() != 0) {
    		// Calculate the ranking
    		Map<PlayerHandler, String> messages = CalculateResult.calculation(resultMap, randomNumber);
    		
    		// Send the message to each player
    		LOGGER.log(Level.INFO, "Game Result: ");
    		List<PlayerResultHandler> playerResults = new LinkedList<>();
    		messages.forEach((k, v)-> {
    			LOGGER.log(Level.INFO, k.getPlayerName() + ": " + v);
    			playerResults.add(new PlayerResultHandler(k, this, v, demo));
    			playerResults.get(playerResults.size()-1).start();
    		});
    		
    		// Check whether all the players has relied to play again or not.
    		for (PlayerResultHandler player: playerResults)
    			player.join();
    	}else {
    		LOGGER.log(Level.INFO, "No result needs to be calculated.");
    	}
	}

    /**
     * use to store result
     * @param player  player thread
     * @param remainingChance  remaining chance of the player
     */
	public void setResultMap(PlayerHandler player, Integer remainingChance) {
    	resultMap.put(player, remainingChance);
    }

	/**
	 * set random number to guess.
	 */
	private void setRandomNumber() {
		randomNumber = new Random().nextInt(12);
		System.out.println("The random number is " + randomNumber + ".");
	}

	/**
	 * return the number which is guessed.
	 * @return
	 */
	public int getRandomNumber() {
		return randomNumber;
	}
	
	/**
	 * return the number of maximum chances to guess.
	 * @return
	 */
	public int getMaxChance() {
		return MAX_CHANCES;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			demo = "demo".equals(args[0].toLowerCase());
			System.out.println("Game server is started in Demo mode.");
        	LOGGER.log(Level.INFO, "Game server is started in Demo mode.");
		}catch (Exception e){
			System.out.println("Game server is started.");
			LOGGER.log(Level.INFO, "Game server is started.");
		}
		
		new GameServer();

	}

}

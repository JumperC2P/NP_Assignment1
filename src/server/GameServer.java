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

import tools.CalculateResult;

/**
 * @author Chih-Hsuan Lee <s3714761>
 *
 */
public class GameServer {
	
	// declare some default parameters
	private final static int PORT = 61618;
	private static final Integer MAX_CHANCES = 4;
	private Socket connection;
    private static Integer randomNumber = null;
    private static Map<PlayerHandler, Integer> resultMap = new HashMap<>();
    static List<PlayerHandler> waitingPlayers = new LinkedList<>();
    static List<PlayerHandler> players = new LinkedList<>();
    private int requeueTimes = 0;
    private final int SERVER_WAITING_TIME = 1000*60*3;
    
    public GameServer() {
		
		ServerSocket server = null;

        try {
        	server = new ServerSocket(PORT);
        	server.setSoTimeout(SERVER_WAITING_TIME);
        	System.out.println("Game server is started.");
			while (true) {
				// start the server
	            System.out.println("Waiting for players for 3 minutes...");
				
				// use while loop to wait for player joining.
	            while(true) {
	            	try {
	            		// accept a connection from client
	            		connection = server.accept();
	            		waitingPlayers.add(new PlayerHandler(connection, this, null));
	            		System.out.println("A new player has joined the game.");
	            	}catch (SocketTimeoutException ste) {
	            		System.out.println("Time's out. Prepare to play.");
	            		break;
	            	}
	            }
	            
	            // play the guessing game until no player in queue.
	            while (true) {
	            	
	            	reset();

	                this.decidePlayers();
	                
	                // generate a random number to guess.
	                this.setRandomNumber();
	                
	                System.out.println(players.size() + " players are ready to start.");
	                players.forEach((player)-> {
	                	try {
	            			player.start();
	                	}catch (IllegalThreadStateException e) {
	                		System.out.println("Thread has already started.");
	                	}
	                });
	                System.out.println(players.size() + " players start");

	                // stop the while loop until all players finish their guessing.
	                while(true) {
	                	TimeUnit.SECONDS.sleep(2);
	                	if (resultMap.size() == players.size())
	                		break;
	                }
	                // send message to each players.
	                sendResultToClient();
	                
//	                stopPlayersThread();
	                
	                if (waitingPlayers.size() == 0)
	                	break;
	                
	            }
			}
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try{
                server.close();
            } catch (IOException e) { }
        }
		
	}
    
    private void reset() {
    	players.removeAll(players);
    	resultMap.clear();
    	requeueTimes = 0;
	}

//    private void stopPlayersThread() {
//    	players.forEach((p)->p.interrupt());
//		
//	}

	private void decidePlayers() {
    	
    	if (waitingPlayers.size() > 3) {
        	for (int i = 0; i < 3; i++) {
        		players.add(waitingPlayers.get(0));
        		waitingPlayers.remove(0);
        	}
        }else {
        	players.addAll(waitingPlayers);
        	waitingPlayers.removeAll(waitingPlayers);
        }
//    	players.forEach((p)->System.out.print(p.getPlayerName()+" "));
	}

	public synchronized void requeue(PlayerHandler player, Boolean isAgain) {
		if (isAgain)
			waitingPlayers.add(new PlayerHandler(player.getConnection(), this, player.getPlayerName()+"-again"));
		
//		waitingPlayers.forEach((p)->System.out.print(p.getPlayerName()+" "));
		requeueTimes++;
    }
    
    private void sendResultToClient() throws InterruptedException {
		Map<PlayerHandler, String> messages = CalculateResult.calculation(resultMap, randomNumber);
		messages.forEach((k, v)-> {
			new PlayerResultHandler(k, this, v).start();
		});
		
		while(true) {
			TimeUnit.SECONDS.sleep(2);
			if (requeueTimes == messages.size())
				break;
		}
	}

	public void setResultMap(PlayerHandler player, Integer remainingChance) {
    	resultMap.put(player, remainingChance);
//    	System.out.println("Result Map is added.");
    }

	/**
	 * set random number to guess.
	 */
	private void setRandomNumber() {
		randomNumber = new Random().nextInt(12);
		System.out.println("The random number is " + randomNumber + ".");
	}

	public int getRandomNumber() {
		return randomNumber;
	}
	
	public int getMaxChance() {
		return MAX_CHANCES;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new GameServer();

	}

}

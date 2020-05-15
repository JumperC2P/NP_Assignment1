package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import tools.GameLogger;

/**
 * The handler is going to handle the response from client whether or not he or she wants to play again.
 * @author Chih-Hsuan Lee <s3714761>
 *
 */
public class PlayerResultHandler extends Thread {
	
	private Boolean demo = false;
	private long delay = demo?1000*5:1000*30;
	private long period = demo?1000*5:1000*30;
	private Scanner connInput;
	private PrintWriter printWriter;
	private GameServer server;
	private Socket connection;
	private PlayerHandler player;
	private String resultMessage;
	private final Logger LOGGER = GameLogger.getGameLogger();
	
	public PlayerResultHandler(PlayerHandler player, GameServer server, String resultMessage, Boolean demo) {
		this.player = player;
		this.connection = player.getConnection();
		this.server = server;
		this.resultMessage = resultMessage;
		this.demo = demo;
		delay = demo?1000*5:1000*30;
		period = demo?1000*5:1000*30;
		try {
			connInput = new Scanner(connection.getInputStream());
			printWriter = new PrintWriter(connection.getOutputStream(), true);

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	/**
	 * ask client whether to play again.
	 */
	@Override
	public void run() {
		sendMessage(resultMessage);
		sendMessage("Please choose to play again (p), or quit (q): ");
		
		Timer timer = null;
		try {
			String str = null;
			while (true) {
				timer = new Timer("ServerTimer");
				ServerTimerTask sender = new ServerTimerTask(connection, printWriter, "Please enter only \"p\" for playing again or \"q\" to quit", demo);
				timer.scheduleAtFixedRate(sender, delay, period);
				str = connInput.nextLine();  
				timer.cancel();
				if (str != null && str.trim().length() > 1) {
					sendMessage("Please enter only \"p\" for playing again or \"q\" to quit");
					continue;
				}
				if ("q".equals(str)) {
					LOGGER.info(player.getPlayerName() + " leaves the game.");
					sendMessage("Thank you for joining the game.");
					server.requeue(player, false);
					break;
				}else if ("p".equals(str)) {
					LOGGER.info(player.getPlayerName() + " will play again. Re-add the player to the lobby.");
					server.requeue(player, true);
					sendMessage("Re-add you to the player lobby. Please wait.");
					break;
				}else {
					sendMessage("Unaccepted input. Please enter only \"p\" for playing again or \"q\" to quit");
					continue;
				}
			}
		}catch (Exception e) {
			timer.cancel();
			LOGGER.log(Level.INFO, (player.getPlayerName()==null?"Someone":player.getPlayerName()) + " quits the game.");
		}
		
		
	}

	/**
	 * send message to client
	 * 
	 * @param message
	 */
	public void sendMessage(String message) {
		printWriter.println(message);
	}
}

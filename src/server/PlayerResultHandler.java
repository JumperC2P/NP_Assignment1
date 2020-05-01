package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class PlayerResultHandler extends Thread {
	
	private Scanner connInput;
	private PrintWriter printWriter;
	private GameServer server;
	private Socket connection;
	private PlayerHandler player;
	private String resultMessage;
	
	public PlayerResultHandler(PlayerHandler player, GameServer server, String resultMessage) {
		this.player = player;
		this.connection = player.getConnection();
		this.server = server;
		this.resultMessage = resultMessage;
		try {
			connInput = new Scanner(connection.getInputStream());
			printWriter = new PrintWriter(connection.getOutputStream(), true);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		sendMessage(resultMessage);
		sendMessage("Please choose to play again (p), or quit (q): ");
		String str = null;
		while (true) {
			str = connInput.nextLine();  
			if (str != null && str.trim().length() > 1) {
				sendMessage("Please enter only \"p\" for playing again or \"q\" to quit");
				continue;
			}
			if ("q".equals(str)) {
				sendMessage("Thank you for joining the game.");
				server.requeue(player, false);
				break;
			}else if ("p".equals(str)) {
				server.requeue(player, true);
				sendMessage("Re-add you to the player lobby. Please wait.");
				break;
			}else {
				sendMessage("Unaccepted input. Please enter only \"p\" for playing again or \"q\" to quit");
				continue;
			}
		}
	}

	public void sendMessage(String message) {
		printWriter.println(message);
	}
}

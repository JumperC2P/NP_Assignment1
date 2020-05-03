package client;

import java.util.Scanner;
import java.util.TimerTask;

/**
 * @author Chih-Hsuan Lee <s3714761>
 *
 */
public class ClientTimerTask extends TimerTask {

	private Scanner scanner;
	
	public ClientTimerTask(Scanner scanner) {
		this.scanner = scanner;
	}
	
	public void run() {
		try {
			System.out.println(scanner.nextLine());
		}catch(Exception e) {}
	}
}

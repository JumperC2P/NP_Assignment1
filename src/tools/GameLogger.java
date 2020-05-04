package tools;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author Chih-Hsuan Lee <s3714761>
 *
 */
public class GameLogger {
private static Logger logger;
	
	
	public static Logger getGameLogger() {
		
		if (logger == null) {
			logger = Logger.getLogger("GuessingGame");  
			FileHandler fh;  
			logger.setLevel(Level.INFO);
			
			try {  
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Calendar cal = Calendar.getInstance();
				String format = formatter.format(cal.getTime());
				
				// This block configure the logger with handler and formatter  
				fh = new FileHandler(System.getProperty("user.dir")+"/../log/"+format+".log");  
				logger.addHandler(fh);
				SimpleFormatter formatterFH = new SimpleFormatter();  
				fh.setFormatter(formatterFH);  
				logger.setUseParentHandlers(false);
				
			} catch (SecurityException e) {  
				e.printStackTrace();  
			} catch (IOException e) {  
				e.printStackTrace();  
			}  
		}
		
		return logger;
	}

}

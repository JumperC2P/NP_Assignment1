package tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import server.GameServer;
import server.PlayerHandler;

/**
 * @author Chih-Hsuan Lee <s3714761>
 *
 */
public class CalculateResult {
	
	private final Logger LOGGER = GameLogger.getGameLogger();
	
	public static Map<PlayerHandler, String> calculation(Map<PlayerHandler, Integer> resultMap, Integer targetNumber){

		String winningMessage = "Congratulations. You are the winner. The number is " + targetNumber + ".";
		String runnerupMessage = "Congratulations. You are right, but unfortunately, other people use less chances to guess the right number than you. The number is " + targetNumber + ". You are runner-up.";
		String loserMessage = "Sorry. You are out of chances. You lose the game. The number is " + targetNumber + ".";
		
		Map<PlayerHandler, String> messageMap = new HashMap<PlayerHandler, String>();
		resultMap = orderMapByValue(resultMap);
		
		Integer bestRecord = null;
		for (PlayerHandler player : resultMap.keySet()) {
			Integer remainingTimes = resultMap.get(player);
			
			// if remainingTimes is less than 0, it means the client quit the game.
			if (remainingTimes < 0) {
				continue;
			}
			
			if (bestRecord == null) {
				bestRecord = remainingTimes;
			}
			if (remainingTimes == 0) {
				messageMap.put(player, loserMessage);
			}else if (bestRecord == remainingTimes) {
				messageMap.put(player, winningMessage);
			}else if (bestRecord > remainingTimes) {
				messageMap.put(player, runnerupMessage);
			}
		}
		
		return messageMap;
	}
	
	private static Map<PlayerHandler, Integer> orderMapByValue(Map<PlayerHandler, Integer> resultMap){
		if (resultMap == null || resultMap.isEmpty()) {
			return null;
		}
		Map<PlayerHandler, Integer> sortedMap = new LinkedHashMap<>();
		List<Map.Entry<PlayerHandler, Integer>> entryList = new ArrayList<>(resultMap.entrySet());
		Collections.sort(entryList, new Comparator<Map.Entry<PlayerHandler, Integer>>(){
			@Override
			public int compare(Entry<PlayerHandler, Integer> o1, Entry<PlayerHandler, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}

		});
 
		Iterator<Entry<PlayerHandler, Integer>> iter = entryList.iterator();
		Map.Entry<PlayerHandler, Integer> tmpEntry = null;
		while (iter.hasNext()) {
			tmpEntry = iter.next();
			sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
		}
		
		return sortedMap;
	}

	
//	public static void main(String[] args) {
//		Map<PlayerHandler, Integer> map = new HashMap<PlayerHandler, Integer>();
//		map.put(new PlayerHandler("John"), 1);
//		map.put(new PlayerHandler("Alan"), 2);
//		map.put(new PlayerHandler("Hank"), 3);
//		map.put(new PlayerHandler("Tim"), 0);
//		
//		map = orderMapByValue(map);
//		
//		map.forEach((k,v)-> System.out.println("Name: " + k.name +", num: " + v));
//		
//		System.out.println("--------------------------------");
//		
//		Map<PlayerHandler, String> resultMap = new HashMap<>();
//		resultMap = calculation(map, 5);
//		
//		resultMap.forEach((k,v)-> System.out.println("Name: " + k.name +", Message: " + v));
//		
//	}
//	
//	static class PlayerHandler{
//		public String name;
//		public PlayerHandler(String name) {
//			this.name = name;
//		}
//	}
}

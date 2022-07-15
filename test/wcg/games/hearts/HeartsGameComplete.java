package wcg.games.hearts;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import wcg.games.GameBot;
import wcg.shared.CardGameException;

/**
 * Make a complete hearts game using only the bots from this game .
 */
public class HeartsGameComplete {
	
	private static final int REPETITIONS = 100;
	
	/**
	 * Make a lot of games in sequence using only bots.
	 * Get the type of the strategy that wins each game and count
	 * the number of wins. In the end the number of wins of the simple
	 * strategy should be less that the regular strategy.
	 * 
	 * @throws InterruptedException
	 * @throws CardGameException
	 */
	@Test
	void testCompleteGame() throws InterruptedException, CardGameException {
		Map<String,Integer> wins = new HashMap<>();

		for(int count = 0 ; count < REPETITIONS; count++) {
			HeartsGameMaster master = new HeartsGameMaster();

			ArrayList<GameBot> bots = new ArrayList<>();
			for(int p=0; p < master.getNumberOfPlayers() ; p++) {
				GameBot bot = new GameBot(master);

				master.addPlayer(bot);
				bots.add(bot);
			}

			for(GameBot bot: bots)
				bot.join();

			String winnerNick = bots.get(0).getWinner();
			GameBot winner = getWinnerBot(bots, winnerNick);
			String type = winner.getStrategy().getClass().getName();
			Integer prev = wins.get(type);
			
			wins.put(type, prev == null ? 1 : prev +1);
		}
		
		assertTrue( 
			wins.getOrDefault(HeartsGameStrategy.class.getName(),0) > 
			wins.getOrDefault(HeartsGameSimpleStrategy.class.getName(),0),
				"Winner bot should have the more complex strategy");

	}

	private GameBot getWinnerBot(ArrayList<GameBot> bots, String winnerNick) {
		GameBot winner = bots.stream()
				.filter(b -> b.getNick().equals(winnerNick) )
				.findFirst()
				.get();
		return winner;
	}
	
	
	
}

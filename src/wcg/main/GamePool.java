package wcg.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import wcg.games.GameFactory;
import wcg.games.GameMaster;
import wcg.shared.CardGameException;
import wcg.shared.GameInfo;

/**
 * Pool of games managed by their ID. Expired games are periodically removed
 * using a timer. Java timers receive a tasks to execute using the Template
 * method design pattern.
 */
public class GamePool {

	private GameFactory factory = new GameFactory();

	private Map<String, GameMaster> currentGames = new HashMap<>();

	private static final int TIMER_DELAY = 60 * 1000; // 1 minute
	private Timer timer = new Timer();
	private TimerTask task = new TimerTask() {
		public void run() {
			removeExpiredGames();
		}
	};

	/**
	 * Create an instance with a timer to schedule the remotion of expired games.
	 */
	GamePool() {
		timer.scheduleAtFixedRate(task, TIMER_DELAY, TIMER_DELAY);
	}

	/**
	 * Returns the list of available game names, as described on GameFactory
	 * 
	 * @return list of game names
	 */
	List<String> getGameNames() {
		return factory.getAvailableGames();
	}

	/**
	 * Create a game master for the game with given name
	 * 
	 * @param name - of game
	 * @return id of game
	 * @throws CardGameException - if game is unknown
	 */
	String createGame(String name) throws CardGameException {
		if (!getGameNames().contains(name.toUpperCase()))
			throw new CardGameException("The given game name is invalid");

		GameMaster newGame = factory.makeGameMaster(name);
		String newGameId = newGame.getGameId();

		currentGames.put(newGameId, newGame);

		return newGameId;
	}

	/**
	 * The game master with given id
	 * 
	 * @param gameId - of intended game
	 * @return game
	 * @throws CardGameException - if id is invalid
	 */
	GameMaster getGameMaster(String gameId) throws CardGameException {
		GameMaster gameMaster = currentGames.get(gameId);
		if (gameMaster == null)
			throw new CardGameException("The given Game ID is invalid");

		return gameMaster;
	}

	/**
	 * Produces a list with information on games still available to human players
	 * 
	 * @return list of games
	 */
	List<GameInfo> getAvailableGameInfos() {
		List<GameInfo> gameInfos = new ArrayList<>();

		for (GameMaster gameMaster : currentGames.values())
			gameInfos.add(gameMaster.getInfo());

		return gameInfos;
	}

	/**
	 * Remove all games in this pool that have already expired. This method is
	 * regularly called by a timer.
	 */
	void removeExpiredGames() {
		for (Iterator<Map.Entry<String, GameMaster>> iterator = currentGames.entrySet().iterator(); iterator
				.hasNext();) {
			Map.Entry<String, GameMaster> entry = iterator.next();
			if (entry.getValue().expired())
				iterator.remove();
		}
	}

	/**
	 * Resets GamePool's fields to enable unit testing on the Manager singleton.
	 * This method must only be used during testing and shoudn't be public.tt
	 */
	void reset() {
		factory = new GameFactory();
		currentGames = new HashMap<>();
	}
}
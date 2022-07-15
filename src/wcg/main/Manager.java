package wcg.main;

import java.util.List;

import wcg.games.GameBot;
import wcg.games.GameMaster;
import wcg.shared.CardGameException;
import wcg.shared.GameInfo;
import wcg.shared.cards.Card;
import wcg.shared.events.GameEvent;

/**
 * <p>
 * An instance of this class is responsible for managing users and game
 * instances.
 * </p>
 * 
 * <p>
 * The methods of this class are those needed by web client thus it follows the
 * Facade design pattern. It also follows the Singleton design pattern to
 * provide a single instance of this class to the application.
 * </p>
 */
public class Manager {

	private static Manager manager;
	private static GamePool gamePool = new GamePool();
	private static UserPool userPool;

	private Manager() {
		try {
			userPool = UserPool.getInstance();
		} catch (CardGameException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Single instance of this class.
	 * 
	 * @return singleton
	 * @throws CardGameException - if an error occurs in the instantiation of
	 *                           UserPool
	 */
	static Manager getInstance() throws CardGameException {
		if (manager == null)
			manager = new Manager();

		return manager;
	}

	/**
	 * Resets manager's fields to enable unit testing. This method must only be used
	 * during testing and shoudn't be public.
	 */
	void reset() {
		gamePool.reset();
		userPool.reset();
	}

	/**
	 * A list of available game names, required to create a new game instance.
	 * 
	 * @return list of game names
	 */
	public List<String> getGameNames() {
		return gamePool.getGameNames();
	}

	/**
	 * Create a new game instance of the game with given name. The game name must be
	 * one in the list returned by getGameNames().
	 * 
	 * @param name - of game
	 * @return a game instance id
	 * @throws CardGameException - if name is invalid
	 */
	public String createGame(String name) throws CardGameException {
		return gamePool.createGame(name);
	}

	/**
	 * A list with information on games available to be played. Information on games
	 * includes their gameId, required join and play in a specific game instance.
	 * 
	 * @return list of games
	 */
	public List<GameInfo> getAvailableGameInfos() {
		return gamePool.getAvailableGameInfos();
	}

	/**
	 * Register a human player (an user) to participate in card games.
	 * 
	 * @param nick     - of player
	 * @param password - of player
	 * @throws CardGameException
	 */
	public void registerPlayer(String nick, String password) throws CardGameException {
		userPool.addUser(nick, password);
	}

	/**
	 * Add a player to a given game instance. The player must have been previously
	 * registered, and the game must be accepting players, otherwise an exception is
	 * raised. The game will automatically start when the required number of players
	 * is added to the game.
	 * 
	 * @param gameId   - of game instance to join
	 * @param nick     - of player
	 * @param password - of player
	 * @throws CardGameException - if gameId is invalid or player authentication
	 *                           failed
	 */
	public void addPlayer(String gameId, String nick, String password) throws CardGameException {
		User userToManage = userPool.getUser(nick, password);
		GameMaster game = gamePool.getGameMaster(gameId);
		game.addPlayer(userToManage);
	}

	/**
	 * Add a bot to the given game instance. Adding a bot will allow users to play,
	 * event if other human opponents are not available. The game will automatically
	 * start when the required number of players is added to the game.
	 * 
	 * @param gameId - of game instance to add player
	 * @throws CardGameException - if gameId is invalid
	 */
	public void addBotPlayer(String gameId) throws CardGameException {
		GameMaster game = gamePool.getGameMaster(gameId);
		GameBot bot = new GameBot(game);
		game.addPlayer(bot);
	}

	/**
	 * Play cards on a game on behalf of an authenticated user.
	 * 
	 * @param gameId   - of game instance
	 * @param nick     - of player
	 * @param password - of player
	 * @param cards    - to play
	 * @throws CardGameException - if gameId is invalid, authentication fails, or
	 *                           given cards cannot be played
	 */
	public void playCards(String gameId, String nick, String password, List<Card> cards) throws CardGameException {
		GameMaster game = gamePool.getGameMaster(gameId);
		userPool.getUser(nick, password);
		game.playCards(nick, cards);
	}

	/**
	 * Get a list of recent events sent to the given user by game instances. This
	 * list may contain multiple events, sent from multiple game instances. The
	 * returned events will not returned again by this method.
	 * 
	 * @param nick     - of player
	 * @param password - of player
	 * @return list of events
	 * @throws CardGameException - if authentication fails
	 */
	public List<GameEvent> getRecentEvents(String nick, String password) throws CardGameException {
		User user = userPool.getUser(nick, password);
		return user.getRecentEvents();
	}
}
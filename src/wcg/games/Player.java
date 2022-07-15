package wcg.games;

/**
 * Common type to all players, including human players and bots. A player must
 * have a nick (unique within a game) and be a GameObserver, i.e. be able to
 * receive events reporting changes in a game.
 */
public interface Player extends GameObserver {

	/**
	 * Players nick, it will used as an ID.
	 * 
	 * @return nick of player
	 */
	String getNick();

}

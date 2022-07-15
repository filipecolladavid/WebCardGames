package wcg.games;

import wcg.shared.events.GameEndEvent;
import wcg.shared.events.RoundConclusionEvent;
import wcg.shared.events.RoundUpdateEvent;
import wcg.shared.events.SendCardsEvent;

/**
 * An observer of events occurred in a game instance. A class implementing this
 * interface will be able to receive information propagated by an observable
 * game.
 */
public interface GameObserver {

	/**
	 * Notify the observer and send her/him/it a list of cards. This event is
	 * typically the first one received by a player in a particular game instance.
	 * In some games this method may be invoked several times during a game
	 * instance. The method is meant to be invoked in a single observer/player and
	 * not to be broadcast.
	 * 
	 * @param event - to send
	 */
	void notify(SendCardsEvent event);

	/**
	 * Notify the observer of updates in a round. This event trigger the player to
	 * play her/his/it cards. These updates are sent when a player plays and are
	 * broadcast to provide the same information to all players simultaneously.
	 * 
	 * @param event - to send.
	 */
	void notify(RoundUpdateEvent event);

	/**
	 * Notify the observer of the round conclusion. The event provides information
	 * on the round status. These notifications are broadcast to provide the same
	 * information to all players simultaneously.
	 * 
	 * @param event - to send.
	 */
	void notify(RoundConclusionEvent event);

	/**
	 * Notify the observer that the game has ended. The event provides information
	 * on the final game status. This event is the last in a game instance and is
	 * broadcast to all players.
	 * 
	 * @param event - to send.
	 */
	void notify(GameEndEvent event);
}

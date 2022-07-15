package wcg.games;

import java.util.HashMap;
import java.util.Map;

import wcg.shared.events.GameEndEvent;
import wcg.shared.events.RoundConclusionEvent;
import wcg.shared.events.RoundUpdateEvent;
import wcg.shared.events.SendCardsEvent;

/**
 * <p>
 * Observer management and event propagation in a game instance. This class
 * provides methods to register observers and propagate 3 types of events
 * SendCardsEvent, RoundUpdateEvent, RoundConclusionEvent and GameEndEvent -,
 * using 2 different communication forms. The former is sent to a single user
 * and the latter two are broadcast to all observers. No enable this 2 forms
 * observers are associated with a nick that is used as an identifier (i.e. is
 * assumed to be unique in a game).
 * </p>
 * 
 * <p>
 * This class is a participant in the <strong>Observable</strong> design
 * pattern.
 * </p>
 *
 */
public class ObservableGame {

	Map<String, GameObserver> observers = new HashMap<>();

	public ObservableGame() {
	}

	/**
	 * Add a GameObserver that will receive a notification when an event is
	 * broadcast.
	 * 
	 * @param nick     - identifying observer
	 * @param observer - that will receive the event
	 */
	protected void addObserver(String nick, GameObserver observer) {
		observers.put(nick, observer);
	}

	/**
	 * Notify a single observer with a send SendCardsEvent. Only previously
	 * registered observers will receive these notifications. Events to
	 * non-registered observers will be silently ignored.
	 * 
	 * @param nick  - identifying observer
	 * @param event - to send
	 */
	protected void notify(String nick, SendCardsEvent event) {
		GameObserver observer = observers.get(nick);
		observer.notify(event);
	}

	/**
	 * Broadcast given RoundUpdateEvent to all registered observers.
	 * 
	 * @param event - to broadcast
	 */
	protected void broadcast(RoundUpdateEvent event) {
		for (GameObserver observer : observers.values()) {
			observer.notify(event);
		}
	}

	/**
	 * Broadcast given RoundConclusionEvent to all registered observers.
	 * 
	 * @param event - to broadcast
	 */
	protected void broadcast(RoundConclusionEvent event) {
		for (GameObserver observer : observers.values()) {
			observer.notify(event);
		}
	}

	/**
	 * Broadcast given GameEndEvent to all registered observers.
	 * 
	 * @param event - to broadcast
	 */
	protected void broadcast(GameEndEvent event) {
		for (GameObserver observer : observers.values()) {
			observer.notify(event);
		}
	}
}

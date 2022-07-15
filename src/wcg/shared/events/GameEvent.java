package wcg.shared.events;

import java.io.Serializable;

/**
 * Abstract class common to all game related events. This class is abstract to
 * prevent the instantiation of generic events. Event classes must extend this
 * class to ensure a common type and common properties, such as the id of the
 * originating game instance.
 */
public abstract class GameEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String gameId;

	public GameEvent() {
		super();
	}

	/**
	 * Create an instance with given id.
	 * 
	 * @param gameId
	 */
	public GameEvent(String gameId) {
		super();
		this.gameId = gameId;
	}

	/**
	 * The id of game instance that originated this event.
	 * 
	 * @return id of game instance.
	 */
	public String getGameID() {
		return gameId;
	}

}

package wcg.shared.events;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import wcg.shared.cards.Card;

/**
 * <p>
 * An event to report changes to the current status of a game. This event should
 * trigger playing, either in bots (automated players) or in human players, if
 * the game was not yet terminated.
 * </p>
 * 
 * <p>
 * This event report the nick of player with the turn to play
 * (getNickWithTurn()). If the game doesn't have turns this value is null and
 * the players play (almost) simultaneously; i.e. upon receiving the event. In
 * turn-based games (most games are) only the player with the turn should play
 * upon receiving this event.
 * </p>
 * 
 * <p>
 * Using the getCardsOnTable() method the player has access to the cards on the
 * table. It is map keyed by player's nicks, hence it is possible to know who
 * played which card. Have in mind that this map will be empty for the first
 * player in the round and only the last player in the turn has information on
 * cards played by all the others.
 * </p>
 * 
 * <p>
 * This event also includes a mode that can be retrieved with getMode(). Certain
 * games may have different modes (e.g. in WAR there is a special mode (war)
 * when the two players played a card with the same value.
 * </p>
 * 
 * 
 */
public class RoundUpdateEvent extends GameEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, List<Card>> onTable;
	private String hasTurn;
	private int roundsCompleted;
	private String mode;

	public RoundUpdateEvent() {
		super();

	}

	/**
	 * Instantiate an event with given data.
	 * 
	 * @param onTable
	 * @param hasTurn
	 * @param roundsCompleted
	 * @param mode
	 */
	public RoundUpdateEvent(String gameId, Map<String, List<Card>> onTable, String hasTurn, int roundsCompleted,
			String mode) {
		super(gameId);
		this.onTable = onTable;
		this.hasTurn = hasTurn;
		this.roundsCompleted = roundsCompleted;
		this.mode = mode;
	}

	/**
	 * Cards on table in this turn for each player. Some players may have not played
	 * yet.
	 * 
	 * @return map of strings (nicks) to lists of cards
	 */
	public Map<String, List<Card>> getCardsOnTable() {
		return onTable;
	}

	/**
	 * Nick of player with turn. This player must be the next to play.
	 * 
	 * @return nick of player with turn.
	 */
	public String getNickWithTurn() {
		return hasTurn;
	}

	/**
	 * Number of rounds completed in this game.
	 * 
	 * @return the roundsCompleted
	 */
	public int getRoundsCompleted() {
		return roundsCompleted;
	}

	/**
	 * Current mode
	 * 
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}
}

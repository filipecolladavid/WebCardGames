/**
 * 
 */
package wcg.shared.events;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import wcg.shared.cards.Card;

/**
 * An event to report the conclusion of a round. This event report the final
 * state of the table using the getCardsOnTable() It is map keyed by player's
 * nicks, hence it is possible to know who played which card. Have in mind that
 * this map will be empty for the first player in the round and only the last
 * player in the turn has information on cards played by all the others.
 */
public class RoundConclusionEvent extends GameEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, List<Card>> onTable;
	private int roundsCompleted;
	private Map<String, Integer> points;

	public RoundConclusionEvent() {
		super();
	}

	/**
	 * Instantiate an event with given data.
	 * 
	 * @param onTable
	 * @param roundsCompleted
	 * @param points
	 */
	public RoundConclusionEvent(String gameId, Map<String, List<Card>> onTable, int roundsCompleted,
			Map<String, Integer> points) {
		super(gameId);
		this.onTable = onTable;
		this.roundsCompleted = roundsCompleted;
		this.points = points;
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
	 * Number of rounds completed in this game.
	 * 
	 * @return the roundsCompleted
	 */
	public int getRoundsCompleted() {
		return roundsCompleted;
	}

	/**
	 * Points of all players
	 * 
	 * @return the points
	 */
	public Map<String, Integer> getPoints() {
		return points;
	}

}

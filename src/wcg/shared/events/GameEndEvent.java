/**
 * 
 */
package wcg.shared.events;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import wcg.shared.cards.Card;

/**
 * An event reporting the end of the game and its final status. An event of this
 * type is the last one broadcast to all players in a given game. This event
 * reports the cards on the table, the number of completed rounds and who won
 * the game. After this event the players should not play any more cards.
 */
public class GameEndEvent extends GameEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, List<Card>> onTable;
	private int roundsCompleted;
	private String winner;
	private Map<String, Integer> points;

	/**
	 * 
	 */
	public GameEndEvent() {
		super();
	}

	/**
	 * Create an event for given game to report the winner.
	 * 
	 * @param onTable
	 * @param roundsCompleted
	 * @param winner
	 * @param points
	 */
	public GameEndEvent(String gameId, Map<String, List<Card>> onTable, int roundsCompleted, String winner,
			Map<String, Integer> points) {
		super(gameId);
		this.onTable = onTable;
		this.roundsCompleted = roundsCompleted;
		this.winner = winner;
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
	 * The winner of this game.
	 * 
	 * @return the winner
	 */
	public String getWinner() {
		return winner;
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


package wcg.shared;

import java.util.Date;
import java.io.Serializable;

/**
 * Summary information on currently available games. Information includes the
 * game id, the name of the game and the number of players. This information is
 * used for selecting a game to play.
 */
public class GameInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String gameId;
	private String gameName;
	private int playersCount;
	private Date startDate;
	private Date lastAccessDate;

	public GameInfo() {
	}

	public GameInfo(String gameId, String gameName, int playersCount, Date startDate, Date lastAccessDate) {
		this.gameId = gameId;
		this.gameName = gameName;
		this.playersCount = playersCount;
		this.startDate = startDate;
		this.lastAccessDate = lastAccessDate;
	}

	/**
	 * Id of this game.
	 * 
	 * @return the gameId
	 */
	public String getGameId() {
		return gameId;
	}

	/**
	 * Name of this game.
	 * 
	 * @return the gameName
	 */
	public String getGameName() {
		return gameName;
	}

	/**
	 * Number players currently in this game.
	 * 
	 * @return the playersCount
	 */
	public int getPlayersCount() {
		return playersCount;
	}

	/**
	 * Moment when this game was created.
	 * 
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Moment when this game was last accessed: a player was added or a card played.
	 * 
	 * @return the lastAcessDate
	 */
	public Date getLastAccessDate() {
		return lastAccessDate;
	}

}

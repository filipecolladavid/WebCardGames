package wcg.games;

import java.util.List;

import wcg.shared.CardGameException;

/**
 * Type of a class that produces specializations of GameMaster for particular
 * games given their names. This type provides also a list of available game
 * names. Abstract component of the Factory design pattern.
 */
public interface AbstractGameFactory {

	/**
	 * A list of available game names. Names of games should be all in capitals
	 * (e.g. HEARTS).
	 * 
	 * @return list of game names.
	 */
	List<String> getAvailableGames();

	/**
	 * A specialization of GameMaster for the given game name.
	 * 
	 * @param name - of game
	 * @return game master
	 * @throws CardGameException - if given game name is not supported.
	 */
	GameMaster makeGameMaster(String name) throws CardGameException;
}

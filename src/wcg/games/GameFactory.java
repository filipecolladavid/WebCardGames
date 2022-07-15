package wcg.games;

import java.util.Arrays;
import java.util.List;

import wcg.games.hearts.HeartsGameMaster;
import wcg.games.war.WarGameMaster;
import wcg.shared.CardGameException;

/**
 * Concrete participant of the Factory design pattern. Is provides a list of
 * available game names and produces a GameMaster from a valid name. It
 * currently supports only w games: WAR and HEARTS.
 */
public class GameFactory implements AbstractGameFactory {

	List<String> availableGames = Arrays.asList("WAR", "HEARTS");

	public GameFactory() {
	}

	@Override
	public List<String> getAvailableGames() {
		return availableGames;
	}

	@Override
	public GameMaster makeGameMaster(String name) throws CardGameException {
		if (name.equals("WAR"))
			return new WarGameMaster();
		else if (name.equals("HEARTS"))
			return new HeartsGameMaster();
		else
			throw new CardGameException(name + " isn't a supported game");
	}
}

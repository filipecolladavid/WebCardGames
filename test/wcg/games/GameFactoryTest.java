package wcg.games;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wcg.shared.CardGameException;

/**
 * Test GameFactory methods.
 */
class GameFactoryTest {
	GameFactory factory;

	@BeforeEach
	void setUp() throws Exception {
		factory = new GameFactory();
	}

	@Test
	void testGetAvailableGames() {
		List<String> games = factory.getAvailableGames();
		
		assertAll(
				() -> assertNotNull(games, "a list of was returned"),
				() -> assertTrue( games.contains("WAR") , "WAR is available"),
				() -> assertTrue( games.contains("HEARTS") , "HEARTS is available")
				);
	}

	/**
	 * Check that each available game can be created
	 * @throws CardGameException
	 */
	@Test
	void testMakeGamePlay() throws CardGameException {
		for(String name: factory.getAvailableGames()) {
			GameMaster rules = factory.makeGameMaster(name);
		
			assertNotNull(rules);
		}
	}
	
	/**
	 * Check that an invalid game name raises an exception
	 */
	@Test
	void testMakeGamePlay_invalid() {
		
		assertThrows(CardGameException.class,
				() -> factory.makeGameMaster("invalidGame"));
	}

}

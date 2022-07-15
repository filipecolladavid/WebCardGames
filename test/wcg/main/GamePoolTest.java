/**
 * 
 */
package wcg.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wcg.WebCardGameTest;
import wcg.games.GameMaster;
import wcg.shared.CardGameException;

/**
 * Test GamePool
 */
class GamePoolTest extends WebCardGameTest {
	private final static String GAME_NAME = "WAR";
	GamePool pool;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		pool = new GamePool();
	}

	/**
	 * Check if an id is returned when a game is created.
	 *  
	 * @throws CardGameException 
	 */
	@Test
	void testCreateGame() throws CardGameException {
		
		String gameId = pool.createGame(GAME_NAME);
		
		assertNotNull( gameId , "game id created");
	}
	
	/**
	 * Check if in invalid game name throws an exception 
	 */
	@Test
	void testCreateGame_invalid() throws CardGameException {
		
		assertThrows(CardGameException.class, 
				() -> pool.createGame("InvalidGameName") );
	}


	/**
	 * Create a single game, get it and check it has the correct id and name
	 * @throws CardGameException 
	 */
	@Test
	void testGetGameMaster() throws CardGameException {
		String gameId = pool.createGame(GAME_NAME);
		
		GameMaster rules = pool.getGameMaster(gameId);
		
		assertNotNull( rules , "game instantiated");
		assertEquals( gameId , rules.getGameId(), "searched id expected");
		assertEquals( GAME_NAME.toUpperCase() , 
				rules.getInfo().getGameName().toUpperCase() , "game name");
	}
	
	/**
	 * Check that using an invalid id to get a game master throws an exception
	 */
	@Test
	void testGetGameMaster_missing() {
		
		CardGameException error = assertThrows(CardGameException.class,
				() -> pool.getGameMaster("invalidID"));
		
		String message = error.getMessage().toLowerCase();
		assertTrue( message.contains("invalid") );
		assertTrue( message.contains("id") );
	}
	
	/**
	 * Create several games masters and check them all 
	 *  
	 * @throws CardGameException
	 */
	@Test
	void testGetGameMaster_several() throws CardGameException {
		List<String> ids = new ArrayList<>();

		for(int c=0; c < 10; c++)
			ids.add( pool.createGame(GAME_NAME) );
				
		for(String gameId: ids) {
			GameMaster rules = pool.getGameMaster(gameId);
		
			assertNotNull( rules , "game instantiated");
			assertEquals( gameId , rules.getGameId(), "searched id expected");
			assertEquals( GAME_NAME.toUpperCase() , 
					rules.getInfo().getGameName().toUpperCase() , "game name");
		}
	}
	
	/**
	 * Create a collection of games a regular intervals.
	 * The number of available games should increase.
	 * At a regular intervals check the number of available games.
	 * Its should decrease.
	 *  
	 * @throws CardGameException
	 * @throws InterruptedException
	 */
	@Test
	void testRemoveExpiredGames() throws CardGameException, InterruptedException {
		
		GameMaster.setExpirationTime(SMALL_EXPIRATION_TIME);
		final int count = 10;
		
		for(int c=0; c < count; c++) {
			assertEquals(c, pool.getAvailableGameInfos().size());
			
			pool.createGame(GAME_NAME);
			
			// use a smaller delay in the last iteration
			long delay = SMALL_EXPIRATION_TIME / count / ( c+1 == count ? 2 : 1);
			Thread.sleep(delay);
		}
		
		for(int c=count; c >= 0; c--) {
			assertEquals(c, pool.getAvailableGameInfos().size());
			
			Thread.sleep(SMALL_EXPIRATION_TIME / count);
			
			pool.removeExpiredGames();
		}
	}
	

}

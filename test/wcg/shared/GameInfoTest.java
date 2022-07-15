package wcg.shared;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.junit.jupiter.api.Test;

import wcg.WebCardGameTest;

/**
 * Tests on the {@code GameInfo} class
 */
class GameInfoTest extends WebCardGameTest {

	/**
	 * Check that a {@code GameInfo} with arguments can be created
	 */
	@Test
	void testGameInfo() {
		assertNotNull( new GameInfo() , "an instance with no argument ");
	}

	/**
	 * Check that a {@code GameInfo} with arguments can be created
	 * and data can be obtained from it the the appropriate getters. 
	 */
	@Test
	void testGameInfoWithArgs() {
		final int  count = 3;
		final Date modified = new Date();
		final Date created = new Date(modified.getTime() - 5000);
		final GameInfo gameInfo = new GameInfo(
				GAME_ID_EX,
				GAME_NAME,
				count,
				created,
				modified);
		
		assertNotNull( gameInfo , "instance expected");
		
		assertAll(
				() -> assertEquals( GAME_ID_EX , gameInfo.getGameId() ),
				() -> assertEquals( GAME_NAME , gameInfo.getGameName() ),
				() -> assertEquals( count , gameInfo.getPlayersCount() ),
				() -> assertEquals( created , gameInfo.getStartDate()),
				() -> assertEquals( modified , gameInfo.getLastAccessDate())
			);
		
	}

}

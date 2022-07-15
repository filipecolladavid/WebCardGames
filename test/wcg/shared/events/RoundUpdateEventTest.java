package wcg.shared.events;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import wcg.shared.cards.Card;

/**
 * Tests on RoundUpdateEventTest
 */
class RoundUpdateEventTest extends EventsTest {

	/**
	 * Test if an empty event can be created
	 */
	@Test
	void testRoundUpdateEvent() {
		assertNotNull( new RoundUpdateEvent() );
	}

	/**
	 * Test if a non-event can be created and its arguments acessed
	 */
	@Test
	void testRoundUpdateEventWithArgs() {
		Map<String, List<Card>> map = new HashMap<>();
		String hasTurn = "me";
		int roundsCompleted = 0;
		String mode = "on";
		
		RoundUpdateEvent event = new RoundUpdateEvent(
				GAMEID,
				map, 
				hasTurn, 
				roundsCompleted,
				mode);
		
		assertAll(
				() -> assertEquals(GAMEID , event.getGameID() ),
				() -> assertEquals( map, event.getCardsOnTable()),
				() -> assertEquals( hasTurn, event.getNickWithTurn()),
				() -> assertEquals( roundsCompleted, event.getRoundsCompleted()),
				() -> assertEquals( mode, event.getMode())
				);		
	}

}

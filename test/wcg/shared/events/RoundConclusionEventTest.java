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
class RoundConclusionEventTest extends EventsTest {

	/**
	 * Test if an empty event can be created
	 */
	@Test
	void testEndUpdateEvent() {
		assertNotNull( new RoundConclusionEvent() );
	}

	/**
	 * Test if a non-event can be created and its arguments acessed
	 */
	@Test
	void testRoundEndEventWithArgs() {
		Map<String, List<Card>> map = new HashMap<>();
		int roundsCompleted = 0;
		Map<String,Integer> points = new HashMap<>();
		
		RoundConclusionEvent event = new RoundConclusionEvent(
				GAMEID,
				map, 
				roundsCompleted,
				points);
		
		assertAll(
				() -> assertEquals(GAMEID , event.getGameID() ),
				() -> assertEquals( map, event.getCardsOnTable()),
				() -> assertEquals( roundsCompleted, event.getRoundsCompleted()),
				() ->  assertEquals(points, event.getPoints()) 
				);		
	}

}

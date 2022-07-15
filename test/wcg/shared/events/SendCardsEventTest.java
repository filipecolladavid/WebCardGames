package wcg.shared.events;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import wcg.shared.cards.Card;

/**
 * Tests on SendCardsEvent
 */
class SendCardsEventTest  extends EventsTest {
	
	/**
	 * Test if an empty event can be created
	 */
	@Test
	void testSendCardsEvent() {
		assertNotNull( new SendCardsEvent() );
	}

	/**
	 * Test if a non-event can be created and its arguments acessed
	 */
	@Test
	void testSendCardsEventWithArgs() {
		List<Card> cards = new ArrayList<>();
		SendCardsEvent event = new SendCardsEvent(GAMEID, cards);
		
		assertAll(
				() -> assertEquals(GAMEID , event.getGameID() ),
				() -> assertEquals( cards,  event.getCards() )
			);
	}

}

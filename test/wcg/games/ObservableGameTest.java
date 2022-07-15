package wcg.games;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wcg.shared.events.GameEndEvent;
import wcg.shared.events.GameEvent;
import wcg.shared.events.RoundConclusionEvent;
import wcg.shared.events.RoundUpdateEvent;
import wcg.shared.events.SendCardsEvent;

/**
 * Check if registered observers receive the events sent to them
 * either by notification or broadcast (depending on event type).
 * 
 * A {@code SimpleGameObserver} class is used to receive and
 * record events. Event objects are compared with == to check
 * if the exact same object was received.    
 */
class ObservableGameTest {
	private static final String[] NICKS = { "Nick 1", "Nick 2", "Nick 3" };
	
	ObservableGame observableGame;
	Map<String,SimpleGameObserver> observers = new HashMap<>();
	
	/**
	 * A simple observer that records the receive event.
	 */
	class SimpleGameObserver implements GameObserver {
		GameEvent event = null;
		
		@Override
		public void notify(SendCardsEvent event) {
			this.event = event;			
		}

		@Override
		public void notify(RoundUpdateEvent event) {
			this.event = event;

		}
		
		@Override
		public void notify(RoundConclusionEvent event) {
			this.event = event;

		}

		@Override
		public void notify(GameEndEvent event) {
			this.event = event;
		}
		
	}
	
	/**
	 * Create an {@code ObservableGame}, some observers,
	 * and add these to the former   
	 */
	@BeforeEach
	void setUp()  {
		observableGame = new ObservableGame();
		observers.clear();
		
		for(String nick: NICKS) {
			SimpleGameObserver observer =  new SimpleGameObserver();
			
			observers.put(nick,observer);
			observableGame.addObserver(nick,observer);
		}
	}

	/**
	 * Notify a single observer with a {@code SendCardsEvent}
	 * and only that particular observer must receive it,
	 * but only after being sent.
	 */
	@Test
	void testSendCardsEvent() {
		SendCardsEvent event = new SendCardsEvent();

		for(String nick: NICKS)
			assertNull( observers.get(nick).event );

		observableGame.notify(NICKS[0],event);
		
		assertTrue( event ==  observers.get(NICKS[0]).event );
		assertNull( observers.get(NICKS[1]).event );
		assertNull( observers.get(NICKS[2]).event );
	}
	
	/**
	 * Broadcast to all observers a {@code RoundUpdateEvent}
	 * and all observers must receive it.
	 * but only after being sent.
	 */
	@Test
	void testRoundUpdateEvent() {
		RoundUpdateEvent event = new RoundUpdateEvent(); 
		
		for(String nick: NICKS)
			assertNull( observers.get(nick).event );
		
		observableGame.broadcast(event);
		
		for(String nick: NICKS)
			assertTrue( event ==  observers.get(nick).event );
	}
	
	/**
	 * Broadcast to all observers a {@code GameEndEvent}
	 * and all observers must receive it,
	 * but only after being sent.
	 */
	@Test
	void testGameEndEvent() {
		GameEndEvent event = new GameEndEvent(); 
		
		for(String nick: NICKS)
			assertNull( observers.get(nick).event );
		
		observableGame.broadcast(event);
		
		for(String nick: NICKS)
			assertTrue( event ==  observers.get(nick).event );
	}
}

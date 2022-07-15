package wcg.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wcg.WebCardGameTest;
import wcg.shared.events.GameEndEvent;
import wcg.shared.events.GameEvent;
import wcg.shared.events.RoundConclusionEvent;
import wcg.shared.events.RoundUpdateEvent;
import wcg.shared.events.SendCardsEvent;

class UserTest extends WebCardGameTest {
	
	User user;

	@BeforeEach
	void setUp() throws Exception {
		user  = new User(NICK,PASSWORD);
	}

	/**
	 * Check if user was created.
	 */
	@Test
	void testUser() {
		assertNotNull( user , "user expected");
		
		assertTrue( Serializable.class.isInstance(user) ,"Should be serializable");
	}

	/**
	 * Check authentication
	 */
	@Test
	void testAuthenticate() {
		assertTrue ( user.authenticate( PASSWORD ) );
		assertFalse( user.authenticate("invalid password"));
		assertFalse( user.authenticate(null));
	}

	/**
	 * Check that the digest is a 16 byte array.
	 * @throws NoSuchAlgorithmException 
	 */
	@Test
	void testDigest() {
		byte[] digest = user.digest(PASSWORD);
		
		assertEquals(16, digest.length , " hash with 16 bytes expected");
	}

	/**
	 * Check if nick is the expected.
	 */
	@Test
	void testGetNick() {
		assertEquals( NICK , user.getNick() );
	}

	/**
	 * Check if {@code SendCardsEvent} are received.
	 */
	@Test
	void testNotifySendCardsEvent() {
		SendCardsEvent event = new SendCardsEvent();
		
		user.notify(event);
		
		checkEventReceived(event);
	}

	/**
	 * Check if {@code RoundUpdateEvent} are received.
	 */
	@Test
	void testNotifyRoundUpdateEvent() {
		RoundUpdateEvent event = new RoundUpdateEvent();
		
		user.notify(event);
		
		checkEventReceived(event);
	}
	
	/**
	 * Check if {@code RoundConclusionEvent} are received.
	 */
	@Test
	void testNotifyRoundConclusionEvent() {
		RoundConclusionEvent event = new RoundConclusionEvent();
		
		user.notify(event);
		
		checkEventReceived(event);
	}

	/**
	 * Check if {@code GameEndEvent} are received.
	 */
	@Test
	void testNotifyGameEndEvent() {
		GameEndEvent event = new GameEndEvent();
		
		user.notify(event);
		
		checkEventReceived(event);
	}

	/**
	 * Check that no events are available when users are created
	 */
	@Test
	void testGetRecentEvents() {
		assertEquals(0, user.getRecentEvents().size() , "No events when created");
	}
	
	/**
	 * Send multiples events to multiples users in random order
	 * and check that they are recovered in the correct order. 
	 */
	@Test
	void testMultipleRandomEventsAndUsers() {
		Map<User,ArrayList<GameEvent>> userEvents = new HashMap<>();
		
		int size = NICKS.length;
		
		for(String nick: NICKS) {
			User user = new User(nick,PASSWORD); 
			userEvents.put(user, new ArrayList<GameEvent>());
		}
		
		List<User> users = new ArrayList<>(userEvents.keySet());
		for(int count=0; count < REPETITIONS; count ++) {
			User user = users.get( (int) (Math.random() * size));
			
			switch((int) (Math.random() * 3)) {
				case 0: {
					SendCardsEvent event = new SendCardsEvent();
					user.notify(event);
					userEvents.get(user).add(event);
				}
				break;
				case 1: {
					RoundUpdateEvent event = new RoundUpdateEvent();
					user.notify(event);
					userEvents.get(user).add(event);
				}
				break;
				case 2: {
					RoundConclusionEvent event = new RoundConclusionEvent();
					user.notify(event);
					userEvents.get(user).add(event);
				}
				break;
				case 3: {
					GameEndEvent event = new GameEndEvent();
					user.notify(event);
					userEvents.get(user).add(event);
				}
				break;
			}
		}
		
		for(User user: users) {
			assertEquals( userEvents.get(user) , user.getRecentEvents() ,
					"all events received in the correct order");

			assertEquals( 0 , user.getRecentEvents().size() , "event cleared ");
			
		}
		
	}
	
	
	/**
	 * Check the recent event list has a single, the one given as parameter,
	 * and that it is cleared after being  
	 * 
	 * @param event
	 */
	private void checkEventReceived(GameEvent event) {
		List<GameEvent> events = user.getRecentEvents();
		
		assertEquals(1 , events.size() , "just one event received");
		assertTrue( events.contains(event) , "event received");
		assertEquals(0, user.getRecentEvents().size() , "event cleared ");
	}
}

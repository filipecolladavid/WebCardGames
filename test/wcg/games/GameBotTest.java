package wcg.games;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import wcg.shared.CardGameException;
import wcg.shared.cards.Card;

/**
 * Test GameBot tests
 */
class GameBotTest {
	GameBot bot;
	List<GameBot> bots = new ArrayList<>();
	
	@BeforeEach
	void setUp() throws Exception {
		setupGameWithBot();
		setupGameWith2Bots();
	}
	
	/**
	 * Create a test game and a bot with it. 
	 * @throws CardGameException 
	 * 
	 */
	void setupGameWithBot() throws CardGameException {
		GameMaster master = new TestGameMaster();
		
		bot = new GameBot( master ); 
		master.addPlayer(bot);
	}
	
	/**
	 * Create a game and add 2 bots. After adding the 2 bots the game starts 
	 * automatically and proceeds till it ends. This bots are in a end of game
	 * situation.
	 * 
	 * @throws CardGameException
	 * @throws InterruptedException 
	 */
	void setupGameWith2Bots() throws CardGameException, InterruptedException {
		GameMaster master = new TestGameMaster();
		
		for(int count=0; count < 2; count++) {
			GameBot b = new GameBot(master);
			master.addPlayer(b);
			bots.add(b);
		}
		
		// wait for all bots to complete their execution
		for(GameBot b: bots)
			b.join();
	}
	

	/**
	 * A bot was instantiated
	 */
	@Test
	void testGameBot() {
		assertNotNull(bot , "bot created");
	}

	/**
	 * Bots have a default strategy, event if none was assigned
	 */
	@Test
	void testGetStrategy() {
		GamePlayingStrategy strategy = bot.getStrategy();
		
		assertNotNull( strategy , "There is a default strategy" );
	}

	/**
	 * Create a strategy, set it in the bot and check it can be recovered
	 */
	@Test
	void testSetStrategy() {
		GamePlayingStrategy strategy = new GamePlayingStrategy() {

			@Override
			public List<Card> pickCards(GameBot bot) {
				return null;
			}
			
		};
		
		bot.setStrategy(strategy);
		
		assertSame( strategy , bot.getStrategy() , "same stragegy expected");
	}

	/**
	 * Bots have a nick and it dosen't change.
	 * Two bots in the same game have different nicks
	 * 
	 */
	@Test
	void testGetNick() {
		String nick  = bots.get(0).getNick();
		String other = bots.get(1).getNick();
		
		assertAll(
				() -> {
					assertNotNull( nick , "a nick was created");
					assertEquals( nick , bots.get(0).getNick() , 
							"nick is always the same");
				},
				() -> {
					assertNotNull( other ,"a nick was created (again)" );
					assertNotEquals( nick , other  , "the new nick is diferent");
				}
		);
		
	}

	/**
	 * Check rounds completed, before starting the game and after
	 */
	@Nested
	class RoundsCompletedTest {
		
		@Test
		void testBefore() {
			assertEquals( 0 , bot.getRoundsCompleted() ,
					"initially no rounds completed");
		}
		
		@Test
		void testAfter() {
			for(GameBot b: bots)
				assertEquals( 3 , b.getRoundsCompleted() , "3 rounds completed");
		}
	}

	/**
	 * Check the hand at the beginning and end of game. It should have no cards,
	 * because none has sent yet
	 */
	@Nested
	class NotifySendCardsEventTest {
	
		@Test
		void testBefore() {
			assertNotNull( bot.getHand() , "collection available");
			assertTrue( bot.getHand().size() == 0 , "no cards received");
		}
		
		@Test
		void testAfter() {
			for(GameBot b: bots) {
				assertNotNull( b.getHand() , "collection available");
				assertTrue( b.getHand().size() == 0 , "all cards played");
			}
		}
	}	

	/**
	 * Check if event is received.
	 * Adding 2 bots (the maximum in the test game) should force an update.
	 * This should change the number of rounds completed for both bots.
	 */
	@Nested
	class NotifyRoundUpdateEventTest {
		
		@Test
		void testBefore() throws CardGameException {
			assertEquals( 0, bot.getRoundsCompleted() ,
					"initially bot completed no rounds" );
			}

		@Test
		void test() throws CardGameException {
			for(GameBot b: bots)
				assertEquals( 3 , b.getRoundsCompleted(),
					"bots completed 3 round in this game"); 
		}		
	}
	
	

	
	
	/**
	 * Check if GameEndedEvent is received (sets gameEnded) 
	 */
	@Nested
	class NotifyGameEndEvent {
		@Test
		void testBefore() {
			assertFalse( bot.gameEnded() , "initially has not ended");
		}

		@Test
		void test() {
			for(GameBot b: bots)
				assertTrue( b.gameEnded() , "after the game should have ended");
		}
	}
	
	/**
	 * Check hand is empty initially. 
	 * Bots added to the game received 3 cards sent by the test game
	 */
	@Nested
	class GetHandTest {
		@Test
		void testBefore() {
			assertEquals( 0 , bot.getHand().size() , "no cards in hand initially" );
		}	
		
		@Test
		void testAfter() {
			for(GameBot b: bots) {
				assertEquals( 0 , b.getHand().size() , "all cards played after game");
			}
		}

	}

	/**
	 * Test setHand by setting collections of cards and retriving them
	 */
	@Test
	void testSetHand() {
		CardCollection cards = CardCollection.getDeck();
		
		while(! cards.isEmpty()) {
			bot.setHand(cards);
			
			assertEquals(cards ,  bot.getHand());
			
			cards.takeCard( cards.getRandomCard() );
		}
	}

	/**
	 * Check if a non null {@link wcg.games.CardComparator} is returned.
	 */
	@Test
	void testGetCardComparator() {
		CardComparator comparator = bot.getCardComparator();
		
		assertNotNull( comparator , "a card comparator was returned");
	}

	/**
	 * Check table for cards: no cards at the beginning and at the end.
	 * Cards in table are removed at the end of each round.
	 */
	@Test
	void testNoCardsOnTable() {
		assertTrue( bot.noCardsOnTable() , "no cards on table initially");
		
		for(GameBot b: bots)
			assertTrue( b.noCardsOnTable() , "no cards on table at the end");
	}

	
	/**
	 * Commented out as discussed with Professor. Test is run at the end of the
	 * game, when the table has been cleared.
	 */
	/**
	 * Both bots see the same cards on table
	 * @throws CardGameException
	 */
	/*
	@Test
	void testGetCardOnTable() throws CardGameException {
		
		
		for(GameBot a: bots)
			for(GameBot b: bots) {
				GameBot other = otherBot(a);
				Card card = a.getCardOnTable(b.getNick());
		
				assertNotNull( card , "card instance");
				assertEquals( card , other.getCardOnTable(b.getNick()));
			}
	}
	
	GameBot otherBot(GameBot a) {
		for(GameBot b: bots)
			if(a != b)
				return b;
		throw new RuntimeException("");
	}*/

	/**
	 * Initially there are not cards of table for bots,
	 * and also after playing all the cards.
	 * Cards are removed from table at the end of rounds.
	 */
	@Nested
	class GetAllCardsOnTable {
		
		@Test
		void testBefore() {
			CardCollection cards = bot.getAllCardsOnTable();
			
			assertNotNull( cards , "a card collection was returned");
			assertEquals( 0 , cards.size() , "initially it is empty");
		}
		
		@Test
		void testAfter() {
			for(GameBot b: bots) {
				CardCollection cards = b.getAllCardsOnTable();
			
				assertNotNull( cards , "a card collection was returned");
				assertEquals( 0 , cards.size() , "initially it is empty");
			}
		}

	}
	
	/**
	 * Check the number of cards played at the beginning and end of a game.
	 */
	@Nested
	class GetPlayedCardsTest {
		
		@Test
		void testBefore() {
			assertEquals( 0 , bot.getPlayedCards().size() , 
					"no cards played yet");
		}
		
		@Test
		void testAfter() {
			for(GameBot b: bots) 
				assertEquals( 6 , b.getPlayedCards().size() , "all cards played");
		}
	}

	/**
	 * Check if initial mode is null.
	 * Modes are game dependent; most games don't need it.
	 */
	@Test
	void testGetMode() {
		assertNull( bot.getMode() , "initial mode is null");
	}

	@Test
	void testSetMode() {
		String mode = "Some mode"; 
		
		bot.setMode(mode);
		
		assertEquals( mode , bot.getMode() , "changed mode expected");
	}

	@Test
	void testGetSuitToFollow() {
		assertNull( bot.getSuitToFollow() , "no suit to follow before start");
	}
	
	/**
	 * Check if game has ended. 
	 */
	@Nested
	class GameEndedTest {
	
		/**
		 * In a bot not added to a game it has not ended.
		 */
		@Test
		void testBefore() {
			assertFalse( bot.gameEnded() , "initially game has not ended");
		}
		
		/**
		 * After the game completes, it ends for both of them.
		 */
		@Test
		void testAfter() {
			for(GameBot b: bots) {
				assertTrue( b.gameEnded() , "after game completion");
			}
		}	
	}
	
	/**
	 * Check the winner. In a bot not added to a game there is no winner.
	 * When the game completes there is a winner, it's the same for both bots
	 * and is one of them. 
	 */	
	@Nested
	class GetWinnerTest {
		
		@Test
		void testBefore() {
			assertNull( bot.getWinner(), "initial winner is null");
		}
		
		@Test
		void testAfterNotNull() {
			for(GameBot b: bots)
				assertNotNull(b.getWinner(), 
						"there is a winner after end of game");
		}
		
		@Test
		void testAfterEqualWinners() {
			assertEquals( 
					bots.get(0).getWinner() , 
					bots.get(1).getWinner(),
					"winner is the same for both players");
		}
		
		@Test
		void testAfterWinnerIsOneOfThePlayers() {
			assertTrue(
					bots.get(0).getNick().equals(bots.get(0).getWinner()) ||
					bots.get(1).getNick().equals(bots.get(1).getWinner()),
					"winner is one of the players");
		}
		
	}
		
	/**
	 * Check if a nick is generated
	 */
	@Test
	void testGenerateNick() {
		GameMaster master = new TestGameMaster();
		String nick = bot.generateNick(master);
		
		assertNotNull(nick , "generated nick");
	}

}

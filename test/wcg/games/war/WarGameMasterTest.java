package wcg.games.war;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wcg.WebCardGameTest;
import wcg.games.CardCollection;
import wcg.games.GamePlayingStrategy;
import wcg.games.TestPlayers;
import wcg.games.TestPlayers.TestPlayer;
import wcg.shared.CardGameException;
import wcg.shared.cards.Card;
import wcg.shared.cards.CardValue;

/**
 * Test WarGameMaster
 */
class WarGameMasterTest  extends WebCardGameTest {
	 
	final static int CARD_COUNT = 52;
	final static int INITIAL_HAND_SIZE = CARD_COUNT / 2;
	final static int[] BOT_INDICES = new int[] { 0, 1 };
	
	final static String[] NICKS= { "Nick 1", "Nick 2" };
	
	WarGameMaster gameMaster;
	TestPlayers testPlayers;
	List<Card> cards;
	
	@BeforeEach
	void setUp() throws Exception {
		gameMaster    = new WarGameMaster();
		testPlayers = new TestPlayers(2);
		cards		= new ArrayList<>(); 
	}

	/**
	 * Check if more than one player with the same nick is accepted
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testRepeatedPlayers() throws CardGameException {
		gameMaster.addPlayer(testPlayers.getPlayer(0));
		
		assertThrows( CardGameException.class, 
				() -> gameMaster.addPlayer(testPlayers.getPlayer(0)),
				"repeated player cannot be acepted");
	}
	
	
	/**
	 * Checks acceptsPlayers() as the number of players is added.
	 * This games accepts just 2 players.
	 *   
	 * @throws CardGameException
	 */
	@Test 
	void testAcceptPlayers() throws CardGameException {
		assertTrue(gameMaster.acceptsPlayers(),"with no players should accept");
		
		gameMaster.addPlayer(testPlayers.getPlayer(0));
		
		assertTrue(gameMaster.acceptsPlayers(),"with just one player should accept");
		
		gameMaster.addPlayer(testPlayers.getPlayer(1));
		
		assertFalse(gameMaster.acceptsPlayers(),"with 2 players should not acept more");
	}
	
	/**
	 * Check the number of cards received by players (should be half a deck).
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testInitialHandSize() throws CardGameException {
		testPlayers.addPlayersTo(gameMaster);
		
		for(TestPlayer player: testPlayers)
			assertEquals(INITIAL_HAND_SIZE,player.hand.size(),"should have half the deck in hand");
	}
	
	/**
	 * In WAR players play simultaneously.
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testInitialTurnInRound() throws CardGameException {
		testPlayers.addPlayersTo(gameMaster);
		
		assertNull( "players play simultaneously in WAR",
				gameMaster.initialTurnInRound() );
	}

	/**
	 * The first move is never a war, hence the player can only play a single card
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testWrongNumberOfCards() throws CardGameException {
		TestPlayer player = testPlayers.getPlayer(0);
		
		testPlayers.addPlayersTo(gameMaster);
		
		assertThrows(CardGameException.class,
						() -> gameMaster.playCards(
								player.getNick(), 
								player.hand.asList().subList(0, 2) ) );
	}
	
	/**
	 * In a simple round one player has a a card with a higher value
	 * than the opponent. In this test players cheat a bit and peek
	 * cards to ensure that bot[1] has a card with higher value than bot[0] 
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testSimpleRound() throws CardGameException {
		int cardCount = INITIAL_HAND_SIZE;
		
		testPlayers.addPlayersTo(gameMaster);
		
		cards.add( testPlayers.getPlayer(0).hand.getLowestCard() );
		cards.add( testPlayers.getPlayer(1).hand.getHighestCard() );
		
		testPlayers.getPlayer(0).hand.takeCard(cards.get(0));
		testPlayers.getPlayer(1).hand.takeCard(cards.get(1));
		
		cardCount--;
		
		for(TestPlayer player: testPlayers) 
			assertEquals(cardCount,player.hand.size());
		
		gameMaster.playCard(testPlayers.getPlayer(0).getNick(), cards.get(0));
		gameMaster.playCard(testPlayers.getPlayer(1).getNick(), cards.get(1));
	
		assertEquals(cardCount,testPlayers.getPlayer(0).hand.size());
		assertEquals(cardCount+2,testPlayers.getPlayer(1).hand.size());
	}
	
	
	/**
	 * Check that initially mode is {@code null} for both players
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testMode() throws CardGameException {
		testPlayers.addPlayersTo(gameMaster);
		
		for(TestPlayer player: testPlayers)
			assertNull("No mode espected", player.getMode() );
	}
		
	/**
	 * Force a war, check mode changed and an exception is raised if
	 * a single card is played (in a war 3 cards both players must play 3 cards).
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testWarMode_wrongCards() throws CardGameException {
		Map<TestPlayer,Card> cardOf = new HashMap<>();
		testPlayers.addPlayersTo(gameMaster);
		
		CardValue value = valueInBothHands();		
		
		for(TestPlayer player: testPlayers) {
			CardCollection hand = player.hand;
			Card card = hand.getCardsWithValue(value).getFirstCard();
			
			hand.takeCard(card);
			cardOf.put(player, card);
		}

		for(TestPlayer player: testPlayers)
			gameMaster.playCard(player.getNick(), cardOf.get(player));
		
		
		assertAll(
				() -> {
					for(TestPlayer player: testPlayers)
						assertEquals("WAR", player.getMode().toUpperCase(), 
								"should be in WAR mode");
				},
				() -> {
					for(TestPlayer player: testPlayers)
						assertThrows(CardGameException.class,
								() -> gameMaster.playCard(player.getNick(), 
										player.hand.takeFirstCard()),
								"in war mode should mplay 3 cards");
				}
			);
	}
	
	/**
	 * Check that game strategy is always {@link WarGameStrategy}.
	 */
	@Test
	void testGetCardGameStrategy() {
		GamePlayingStrategy strategy = gameMaster.getCardGameStrategy();
		
		assertNotNull( strategy , "a strategy expected");
		
		assertTrue( strategy instanceof WarGameStrategy , "war strategy");
		
		assertTrue( gameMaster.getCardGameStrategy() instanceof WarGameStrategy,
				"it's always the war strategy");
	 }
	
	/**
	 * When both players play cards with the same value they
	 * go on war mode. Both play 3 cards. If the first one has
	 * the same value than its war all over again.
	 *     
	 *   
	 * @throws CardGameException
	 */
	@Test
	void testForcedWar() throws CardGameException {
		Map<TestPlayer,Card> cardOf = new HashMap<>();
		
		testPlayers.addPlayersTo(gameMaster);
		
		int cardCount = INITIAL_HAND_SIZE;
		
		// initial war
		{ 
			CardValue value = valueInBothHands();		
		
			for(TestPlayer player: testPlayers) {
				CardCollection hand = player.hand;
				Card card = hand.getCardsWithValue(value).getFirstCard();
				
				hand.takeCard(card);
				cardOf.put(player, card);
			}
		}
		cardCount--;
		
		for(TestPlayer player: testPlayers) {
			assertEquals(cardCount,player.hand.size(),"should have 1 less card");
			gameMaster.playCard(player.getNick(), cardOf.get(player));
		}
		
		for(TestPlayer player: testPlayers) 
			assertEquals(cardCount, player.hand.size(),"war: no change in # cards");
		
		// force consecutive wars
		for(int warCount=0; warCount < 3; warCount++) {
			CardValue value = valueInBothHands();
			
			for(TestPlayer player: testPlayers) { 
				List<Card> cards = new ArrayList<>();
			
				// take 3 cards from each hand,  the first with the same value
				Card card = player.hand.getCardsWithValue(value).getFirstCard();
				
				player.hand.takeCard(card);
				
				cards.add( card ); 
				cards.add( player.hand.takeFirstCard() );
				cards.add( player.hand.takeFirstCard() );

				gameMaster.playCards(player.getNick(),cards);
			}
			
			cardCount -= 3;
			
			for(TestPlayer player: testPlayers) {
				assertEquals(cardCount,player.hand.size());
			}
		}
	}
	
	/**
	 * Play the game for several rounds with a test player.
	 * Check that modes are received by both players and
	 * that they have all the when they are in a war.
	 * 
	 * @throws CardGameException
	 * @throws InterruptedException
	 */
	@Test
	void testGameToEnd() throws CardGameException, InterruptedException {
		
		testPlayers.addPlayersTo(gameMaster);
				
		for(int count = 0; ! gameMaster.hasEnded() && count < REPETITIONS; count++) {
			int cardCount = 0;
			String mode = testPlayers.getPlayer(0).getMode();
			
			assertEquals( 
					testPlayers.getPlayer(0).getMode(),
					testPlayers.getPlayer(1).getMode(), 
					"equal mode for both players");
			
			for(TestPlayer player: testPlayers) {
				String nick = player.getNick();
				CardCollection hand = player.hand;
				
				cardCount += player.hand.size();
				
				if(mode == null) 
					gameMaster.playCard(nick, hand.takeFirstCard() );
				else if("WAR".equals( mode.toUpperCase() )) 
					gameMaster.playCards(nick, hand.takeFirstCards(3).asList());
				else 
					fail("unexpected mode: "+mode);
			}				
			
			if(mode == null)
				assertEquals( 52 , cardCount, 
						"unless in a war, card count remains constant");	
		}
	}
	
	

	private CardValue valueInBothHands() {
		for(CardValue value: CardValue.values()) 
			if(
					testPlayers.getPlayer(0).hasCardWithValue(value) && 
					testPlayers.getPlayer(1).hasCardWithValue(value)
				) 
				return value;

		return null;
	}
	
}

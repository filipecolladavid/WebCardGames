package wcg.games;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wcg.WebCardGameTest;
import wcg.games.TestPlayers.TestPlayer;
import wcg.shared.CardGameException;
import wcg.shared.GameInfo;
import wcg.shared.cards.Card;

class GameMasterTest extends WebCardGameTest {
	GameMaster gameMaster;
	TestPlayers testPlayers;
	
	@BeforeEach
	void setUp() throws Exception {
		gameMaster  = new TestGameMaster();
		testPlayers = new TestPlayers(2);
	}
	
	@AfterEach() 
	void tearDown() {
		GameMaster.setExpirationTime(DEFAULT_EXPIRATION_TIME);
	}

	/**
	 * Check if an expiration date is available and can be changed.
	 */
	@Test
	void testExpirationTimeGetterAndSetter() {
		
		long expirationTime = GameMaster.getExpirationTime(); 
		assertEquals( DEFAULT_EXPIRATION_TIME, expirationTime, 
				"default is 10 minutes");
		
		GameMaster.setExpirationTime( SMALL_EXPIRATION_TIME );
		
		assertEquals( SMALL_EXPIRATION_TIME , GameMaster.getExpirationTime() ,
				"should have changed to the previouly set value");
	}
	
	/**
	 * Set a small expiration time and check that game master expires.
	 * @throws InterruptedException 
	 */
	@Test
	void testExpired() throws InterruptedException {
		
		GameMaster.setExpirationTime( SMALL_EXPIRATION_TIME );
		
		TestGameMaster master = new TestGameMaster();
		
		assertFalse( master.expired() , "not expired after creation");
		
		Thread.sleep(SMALL_EXPIRATION_TIME  + 1);
		
		assertTrue( master.expired() , "expired after expiration time" );
	}
	
	/**
	 * Checks that adding players delays expiration
	 * 
	 * @throws InterruptedException
	 * @throws CardGameException
	 */
	@Test
	void testExpired_after() throws InterruptedException, CardGameException {
		
		GameMaster.setExpirationTime( SMALL_EXPIRATION_TIME );
		
		TestGameMaster master = new TestGameMaster();
		
		assertFalse( master.expired() , "not expired fater creation");
		
		Thread.sleep(SMALL_EXPIRATION_TIME / 2);
		
		for(int p=0; p<2; p++) {
			master.addPlayer( testPlayers.getPlayer(p) );
		
			Thread.sleep(SMALL_EXPIRATION_TIME / 2);
		
			assertFalse( master.expired() , "still not expired");
		}
		
		Thread.sleep(SMALL_EXPIRATION_TIME / 2 + 1 );
		
		assertTrue( master.expired() , "expired after expiration time" );
	}
	
	/**
	 * Check game if: show start with game name and not change 
	 */
	@Test
	void testGetGameId() {
		String id = gameMaster.getGameId();
		
		assertTrue(  id.startsWith(TestGameMaster.GAME_NAME) , "id starts with game name");
		assertEquals( id, gameMaster.getGameId(), "id doesnt change");
	}

	/**
	 * Check information produced to describe a game, and how it evolves as players are added
	 * @throws CardGameException 
	 */
	@Test
	void testGetInfo() throws CardGameException {	
		GameInfo info = gameMaster.getInfo();
		int playerCount = 0;
		
		assertNotNull(info);
		assertEquals( gameMaster.getGameId(), info.getGameId() , 
				"should be the same ID" );
		assertEquals( TestGameMaster.GAME_NAME.toUpperCase(), 
				info.getGameName().toUpperCase() , "same name");
		assertEquals( playerCount , info.getPlayersCount() , "no players at start" );
		
		for( TestPlayer player: testPlayers) {
			gameMaster.addPlayer(player);
			
			info = gameMaster.getInfo();
			
			assertEquals( gameMaster.getGameId(), info.getGameId() , 
					"Should be the same ID" );
			assertEquals( TestGameMaster.GAME_NAME.toUpperCase(), 
					info.getGameName().toUpperCase() , "same name");
			assertEquals( ++playerCount , info.getPlayersCount() , 
					"increased player count" );
		}
	}
	
	/**
	 * By default the comparator is the default comparator
	 */
	@Test
	void testGetComparator() {
		
		assertSame( DefaultCardComparator.getInstance() , gameMaster.getCardComparator() );
		
	}
	
	/**
	 * Check you can redefine the comparator
	 */
	@Test
	void testGetComparator_myComparator() {
		class AnotherCardComparator extends DefaultCardComparator {};
		AnotherCardComparator comparator = new AnotherCardComparator();
		class AnotherTestGameMaster extends TestGameMaster {
			
			protected CardComparator getCardComparator() {
				return comparator;
			}
		}
		
		AnotherTestGameMaster rules = new AnotherTestGameMaster();
		
		assertAll(
				() -> assertEquals( 
						AnotherCardComparator.class , 
						rules.getCardComparator().getClass() , "wrong class"),
				() -> assertSame( comparator , rules.getCardComparator() )
			);
	}
	
	/**
	 * Check game deck (by default should be a regular deck)
	 */
	@Test
	void testGetDeck() {
		
		assertEquals( CardCollection.getDeck() , gameMaster.getDeck() , 
				"game deck is the default deck");
	}
	
	/**
	 * Check you can redefine the deck to a full deck
	 */
	@Test
	void testGetDeck_myDeck() {
		class AnotherTestGameMaster extends TestGameMaster {
			
			protected CardCollection getDeck() {
				return CardCollection.getFullDeck();
			}
		}
		AnotherTestGameMaster rules = new AnotherTestGameMaster();
		
		assertEquals( CardCollection.getFullDeck() , rules.getDeck() , 
				"Expected a full deck");	
	}

	/**
	 * Test game is a 2 players game. 
	 * Should accept players until this number is reached.
	 * Repeated players shouln't be accepted.
	 * 
	 * @throws CardGameException 
	 */
	@Test
	void testAddAcceptsPlayers() throws CardGameException {
		assertTrue( gameMaster.acceptsPlayers() , "No player, should accept");
		
		gameMaster.addPlayer(testPlayers.getPlayer(0));
		
		assertThrows(CardGameException.class, () -> {
			gameMaster.addPlayer(testPlayers.getPlayer(0));
		}, "Player P0 already in game");
		
		
		assertTrue( gameMaster.acceptsPlayers() , "One player, should accept");
		
		gameMaster.addPlayer(testPlayers.getPlayer(1));
		
		assertFalse( gameMaster.acceptsPlayers() , "With players shouldn' accept");
		
		assertThrows(CardGameException.class, () -> {
			gameMaster.addPlayer(testPlayers.new TestPlayer("P2"));
		}, "No more players accepted in this game");

	}
	
	/**
	 * Check if the nicks of all added players are reported 
	 * @throws CardGameException
	 */
	@Test
	void testGetPlayerNicks() throws CardGameException {	
		testPlayers.addPlayersTo(gameMaster);
		
	
		List<String> nicks = gameMaster.getPlayerNicks();
		
		assertTrue( nicks.contains(testPlayers.getPlayer(0).getNick()) , "Nick P0 expected");
		assertTrue( nicks.contains(testPlayers.getPlayer(1).getNick()) , "Nick P1 expected");
		assertFalse( nicks.contains("non-player") , "Unexpected nick");
	}
	
	/**
	 * Check mode setter and getter.
	 */
	@Test
	void testGetSetMode() {
		final String mode = "some mode";
		
		assertNull( gameMaster.getMode() , "initially null");
		
		gameMaster.setMode(mode);
		
		assertEquals( mode , gameMaster.getMode());
	}
	
	/**
	 * Check hand from players. Should have 3 cards each.
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testGetPlayerHand() throws CardGameException {
		testPlayers.addPlayersTo(gameMaster);
		
		for(String nick: gameMaster.getPlayerNicks()) {
			CardCollection hand = gameMaster.getHand(nick) ;
			
			assertNotNull( hand );
			assertEquals( 3 , hand.size() , "3 cards for test game");
		}
	}

	/**
	 * A non added player must raise an exception
	 *  
	 * @throws CardGameException
	 */
	@Test
	void testInvalidPlayer() throws CardGameException {
		Card card = CardCollection.getDeck(gameMaster.getCardComparator()).getRandomCard();
		
		testPlayers.addPlayersTo(gameMaster);
		
		CardGameException error = assertThrows(CardGameException.class,
				() -> gameMaster.playCard( "invalid player", card ) );
		assertTrue( error.getMessage().toLowerCase().contains("not in this game")); 
	}
	
	@Test
	void testPlayingBeforeReceivingCards() throws CardGameException {
		Card card = CardCollection.getDeck(gameMaster.getCardComparator()).getRandomCard();
		TestPlayer player = testPlayers.getPlayer(0);
		
		gameMaster.addPlayer(player);
	
		CardGameException error = assertThrows(CardGameException.class,
				() -> gameMaster.playCard( player.getNick(), card ) );
		assertTrue( error.getMessage().toLowerCase().contains("not in playing stage")); 

	}
	
	/**
	 * Try playing a null player 
	 * @throws CardGameException
	 */
	@Test
	void testPlayWithNullPlayer() throws CardGameException {
		Card card = CardCollection.getDeck(gameMaster.getCardComparator()).getRandomCard();

		testPlayers.addPlayersTo(gameMaster);
		
		CardGameException error = assertThrows(CardGameException.class,
				() -> gameMaster.playCard(null, card));
		
		assertTrue( error.getMessage().toLowerCase().contains("nick") );

	}
	
	/**
	 * Try playing a null card 
	 * @throws CardGameException
	 */
	@Test
	void testPlayWithNullClard() throws CardGameException {
		testPlayers.addPlayersTo(gameMaster);
		
		CardGameException error = assertThrows(CardGameException.class,
				() -> gameMaster.playCard(testPlayers.getPlayer(0).getNick(),null));
		
		assertTrue( error.getMessage().toLowerCase().contains("invalid card") );
		
	}
	
	
	/**
	 * Try playing a card not in hand.
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testPlayACardNotInHand() throws CardGameException {
		testPlayers.addPlayersTo(gameMaster);
		
		for(TestPlayer player: testPlayers) {
				CardGameException error = assertThrows(CardGameException.class,
						() -> gameMaster.playCard(player.getNick(), getCardNotIn(player.hand)));
				
				assertTrue( error.getMessage().toLowerCase().contains("invalid card") );
				
				// advance turn to next player
				gameMaster.playCard(player.getNick(), player.hand.getFirstCard() );
		}
	}
	
	/**
	 * Try playing out of turn.
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testPlayerNotInTurn() throws CardGameException {
		TestPlayer player;
		
		testPlayers.addPlayersTo(gameMaster);
		
		player = getPlayerNotInTurn(gameMaster);
		CardGameException error = assertThrows(CardGameException.class,
				() -> gameMaster.playCard(player.getNick(), player.hand.getFirstCard() ) );
				
		assertTrue( error.getMessage().toLowerCase().contains("not your turn") );
				
	}
	
	/**
	 * Play a game to the end and check if 
	 * game is marked as having ended
	 * winner is propagated to players
	 * and they play all their cards
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testEndOfGame() throws CardGameException {
		testPlayers.addPlayersTo(gameMaster);
		
		for(int count = 0; count < 6; count++) {
			TestPlayer		player	= testPlayers.getPlayerWithTurn(gameMaster);
			String 			nick	= player.getNick();
			CardCollection	hand 	= player.hand;
			// players pick cards with different but equally naive strategies
			Card			card	= nick.endsWith("0") ? hand.getHighestCard() : hand.getLowestCard();
		
			hand.takeCard(card);
		
			gameMaster.playCard(nick, card);
		}
		
		assertAll(
				() -> assertEquals( 3 , gameMaster.getRoundsCompleted() , "should be 3 completed rounds"),
				() -> assertTrue( gameMaster.hasEnded() , "Should end after 3 rounds"),
				() -> {
					for(TestPlayer player: testPlayers)
						assertEquals( gameMaster.getWinner() , player.winner , "winner sent to player");
				},
				() -> {
					for(TestPlayer player: testPlayers)
						assertEquals(0, gameMaster.getHand(player.getNick()).size(), "all cards played");
				}
				);
		
	}
	
	
	/*----------------------------------------------------------------------*\
	 * 							Helper methods 								* 
	\*----------------------------------------------------------------------*/
	
	
	/**
	 * The first player that doesn't have the turn
	 * @param gameMaster
	 * @return
	 */
	TestPlayer getPlayerNotInTurn(GameMaster gameMaster) {
		for(TestPlayer player: testPlayers)
			if(! player.getNick().equals(gameMaster.getNickWithTurn())) 
				return player;
		
		return null;
	}
	

	/**
	 * Helper method to pick a card not in given collection 
	 * @param hand
	 * @return
	 */
	Card getCardNotIn(CardCollection hand) {
		for(Card card: CardCollection.getDeck())
			if( ! hand.containsCard(card))
				return card;
		return null;
	}
	
	
	@Test
	void testGetRoundsCompleted() throws CardGameException {
		TestPlayer player;
		testPlayers.addPlayersTo(gameMaster);
		
		int completedRounds;
		
		for(completedRounds = 0; completedRounds < 3; completedRounds ++) 
			for(int playerCount = 0; playerCount < 2; playerCount++) {
			
				assertEquals(completedRounds, gameMaster.getRoundsCompleted() );
		
				player = testPlayers.getPlayerWithNick(gameMaster.getNickWithTurn());
			
				gameMaster.playCard(player.getNick() , player.hand.takeFirstCard() );
				
			}
		
		assertEquals(completedRounds, gameMaster.getRoundsCompleted() );
	}
	
	/**
	 * Check that newCardCollection() produces an empty instance 
	 * with the expected comparator
	 */
	@Test
	void testNewCardCollection() {
		 CardCollection collection = gameMaster.newCardCollection();
		 
		 assertNotNull(collection , "collection was created");
		
		 assertEquals( 0 , collection.size(), "should be empty");
		 
		 assertSame(
				 collection.getCardComparator(),
				 DefaultCardComparator.getInstance(),
				 " new collection should have the default comparator"); 
		 
	}
	
	/**
	 * Check suit to follow. Initially should be null.
	 * After playing a card the suit to follow should be 
	 * that of the first played card.
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testGetSuitToFollow() throws CardGameException {
		TestPlayer player = testPlayers.getPlayer(0);
		
		testPlayers.addPlayersTo(gameMaster);
		
		Card card = player.hand.takeFirstCard();
		
		assertNull(gameMaster.getSuitToFollow());
		
		gameMaster.playCard( player.getNick() , card );
		
		assertEquals( card.getSuit() , gameMaster.getSuitToFollow() );
				
	}
	
}

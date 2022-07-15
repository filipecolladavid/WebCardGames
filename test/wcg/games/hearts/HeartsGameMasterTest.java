package wcg.games.hearts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import wcg.games.CardCollection;
import wcg.games.CardComparator;
import wcg.games.DefaultCardComparator;
import wcg.games.GamePlayingStrategy;
import wcg.games.TestPlayers;
import wcg.games.TestPlayers.TestPlayer;
import wcg.shared.CardGameException;
import wcg.shared.cards.Card;
import wcg.shared.cards.CardSuit;

/**
 * Test HEARTS GameMaster
 */
class HeartsGameMasterTest {
	HeartsGameMaster gameMaster;
	TestPlayers testPlayers; 
	
	@BeforeEach
	void setUp() throws Exception {
		gameMaster = new HeartsGameMaster();
		testPlayers = new TestPlayers(4);
	}
	
	/**
	 * Check name: HEARTS
	 */
	@Test
	void testGetGameName() {
		assertEquals( "HEARTS" , gameMaster.getGameName() );
	}

	/**
	 * Check number of players: 4
	 */
	@Test
	void testGetNumberOfPlayers() {
		assertEquals( 4, gameMaster.getNumberOfPlayers() );
	}

	/**
	 * Check cards per player: 13
	 */
	@Test
	void testGetCardsPerPlayer() {
		assertEquals( 13, gameMaster.getCardsPerPlayer() );
	}

	/**
	 * Check with turns: true
	 */
	@Test
	void testIsWithTurns() {
		assertTrue( gameMaster.isWithTurns() );
	}

	/**
	 * Check if first player has the turn in first round.
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testInitialTurn() throws CardGameException {
		testPlayers.addPlayersTo(gameMaster);
		
		assertTrue(   testPlayers.getPlayer(0).hasTurn(), 
				"turn should be in bot 0");
		assertFalse( testPlayers.getPlayer(1).hasTurn(), 
				"turn shouldn't be in bot 1");
	}
	
	/**
	 * Try playing a heart in first round and an exception should be raised
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testPlayHeartsInFirstTrick() throws CardGameException {
		testPlayers.addPlayersTo(gameMaster);
				
		Card someHeart = testPlayers.getPlayer(0).hand.getCardsFromSuit(CardSuit.HEARTS).getFirstCard();
		
		CardGameException error = assertThrows(CardGameException.class, 
				() -> gameMaster.playCard(testPlayers.getPlayer(0).getNick(), someHeart) );
		
		assertTrue( error.getMessage().toLowerCase().contains("cannot play hearts"));
	}
	
	/**
	 * Try to play a card without following suit an exception must be raised
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testPlayInvalidSuit() throws CardGameException {
		
		testPlayers.addPlayersTo(gameMaster);
		
		Card card = testPlayers
				.getPlayer(0)
				.hand
				.getHighestCard();
		gameMaster.playCard(testPlayers.getPlayer(0).getNick(), card);
		
		Card other = testPlayers
				.getPlayer(1)
				.hand
				.getCardsNotFromSuit(card.getSuit())
				.getFirstCard();
		
		assertThrows( CardGameException.class,
				() -> gameMaster.playCard(testPlayers.getPlayer(1).getNick(), other),
				" must follow suit ");
	}
	
	/**
	 * Check that initially all player have 0 points. 
	 * Point computation is also checked by testCompleteGame().
	 *  
	 * @throws CardGameException
	 */
	@Test
	void testGetPoints() throws CardGameException {
		testPlayers.addPlayersTo(gameMaster);
		
		for(TestPlayer player: testPlayers) 
			assertEquals( 0 , gameMaster.getRoundPoints(player.getNick()) , 
					"poinsts start with 0");
	}

	/**
	 * At the first round the first player should have the turn to play
	 * and none of the other player should have it.
	 * If the last player plays the highest card the it must have the turn
	 * and not any of the other players.
	 * Repeat several times, ignore cases where a card to take suit by
	 * player 3 cannot be select, or any of the other player cannot play a
	 * uit different from hearts
	 * 
	 * @throws CardGameException
	 */
	@RepeatedTest(10)
	void testInitialTurnInRound() throws CardGameException {
		CardComparator comparator = DefaultCardComparator.getInstance();
		
		testPlayers.addPlayersTo(gameMaster);
		
		assertTrue( testPlayers.getPlayer(0).hasTurn() , "first player has turn");
		for(int i=1; i<4; i++)
			assertFalse( testPlayers.getPlayer(i).hasTurn() , "other players don't");
		
		Card maxCard = null;
		for(int i=0; i<3; i++) {
			TestPlayer player = testPlayers.getPlayer(i); 
			CardCollection validCards =  i == 0 ? 
					player.hand.getCardsNotFromSuit(CardSuit.HEARTS) :
					player.hand.getCardsFromSuit(gameMaster.getSuitToFollow());
			Card card = validCards.getLowestCard();
			
			if(card == null)
				return; // could not pick non hearts; ignore this test
			
			gameMaster.playCards(player.getNick(), Arrays.asList(card));
			
			if(maxCard == null || comparator.compare(card, maxCard) > 0)
				maxCard = card;
		}	
		{ 
			TestPlayer player = testPlayers.getPlayer(3);
			CardCollection validCards = player.hand
					.getCardsFromSuit(gameMaster.getSuitToFollow());
			Card card = validCards.getHighestCard();
			
			if(card == null || comparator.compare(card, maxCard) < 0)
				return; // not able to pick a card and take the hand; ignore
			
			gameMaster.playCards(player.getNick(), Arrays.asList(card));
		}
		
		for(int i=0; i<3; i++) 
			assertFalse( testPlayers.getPlayer(i).hasTurn() , "other players don't:");
		assertTrue( testPlayers.getPlayer(3).hasTurn() , "last player has turn");		
	}
	
	/**
	 * Check that game strategy alternate between 
	 * {@link HeartsGameSimpleStrategy} and {@link HeartsGameStrategy}.
	 */
	@Test
	void testGetCardGameStrategy() {
		GamePlayingStrategy strategy = gameMaster.getCardGameStrategy();
		
		assertNotNull(strategy , "a strategy expected");
		assertTrue( strategy instanceof HeartsGameSimpleStrategy , 
				"simple strategy expected");
		
		assertTrue( gameMaster.getCardGameStrategy() instanceof HeartsGameStrategy,
				"different strategy expected");
		
		assertTrue( gameMaster.getCardGameStrategy() instanceof HeartsGameSimpleStrategy,
				"back to simple strategy");
		
		assertTrue( gameMaster.getCardGameStrategy() instanceof HeartsGameStrategy,
				"and repeats");
	}

	/**
	 * Play a game to the end with a naive strategy and check that game 
	 * is marked as ended only at the last turn, and a winner is produced,
	 * points account for all the 13 hearts and the winner is the player with
	 * most points (less hearts, each heart is a negative points). 
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testCompleteGame() throws CardGameException {
		testPlayers.addPlayersTo(gameMaster);
		
		for(int round = 0; round < 13 ; round++) {
			for(int p=0; p<4; p++) {
				TestPlayer player = testPlayers.getPlayerWithTurn(gameMaster);
				CardSuit suit = gameMaster.getSuitToFollow();
				CardCollection hand = player.hand;
				CardCollection nonHearts = 
						hand.getCardsNotFromSuit(CardSuit.HEARTS);
				CardCollection nonHeartsPossible = 
						nonHearts.isEmpty() ? hand : nonHearts;
				CardCollection validCards = round < 2 ?
						nonHeartsPossible : hand;
				
				validCards = suit == null ? validCards :
					validCards.getCardsFromSuit(suit);
				
				validCards = validCards.isEmpty() ? nonHeartsPossible : validCards;
				
				Card card = validCards.getHighestCard();
			
				hand.takeCard(card);
			
				hand.takeCard(card);
				gameMaster.playCards(player.getNick(), Arrays.asList(card));
			}
			
			String hasTurn = gameMaster.initialTurnInRound();
			int withHartsInRound = 0;
			for(TestPlayer player: testPlayers) {
				String nick = player.getNick();
				int points = gameMaster.getRoundPoints(nick);
				
				if(points < 0) {
					withHartsInRound++;
					assertEquals(hasTurn,nick, 
							"if has negative points it must have the turn");
				}
			}
			assertTrue( withHartsInRound <= 1 , 
					"at most a player has hearts in a turn");
			
			if( round < 12) 
				assertFalse( gameMaster.hasEnded() , "game not yet completed");
			else
				assertTrue( gameMaster.hasEnded() , "13 round completed");
		}
		
		assertNotNull(  gameMaster.getWinner() , "some winner espected");
		
		int totalPoints = 0;
		int maxPoints = Integer.MIN_VALUE;
		String winner = null;
		Map<String,Integer> playerPoints = testPlayers.getPlayer(0).points;
		
		for(TestPlayer player: testPlayers) {
			String nick = player.getNick();
			int points  = playerPoints.get(nick);
			
			assertTrue( points <= 0 , "hearts cound as negative points" );
			
			totalPoints += points;
			if(points > maxPoints) {
				maxPoints = points;
				winner    = nick;
			}
		}
		
		assertEquals( -13 , totalPoints , "all hearts are accounted for");
		
		assertEquals( winner , gameMaster.getWinner() , 
				"winner must be player with less hearts");
	}

}

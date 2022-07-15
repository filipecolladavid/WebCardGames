package wcg.games.war;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static wcg.shared.cards.CardSuit.*;
import static wcg.shared.cards.CardValue.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wcg.WebCardGameTest;
import wcg.games.CardCollection;
import wcg.games.GameBot;
import wcg.shared.CardGameException;
import wcg.shared.cards.Card;

/**
 * Test WarGameStrategy by picking cards in non war and war mode
 */
class WarGameStrategyTest extends WebCardGameTest {
	WarGameStrategy strategy;
	GameBot bot;
	WarGameMaster master; 
	
	
	@BeforeEach
	void setUp() throws Exception {
		strategy = new WarGameStrategy();
		master = new WarGameMaster();
		bot = new GameBot(master);
	}

	/**
	 * If the player has a few cards and is not in war mode 
	 * should pick just the first
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testPickCards_notWar() throws CardGameException {
		Card aceOfClubs		= new Card(CLUBS,	ACE);
		Card kingOfSpades	= new Card(SPADES,	KING);
		Card twoOfDiamonds	= new Card(DIAMONDS,V02);
		
		CardCollection cards = new CardCollection(Arrays.asList(
				aceOfClubs, kingOfSpades, twoOfDiamonds ));
		
		bot.setHand(  cards  );
		
		
		assertEquals( Arrays.asList(aceOfClubs), strategy.pickCards(bot) );
	}

	/**
	 * If the player has a few cards and is in war mode 
	 * should pick the first 3 cards
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testPickCards_War() throws CardGameException {
		Card aceOfClubs		= new Card(CLUBS,	ACE);
		Card kingOfSpades	= new Card(SPADES,	KING);
		Card twoOfDiamonds	= new Card(DIAMONDS,V02);
		Card threeOfHearts	= new Card(HEARTS,	V03);
		
		CardCollection cards = new CardCollection(Arrays.asList(
				aceOfClubs, kingOfSpades, twoOfDiamonds, threeOfHearts ));
		
		List<Card> expected = Arrays.asList(aceOfClubs,kingOfSpades,twoOfDiamonds);
		
		bot.setHand(  cards  );
		bot.setMode(  "War"  );
		
		assertEquals(expected, strategy.pickCards(bot) );
	}
	
}

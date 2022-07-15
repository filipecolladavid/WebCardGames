package wcg.games.hearts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import wcg.games.CardCollection;
import wcg.games.GameBot;
import wcg.games.TestPlayers;
import wcg.shared.CardGameException;
import wcg.shared.cards.Card;
import wcg.shared.cards.CardSuit;

class HeartsGameSimpleStategyTest {
	
	HeartsGameSimpleStrategy strategy;
	
	HeartsGameMaster master; 
	TestPlayers players;
	
	List<GameBot> bots;
	
	@BeforeEach
	void setUp() throws Exception {
		strategy = new HeartsGameSimpleStrategy();
		master = new HeartsGameMaster();
		players = new TestPlayers(4);
		CardCollection deck = CardCollection.getDeck().shuffle();
		
		bots = new ArrayList<>();
		for(int i=0; i<4; i++) {
			GameBot bot = new GameBot(master);
			// master.addPlayer(bot);
			bot.setHand(deck.takeFirstCards(13));
			bots.add(bot);
		}
		
	}

	
	/**
	 * Check if the bots play hearts in the first round
	 * 
	 * @throws CardGameException
	 */
	@RepeatedTest(10)
	void testPickCards_firstCards() throws CardGameException {

		for(GameBot bot: bots) {
			List<Card> cards = strategy.pickCards(bot);

			assertEquals( 1 , cards.size(), "can play just one card");

			Card card = cards.get(0);

			assertTrue( card.getSuit() != CardSuit.HEARTS , 
				"cannot play hearts if has other cards to play");
		}
	}

}

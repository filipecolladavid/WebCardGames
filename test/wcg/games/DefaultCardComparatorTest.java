package wcg.games;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static wcg.shared.cards.CardSuit.DIAMONDS;
import static wcg.shared.cards.CardSuit.SPADES;
import static wcg.shared.cards.CardValue.ACE;
import static wcg.shared.cards.CardValue.V02;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import wcg.shared.cards.Card;
import wcg.shared.cards.CardSuit;
import wcg.shared.cards.CardValue;

/**
 * Test default comparator with regular cards and jokers
 */
class DefaultCardComparatorTest {

	DefaultCardComparator cardComparator;
	
	@BeforeEach
	public void setUp() {
		cardComparator = DefaultCardComparator.getInstance();
	}
	
	/**
	 * Compare on pairs of regular cards.
	 * Card suits and values to construct cards 
	 * are in  ascending order (card1 < card2)
	 * 
	 * @param suit1 suit of 1st card
	 * @param value1 value of 1st card
	 * @param suit2 suit of 2nd card
	 * @param value2 value of2nd card
	 */
	@ParameterizedTest
    @CsvSource({
    	"CLUBS, V02, CLUBS, V03",
    	"CLUBS, KING, CLUBS, ACE",
    	"CLUBS, ACE, DIAMONDS, V02",
    	"DIAMONDS, V02, DIAMONDS, V03",
    	"DIAMONDS, V02, DIAMONDS, V04",
    	"DIAMONDS, QUEEN, DIAMONDS, KING",
    	"DIAMONDS, QUEEN, DIAMONDS, JACK",
    	"DIAMONDS, JACK, DIAMONDS, KING",
    	"DIAMONDS, ACE, HEARTS, V02",
    	"HEARTS, V02, HEARTS, V03",
    	"HEARTS, V03, HEARTS, V04",
    	"HEARTS, V03, HEARTS, ACE",
    	"HEARTS, ACE, SPADES, V02",
    	"SPADES, V02, SPADES, JACK",
    	"SPADES, QUEEN, SPADES, ACE"
    })
	void testCompareRegularCards(
			CardSuit suit1, CardValue value1, 
			CardSuit suit2, CardValue value2) {
		Card card1 = new Card(suit1,value1);
		Card card2 = new Card(suit2,value2);
		
		assertTrue(cardComparator.compare(card1,card2) < 0);
	}
	
	/**
	 * Test compare with jokers - cards with null value
	 */
	@Test
	void testCompareJoker() {
		Card regularCard1 = new Card(DIAMONDS,ACE);
		Card regularCard2 = new Card(SPADES,V02);
		Card joker1 = new Card(DIAMONDS,null);
		Card joker2 = new Card(SPADES,null);
		Card joker5 = new Card(null,null);
		
		assertTrue ( cardComparator.compare(regularCard1,joker1) > 0);
		
		assertAll("jokers are smaller than same suit cards",
				() -> assertTrue ( cardComparator.compare(regularCard1,joker1) > 0),
				() -> assertTrue ( cardComparator.compare(joker1,regularCard1) < 0),
				() -> assertTrue ( cardComparator.compare(regularCard2,joker2) > 0),
				() -> assertTrue ( cardComparator.compare(joker2,regularCard2) < 0),
				() -> assertTrue ( cardComparator.compare(joker1,joker2) < 0),
				() -> assertTrue ( cardComparator.compare(joker2,joker1) > 0),
				() -> assertTrue ( cardComparator.compare(joker5,joker1) < 0),
				() -> assertTrue ( cardComparator.compare(joker1,joker5) > 0)
				);
		 
	}
	
	/**
	 * Verifies if the class is behaving properly as Singleton	
	 */
	@Test
	void testNewInstances() {
		DefaultCardComparator comparator = DefaultCardComparator.getInstance();
		
		assertEquals("comparators should be of the same instance", cardComparator, comparator);
	}


}

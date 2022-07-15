package wcg.shared.cards;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static wcg.shared.cards.CardSuit.CLUBS;
import static wcg.shared.cards.CardSuit.DIAMONDS;
import static wcg.shared.cards.CardSuit.HEARTS;
import static wcg.shared.cards.CardSuit.SPADES;
import static wcg.shared.cards.CardValue.ACE;
import static wcg.shared.cards.CardValue.V02;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CardTest {

	@Test
	void testCreate() {
		
		for(CardSuit suit: CardSuit.values())
			for(CardValue value: CardValue.values()) {
				Card card = new Card(suit,value);
				
				assertEquals(suit,card.getSuit());
				assertEquals(value,card.getValue());
			}
		
	}
	
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
	void testCompare(CardSuit suit1, CardValue value1, CardSuit suit2, CardValue value2) {
		Card card1 = new Card(suit1,value1);
		Card card2 = new Card(suit2,value2);
		
		assertTrue(card1.compareTo(card2) < 0);
	}
	
	/**
	 * Test compare with jokers
	 */
	@Test
	void testCompareJoker() {
		Card regularCard1 = new Card(DIAMONDS,ACE);
		Card regularCard2 = new Card(SPADES,V02);
		Card joker1 = new Card(DIAMONDS,null);
		Card joker2 = new Card(SPADES,null);
		Card joker5 = new Card(null,null);
		
		assertAll("jokers are smaller than same suit cards",
				() -> assertTrue ( regularCard1.compareTo(joker1) > 0),
				() -> assertTrue ( joker1.compareTo(regularCard1) < 0),
				() -> assertTrue ( regularCard2.compareTo(joker2) > 0),
				() -> assertTrue ( joker2.compareTo(regularCard2) < 0),
				() -> assertTrue ( joker1.compareTo(joker2) < 0),
				() -> assertTrue ( joker2.compareTo(joker1) > 0),
				() -> assertTrue ( joker5.compareTo(joker1) < 0),
				() -> assertTrue ( joker1.compareTo(joker5) > 0)
				);
		 
	}
	
	
	/**
	 * Test {@code toString()} on regular cards (not jokers)
	 * 
	 * @param suit of card
	 * @param value of card
	 * @param expected string representation
	 */
	@ParameterizedTest
    @CsvSource({
    	"CLUBS, V02, 2♣",
    	"CLUBS, KING, K♣",
    	"CLUBS, ACE, A♣",
    	"CLUBS, V10, 10♣",
    	"CLUBS,, *♣",
    	"DIAMONDS, V02, 2♦",
    	"DIAMONDS, V09, 9♦",
    	"DIAMONDS, V10, 10♦",
    	"DIAMONDS, QUEEN, Q♦",
    	"DIAMONDS, JACK, J♦",
    	"DIAMONDS, ACE, A♦",
    	"DIAMONDS,, *♦",
    	"HEARTS, V03, 3♥",
    	"HEARTS, V07, 7♥",
    	"HEARTS, QUEEN, Q♥",
    	"HEARTS, KING, K♥",
    	"HEARTS,, *♥",
    	"SPADES, V02, 2♠",
    	"SPADES, V10, 10♠",
    	"SPADES, KING, K♠",
    	"SPADES, ACE, A♠",
    	"SPADES,, *♠"
    	
    })
    void testToString(CardSuit suit, CardValue value, String expected) {
    	String name = new Card(suit,value).toString();
    	assertEquals(expected, name);
    }
	
	/**
	 * Test {@code toString()} on jokers - cards with a null value;
     */
	void testToStringJoker() {
	
		assertEquals( "*♣" , new Card(CLUBS,null));
		assertEquals( "*♦" , new Card(DIAMONDS,null));
		assertEquals( "*♥" , new Card(HEARTS,null));
		assertEquals( "*♠" , new Card(SPADES,null));
	}

}

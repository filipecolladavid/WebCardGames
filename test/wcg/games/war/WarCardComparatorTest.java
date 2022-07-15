package wcg.games.war;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import wcg.shared.cards.Card;
import wcg.shared.cards.CardSuit;
import wcg.shared.cards.CardValue;

class WarCardComparatorTest {

	WarCardComparator cardComparator;
	
	@BeforeEach
	public void setUp() {
		cardComparator = new WarCardComparator();
	}
	
	@ParameterizedTest
    @CsvSource({
    	"CLUBS, V02, CLUBS, V03",
    	"CLUBS, KING, CLUBS, ACE",
    	"DIAMONDS, V02, CLUBS, ACE",
    	"DIAMONDS, V02, DIAMONDS, V03",
    	"DIAMONDS, V02, DIAMONDS, V04",
    	"DIAMONDS, QUEEN, DIAMONDS, KING",
    	"DIAMONDS, JACK, DIAMONDS, QUEEN",
    	"DIAMONDS, JACK, DIAMONDS, KING",
    	"DIAMONDS, V02, HEARTS, ACE",
    	"HEARTS, V02, HEARTS, V03",
    	"HEARTS, V03, HEARTS, V04",
    	"HEARTS, V03, HEARTS, ACE",
    	"HEARTS, V02, SPADES, ACE",
    	"SPADES, V02, SPADES, JACK",
    	"SPADES, QUEEN, SPADES, ACE"
    })
	void testCards(CardSuit suit1, CardValue value1, CardSuit suit2, CardValue value2) {
		Card card1 = new Card(suit1,value1);
		Card card2 = new Card(suit2,value2);
		
		assertTrue(cardComparator.compare(card1,card2) < 0);
	}

}

package wcg.games;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static wcg.shared.cards.CardSuit.CLUBS;
import static wcg.shared.cards.CardSuit.DIAMONDS;
import static wcg.shared.cards.CardSuit.HEARTS;
import static wcg.shared.cards.CardSuit.SPADES;
import static wcg.shared.cards.CardValue.ACE;
import static wcg.shared.cards.CardValue.JACK;
import static wcg.shared.cards.CardValue.KING;
import static wcg.shared.cards.CardValue.QUEEN;
import static wcg.shared.cards.CardValue.V02;
import static wcg.shared.cards.CardValue.V03;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import wcg.WebCardGameTest;
import wcg.shared.cards.Card;
import wcg.shared.cards.CardSuit;
import wcg.shared.cards.CardValue;

class CardCollectionTest extends WebCardGameTest {
	static final CardComparator comparator = DefaultCardComparator.getInstance();

	static final int SUITS_COUNT = CardSuit.values().length;
	static final int VALUES_COUNT = CardValue.values().length;
	static final int DECK_CARD_COUNT = SUITS_COUNT * VALUES_COUNT;

	static final List<Card> SOME_CARDS = Arrays.asList(
			new Card(SPADES, ACE), 
			new Card(DIAMONDS, QUEEN),
			new Card(CLUBS, V03));

	CardCollection cards, someCards, deck;

	@BeforeEach
	void setUp() throws Exception {
		cards = new CardCollection(comparator);

		try {
			someCards = new CardCollection(comparator, SOME_CARDS);
		} catch (Exception e) {
		}

		try {
			deck = CardCollection.getDeck();
		} catch (Exception e) {
		}
	}

	/**
	 * Check that all cards are created, all different, covering all suits and
	 * values
	 */
	@Test
	void testGetDeck() {
		Set<Card> cards = new HashSet<>();
		Map<CardSuit, Integer> suits = new HashMap<>();
		Map<CardValue, Integer> values = new HashMap<>();
		CardCollection deck = CardCollection.getDeck();

		assertEquals(DECK_CARD_COUNT, deck.size(), "");

		for (Card card : deck) {
			cards.add(card);

			suits.merge(card.getSuit(), 1, (x, y) -> x + y);
			values.merge(card.getValue(), 1, (x, y) -> x + y);
		}

		assertEquals(DECK_CARD_COUNT, cards.size(), "all cards are different");
		assertEquals(SUITS_COUNT, suits.keySet().size(), "cards of all 4 suits");
		assertEquals(VALUES_COUNT, values.keySet().size(), "cards of all 13 values");

		for (CardSuit suit : CardSuit.values())
			assertEquals(CardValue.values().length, suits.get(suit), "13 cards of each suit");

		for (CardValue value : CardValue.values())
			assertEquals(CardSuit.values().length, values.get(value), "4 cards of each value");
	}
	
	/**
	 * Check full deck. Should have the same cards plus 2 jokers
	 */
	@Test
	void testGetFullDeck() {
		CardCollection fullDeck = CardCollection.getFullDeck();
		CardCollection deck = CardCollection.getDeck();
		
		for(Card card: deck) {
			assertNotNull( fullDeck.takeCard(card) , card+" expected");
		}
		assertEquals( 2 , fullDeck.size() , "2 jokers should remain");
		
		for(Card card: fullDeck)
			assertNull( card.getValue() , "jokers have null value");
	}

	@Test
	void testCardCollectionComparatorOfCard() {
		assertNotNull(cards);
	}

	@Test
	void testCardCollectionComparatorOfCardListOfCard() {
		assertNotNull(someCards);
	}

	@Test
	void testIsEmpty() {
		assertTrue(cards.isEmpty(), "Is empty after instanctiation");

		cards.addCard(new Card(SPADES, ACE));

		assertFalse(cards.isEmpty(), "Not empty after adding card");
	}

	/**
	 * Collections should be empty after being cleared
	 */
	@Test
	void testClearCards() {

		assertAll(() -> {
			assertSame(cards, cards.clearCards(), "return the same collection for chaining");

			assertTrue(cards.isEmpty(), "should be empty after being cleared");
		}, () -> {
			assertSame(someCards, someCards.clearCards(), "return the same collection for chaining");

			assertTrue(someCards.isEmpty(), "should be empty after being cleared");
		}, () -> {
			assertSame(deck, deck.clearCards(), "return the same collection for chaining");

			assertTrue(deck.isEmpty(), "should be empty after being cleared");
		});
	}

	/**
	 * Check empty collections have no cards and adding a cards increases size.
	 * Method should be usable with chaining
	 */
	@Test
	void testSize_and_AddCard() {
		assertEquals(0, cards.size(), "no cards initially");

		assertSame(cards, cards.addCard(new Card(SPADES, ACE)), "should return itself for chaining");

		assertEquals(1, cards.size(), "a card was added");

		cards.addCard(new Card(HEARTS, ACE)).addCard(new Card(DIAMONDS, KING));

		assertEquals(3, cards.size(), "2 more cards were added");

	}

	/**
	 * Check empty collections have no cards and adding a cards increases size.
	 * Method should be usable with chaining
	 */
	@Test
	void testSize_and_AddCard2() {
		assertEquals(0, cards.size(), "no cards initially");

		assertSame(cards, cards.addCard(SPADES, ACE), "should return itself for chaining");

		assertEquals(1, cards.size(), "a card was added");

		cards.addCard(HEARTS, ACE).addCard(DIAMONDS, KING);

		assertEquals(3, cards.size(), "2 more cards were added");

	}

	/**
	 * Adding several cards increases the size accordingly
	 */
	@Test
	void testAddAllCards() {
		assertSame(cards, cards.addAllCards(SOME_CARDS), "should return itself for chaining");

		assertEquals(SOME_CARDS.size(), cards.size());
	}

	/**
	 * Merge with another collection, returns the context collection
	 */
	@Test
	void testMerge() {
		assertSame(cards, cards.addCardCollection(someCards), "should return itself for chaining");

		assertEquals(SOME_CARDS.size(), cards.size());
	}

	@ParameterizedTest
	@MethodSource("allCardsStream")
	void testContainsCard(Card card) {

		assertSame(someCards.containsCard(card), (SOME_CARDS.contains(card)));
	}

	/**
	 * Check list is equivalent to that that created the sequence. Clear the list to
	 * check that it doesn't affect the sequence.
	 */
	@Test
	void testAsList() {
		List<Card> list = someCards.asList();

		assertEquals(SOME_CARDS, list);

		list.clear();

		assertEquals(SOME_CARDS, someCards.asList());
	}
	

	@Test
	void testRemoveDuplicates() {
		int size = someCards.size();
		
		someCards.addCardCollection(someCards);
		assertEquals( 2*size , someCards.size());
		
		someCards.removeRepeated();
		assertEquals( size , someCards.size());
	}
	
	@Test
	void testRemoveDuplicates_shuffle() {
		int size = deck.size();
		
		deck.addCardCollection(someCards);
		assertEquals( size + someCards.size() , deck.size());
		
		deck.shuffle();
		
		deck.removeRepeated();
		assertEquals( size , deck.size());
	}

	
	@Nested 
	class ShuffleTest {
		/**
		 * Shuffle a collection and check the number of pairs of cards in increasing
		 * order
		 */
		@Test
		void testRepetitons() {
			int repetitions = 100;
			int up = 0;
				
			for(int t=0; t<repetitions; t++) {
				deck = CardCollection.getDeck();
						;
				deck.shuffle();
				
				for(int c=0; c< MAX_CARDS /2; c++) {
					Card a = deck.takeFirstCard();
					Card b = deck.takeFirstCard();
				
					if(a.compareTo(b) < 0)
						up++;
				}
			}
				
			assertTrue(up / repetitions > MAX_CARDS / 8);

		}
		
		/**
		 * shuffle() should return itself for chaining 
		 */
		@Test
		void testChainable() {
			
			assertSame(deck, deck.shuffle(), "should return itself for chaining");
		}
	}
	

	/**
	 * Get first card from a collection, check it is the right one and the
	 * collection's size doesn't decreases.
	 */
	@Test
	void testGetFirstCard() {
		Card expectedFirstCard = SOME_CARDS.get(0);
		int size = SOME_CARDS.size();

		assertEquals(expectedFirstCard, someCards.getFirstCard(), 
				"first inserted card expected");
		assertEquals( size , someCards.size(), 
				"size stays the same");
		assertEquals(expectedFirstCard, someCards.getFirstCard(), 
				"can get it twice");
		assertNull( cards.getFirstCard() , 
				"cannot get a card from an empty set");
	}
	
	

	/**
	 * Take first card from a collection, check it is the right one and the
	 * collection's size decreases
	 */
	@Test
	void testGetLastCard() {
		Card expectedLastCard = SOME_CARDS.get(SOME_CARDS.size() - 1);

		assertEquals(expectedLastCard, someCards.getLastCard(), "last inserted card expected");
		assertNull( cards.getLastCard() , "cannot get a card from an empty set");
	}

	/**
	 * Getting the cards from a suit from the full deck returns one of each value
	 */
	@ParameterizedTest
	@MethodSource("suitStream")
	void testGetCardsFromSuit(CardSuit suit) {

		CardCollection cardsFromSuit = deck.getCardsFromSuit(suit);

		assertAll(() -> assertEquals(VALUES_COUNT, cardsFromSuit.size(), "All values in suit expected"), () -> {
			for (Card card : cardsFromSuit)
				assertEquals(suit, card.getSuit(), "Should be from expected suit: " + suit);
		}, () -> assertSame(deck.getCardComparator(), cardsFromSuit.getCardComparator(), "with same card comparator")

		);
	}

	/**
	 * Getting the cards from a suit from the full deck returns one of each value
	 */
	@ParameterizedTest
	@MethodSource("suitStream")
	void testGetCardsNotFromSuit(CardSuit suit) {

		CardCollection cardsFromSuit = deck.getCardsNotFromSuit(suit);

		assertAll(
				() -> assertEquals(DECK_CARD_COUNT - VALUES_COUNT, cardsFromSuit.size(), "All values in suit expected"),
				() -> {
					for (Card card : cardsFromSuit)
						assertNotEquals(suit, card.getSuit(), "Shouldn't be from suit: " + suit);
				}, () -> assertSame(deck.getCardComparator(), cardsFromSuit.getCardComparator(),
						"with same card comparator"));
	}

	@ParameterizedTest
	@MethodSource("suitStream")
	void testGetCardsLargerThan(CardSuit suit) {
		int count = VALUES_COUNT;

		for (CardValue value : CardValue.values()) {
			CardCollection cardsInSuit = CardCollection.getDeck().getCardsFromSuit(suit);
			CardCollection larger = cardsInSuit.getCardsLargerThan(new Card(suit, value));
			int thisCount = --count;

			assertAll(() -> assertEquals(CardCollection.class, larger.getClass()),
					() -> assertEquals(thisCount, larger.size()),
					() -> assertSame(cardsInSuit.getCardComparator(), larger.getCardComparator()));
		}
	}

	@ParameterizedTest
	@MethodSource("suitStream")
	void testGetCardsSmallerThan(CardSuit suit) {
		int count = 0;

		for (CardValue value : CardValue.values()) {
			CardCollection cardsInSuit = CardCollection.getDeck().getCardsFromSuit(suit);
			CardCollection smaller = cardsInSuit.getCardsSmallerThan(new Card(suit, value));
			int thisCount = count++;

			assertAll(() -> assertEquals(CardCollection.class, smaller.getClass()),
					() -> assertEquals(thisCount, smaller.size()),
					() -> assertSame(cardsInSuit.getCardComparator(), smaller.getCardComparator()));
		}
	}

	/**
	 * Check there isn't a highest card on a empty collection
	 */
	@Test
	void testGetHigestValueCard_empty() {
		assertNull( cards.getHighestCard() , "cannot get a card from an empty collection");
	}
	
	@ParameterizedTest
	@MethodSource("suitStream")
	void testGetHighestValueCard_suits(CardSuit suit) {
		CardCollection suitCards = deck.getCardsFromSuit(suit).shuffle();

		assertEquals(new Card(suit, ACE), suitCards.getHighestCard(), "ace expected");
	}

	@Test
	void testGetHighestValueCards_someCards() {
		CardCollection expected = new CardCollection(SOME_CARDS.subList(0, 1));
		CardCollection obtained = someCards.getHighestValueCards();
		assertEquals(expected, obtained, "just an ace expected");
	}

	@Test
	void testGetHighestValueCards_allCards() {
		CardCollection expected = new CardCollection();
		CardCollection obtained = deck.getHighestValueCards();

		for (CardSuit suit : CardSuit.values())
			expected.addCard(suit, ACE);

		assertEquals(expected, obtained, "Aces expected");
	}

	@Test
	void testGetHighestValueCards_other_cards() {
		cards.addCard(HEARTS, ACE).addCard(HEARTS, KING).addCard(DIAMONDS, ACE).addCard(CLUBS, JACK)
				.addCard(HEARTS, JACK).addCard(CLUBS, ACE);

		CardCollection expected = new CardCollection();

		expected.addCard(HEARTS, ACE).addCard(DIAMONDS, ACE).addCard(CLUBS, ACE);

		assertEquals(expected, cards.getHighestValueCards(), "only aces expected");

	}
	
	/**
	 * Check there isn't a lowest card on a empty collection
	 */
	@Test
	void testGetLowestValueCard_empty() {
		assertNull( cards.getLowestCard() , "cannot get a card from an empty collection");
	}
	
	@ParameterizedTest
	@MethodSource("suitStream")
	void testGetLowestValueCard_suits(CardSuit suit) {
		CardCollection suitCards = deck.getCardsFromSuit(suit).shuffle();

		assertEquals(new Card(suit, V02), suitCards.getLowestCard(), "ace expected");
	}

	@Test
	void testGetLowestestValueCards_someCards() {
		CardCollection expected = new CardCollection(SOME_CARDS.subList(2, 3));
		CardCollection obtained = someCards.getLowestValueCards();
		assertEquals(expected, obtained, "3 expected");
	}

	@Test
	void testGetLowestValueCards_allCards() {
		CardCollection expected = new CardCollection();
		CardCollection obtained = deck.getLowestValueCards();

		for (CardSuit suit : CardSuit.values())
			expected.addCard(suit, V02);

		assertEquals(expected, obtained, "2s expected");
	}

	@Test
	void testGetLowestValueCards_other_cards() {
		cards.addCard(HEARTS, ACE).addCard(HEARTS, KING).addCard(DIAMONDS, ACE).addCard(CLUBS, JACK)
				.addCard(HEARTS, JACK).addCard(CLUBS, ACE);
		CardCollection expected = new CardCollection();

		expected.addCard(CLUBS, JACK).addCard(HEARTS, JACK);

		assertEquals(expected, cards.getLowestValueCards(), "only jacks expected");

	}
	

	@ParameterizedTest
	@MethodSource("someCardsStream")
	void testGetCardsWithValue_someCards(Card card) {
		CardValue value = card.getValue();
		CardCollection expected = new CardCollection();

		expected.addCard(card);

		assertEquals(expected, someCards.getCardsWithValue(value));
	}

	@ParameterizedTest
	@MethodSource("valueStream")
	void testGetCardsWithValue_fullDeck(CardValue value) {
		CardCollection expected = new CardCollection();

		for (CardSuit suit : CardSuit.values())
			expected.addCard(suit, value);

		assertEquals(expected, deck.getCardsWithValue(value));
	}

	@ParameterizedTest
	@MethodSource("suitStream")
	void testGetHighestCard(CardSuit suit) {
		CardCollection cardsInSuit = CardCollection.getDeck().getCardsFromSuit(suit);
		Card ace = new Card(suit, ACE);

		assertAll(() -> assertEquals(ace, cardsInSuit.getHighestCard(), "ace is highest"), () -> {
			cardsInSuit.shuffle();
			assertEquals(ace, cardsInSuit.getHighestCard(), "ace is highest after shuffling");
		});
	}

	@ParameterizedTest
	@MethodSource("suitStream")
	void testGetLowestCard(CardSuit suit) {
		CardCollection cardsInSuit = CardCollection.getDeck().getCardsFromSuit(suit);
		Card v02 = new Card(suit, V02);

		assertAll(
				() -> assertEquals(v02, cardsInSuit.getLowestCard(), 
						"v02 is highest"), 
				() -> {
					cardsInSuit.shuffle();
					assertEquals(v02, cardsInSuit.getLowestCard(), 
							"v02 is highest after shuffling");
		});
	}

	/**
	 * Get many random cards from a deck and check that most of them are different
	 */
	@Test
	void testGetRandomCard() {
		Set<Card> set = new HashSet<>();

		for (int c = 0; c < DECK_CARD_COUNT; c++)
			set.add(deck.getRandomCard());

		assertTrue(set.size() > DECK_CARD_COUNT / 2, "many different random cards expected");
	}
	
	/**
	 * Check that {@code null} is returned when picking a random card from an empty set
	 */
	@Test
	void testGetRandomCard_empty() {
		assertNull( cards.getRandomCard() , "cannot retrivge a card from an empty collection" );
	}

	/**
	 * Take first card from a collection, check it is the right one and the
	 * collection's size decreases
	 */
	@Test
	void testTakeFirstCard() {
		cards.addAllCards(SOME_CARDS);
		int size = SOME_CARDS.size();
		Card expectedFirstCard = SOME_CARDS.get(0);
		assertAll(() -> assertEquals(size, cards.size(), "unexpected size"),
				() -> assertEquals(expectedFirstCard, cards.takeFirstCard(), 
						"first inserted card expected"),
				() -> assertEquals(size - 1, cards.size(), 
						"one less card expected"), 
				() -> {
					for (Card other : cards)
						assertNotEquals(expectedFirstCard, other, 
								"taken card should be removed");
				});
	}

	/**
	 * Check that taking the first card from an empty collection returns null
	 */
	@Test
	void testTakeFirstCard_empty() {
		assertNull( cards.takeFirstCard() , 
				"cannot take a card from an empty set");
	}
	
	
	@Test
	void testTakeFirstCardsInt() {

		for (int n = 0; n < SOME_CARDS.size(); n++) {
			cards = new CardCollection(comparator, SOME_CARDS);

			CardCollection first = cards.takeFirstCards(n);

			int count = n;
			assertAll(() -> assertEquals(count, first.size()),
					() -> assertEquals(SOME_CARDS.size() - count, cards.size()),
					() -> assertEquals(SOME_CARDS.subList(0, count), first.asList()));
		}
	}
	
	/**
	 * Take more cards than available and expect all cards
	 */
	@Test
	void testTakeFirstCardsInt_notEnought() {
		int size = someCards.size();
		CardCollection takenCards = someCards.takeFirstCards( size + 1);
		
		assertEquals( size , takenCards.size() , "same number of cards");
	}

	/**
	 * Take first card from a collection, check it is the right one and the
	 * collection's size decreases
	 */
	@Test
	void testTakeLastCard() {
		cards.addAllCards(SOME_CARDS);

		int size = SOME_CARDS.size();
		Card expectedLastCard = SOME_CARDS.get(SOME_CARDS.size() - 1);

		assertAll(
				() -> assertEquals(size, cards.size(), 
						"unexpected size"),
				() -> assertEquals(expectedLastCard, cards.takeLastCard(), 
						"last inserted card expected"),
				() -> assertEquals(size - 1, cards.size(), 
						"one less card expected"), () -> {
					for (Card other : cards)
						assertNotEquals(expectedLastCard, other, 
								"taken card should be removed");
				});

	}
	
	/**
	 * Check that taking the last card from an empty collection returns null
	 */
	@Test
	void testTakeLastCard_empty() {
		assertNull( cards.takeLastCard() , "cannot take a card from an empty set");
	}


	/**
	 * Take one card and check it was removed
	 */
	@ParameterizedTest
	@MethodSource("someCardsStream")
	void testTakeCard(Card card) {
		cards = new CardCollection(comparator, SOME_CARDS);

		int size = SOME_CARDS.size();

		assertAll(() -> assertEquals(size, cards.size(), "unexpected size"),
				() -> assertEquals(card, cards.takeCard(card), "card expected"),
				() -> assertEquals(size - 1, cards.size(), "one less card expected"), () -> {
					for (Card other : cards)
						assertNotEquals(card, other, "taken card should be removed");
				});
	}
	
	/**
	 * Take a non existing card and expect {@code null}.
	 */
	@Test
	void testTakeCard_nonExisting() {
		cards = new CardCollection(comparator, SOME_CARDS);
		
		assertNull( cards.takeCard( new Card(CLUBS,ACE) ) , "cannot take non existing card");
	
	}
		
	/**
	 * Iterate over collection and check all cards are the expected ones and the
	 * count is correct
	 */
	@Test
	void testIterator() {
		int count = 0;

		for (Card card : someCards) {
			assertTrue(SOME_CARDS.contains(card));
			count++;
		}

		assertEquals(SOME_CARDS.size(), count, "Wrong number of cards");
	}

	/**
	 * Check redefinition of {@code equals()} 
	 */
	@Test
	void testEquals() {
		assertEquals(someCards,someCards);
		assertNotEquals(someCards,null);
		assertNotEquals(someCards,SOME_CARDS.get(0));
	}
	
	/**
	 * Check {@code hashCode is working as expected}
	 */
	@Test
	void testHasCode() {
		someCards.hashCode();
	}
	
	/**
	 * Check if to string contains standard representation of the existing cards
	 */
	@Test
	void testToString() {
		String text = someCards.toString();
		
		assertTrue(  text.contains("A♠" ));
		assertTrue(  text.contains("Q♦" ));
		assertTrue(  text.contains("3♣" ));
	}
	
	
	static Stream<Card> allCardsStream() {
		Stream.Builder<Card> builder = Stream.builder();

		for (Card card : CardCollection.getDeck())
			builder.add(card);

		return builder.build();
	}

	static Stream<Card> someCardsStream() {
		Stream.Builder<Card> builder = Stream.builder();

		for (Card card : SOME_CARDS)
			builder.add(card);

		return builder.build();
	}

	static Stream<CardSuit> suitStream() {
		Stream.Builder<CardSuit> builder = Stream.builder();

		for (CardSuit suit : CardSuit.values())
			builder.add(suit);

		return builder.build();
	}

	static Stream<CardValue> valueStream() {
		Stream.Builder<CardValue> builder = Stream.builder();

		for (CardValue value : CardValue.values())
			builder.add(value);

		return builder.build();
	}

	static Stream<Integer> upToMaxStream() {
		return Stream.iterate(0, n -> n + 1).limit(MAX_CARDS);
	}

}

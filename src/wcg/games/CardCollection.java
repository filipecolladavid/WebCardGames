package wcg.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import wcg.shared.cards.Card;
import wcg.shared.cards.CardSuit;
import wcg.shared.cards.CardValue;

/**
 * A collection of cards that may represent a full deck, a player's hand or the
 * cards on the table, for instance. As the name suggests, it contains a
 * collection of Card and their order is relevant. This class provides several
 * methods to operate on card collections.
 * 
 * This class can be instantiated but instances can also be obtained using
 * static methods such as getDeck() to create a collections with cards of all
 * suits and values.
 * 
 * Each instance has a CardComparator that is used if a particular card order
 * needs to be considered such as in getHighestCard(). If a comparator is not
 * not provided then DefaultCardComparator is used.
 * 
 * Methods with a get prefix simply return a card without changing the
 * collection. Methods with a take prefix remove a card from the collection. If
 * a collection is empty, methods that would return a card will return null
 * instead.
 * 
 * Several methods return a CardCollection to enable chaining, including the
 * static methods for generating a deck. For instance:
 * 
 * CardCollection allSpades =
 * CardCollection.getDeck().getAllCardsWithSuit(CardSuit.SPADE); Card
 * lowestHeartonHand =
 * hand.getAllCardsWithSuit(CardSuit.HEARTS).getLowestCard();
 * 
 * In card collections the order of cards is relevant and they may hold repeated
 * cards. If needed, the removeRepeated() method removes duplicates and returns
 * the collections itself for possible chaining;
 *
 */

public class CardCollection implements Iterable<Card> {
	private LinkedList<Card> cards = new LinkedList<Card>();
	private CardComparator comparator = DefaultCardComparator.getInstance();

	/**
	 * An empty instance backed by the DefaultCardComparator.
	 */
	public CardCollection() {
	}

	/**
	 * An instance backed by the by the DefaultCardComparator and populated with
	 * given list of cards.
	 * 
	 * @param cards - to populate this collection.
	 */
	public CardCollection(List<Card> cards) {
		this.cards.addAll(cards);
	}

	/**
	 * An empty instance backed by the given CardComparator.
	 * 
	 * @param comparator - of cards
	 */
	public CardCollection(CardComparator comparator) {
		this.comparator = comparator;

	};

	/**
	 * An instance backed by the given CardComparator and populated with given list
	 * of cards.
	 * 
	 * @param comparator
	 * @param cards
	 */

	public CardCollection(CardComparator comparator, List<Card> cards) {
		this.comparator = comparator;
		this.cards.addAll(cards);
	}

	/**
	 * A CardCollection with a full deck of cards, including 2 jokers, backed by the
	 * default card comparator
	 * 
	 * @return deck of cards
	 */
	public static CardCollection getFullDeck() {
		List<Card> list = addToDeck();
		list.add(new Card(CardSuit.CLUBS, null));
		list.add(new Card(CardSuit.DIAMONDS, null));

		return new CardCollection(list);
	}

	/**
	 * A CardCollection with a full deck of cards, including 2 jokers, backed by the
	 * given card comparator
	 * 
	 * @param comparator - of cards
	 * @return deck of cards
	 */
	public static CardCollection getFullDeck(CardComparator comparator) {
		List<Card> list = addToDeck();
		list.add(new Card(CardSuit.CLUBS, null));
		list.add(new Card(CardSuit.DIAMONDS, null));
		list.sort(comparator);
		return new CardCollection(comparator, list);
	}

	/**
	 * A CardCollection with a deck cards of all suits and values, backed by the
	 * default card comparator
	 * 
	 * @return deck of cards
	 */
	public static CardCollection getDeck() {
		List<Card> list = addToDeck();
		CardComparator comp = DefaultCardComparator.getInstance();
		list.sort(comp);
		return new CardCollection(list);
	}

	/**
	 * A {code CardCollection} with a deck of cards of all suits and values with
	 * given card comparator
	 * 
	 * @param comparator - of cards
	 * @return deck of cards
	 */
	public static CardCollection getDeck(CardComparator comparator) {
		List<Card> list = addToDeck();
		list.sort(comparator);
		return new CardCollection(list);
	}

	private static List<Card> addToDeck() {
		List<Card> list = new LinkedList<Card>();
		CardSuit suits[] = CardSuit.values();
		CardValue values[] = CardValue.values();
		for (CardSuit suit : suits) {
			for (CardValue value : values) {
				Card c = new Card(suit, value);
				list.add(c);
			}
		}
		return list;
	}

	/**
	 * Card comparator used by this card collection.
	 * 
	 * @return comparator
	 */

	public CardComparator getCardComparator() {
		return this.comparator;
	}

	/**
	 * Return cards as a new list of cards. This list can be changed without
	 * affecting this instance.
	 * 
	 * @return list of cards.
	 */
	public List<Card> asList() {
		List<Card> list = new LinkedList<Card>();
		list.addAll(this.cards);
		return list;
	}

	/**
	 * Shuffle the cards in this collection.
	 * 
	 * @return this collection for chaining.
	 */
	public CardCollection shuffle() {
		Collections.shuffle(cards);
		return this;
	}

	/**
	 * Removes duplicates in this collection. If a card exists multiple times, the
	 * first occurrence is kept and equals cards to the right are removed.
	 * 
	 * A by transforming a LinkedList into a set, we're removing all duplicates. A
	 * set is by definition a set of unique values
	 * 
	 * @return this collection for chaining
	 */
	public CardCollection removeRepeated() {
		cards = new LinkedList<>(new HashSet<>(cards));
		return new CardCollection();
	}

	/**
	 * Is this card collection empty?
	 * 
	 * @return true if empty; false otherwise.
	 */
	public boolean isEmpty() {
		return cards.size() == 0;
	}

	/**
	 * Clear all cards from this collection
	 *
	 * @return this collection
	 */
	public CardCollection clearCards() {
		cards.clear();
		return this;
	}

	/**
	 * Number of cards in this collection.
	 * 
	 * @return number of cards.
	 */
	public int size() {
		return cards.size();
	}

	/**
	 * Add a single card to this collection.
	 * 
	 * @param card - to add.
	 * @return this collection.
	 */
	public CardCollection addCard(Card card) {
		cards.add(card);
		return this;
	}

	/**
	 * Convenience methods to add card given suit and value.
	 * 
	 * @param suit  - of card
	 * @param value - of card
	 * @return this collection
	 */
	public CardCollection addCard(CardSuit suit, CardValue value) {
		Card c = new Card(suit, value);
		cards.add(c);
		return this;
	}

	/**
	 * Add a list of cards to this collection. Cards are added to the end of this
	 * one.
	 * 
	 * @param cards - to add
	 * @return this collection PASS
	 */
	public CardCollection addAllCards(List<Card> cards) {
		this.cards.addAll(cards);
		return this;
	}

	/**
	 * Add another collection to this one. Cards from other collection are added to
	 * the end of this one.
	 * 
	 * @param collection - to add.
	 * @return this collection
	 */
	public CardCollection addCardCollection(CardCollection collection) {
		cards.addAll(collection.asList());
		return this;
	}

	/**
	 * Collection contains this card?
	 * 
	 * @param card - to check
	 * @return true if card is contained and false otherwise.
	 */
	public boolean containsCard(Card card) {
		return cards.contains(card);
	}

	/**
	 * Get first card from collection. If this collection is empty returns null.
	 * 
	 * @return first card or null.
	 */
	public Card getFirstCard() {
		if (this.size() == 0)
			return null;
		else
			return this.cards.getFirst();
	}

	/**
	 * Get first card from collection If this collection is empty returns null.
	 * 
	 * @return first card or null.
	 */
	public Card getLastCard() {
		if (this.size() == 0)
			return null;
		else
			return this.cards.getLast();
	}

	/**
	 * New collection with same comparator only with cards from given suit.
	 * 
	 * @param suit - on interest
	 * @return collection
	 */
	public CardCollection getCardsFromSuit(CardSuit suit) {
		List<Card> cardsFromSuit = new ArrayList<>();
		for (Card c : cards)
			if (c.getSuit().equals(suit))
				cardsFromSuit.add(c);
		return new CardCollection(comparator, cardsFromSuit);
	}

	/**
	 * New collection with same comparator only with cards other than given suit.
	 * 
	 * @param suit - on interest
	 * @return collection
	 */
	public CardCollection getCardsNotFromSuit(CardSuit suit) {
		List<Card> cardsNotFromSuit = new ArrayList<>();
		for (Card c : cards)
			if (!c.getSuit().equals(suit))
				cardsNotFromSuit.add(c);

		return new CardCollection(comparator, cardsNotFromSuit);
	}

	/**
	 * New collection with same comparator and only with cards larger than given
	 * card
	 * 
	 * @param limit - card
	 * @return collection
	 */
	public CardCollection getCardsLargerThan(Card limit) {
		List<Card> cardsLargerThan = new ArrayList<>();
		for (Card c : cards)
			if (c.compareTo(limit) > 0)
				cardsLargerThan.add(c);
		return new CardCollection(comparator, cardsLargerThan);
	}

	/**
	 * New collection with same comparator and only with cards smaller than given
	 * card
	 * 
	 * @param limit - card
	 * @return collection
	 */
	public CardCollection getCardsSmallerThan(Card limit) {
		List<Card> cardsSmallerThan = new ArrayList<>();
		for (Card c : cards)
			if (c.compareTo(limit) < 0)
				cardsSmallerThan.add(c);
		return new CardCollection(comparator, cardsSmallerThan);
	}

	/**
	 * New collection with same comparator and the cards with the highest value from
	 * any suit. For instance, if the original collection has several aces then
	 * returns a new collection containing only those cards.
	 * 
	 * @return collection with highest value cards
	 */
	public CardCollection getHighestValueCards() {
		if (cards.size() == 0)
			return null;
		List<Card> newCards = new ArrayList<>();
		CardValue highestValue = this.getHighestCard().getValue();
		for (Card c : cards)
			if (c.getValue().equals(highestValue)) {
				newCards.add(c);
			}
		return new CardCollection(comparator, newCards);
	}

	/**
	 * New collection with same comparator and the cards with given value from any
	 * suit.
	 * 
	 * @param value - of card (e.g. ACE).
	 * @return collection with given value cards.
	 */
	public CardCollection getCardsWithValue(CardValue value) {
		List<Card> cardsWithValue = new ArrayList<>();
		for (Card c : cards)
			if (c.getValue().equals(value))
				cardsWithValue.add(c);
		return new CardCollection(comparator, cardsWithValue);
	}

	/**
	 * Get the highest card using this collection comparator If this collection is
	 * empty returns null.
	 * 
	 * @return highest value or null.
	 * @implNote consider using Collections.max(Collection,Comparator)
	 */
	public Card getHighestCard() {
		if (cards.size() == 0)
			return null;
		return Collections.max(cards, comparator);
	}

	/**
	 * Get the lowest card using this collection comparator. If this collection is
	 * empty returns null.
	 * 
	 * @return lowest value or null
	 * @implNote consider using Collections.min(Collection,Comparator)
	 */
	public Card getLowestCard() {
		if (cards.size() == 0)
			return null;
		return Collections.min(cards, comparator);
	}

	/**
	 * New collection with same comparator and the cards with the lowest value from
	 * any suit. For instance, if the original collection has several 2 then returns
	 * a new collection containing only those cards.
	 * 
	 * @returns: Collection with highest value cards.
	 */
	public CardCollection getLowestValueCards() {
		if (cards.size() == 0)
			return null;
		List<Card> newCards = new ArrayList<>();
		CardValue lowestValue = this.getLowestCard().getValue();
		for (Card c : cards)
			if (c.getValue().equals(lowestValue)) {
				newCards.add(c);
			}
		return new CardCollection(comparator, newCards);
	}

	/**
	 * A random card from this collection. If the collection is empty returns null.
	 * 
	 * @return card at random or null.
	 */
	public Card getRandomCard() {
		int size = cards.size();
		if (size == 0)
			return null;
		int random = (int) Math.floor(Math.random() * (size - 0));
		return cards.get(random);
	}

	/**
	 * Take the first card and return it, if collection has at least one card.
	 * Otherwise return null. The returned card is removed from the collection.
	 * 
	 * @return a single card or null.
	 */
	public Card takeFirstCard() {
		if (this.size() == 0)
			return null;
		Card c = this.getFirstCard();
		this.cards.removeFirst();
		return c;
	}

	/**
	 * Take the last card and return it, if collection has at least one card;
	 * otherwise return null. The returned card is removed from the collection.
	 * 
	 * @return card or null.
	 */
	public Card takeLastCard() {
		if (this.size() == 0)
			return null;
		Card c = this.getLastCard();
		this.cards.removeLast();
		return c;
	}

	/**
	 * Take given count of cards from the beginning of this collection. A smaller
	 * number of cards may be returned if not enough are available.
	 * 
	 * @param count - of cards to take.
	 * @return collection.
	 */
	public CardCollection takeFirstCards(int count) {
		List<Card> takenCards = new ArrayList<>();
		int nCardsToRemove = Math.min(count, cards.size());
		for (int i = 0; i < nCardsToRemove; i++) {
			takenCards.add(cards.removeFirst());
		}
		return new CardCollection(takenCards);
	}

	/**
	 * Take a specific card and return it, if collection has at least one card;
	 * otherwise return null. The returned card is removed from the collection.
	 * 
	 * @param card - to take
	 * @return removed card or null
	 */
	public Card takeCard(Card card) {
		if (cards.remove(card))
			return card;
		return null;
	}

	/**
	 * An iterator over the cards in this collection.
	 * 
	 * @implSpec iterator in interface java.lang.Iterable<Card>
	 */
	@Override
	public Iterator<Card> iterator() {
		return cards.iterator();
	}

	@Override
	public int hashCode() {
		return Objects.hash(cards, comparator);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CardCollection other = (CardCollection) obj;
		return Objects.equals(cards, other.cards) && Objects.equals(comparator, other.comparator);
	}

	@Override
	public String toString() {
		return cards.toString();
	}
}

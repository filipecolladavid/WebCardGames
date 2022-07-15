package wcg.games;

import wcg.shared.cards.Card;
import wcg.shared.cards.CardSuit;
import wcg.shared.cards.CardValue;

/**
 * A default implementation implementation of CardComparator based on the ranks
 * of suits and values as defined by the respective enumerations.
 * 
 * Different games have different game orders and the game definitions may
 * define their particular card comparator by extending this class, redefining
 * only the CardValue comparator, for example.
 * 
 * Comparison of suits and values must take in consideration these can be null
 * (e.g. jokers). This class is a singleton to prevent the proliferation of
 * redundant objects in the application. Extensions of this class should also be
 * singletons.
 *
 */
public class DefaultCardComparator implements CardComparator {

	private static DefaultCardComparator comparator = null;

	/**
	 * Although a singleton, this class constructor is protected rather than private
	 * to enable its extension.
	 */
	protected DefaultCardComparator() {
	}

	/**
	 * Single instance if this class.
	 * 
	 * @return instance
	 */
	public static DefaultCardComparator getInstance() {
		if (comparator == null)
			comparator = new DefaultCardComparator();

		return comparator;
	}

	/**
	 * Card order used in this game. Extensions may force a different order by
	 * redefining compare(CardSuit o1, CardSuit o2) and/or compare(CardValue o1,
	 * CardValue o2).
	 */
	@Override
	public int compare(Card o1, Card o2) {
		int suitComparison = compare(o1.getSuit(), o2.getSuit());
		if (suitComparison != 0) {
			return suitComparison;
		}
		return compare(o1.getValue(), o2.getValue());
	}

	/**
	 * Implementation of a method similar the Comparator<CardSuit> interface using
	 * the compareTo() method of Enum.
	 */
	@Override
	public int compare(CardSuit o1, CardSuit o2) {
		if (o1 == o2)
			return 0;
		if (o1 == null)
			return -1;
		if (o2 == null)
			return 1;
		return o1.compareTo(o2);
	}

	/**
	 * Implementation of a method similar the Comparator<CardValue> interface using
	 * the the compareTo() method of Enum.
	 */
	@Override
	public int compare(CardValue o1, CardValue o2) {
		if (o1 == o2)
			return 0;
		if (o1 == null)
			return -1;
		if (o2 == null)
			return 1;
		return o1.compareTo(o2);
	}
}
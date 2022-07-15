package wcg.games;

import java.util.Comparator;

import wcg.shared.cards.Card;
import wcg.shared.cards.CardSuit;
import wcg.shared.cards.CardValue;

/**
 * Comparison of 2 cards based on their suits and values. This interface is
 * essentially an extension of Comparator<Card>, Comparator<CardSuit>,
 * Comparator<CardValue>. In fact, the 2 methods declared in this interface are
 * those that would be declared by the last 2 comparators. Although a Java
 * interface can extend multiples interfaces, it cannot extend the same
 * interface with different parametric types.
 */

public interface CardComparator extends Comparator<Card> {

	/**
	 * Compare card suits as in Comparator
	 * 
	 * @param o1 - 1st object to compare.
	 * @param o2 - 2st object to compare.
	 * @return a negative integer, zero, or a positive integer as the first argument
	 *         is less than, equal to, or greater than the second.
	 */
	int compare(CardSuit o1, CardSuit o2);

	/**
	 * 
	 * @param o1 - 1st object to compare.
	 * @param o2 - 2st object to compare.
	 * @return a negative integer, zero, or a positive integer as the first argument
	 *         is less than, equal to, or greater than the second.
	 */
	int compare(CardValue o1, CardValue o2);

}

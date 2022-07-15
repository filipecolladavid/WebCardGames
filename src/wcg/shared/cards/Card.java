package wcg.shared.cards;

import java.io.Serializable;
import java.util.Objects;

/**
 * Instances of this class represent cards. Regular cards have a suit - ♣, ♦, ♥,
 * ♠ - and a value - A, 2, 3 ... 9, J, Q, K. Jokers have a null value, hence 5
 * jokers are possible - the 4 regular suits and the null suit -, although only
 * 2 are typically available in physical card decks. In compareTo(), null suits
 * and values are before all others.
 */
public class Card implements Comparable<Card>, Serializable {

	private static final long serialVersionUID = 1L;

	private CardSuit suit;
	private CardValue value;

	public Card(CardSuit suit, CardValue value) {
		this.suit = suit;
		this.value = value;
	}

	/**
	 * @return The Card's suit
	 */
	public CardSuit getSuit() {
		return suit;
	}

	/**
	 * @return The Card's value
	 */
	public CardValue getValue() {
		return value;
	}

	/**
	 * Compares two cards First compares the <em>suit</em>s on the two cards, and if
	 * they are the same, then proceeds to compare the <em>value</em>s on the two
	 * cards. As specified, returns 0 if the cards have the same "score", returns -1
	 * if this card has a lower "score" than the other, return 1 if this card has a
	 * higher "score" than the other.
	 * 
	 * @param otherCard Another card
	 * @return A value indicating how this card compares to the other
	 */
	@Override
	public int compareTo(Card otherCard) {
		int suitComparison = compareSuit(otherCard);
		if (suitComparison != 0) {
			return suitComparison;
		}
		return compareValue(otherCard);
	}

	/**
	 * Auxiliary method used in compareTo, to determine the <em>suit</em> compares
	 * to the other card's.
	 * 
	 * @param otherCard Another Card
	 * @return A value indicating how the <em>suit</em> of this card compares to the
	 *         other
	 */
	private int compareSuit(Card otherCard) {
		if (suit == otherCard.suit) {
			return 0;
		}

		if (suit == null) {
			return -1;
		}

		if (otherCard.suit == null) {
			return 1;
		}

		if (suit.getScore() < otherCard.suit.getScore()) {
			return -1;
		}

		return 1;
	}

	/**
	 * Auxiliary method used in compareTo to determine how the <em>value</em>
	 * compares to the other card's.
	 * 
	 * @param otherCard Another Card
	 * @return A value indicating how the <em>value</em> of this card compares to
	 *         the other
	 */
	private int compareValue(Card otherCard) {
		if (value == otherCard.value) {
			return 0;
		}

		if (value == null) {
			return -1;
		}

		if (otherCard.value == null) {
			return 1;
		}

		if (value.getScore() < otherCard.value.getScore()) {
			return -1;
		}

		return 1;
	}

	@Override
	public int hashCode() {
		return Objects.hash(suit, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Card otherCard = (Card) obj;
		return suit == otherCard.suit && value == otherCard.value;
	}

	/**
	 * <p>
	 * Show the card as a short string with value and suit given by the toString()
	 * methods of these enumerations. (ex: 2♣, K♣, 10♣, 9♦, J♦, 7♥, Q♥, 2♠, A♠).
	 * Jokers are cards with null value represented as an asterisk (*).
	 * </p>
	 * <p>
	 * "Assumes" from the start the <em>Card</em>'s fields will be
	 * <strong>null</strong>, but tests this assumption, and if it's incorrect,
	 * replaces <em>cardValueString</em> and <em>cardSuitString</em> by their
	 * corresponding strings.
	 * </p>
	 * 
	 * @return The concatenated string of the <em>Card</em>'s <em>value</em> and
	 *         <em>suit</em>
	 */
	@Override
	public String toString() {
		String cardValueString = "*";
		String cardSuitString = "*";

		if (value != null) {
			cardValueString = value.toString();
		}
		if (suit != null) {
			cardSuitString = suit.toString();
		}

		return cardValueString + cardSuitString;
	}
}

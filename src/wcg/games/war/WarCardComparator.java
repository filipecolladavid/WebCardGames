/**
 * 
 */
package wcg.games.war;

import wcg.games.DefaultCardComparator;
import wcg.shared.cards.CardSuit;
import wcg.shared.cards.CardValue;

/**
 * A specialization of CardComparator for the this game. It ignores suits and
 * queens win over jacks.
 */
public class WarCardComparator extends DefaultCardComparator {

	public WarCardComparator() {
	}

	/**
	 * Similar order to the default but queens win over jacks.
	 * 
	 * @return comparison of objects as values
	 */
	@Override
	public int compare(CardValue o1, CardValue o2) {
		if ((o1.equals(CardValue.JACK) && o2.equals(CardValue.QUEEN))
				|| (o1.equals(CardValue.QUEEN) && o2.equals(CardValue.JACK))) {
			return -1 * super.compare(o1, o2);
		}
		return super.compare(o1, o2);
	}

	/**
	 * All suits are equal.
	 * 
	 * @return comparison of objects as suits
	 */
	@Override
	public int compare(CardSuit o1, CardSuit o2) {
		return 0;
	}
}

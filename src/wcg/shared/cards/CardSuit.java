package wcg.shared.cards;

/**
 * Enumeration with the 4 suits in cards
 */
public enum CardSuit {

	CLUBS("♣", 1), DIAMONDS("♦", 2), HEARTS("♥", 3), SPADES("♠", 4);

	private final String label;
	private final int score;

	private CardSuit(String label, int score) {
		this.label = label;
		this.score = score;
	}

	/**
	 * Replaces the default toString() in <strong>enum</strong>, returning the
	 * <em>label</em> provided when creating the enumeration.
	 * 
	 * @return The <em>CardSuit</em>'s <em>label</em>
	 */
	@Override
	public String toString() {
		return label;
	}

	/**
	 * @return The <em>CardSuit</em>'s <em>score</em>
	 */
	public int getScore() {
		return score;
	}

}

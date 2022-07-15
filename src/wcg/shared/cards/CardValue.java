package wcg.shared.cards;

/**
 * Enumeration with the values of cards
 */
public enum CardValue {
	V02("2", 2), V03("3", 3), V04("4", 4), V05("5", 5), V06("6", 6), V07("7", 7), V08("8", 8), V09("9", 9),
	V10("10", 10), QUEEN("Q", 11), JACK("J", 12), KING("K", 13), ACE("A", 14);

	private final String label;
	private final int score;

	private CardValue(String label, int score) {
		this.label = label;
		this.score = score;
	}

	/**
	 * Replaces the default toString() in <strong>enum</strong>, returning the
	 * <em>label</em> provided when creating the enumeration.
	 * 
	 * @return The <em>CardValue</em>'s <em>label</em>
	 */
	@Override
	public String toString() {
		return label;
	}

	/**
	 * @return The <em>CardValue</em>'s <em>score</em>
	 */
	public int getScore() {
		return score;
	}
}

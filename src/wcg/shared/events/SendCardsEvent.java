package wcg.shared.events;

import java.io.Serializable;
import java.util.List;

import wcg.shared.cards.Card;

/**
 * An event to send cards to players. It is mostly sent when the game starts,
 * but also during the game in some cases. Then event itself reports a list of
 * cards that can be retrieved with the getCards() method.
 */
public class SendCardsEvent extends GameEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Card> cards;

	public SendCardsEvent() {
		super();
	}

	/**
	 * @param cards
	 */
	public SendCardsEvent(String gameId, List<Card> cards) {
		super(gameId);
		this.cards = cards;
	}

	/**
	 * @return the cards
	 */
	public List<Card> getCards() {
		return cards;
	}
}

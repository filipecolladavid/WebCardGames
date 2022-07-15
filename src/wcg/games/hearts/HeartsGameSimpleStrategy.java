package wcg.games.hearts;

import java.util.Arrays;
import java.util.List;

import wcg.games.CardCollection;
import wcg.games.GameBot;
import wcg.games.GamePlayingStrategy;
import wcg.shared.cards.Card;
import wcg.shared.cards.CardSuit;

/**
 * <p>
 * A simple strategy to play hearts. It follows the basic rules, namely:
 * </p>
 * <ul>
 * <li>if not the first player in the round, always follow suit, (if possible);
 * <li>in the first two rounds don't play hearts.
 * </ul>
 * <p>
 * Following these rules the strategy selects a collection of playable cards.
 * The card to play is randomly selected from this collection.
 * </p>
 */
public class HeartsGameSimpleStrategy implements GamePlayingStrategy {

	private CardSuit suitToFollow = null;

	public HeartsGameSimpleStrategy() {

	}

	/**
	 * Select a collection of cards in hand that: follow suit (if necessary and
	 * possible); don't include hearts, in the first two rounds. Select random card
	 * from that collection.
	 */
	@Override
	public List<Card> pickCards(GameBot bot) {
		suitToFollow = bot.getSuitToFollow();

		CardCollection hand = bot.getHand();

		Card cardToPlay = null;

		// Can't play Hearts in the first two rounds
		if (bot.getRoundsCompleted() < 2) {
			cardToPlay = chooseCard(hand.getCardsNotFromSuit(CardSuit.HEARTS));
		} else {
			cardToPlay = chooseCard(hand);
		}

		hand.takeCard(cardToPlay);

		return Arrays.asList(cardToPlay);
	}

	/**
	 * Checks if there are cards in hand from the suit to follow, if there are, get
	 * a random card from them otherwise, get a random card from the given hand
	 * 
	 * @param hand - the bot's hand, already with or without hearts
	 * @return the card to play
	 */
	private Card chooseCard(CardCollection hand) {
		Card cardToPlay = null;

		CardCollection handWithCardsInSuit = hand.getCardsFromSuit(suitToFollow);

		if (handWithCardsInSuit.size() > 0)
			cardToPlay = handWithCardsInSuit.getRandomCard();
		else
			cardToPlay = hand.getRandomCard();

		return cardToPlay;
	}
}

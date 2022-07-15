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
 * A slightly better strategy to play HEARTS. (if compared with the
 * HeartsGameSimpleStrategy).
 * </p>
 * <ul>
 * <li>If bot is the first to play in turn, pick the suit with more cards to
 * play and then pick the lowest card of than suit in hand.
 * <li>If there suit to follow and the no cards from that suit in hand then pick
 * highest card from the suit with less cards to play.
 * <li>If the hand has cards from the suit to follow then try to pick the
 * highest card smaller than the highest on table.
 * <li>If that is not possible play the highest card from the suit to follow
 * </ol>
 */
public class HeartsGameStrategy implements GamePlayingStrategy {

	private CardSuit suitToFollow = null;

	public HeartsGameStrategy() {

	}

	/**
	 * Try to select a the highest or lowest card from a collections of valid cards,
	 * taking in consideration the round, the suit to follow, and the cards already
	 * played.
	 */
	@Override
	public List<Card> pickCards(GameBot bot) {
		suitToFollow = bot.getSuitToFollow();

		CardCollection hand = bot.getHand();
		Card cardToPlay = null;

		// First to play
		if (suitToFollow == null) {
			// Can't play Hearts in the first two rounds
			if (bot.getRoundsCompleted() < 2)
				cardToPlay = firstInTurn(hand.getCardsNotFromSuit(CardSuit.HEARTS));
			else
				cardToPlay = firstInTurn(hand);
		}
		// Follow Suit
		else {
			// Can't play Hearts in the first two rounds
			if (bot.getRoundsCompleted() < 2)
				cardToPlay = notFirstInTurn(hand.getCardsNotFromSuit(CardSuit.HEARTS), bot);
			else
				cardToPlay = notFirstInTurn(hand, bot);
		}

		hand.takeCard(cardToPlay);

		return Arrays.asList(cardToPlay);
	}

	/**
	 * Pick the suit with more cards to play and then pick the lowest card of than
	 * suit in hand.
	 * 
	 * @param hand
	 * @return Card - to play
	 */
	private Card firstInTurn(CardCollection hand) {
		Card cardToPlay = null;
		int size = Integer.MIN_VALUE;
		for (CardSuit suit : CardSuit.values()) {
			if (hand.getCardsFromSuit(suit).size() > size) {
				cardToPlay = hand.getCardsFromSuit(suit).getLowestCard();
				size = hand.getCardsFromSuit(suit).size();
			}
		}
		return cardToPlay;
	}

	/**
	 * Card to play when is not the first one to play
	 * 
	 * @param hand - of bot
	 * @param bot  - self, to check cards on Table
	 * @return Card - to play
	 */
	private Card notFirstInTurn(CardCollection hand, GameBot bot) {
		Card cardToPlay = null;

		CardCollection cardsFromSuitInHand = hand.getCardsFromSuit(suitToFollow);

		// Pick highest card from the suit with less cards to play
		if (cardsFromSuitInHand.size() == 0) {
			int size = Integer.MAX_VALUE;
			for (CardSuit suit : CardSuit.values()) {
				if (hand.getCardsFromSuit(suit).size() < size && hand.getCardsFromSuit(suit).size() > 0) {
					cardToPlay = hand.getCardsFromSuit(suit).getHighestCard();
					size = hand.getCardsFromSuit(suit).size();
				}
			}
		}
		// Pick the highest card smaller than the highest on table.
		else {
			CardCollection cardsOnTable = bot.getAllCardsOnTable();
			Card highestCardOnTable = cardsOnTable.getHighestCard();
			CardCollection cardsSmallerThan = cardsFromSuitInHand.getCardsSmallerThan(highestCardOnTable);

			if (cardsSmallerThan.size() == 0)
				cardToPlay = cardsFromSuitInHand.getHighestCard();

			// There are no smaller cards in hand, from suit to follow
			else
				cardToPlay = cardsSmallerThan.getHighestCard();
		}

		return cardToPlay;
	}
}

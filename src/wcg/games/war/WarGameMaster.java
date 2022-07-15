package wcg.games.war;

import java.util.List;

import wcg.games.CardCollection;
import wcg.games.CardComparator;
import wcg.games.GameMaster;
import wcg.games.GamePlayingStrategy;
import wcg.shared.CardGameException;
import wcg.shared.cards.Card;
import wcg.shared.events.RoundUpdateEvent;
import wcg.shared.events.SendCardsEvent;

/**
 * A game master for the WAR card game.
 */
public class WarGameMaster extends GameMaster {

	private static WarCardComparator cardComparator = new WarCardComparator();
	private CardCollection heap = new CardCollection(cardComparator);

	private static final int WAR_NUMBER_OF_PLAYERS = 2;
	private static final int WAR_POINTS_PER_ROUND = 0;

	public WarGameMaster() {
	}

	/**
	 * Name of this game all in capitals, as in GameFactory
	 */
	@Override
	protected String getGameName() {
		return "WAR";
	}

	/**
	 * War uses a special card comparator, defined in this package
	 */
	@Override
	protected CardComparator getCardComparator() {
		return cardComparator;
	}

	/**
	 * WAR is a 2 player game
	 */
	@Override
	protected int getNumberOfPlayers() {
		return WAR_NUMBER_OF_PLAYERS;
	}

	/**
	 * Each player receives half of a complete deck when the game starts
	 */
	@Override
	protected int getCardsPerPlayer() {
		return getDeck().size() / 2;
	}

	/**
	 * Players play at once, not in turns.
	 */
	@Override
	protected boolean isWithTurns() {
		return false;
	}

	/**
	 * Return null since this game is not played in turns.
	 */
	@Override
	protected String initialTurnInRound() {
		return null;
	}

	/**
	 * No initializations needed
	 */
	@Override
	protected void startGame() {
	}

	/**
	 * Normally players play a single card, but in a war they must play 3 cards.
	 */
	@Override
	protected void checkCards(String nick, List<Card> cards) throws CardGameException {
		if (getMode() != "War") {
			if (cards.size() != 1)
				throw new CardGameException("Just one card expected");
		} else {
			if (cards.size() != 3)
				throw new CardGameException("Three cards expected");
		}
	}

	/**
	 * The player with the higher card value wins the round. If both cards have the
	 * same value then its a war.
	 */
	@Override
	protected void beforeRoundConclusion() {
		List<String> nicks = players.getPlayerNicks();

		String nickPlayerOne = nicks.get(0);
		String nickPlayerTwo = nicks.get(1);

		if (getMode() != "War") {

			Card cardPlayerOne = getCardOnTable(nickPlayerOne);
			Card cardPlayerTwo = getCardOnTable(nickPlayerTwo);

			// Adds both cards to heap, so that they'll remain stored when table is cleared
			heap.addCard(cardPlayerOne);
			heap.addCard(cardPlayerTwo);

			int cardComparison = getCardComparator().compare(cardPlayerOne, cardPlayerTwo);

			if (cardComparison != 0) {
				SendCardsEvent event = new SendCardsEvent(getGameId(), heap.asList());

				if (cardComparison > 0) {
					getHand(nickPlayerOne).addAllCards(heap.asList());
					notify(nickPlayerOne, event);
				}

				if (cardComparison < 0) {
					getHand(nickPlayerTwo).addAllCards(heap.asList());
					notify(nickPlayerTwo, event);
				}

				// Heap is cleared since this isn't a War, and a player took the cards
				heap.clearCards();

				setMode(null);
			} else {
				setMode("War");
			}
		} else {
			CardCollection cardsPlayerOne = getCardsOnTable(nickPlayerOne);
			CardCollection cardsPlayerTwo = getCardsOnTable(nickPlayerTwo);

			// Adds all cards to heap, so that they'll remain stored when table is cleared
			heap.addAllCards(cardsPlayerOne.asList());
			heap.addAllCards(cardsPlayerTwo.asList());

			Card firstCardPlayerOne = cardsPlayerOne.getFirstCard();
			Card firstCardPlayerTwo = cardsPlayerTwo.getFirstCard();

			int cardComparison = getCardComparator().compare(firstCardPlayerOne, firstCardPlayerTwo);

			if (cardComparison != 0) {

				SendCardsEvent event = new SendCardsEvent(getGameId(), heap.asList());

				if (cardComparison > 0) {
					getHand(nickPlayerOne).addAllCards(heap.asList());
					notify(nickPlayerOne, event);
				}

				if (cardComparison < 0) {
					getHand(nickPlayerTwo).addAllCards(heap.asList());
					notify(nickPlayerTwo, event);
				}

				// Heap is cleared since War mode was finished, and a player took the cards
				heap.clearCards();

				setMode(null);
			} else {
				setMode("War");
			}
		}

		RoundUpdateEvent event = new RoundUpdateEvent(getGameId(), players.getCardsOnTable(),
				players.getPlayerWithTurn(), getRoundsCompleted(), getMode());
		broadcast(event);
	}

	/**
	 * No points in turns. Return always 0.
	 */
	@Override
	protected int getRoundPoints(String nick) {
		return WAR_POINTS_PER_ROUND;
	}

	/**
	 * Game ends when one player runs out of cards to play.
	 */
	@Override
	protected boolean hasEnded() {
		for (String nick : getPlayerNicks()) {
			if (getHand(nick).size() == 0)
				return true;
		}
		return false;
	}

	/**
	 * The winner is the player with cards to play.
	 */
	@Override
	protected String getWinner() {
		for (String nick : getPlayerNicks()) {
			if (getHand(nick).size() != 0)
				return nick;
		}
		return null;
	}

	/**
	 * There is a single strategy available for this game - WarGameStrategy.
	 */
	@Override
	protected GamePlayingStrategy getCardGameStrategy() {
		return new WarGameStrategy();
	}
}
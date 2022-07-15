package wcg.games.hearts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wcg.games.CardCollection;
import wcg.games.GameMaster;
import wcg.games.GamePlayingStrategy;
import wcg.shared.CardGameException;
import wcg.shared.cards.Card;
import wcg.shared.cards.CardSuit;

/**
 * <p>
 * Game master for the game of HEARTS. This game with the following rules:
 * </p>
 * <ul>
 * <li>game for 4 players;
 * <li>each player receives 13 cards;
 * <li>first player to join the game starts the first trick (round);
 * <li>first player in a turn plays any card, but not hearts in the first 2
 * tricks;
 * <li>other players follow suit;
 * <li>player with highest card of that suit collects all cards on the table;
 * <li>each heart add a negative point to the player's score;
 * <li>at the end, the player with less negative points wins.
 * </ol>
 *
 */
public class HeartsGameMaster extends GameMaster {

	private String holdTurn;
	private int countHeartsInTurn;
	private GamePlayingStrategy strategy = null;

	private static final int HEARTS_NUMBER_OF_PLAYERS = 4;

	private Map<String, Integer> roundPoints = new HashMap<>();

	public HeartsGameMaster() {
	}

	/**
	 * Name of this game in capitals, as in GameFactory.
	 */
	@Override
	protected String getGameName() {
		return "HEARTS";
	}

	/**
	 * The number of players in HEARTS is 4.
	 */
	@Override
	protected int getNumberOfPlayers() {
		return HEARTS_NUMBER_OF_PLAYERS;
	}

	/**
	 * All players get 1/4 of the deck cards.
	 */
	@Override
	protected int getCardsPerPlayer() {
		return getDeck().size() / 4;
	}

	/**
	 * HEARTS is played in turns.
	 */
	@Override
	protected boolean isWithTurns() {
		return true;
	}

	/**
	 * No preparation is needed for HEARTS
	 */
	@Override
	protected void startGame() {
	}

	/**
	 * In HEARTS the player must play a single card per turn. In the first 2 turns
	 * the card cannot be HEARTS, unless the player has not other option to play
	 * (improbable). If the player is to the first in round then can choose cards
	 * from any suit (unless its the first 2 rounds); otherwise, the player has to
	 * follow the suit of the first player, unless she/he/it doesn't have cards from
	 * that suit.
	 */
	@Override
	protected void checkCards(String nick, List<Card> cards) throws CardGameException {

		if (cards.size() != 1)
			throw new CardGameException("Just one card expected");

		CardCollection playerHand = players.getHand(nick);
		CardCollection cardsNotHearts = playerHand.getCardsNotFromSuit(CardSuit.HEARTS);
		CardCollection cardsFollowSuit = playerHand.getCardsFromSuit(getSuitToFollow());
		Card card = cards.get(0);

		if (getRoundsCompleted() < 2 && cardsNotHearts.size() > 0 && card.getSuit().equals(CardSuit.HEARTS))
			throw new CardGameException(" cannot play hearts ");

		if (getSuitToFollow() != card.getSuit() && cardsFollowSuit.size() > 0)
			throw new CardGameException(" must follow suit ");
	}

	/**
	 * Check the cards played by each player.
	 */
	@Override
	protected void beforeRoundConclusion() {
		countHeartsInTurn = 0;

		roundPoints.clear();

		Card bestCard = new Card(null, null);

		for (String nick : getPlayerNicks()) {
			Card cardToEvaluate = players.getCardOnTable(nick);
			if (cardToEvaluate.getSuit().equals(getSuitToFollow()) && cardToEvaluate.compareTo(bestCard) > 0) {
				holdTurn = nick;
				bestCard = cardToEvaluate;
			}

			if (cardToEvaluate.getSuit().equals(CardSuit.HEARTS))
				countHeartsInTurn++;

			roundPoints.put(nick, 0);
		}

		roundPoints.replace(holdTurn, countHeartsInTurn * -1);
	}

	/**
	 * Report the points of given player, as computed in beforeRoundConclusion().
	 * Hearts count as negative points
	 */
	@Override
	protected int getRoundPoints(String nick) {
		if (getRoundsCompleted() == 0)
			return 0;
		return roundPoints.get(nick);
	}

	/**
	 * The player that played the highest card from the suit to follow start the
	 * next round, as computed in beforeRoundConclusion().
	 */
	@Override
	protected String initialTurnInRound() {
		if (getRoundsCompleted() == 0) {
			return players.getNickFirstPlayer();
		}
		return holdTurn;
	}

	/**
	 * The game ends after 13 rounds.
	 */
	@Override
	protected boolean hasEnded() {
		return (getRoundsCompleted() == 13);
	}

	/**
	 * The winner is the player with most points (less negative points).
	 */
	@Override
	protected String getWinner() {
		int bestScore = Integer.MIN_VALUE;
		String winner = null;
		for (String nick : players.getPlayerNicks()) {
			if (players.getPointsFromNick(nick) > bestScore) {
				winner = nick;
				bestScore = players.getPointsFromNick(nick);
			}
		}
		return winner;
	}

	/*
	 * Produce a game strategy for this game instance. Alternates between
	 * HeartsGameSimpleStrategy and HeartsGameStrategy.
	 */
	@Override
	protected GamePlayingStrategy getCardGameStrategy() {
		if (strategy instanceof HeartsGameSimpleStrategy)
			strategy = new HeartsGameStrategy();
		else
			strategy = new HeartsGameSimpleStrategy();
		return strategy;
	}
}
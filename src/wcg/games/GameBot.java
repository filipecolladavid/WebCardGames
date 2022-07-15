package wcg.games;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import wcg.shared.CardGameException;
import wcg.shared.cards.Card;
import wcg.shared.cards.CardSuit;
import wcg.shared.events.GameEndEvent;
import wcg.shared.events.RoundConclusionEvent;
import wcg.shared.events.RoundUpdateEvent;
import wcg.shared.events.SendCardsEvent;

/**
 * <p>
 * An automatic card game player - a ro<strong>bot</strong> - to plays all kinds
 * of card games.
 * </p>
 * 
 * <p>
 * This class has the generic features of a <em>bot</em>. It relies on the
 * <strong>Strategy</strong> design pattern to shift between different ways to
 * pick the cards to play. Although a default strategy may be provided by this
 * class, each game should have a least one strategy and may select among
 * several different ones for its <em>bots</em>.
 * </p>
 * 
 * <p>
 * GameBot implements the Player interface, hence it must provide a nick and
 * handle events. Nicks of <em>bots</em> are picked from a list, making sure the
 * it is not already being used in the game where <em>bot</em> will be added. As
 * GameBot implements the GameObserver interface, it can receive events sent by
 * its GameMaster.
 * </p>
 * 
 * <p>
 * GameBot extends Thread and uses the run() method to process process events.
 * This way, event processing is decoupled from event propagation. Threads in
 * Java follow the <strong>template method</strong> design pattern.
 * </p>
 */
public class GameBot extends Thread implements Player {

	class PlayerCards {
		Map<String, List<Card>> onTable = new HashMap<>();
		CardCollection hasPlayed = new CardCollection();

		public PlayerCards() {
		}

		/**
		 * Adds a list of cards to hasPlayed
		 * 
		 * @param played
		 */
		void playedCards(List<Card> played) {
			hasPlayed.addAllCards(played);
		}

		/**
		 * Returns map of <strong>Cards</strong> currently on the table
		 * 
		 * @return cards
		 */
		public List<Card> getCardsOnTable() {
			List<Card> cards = new ArrayList<>();
			for (String nick : onTable.keySet())
				cards.addAll(onTable.get(nick));
			return cards;
		}

		/**
		 * Returns all the cards played in the game so far
		 * 
		 * @return Cards played
		 */
		public CardCollection getHasPlayed() {
			return hasPlayed;
		}

		/**
		 * Represents PlayerCards as a string, containing the cards currently on the
		 * table, and all the cards that have been played.
		 */
		public String toString() {
			return "onTable: " + onTable + "\n hasPlayed" + hasPlayed + "\n CardsOnTable: " + getCardsOnTable();
		}
	}

	boolean hasEventToProcess = false;
	boolean hasToPlay = false;

	private GamePlayingStrategy strategy;
	private String nick;
	private GameMaster gameMaster;
	private CardCollection hand;
	private String mode;
	private int roundsCompleted = 0;
	private PlayerCards playerCards;

	private List<String> botNicks = Arrays.asList("Alpha", "Andy Roid", "Cyd", "Earl", "Erroid", "Greez", "Ije",
			"Ijyoid", "Mach", "Max", "Mechan", "Mig", "Nozzle", "Ofog", "Ozvroid", "Robbie", "Socket", "Test", "Uzax",
			"Wire");

	private String winner;
	private Map<String, Integer> points;
	private String hasTurn;

	/**
	 * A GameBot instance for a particular GameMaster instance. The latter instance
	 * influences the initialization of some fields. The nick must be different from
	 * those already in the game instance. The strategy must be given by GameMaster
	 * to ensure its adequacy.
	 * 
	 * @param gameMaster - here <em>bot</em> will be added
	 */
	public GameBot(GameMaster gameMaster) {
		this.gameMaster = gameMaster;
		setStrategy(gameMaster.getCardGameStrategy());
		hand = new CardCollection(gameMaster.getCardComparator());
		mode = gameMaster.getMode();
		nick = generateNick(gameMaster);
		playerCards = new PlayerCards();
		this.start();
	}

	/**
	 * The active part of the thread is responsible for processing events. This
	 * method is invoked when the <em>bot</em> is instantiated and must run until
	 * the game end, when the GameEndEvent is received. It uses the
	 * waitForEventToProcess() to passively wait for a notification of an event to
	 * process. It must play (a) card(s) on its GameMaster when required, or
	 * terminate the thread when the game ends. This method invokes a
	 * GamePlayingStrategy to pick the cards to play.
	 */
	@Override
	public void run() {
		while (!gameMaster.hasEnded()) {
			waitForEventToProcess();
			if (hasToPlay) {
				try {
					List<Card> cardsToPlay = strategy.pickCards(this);
					if (cardsToPlay.size() > 0 && cardsToPlay.get(0) != null)
						gameMaster.playCards(nick, cardsToPlay);
				} catch (CardGameException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Make this thread wait for the arrival of event to process. This method is
	 * meant to be called from run(). It does not block if there is an event pending
	 * to be processed; otherwise, it blocks on Object.wait() until the
	 * notifyEventArrived() is executed.
	 */
	synchronized void waitForEventToProcess() {
		while (!hasEventToProcess)
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		hasEventToProcess = false;
	}

	/**
	 * Method invoked when a new event in received by this bot. It notifies the
	 * threads blocked on Object.wait() for an event to process using
	 * Object.notifyAll().
	 */
	synchronized void notifyEventArrived() {
		hasEventToProcess = true;
		this.notifyAll();
	}

	/**
	 * <p>
	 * <strong>Description copied from interface: GameObserver</strong>
	 * </p>
	 * <p>
	 * Notify the observer and send her/him/it a list of cards. This event is
	 * typically the first one received by a player in a particular game instance.
	 * In some games this method may be invoked several times during a game
	 * instance. The method is meant to be invoked in a single observer/player and
	 * <strong>not</strong> to be broadcast.
	 * </p>
	 * 
	 * @param event - to send
	 */
	@Override
	public void notify(SendCardsEvent event) {
		hand.addAllCards(event.getCards());
	}

	/**
	 * <p>
	 * <strong>Description copied from interface: GameObserver</strong>
	 * </p>
	 * <p>
	 * Notify the observer of updates in a round. This event trigger the player to
	 * play her/his/it cards. These updates are sent when a player plays and are
	 * broadcast to provide the same information to all players simultaneously.
	 * </p>
	 * 
	 * @param event - to send
	 */
	@Override
	public void notify(RoundUpdateEvent event) {
		playerCards.onTable = event.getCardsOnTable();
		hasTurn = event.getNickWithTurn();
		roundsCompleted = event.getRoundsCompleted();
		mode = event.getMode();

		if (hasTurn.equals(nick)) {
			hasToPlay = true;
		} else {
			hasToPlay = false;
		}
		notifyEventArrived();
	}

	/**
	 * <p>
	 * <strong>Description copied from interface: GameObserver</strong>
	 * </p>
	 * <p>
	 * Notify the observer of the round conclusion. The event provides information
	 * on the round status. These notifications are broadcast to provide the same
	 * information to all players simultaneously.
	 * </p>
	 * 
	 * @param event - to send
	 */
	@Override
	public void notify(RoundConclusionEvent event) {
		playerCards.onTable = event.getCardsOnTable();
		playerCards.playedCards(playerCards.getCardsOnTable());
		roundsCompleted = event.getRoundsCompleted();
		points = event.getPoints();
		notifyEventArrived();
	}

	/**
	 * <p>
	 * <strong>Description copied from interface: GameObserver</strong>
	 * </p>
	 * <p>
	 * Notify the observer that the game has ended. The event provides information
	 * on the final game status. This event is the last in a game instance and is
	 * broadcast to all players.
	 * </p>
	 * 
	 * @param event - to send
	 */
	@Override
	public void notify(GameEndEvent event) {
		winner = event.getWinner();
		playerCards.onTable = event.getCardsOnTable();
		points = event.getPoints();
		roundsCompleted = event.getRoundsCompleted();
	}

	/**
	 * Current strategy for picking cards
	 * 
	 * @return the strategy
	 */
	public GamePlayingStrategy getStrategy() {
		return strategy;
	}

	/**
	 * Set the card picking strategy to the given one.
	 * 
	 * @param strategy - to set
	 */
	public void setStrategy(GamePlayingStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * <strong>Description copied from interface: Player</strong> Players nick, it
	 * will used as an ID.
	 * 
	 * @return nick - of player
	 */
	@Override
	public String getNick() {
		return nick;
	}

	/**
	 * Number of rounds completed in this game. Its 0 when the game starts.
	 * 
	 * @return rounds completed
	 */
	public int getRoundsCompleted() {
		return roundsCompleted;
	}

	/**
	 * Points received by each player in the game.
	 * 
	 * @return points map
	 */
	public Map<String, Integer> getPoints() {
		return points;
	}

	/**
	 * Has this game ended? This information is notified by the GameEndEvent.
	 * 
	 * @return true if the game has ended; false otherwise
	 */
	public boolean gameEnded() {
		return gameMaster.hasEnded();
	}

	/**
	 * Nick of the player that won the game: null if the game was not ended yet or
	 * ended in a tie.
	 * 
	 * @return nick or null
	 */
	public String getWinner() {
		return winner;
	}

	/**
	 * The collection of cards this player holds in their hand. These cards are
	 * normally received by events.
	 * 
	 * @return cards
	 */
	public CardCollection getHand() {
		return hand;
	}

	/**
	 * Change the cards this player holds in her hand <strong>for testing purposes
	 * only</strong>.
	 * 
	 * @param hand - to set
	 */
	public void setHand(CardCollection hand) {
		this.hand = hand;
	}

	/**
	 * Card comparator for this game
	 * 
	 * @return comparator
	 */
	public CardComparator getCardComparator() {
		return hand.getCardComparator();
	}

	/**
	 * Nick of player currently with turn. This is the next player to play. May be
	 * null in games where players don't play in turns.
	 * 
	 * @return nick or null
	 */
	public String getNickWithTurn() {
		return hasTurn;
	}

	/**
	 * Convenience method to check if there are no card is on the table. Player with
	 * turn is the first to play in this round.
	 * 
	 * @return true if no cards are on the table; false otherwise
	 */
	public boolean noCardsOnTable() {
		return (playerCards.onTable.size() == 0);
	}

	/**
	 * Convenience method to retrieve the card on table from player with given nick.
	 * Assumes that in this games players play a single card per turn. May return
	 * null if no card is on the table.
	 * 
	 * @param nick - of player
	 * @return card or null
	 */
	public Card getCardOnTable(String nick) {
		if (playerCards.onTable.get(nick) == null)
			return null;
		return playerCards.onTable.get(nick).get(0);
	}

	/**
	 * Convenience method to retrieve all cards on the table. Assumes each player
	 * played a single card, although some players may have not played yet in this
	 * turn.
	 * 
	 * @return cards on table
	 */
	public CardCollection getAllCardsOnTable() {
		return new CardCollection(hand.getCardComparator(), playerCards.getCardsOnTable());
	}

	/**
	 * All cards already played in this game.
	 * 
	 * @return collection of played cards
	 */
	public CardCollection getPlayedCards() {
		return playerCards.getHasPlayed();
	}

	/**
	 * Mode in which the game currently is. This information is sent by the game
	 * master using update events. It will be null if undefined.
	 * 
	 * @return mode or null
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * Change the mode is currently in - <strong>for testing purposes only</strong>.
	 * This information is normally sent by the game master using update events. It
	 * will be null if undefined.
	 * 
	 * @param mode - to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * Convenience method to retrieve the trump suite . That is, the suit of the
	 * card player by the eldest hand (the first player in this turn).
	 * 
	 * @return suit
	 */
	public CardSuit getSuitToFollow() {
		return gameMaster.getSuitToFollow();
	}

	/**
	 * Pick a name from a list of bot names, different from any of the nicks already
	 * in game.
	 * 
	 * @param gameMaster - where nick will be used
	 * @return nick
	 */
	protected String generateNick(GameMaster gameMaster) {
		String randomNick;
		do {
			Random rand = new Random();
			randomNick = botNicks.get(rand.nextInt(botNicks.size()));
		} while (gameMaster.getPlayerNicks().contains(randomNick));

		return randomNick;
	}

}

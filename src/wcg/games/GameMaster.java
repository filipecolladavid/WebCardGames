package wcg.games;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wcg.shared.CardGameException;
import wcg.shared.GameInfo;
import wcg.shared.cards.Card;
import wcg.shared.cards.CardSuit;
import wcg.shared.events.GameEndEvent;
import wcg.shared.events.RoundConclusionEvent;
import wcg.shared.events.RoundUpdateEvent;
import wcg.shared.events.SendCardsEvent;

/**
 * Abstract class common to all game masters. A game master is an object to
 * manage a particular kind of game. For example, a HEARTS game is managed by an
 * instance of HeartsGameMaster, which is a specialization of this class. It
 * provides core features such as: preparing deck of cards (shuffling); sending
 * initial hand to players; receive played cards; updating round information
 * (cards on table, round); keeping points etc.
 */
public abstract class GameMaster extends ObservableGame {
	/**
	 * Enumeration representing the stages of the game.
	 * <ol>
	 * <li>Preparing - expecting further players
	 * <li>Playing - all players joined, cards can be played
	 * <li>Concluded - no more cards can be played.
	 * </ol>
	 */
	static enum GameStage {
		PREPARING, PLAYING, CONCLUDED;
	}

	static final Map<String, Integer> COUNTER = new HashMap<>();

	private static final int MINUTES = 10;
	private static final int SECONDS = 60;
	private static final int MILLISECONDS = 1000;
	private static long expirationTime = MINUTES * SECONDS * MILLISECONDS;

	private GameInfo info;

	private GameStage stage;
	private int roundsCompleted = 0;
	private String mode = null;
	private CardSuit suitToFollow = null;

	private CardComparator comparator = DefaultCardComparator.getInstance();

	protected Players players = new Players();

	/**
	 * Create a game and assign it an id.
	 */
	protected GameMaster() {
		String gameId = makeGameId();
		Date currentTime = new Date();
		int playerCount = 0;

		stage = GameStage.PREPARING;

		info = new GameInfo(gameId, getGameName(), playerCount, currentTime, currentTime);
	}

	/**
	 * Time in milliseconds for a game to expire. Default is 10 minutes.
	 * 
	 * @return the expiration time.
	 */
	public static long getExpirationTime() {
		return expirationTime;
	}

	/**
	 * Change time in milliseconds for a game to expire. Default is 10 minutes. Non
	 * positive values make games expire immediately.
	 * 
	 * @param expirationTime - to set
	 */
	public static void setExpirationTime(long expirationTime) {
		GameMaster.expirationTime = expirationTime;
	}

	/**
	 * A string to identify this particular game instance. It must always return the
	 * same value and it must uniquely identify the instance. That is, distinct game
	 * instances have different IDs.
	 * 
	 * @return id of this game
	 */
	public String getGameId() {
		return info.getGameId();
	}

	public GameStage getStage() {
		return stage;
	}

	/**
	 * Information describing this game.
	 * 
	 * @return info on game
	 */
	public final GameInfo getInfo() {
		return info;
	}

	/**
	 * Has this game expired? I.e. the difference between the current time and the
	 * last access (add a player or play cards) is greater than the the defined
	 * expiration time?
	 * 
	 * @return true if expired; false otherwise
	 */
	public final boolean expired() {
		Date currentTime = new Date();
		return ((currentTime.getTime() - info.getLastAccessDate().getTime()) > expirationTime);
	}

	/**
	 * The card comparator used in this game. The class defined the
	 * DefaultCardComparator but may be redefined by concrete game rules.
	 * 
	 * @return comparator used in this collection
	 */
	protected CardComparator getCardComparator() {
		return comparator;
	}

	/**
	 * The deck of cards used by this game, backed by the defined card comparator.
	 * The default implementation uses CardCollection.getDeck() but concrete game
	 * rules may redefine this method to remove some card values, add other cards
	 * such as jokers, or use a double deck.
	 * 
	 * @return collection of cards to use in this game
	 */
	protected CardCollection getDeck() {
		return CardCollection.getDeck();
	}

	/**
	 * Is this game still accepting players?
	 * 
	 * @return true if accepting; false otherwise
	 */
	public boolean acceptsPlayers() {
		return info.getPlayersCount() < getNumberOfPlayers();
	}

	/**
	 * Adds given player to this game. If the final number of players is reached
	 * then game starts
	 * 
	 * @param player - to add
	 * @throws CardGameException - if not in preparing stage or if player already in
	 *                           game.
	 */
	public final void addPlayer(Player player) throws CardGameException {
		if (stage != GameStage.PREPARING)
			throw new CardGameException("Game not in PREPARING stage");
		if (players.playerIsPresent(player))
			throw new CardGameException("Player " + player.getNick() + " is already in game");
		if (!acceptsPlayers())
			throw new CardGameException("Number of players already reached");

		players.addPlayer(player, comparator);
		addObserver(player.getNick(), player);

		updateGameInfoAddPlayer();

		if (!acceptsPlayers())
			startPlaying();
	}

	/**
	 * Start playing this game: change stage to PLAYING, start the game and update
	 * players.
	 */
	final void startPlaying() {
		stage = GameStage.PLAYING;

		dealCards();
	}

	/**
	 * Deal cards to each player, set turn (if needed) and notify all players
	 */
	final void dealCards() {
		CardCollection deck = getDeck();
		deck.shuffle();

		if (isWithTurns()) {
			String nick = initialTurnInRound();
			if (nick != null)
				players.setPlayerWithTurn(nick);
			else
				players.setPlayerWithTurn(players.getNickFirstPlayer());
		}

		for (String nick : players.getPlayerNicks()) {
			CardCollection cardsToSend = deck.takeFirstCards(getCardsPerPlayer());
			sendCards(nick, cardsToSend);
			players.addCardsToHand(nick, cardsToSend);
		}

		RoundUpdateEvent event = new RoundUpdateEvent(getGameId(), players.getCardsOnTable(),
				players.getPlayerWithTurn(), roundsCompleted, mode);

		broadcast(event);
	}

	/**
	 * Send to player with the given nick the given card collection. These cards are
	 * recorded as part of the player's hand to later verify if played cards
	 * correspond to those (s)he/it is holding.
	 * 
	 * @param nick  - of player
	 * @param cards - to send
	 */
	protected final void sendCards(String nick, CardCollection cards) {
		SendCardsEvent event = new SendCardsEvent(info.getGameId(), cards.asList());

		notify(nick, event);
	}

	/**
	 * Convenience method to play a single card. In general players may play several
	 * cards at once, but many trick-taking games only allow a single card per trick
	 * (round), in the players turn. This method delegates in playCards(String,
	 * List).
	 * 
	 * 
	 * @param nick - of player
	 * @param card - to be played
	 * @throws CardGameException - if some game rule is violated
	 */
	public final void playCard(String nick, Card card) throws CardGameException {
		updateGameInfo();
		if (!stage.equals(GameStage.PLAYING))
			throw new CardGameException(info.getGameId() + " is not in playing stage");

		if (nick == null)
			throw new CardGameException("Please enter a valid nick");
		if (!players.getPlayerNicks().contains(nick))
			throw new CardGameException(nick + " is not in this game");
		if (isWithTurns() && !(nick.equals(getNickWithTurn())))
			throw new CardGameException(nick + ", it is not your turn");

		checkCards(nick, Arrays.asList(card));

		if (card == null)
			throw new CardGameException("This is an invalid card");
		if (!players.getHand(nick).containsCard(card))
			throw new CardGameException(nick + ": " + card.toString() + " is an invalid card");

		if (suitToFollow == null)
			suitToFollow = card.getSuit();

		players.moveCardHandTable(nick, card);

		if (isWithTurns())
			players.setPlayerWithTurn(nextInTurn());

		if (!players.checkAllPlayersPlayed()) {
			RoundUpdateEvent event = new RoundUpdateEvent(getGameId(), players.getCardsOnTable(),
					players.getPlayerWithTurn(), roundsCompleted, mode);
			broadcast(event);
		} else
			concludeRound();
	}

	/**
	 * <p>
	 * Play your cards. It may be more than one card if this is allowed by the game
	 * rules. This method checks if this is cards violate game rules. These may
	 * cover
	 * </p>
	 * <ul>
	 * <li>is this game in the playing stage?
	 * <li>is this the player's turn? (some games don't have turns)
	 * <li>were these cards on the player's hand?
	 * <li>is it allowed to play these cards at this moment?
	 * </ul>
	 * 
	 * @param nick  - of player
	 * @param cards - to be played
	 * @throws CardGameException - if some game rule is violated
	 */
	public synchronized final void playCards(String nick, List<Card> cards) throws CardGameException {
		updateGameInfo();
		if (!stage.equals(GameStage.PLAYING))
			throw new CardGameException(info.getGameId() + " is not in playing stage");

		if (nick == null)
			throw new CardGameException("Please enter a valid nick");
		if (!players.getPlayerNicks().contains(nick))
			throw new CardGameException(nick + " is not in this game");
		if (isWithTurns() && !(nick.equals(getNickWithTurn())))
			throw new CardGameException(nick + ", it is not your turn");

		checkCards(nick, cards);

		for (Card card : cards) {
			if (card == null)
				throw new CardGameException("This is an invalid card");
			if (!players.getHand(nick).containsCard(card))
				throw new CardGameException(nick + ": " + card.toString() + " is an invalid card");

			if (players.isTableEmpty())
				suitToFollow = card.getSuit();

			players.moveCardHandTable(nick, card);
		}

		if (isWithTurns())
			players.setPlayerWithTurn(nextInTurn());

		if (!players.checkAllPlayersPlayed()) {
			RoundUpdateEvent event = new RoundUpdateEvent(getGameId(), players.getCardsOnTable(),
					players.getPlayerWithTurn(), roundsCompleted, mode);
			broadcast(event);
		} else
			concludeRound();
	}

	/**
	 * Return the next nick in the list of players. If the turn was on the the last
	 * player on the list, the next will be the first one.
	 * 
	 * @return nick of player to play next
	 */
	String nextInTurn() {
		return players.getNextInTurn();
	}

	/**
	 * Conclude round by broadcasting the conclusion event, updating points for all
	 * players, setting turn for the next round
	 */
	void concludeRound() {

		beforeRoundConclusion();

		suitToFollow = null;

		roundsCompleted++;

		for (String nick : getPlayerNicks()) {
			players.addToPlayersPoints(nick, getRoundPoints(nick));
		}

		players.setPlayerWithTurn(initialTurnInRound());

		RoundUpdateEvent event = new RoundUpdateEvent(getGameId(), players.getCardsOnTable(),
				players.getPlayerWithTurn(), getRoundsCompleted(), getMode());
		broadcast(event);

		RoundConclusionEvent roundConclusionEvent = new RoundConclusionEvent(getGameId(), players.getCardsOnTable(),
				roundsCompleted, players.getPlayersPoints());
		broadcast(roundConclusionEvent);

		if (hasEnded()) {
			GameEndEvent gameEndEvent = new GameEndEvent(getGameId(), players.getCardsOnTable(), roundsCompleted,
					getWinner(), players.getPlayersPoints());
			broadcast(gameEndEvent);
		}

		players.cleanTable();

	}

	/**
	 * Convenience method to create a new empty card collection backed by the this
	 * games's CardComparator.
	 * 
	 * @return collection
	 */
	public CardCollection newCardCollection() {
		return new CardCollection(comparator);
	}

	/**
	 * A list with the nicks of players in this game.
	 * 
	 * @return list with nicks
	 */
	protected List<String> getPlayerNicks() {
		return players.getPlayerNicks();
	}

	/**
	 * The nick of the player currently with the turn to play
	 * 
	 * @return nick with turn
	 */
	protected String getNickWithTurn() {
		return players.getPlayerWithTurn();
	}

	/**
	 * The nick of the player currently with the turn to play.
	 * 
	 * @return mode
	 */
	protected String getMode() {
		return mode;
	}

	/**
	 * Change current game mode
	 * 
	 * @param mode - to set
	 */
	protected void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * Get a cards in given player's hand.
	 * 
	 * @param nick - of player
	 * @return card collection
	 */
	protected CardCollection getHand(String nick) {
		return players.getHand(nick);
	}

	/**
	 * Retrieve cards on table of player with given nick.
	 * 
	 * @param nick - of player
	 * @return card collection
	 */
	protected CardCollection getCardsOnTable(String nick) {
		return new CardCollection(comparator, players.getCardsOnTable(nick));
	}

	/**
	 * Convenience method to retrieve the single card on table of player with given
	 * nick.
	 * 
	 * @param nick - of player
	 * @return card
	 */
	protected Card getCardOnTable(String nick) {
		return players.getCardOnTable(nick);
	}

	/**
	 * Convenience method with suit to follow: the suit of the first card played in
	 * this round. May be null if there are no cards on the table.
	 * 
	 * @return suit or null
	 */
	public CardSuit getSuitToFollow() {
		return suitToFollow;
	}

	/**
	 * Number of rounds completed in this game. Its 0 when the game starts.
	 * 
	 * @return rounds
	 */
	protected int getRoundsCompleted() {
		return roundsCompleted;
	}

	/**
	 * Nick of player with most points, usually the winner.
	 * 
	 * @return winner
	 */
	protected String nickWithMostPoints() {
		return players.nickWithMostPoints();
	}

	/**
	 * A game playing strategy for this kind of game. This method can return
	 * different playing strategies.
	 * 
	 * @return automatic player
	 */
	protected abstract GamePlayingStrategy getCardGameStrategy();

	/**
	 * The name of this kind of game. The name should be the same used by the game
	 * factory and all in capitals (e.g. HEARTS).
	 * 
	 * @return name of game
	 */
	protected abstract String getGameName();

	/**
	 * Number of players in this game.
	 * 
	 * @return number of players
	 */
	protected abstract int getNumberOfPlayers();

	/**
	 * Number of cards each player receives at the start
	 * 
	 * @return number of cards
	 */
	protected abstract int getCardsPerPlayer();

	/**
	 * Do players play in turn, one after the other, in the order they were given in
	 * the constructor? Or can they play in any order?
	 * 
	 * @return true if game is with turns; false otherwise
	 */
	protected abstract boolean isWithTurns();

	/**
	 * Method invoked when all players joined the game.
	 */
	protected abstract void startGame();

	/**
	 * Check if these cards can be played at this moment. Throw an exception if a
	 * game specific rule was violated. General rules such as playing in turn,
	 * playing cards in hand, are already covered and don't need to be addressed.
	 * 
	 * @param nick  - of player
	 * @param cards - to check
	 * @throws CardGameException - if a rule was violated
	 */
	protected abstract void checkCards(String nick, List<Card> cards) throws CardGameException;

	/**
	 * This method is invoked before concluding the round, after all players have
	 * played.
	 */
	protected abstract void beforeRoundConclusion();

	/**
	 * Points collected by the player with given nick in the last round. This method
	 * is invoked after the round is concluded.
	 * 
	 * @param nick - of player
	 * @return points in round
	 */
	protected abstract int getRoundPoints(String nick);

	/**
	 * In turn based games, provides the nick of the player player to play in the
	 * next round. Other players play in a predefined order
	 * 
	 * @return nick of first player in round
	 */
	protected abstract String initialTurnInRound();

	/**
	 * Has this game ended?
	 * 
	 * @return true if game ended; false otherwise
	 */
	protected abstract boolean hasEnded();

	/**
	 * Who is the winner of this game? This method is invoked if game hasEnded().
	 * 
	 * @return nick of winner
	 */
	protected abstract String getWinner();

	/**
	 * Generate a string id for a game instance using the game name as prefix and a
	 * counter. This method must be invoked only once during instantiation.
	 * 
	 * @return id of this game instance
	 */
	String makeGameId() {
		String gameName = getGameName();
		Integer gameCounter = COUNTER.get(gameName);
		if (gameCounter == null) {
			COUNTER.put(gameName, 0);
		} else {
			COUNTER.replace(gameName, gameCounter + 1);
		}

		String gameId = gameName + COUNTER.get(gameName);

		return gameId;
	}

	/**
	 * Simple method to update the GameInfo when a new player is added
	 */
	private void updateGameInfoAddPlayer() {
		GameInfo currentInfo = info;

		info = new GameInfo(currentInfo.getGameId(), currentInfo.getGameName(), currentInfo.getPlayersCount() + 1,
				currentInfo.getStartDate(), new Date());
	}

	/**
	 * Simple method to update the GameInfo with a new lastAccessDate
	 */
	private void updateGameInfo() {
		GameInfo currentInfo = info;

		info = new GameInfo(currentInfo.getGameId(), currentInfo.getGameName(), currentInfo.getPlayersCount(),
				currentInfo.getStartDate(), new Date());
	}

}
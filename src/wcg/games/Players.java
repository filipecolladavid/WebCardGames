package wcg.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import wcg.shared.cards.Card;

/**
 * An auxiliary class to store the data about the set of players and the table
 * inside a game
 */
public class Players implements Iterable<Player> {

	private final List<Player> playersList = new ArrayList<>();
	private final Map<String, Player> playersMap = new HashMap<>();
	private final Map<String, Integer> playersPoints = new HashMap<>();
	private final Map<String, CardCollection> playersHands = new HashMap<>();
	private final Map<String, List<Card>> onTable = new HashMap<>();
	private String playerWithTurn = null;
	private String playerNextTurn = null;

	public Players() {
	}

	/**
	 * Adds a player and instantiates it's fields
	 * 
	 * @param player
	 * @param comparator
	 */
	public void addPlayer(Player player, CardComparator comparator) {
		Integer STARTING_POINTS = 0;
		String nick = player.getNick();
		playersList.add(player);
		playersMap.put(nick, player);
		playersPoints.put(nick, STARTING_POINTS);
		playersHands.put(nick, new CardCollection(comparator));
	}

	/**
	 * Gets the first player on the list
	 * 
	 * @return nick - first player on the list
	 */
	public String getNickFirstPlayer() {
		return playersList.get(0).getNick();
	}

	/**
	 * Gets player with nick
	 * 
	 * @param nick
	 * @return player - with nick as nickname
	 */
	public Player getPlayerWithNick(String nick) {
		return playersMap.get(nick);
	}

	/**
	 * Checks if player exists in the game
	 * 
	 * @param player
	 * @return true if exists | false if doesn't exist
	 */
	public boolean playerIsPresent(Player player) {
		return playersMap.containsValue(player);
	}

	/**
	 * Gets the list of players present in the game
	 * 
	 * @return list - of players nicks
	 */
	public List<String> getPlayerNicks() {
		List<String> nicks = new ArrayList<>();
		for (String nick : playersMap.keySet())
			nicks.add(nick);
		return nicks;
	}

	/**
	 * Adds points to the current score of the player with <b>nick<b>
	 * 
	 * @param nick   - of the player
	 * @param points - to add to the current score
	 */
	public void addToPlayersPoints(String nick, Integer points) {
		Integer currentPoints = playersPoints.get(nick);
		playersPoints.replace(nick, currentPoints + points);
	}

	/**
	 * Gets points from player with nick
	 * 
	 * @param nick
	 * @return points - of the player
	 */
	public Integer getPointsFromNick(String nick) {
		return playersPoints.get(nick);
	}

	/**
	 * Getter for playerPoints
	 * 
	 * @return playersPoints
	 */
	public Map<String, Integer> getPlayersPoints() {
		return playersPoints;
	}

	/**
	 * Gets the nick with most points
	 * 
	 * @return nick - of the player with most points
	 */
	public String nickWithMostPoints() {
		Integer mostPoints = Integer.MIN_VALUE;

		String nickWithMostPoints = null;

		for (String nick : playersPoints.keySet())
			if (playersPoints.get(nick) > mostPoints) {
				nickWithMostPoints = nick;
				mostPoints = playersPoints.get(nick);
			}

		return nickWithMostPoints;
	}

	/**
	 * Returns a CardCollection that the player is currently holding
	 * 
	 * @param nick - of the player
	 * @return hand - CardCollection that the player is holding
	 */
	public CardCollection getHand(String nick) {
		return playersHands.get(nick);
	}

	/**
	 * Adds a CardCollection to the hand of the player with <b>nick<b>
	 * 
	 * @param nick
	 * @param cards
	 */
	public void addCardsToHand(String nick, CardCollection cards) {
		CardCollection nicksHand = playersHands.get(nick);
		nicksHand.addAllCards(cards.asList());
	}

	/**
	 * Removes <b>card<b> from player with <b>nick<b>
	 * 
	 * @param nick
	 * @param card
	 */
	public void takeCardFromHand(String nick, Card card) {
		CardCollection cards = playersHands.get(nick);
		cards.takeCard(card);
	}

	/**
	 * Moves card from Player's hand to the Table
	 * 
	 * @param nick
	 * @param card
	 */
	public void moveCardHandTable(String nick, Card card) {

		List<Card> cards;
		if (!onTable.containsKey(nick)) {
			cards = new ArrayList<>();
			onTable.put(nick, cards);
		} else
			cards = onTable.get(nick);

		cards.add(card);

		CardCollection cardsInHand = playersHands.get(nick);
		cardsInHand.takeCard(card);
	}

	/**
	 * Gets cards of the table played by a player with <b>nick<b>
	 * 
	 * @param nick
	 * @return list - of cards played by the player with <b>nick<b>
	 */
	public List<Card> getCardsOnTable(String nick) {
		return onTable.get(nick);
	}

	/**
	 * Returns a Map with the cards on the table with the name of the player and
	 * their respective played cards
	 * 
	 * @return Map - of the cards on the table
	 */
	public Map<String, List<Card>> getCardsOnTable() {
		return onTable;
	}

	/**
	 * Returns the card on the table played by the player with <b>nick<b>
	 * 
	 * @apiNote used when players only play on card at a time
	 * 
	 * @param nick - of the player who played the card
	 * @return card - of the player who played the card
	 */
	public Card getCardOnTable(String nick) {
		List<Card> onTableNick = onTable.get(nick);
		return onTableNick.get(0);
	}

	/**
	 * Returns if table has cards on it or not
	 * 
	 * @return true if is empty | false if has cards
	 */
	public boolean isTableEmpty() {
		return onTable.isEmpty();
	}

	/**
	 * Removes all cards from the table - to be used at the end of each round
	 */
	public void cleanTable() {
		onTable.clear();
	}

	/**
	 * Checks if all players have played in this round
	 * 
	 * @return true - if all players played | false - if not all players have played
	 */
	public boolean checkAllPlayersPlayed() {
		boolean flagPlayed = true;
		boolean flagSameNumberOfCards = true;
		int numberOfCards = 0;
		for (String nick : playersMap.keySet()) {
			if (onTable.get(nick) == null) {
				flagPlayed = false;
				break;
			} else {
				if (numberOfCards == 0) {
					numberOfCards = onTable.get(nick).size();
				}
				if (onTable.get(nick).size() != numberOfCards) {
					flagSameNumberOfCards = false;
					break;
				}
			}
		}
		return flagPlayed && flagSameNumberOfCards;
	}

	/**
	 * Returns who's next to play
	 * 
	 * @return nick - of the player who's next
	 */
	public String getNextInTurn() {
		Player currentPlayer = playersMap.get(playerWithTurn);
		Player nextPlayer = null;

		int indexOfPlayerWithTurn = playersList.indexOf(currentPlayer);

		if (indexOfPlayerWithTurn == playersList.size() - 1)
			nextPlayer = playersList.get(0);
		else
			nextPlayer = playersList.get(indexOfPlayerWithTurn + 1);

		return nextPlayer.getNick();
	}

	/**
	 * Returns nick of the player that holds turn
	 * 
	 * @return nick - of the playerWithTurn
	 */
	public String getPlayerWithTurn() {
		return playerWithTurn;
	}

	/**
	 * Sets player with turn
	 * 
	 * @param playerWithTurn - the playerWithTurn to set
	 */
	public void setPlayerWithTurn(String playerWithTurn) {
		this.playerWithTurn = playerWithTurn;
	}

	/**
	 * Getter for playerNextTurn
	 * 
	 * @return playerNextTurn
	 */
	public String getPlayerNextTurn() {
		return playerNextTurn;
	}

	/**
	 * Setter for playerNextTurn
	 * 
	 * @param playerNextTurn - nick of player to be set
	 */
	public void setPlayerNextTurn(String playerNextTurn) {
		this.playerNextTurn = playerNextTurn;
	}

	@Override
	public Iterator<Player> iterator() {
		return playersList.iterator();
	}
}

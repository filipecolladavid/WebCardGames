package wcg.games;

import java.util.List;

import wcg.shared.cards.Card;

/**
 * A strategy for playing a particular game by an automated player (a bot).
 * Declares a method that receives the bot and had access to its state, and
 * returns a list of cards to play. These cards must be part of the player's
 * hand (obtainable through the bot's) and adjusted by to the game's rules. This
 * interface is the abstract component of the Strategy design pattern.
 */
public interface GamePlayingStrategy {

	/**
	 * Play cards for bot following a certain strategy.
	 * 
	 * @param bot - playing the cards
	 * @return cards to play
	 */
	List<Card> pickCards(GameBot bot);

}

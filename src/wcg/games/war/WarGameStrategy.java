package wcg.games.war;

import java.util.ArrayList;
import java.util.List;

import wcg.games.GameBot;
import wcg.games.GamePlayingStrategy;
import wcg.shared.cards.Card;

/**
 * A game strategy for WAR. It normally plays a single cards if mode is null and
 * plays 3 cards otherwise.
 */
public class WarGameStrategy implements GamePlayingStrategy {

	@Override
	public List<Card> pickCards(GameBot bot) {
		List<Card> cardsPicked = new ArrayList<>();
		if (bot.getMode() != "War")
			cardsPicked.add(bot.getHand().takeFirstCard());
		else
			cardsPicked.addAll(bot.getHand().takeFirstCards(3).asList());

		return cardsPicked;
	}
}

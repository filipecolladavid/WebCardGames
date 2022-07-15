package wcg.games;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import wcg.games.TestPlayers.TestPlayer;
import wcg.shared.CardGameException;
import wcg.shared.cards.Card;
import wcg.shared.cards.CardValue;
import wcg.shared.events.GameEndEvent;
import wcg.shared.events.RoundConclusionEvent;
import wcg.shared.events.RoundUpdateEvent;
import wcg.shared.events.SendCardsEvent;

/**
 * A collection of simple players for performing tests
 */
public class TestPlayers implements Iterable<TestPlayer> {

	/**
	 * A simple player to use in tests.
	 * It just collects data from events sent by {@code GameMaster} extensions
	 * and doesn't automatically react to update events, 
	 * as {@code GameBot} extensions do.
	 */
	public class TestPlayer implements Player {
		private String nick;
		public CardCollection hand = new CardCollection();
		private  Map<String, List<Card>> myOnTable;
		public String nickWithTurn;
		private String mode;	
		public String winner = null;
		public Map<String, Integer> points;

		public TestPlayer(String nick) {
			this.nick = nick;
		}

		public boolean hasTurn() {
			return nickWithTurn.equals(nick);
		}

		/**
		 * Check if has card in hand with given value
		 * @param value
		 * @return
		 */
		public boolean hasCardWithValue(CardValue value) {
			for(Card card: hand)
				if(card.getValue() == value)
					return true;
			return false;
		}


		@Override
		public String getNick() {
			return nick;
		}
		
		/**
		 * Game mode, received by update events
		 * 
		 * @return
		 */
		public String getMode() {
			return mode;
		}
		
		/**
		 * Cards on table from given player
		 * @param nick of player
		 * @return cards
		 */
		public List<Card> GetCardsOntableFrom(String nick) {
			return myOnTable.get(nick);
		}

		@Override
		public void notify(SendCardsEvent event) {
			hand.addAllCards(event.getCards());
		}

		@Override
		public void notify(RoundUpdateEvent event) {
			myOnTable = event.getCardsOnTable();
			nickWithTurn = event.getNickWithTurn();
			mode = event.getMode();
		}
		
		@Override
		public void notify(RoundConclusionEvent event) {
			myOnTable = event.getCardsOnTable();
			points = event.getPoints();
		}

		@Override
		public void notify(GameEndEvent event) {
			winner = event.getWinner();
		}
	}

	List<TestPlayer> players = new ArrayList<>();
	
	/**
	 * Create a collection of n test players
	 * @param n number of test players
	 */
	public TestPlayers(int n) {
		for(int p=0; p < n; p++)
			players.add(new TestPlayer("P"+p));
	}
	
	/**
	 * Player with given index
	 * 
	 * @param index of player
	 * @return player
	 */
	public TestPlayer getPlayer(int index) {
		return players.get(index);
	}
	
	/**
	 * Add all players to given game master.
	 * 
	 * @param gameMaster
	 * @throws CardGameException 
	 */
	public void addPlayersTo(GameMaster gameMaster) throws CardGameException {
		for(TestPlayer player:players) 
			gameMaster.addPlayer(player);
	}
	
	/**
	 * Player with given nick
	 * 
	 * @param nick of player
	 * @return player
	 */
	public TestPlayer getPlayerWithNick(String nick) {
		for(TestPlayer player:players) 
			if(player.getNick().equals(nick))
				return player;
		
		throw new RuntimeException("no player found with nick "+nick);
	}
	
	/**
	 * Player currently with turn
	 * @param gameMaster
	 * @return
	 */
	public TestPlayer getPlayerWithTurn(GameMaster gameMaster) {
		return getPlayerWithNick(gameMaster.getNickWithTurn());
	}
	
	@Override
	public Iterator<TestPlayer> iterator() {
		return players.iterator();
	}
}
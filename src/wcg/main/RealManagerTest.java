package wcg.main;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wcg.WebCardGameTest;
import wcg.games.CardCollection;
import wcg.shared.CardGameException;
import wcg.shared.cards.Card;
import wcg.shared.cards.CardSuit;

/**
 *
 */
public class RealManagerTest extends WebCardGameTest {

	/**
	 * A class similar to TestPlayers but using manager rather than Gamemaster.
	 *
	 */
	class TestPlayers {

		/**
		 * Class to collect data from events
		 *
		 */
		class TestPlayer {
			String nick;
			String password;
			CardCollection hand;
			String nickWithTurn;
			String mode;
			Map<String, List<Card>> cardsOnTable;
			CardCollection allCardsOnTable;
			boolean gameEnded;
			int roundsCompleted;

			TestPlayer(String nick) {

			}

			/**
			 * @throws CardGameException
			 */
			void processEvents() throws CardGameException {

			}
		}

		List<TestPlayer> players;

		/**
		 * Create a collection of n test players
		 * 
		 * @param n - number of test players
		 */
		public TestPlayers(int n) {

		}

		/**
		 * Player with given index
		 * 
		 * @param index - of player
		 * @return player
		 */
		public TestPlayer getPlayer(int index) {
			return null;
		}

		/**
		 * Add all players to given game.
		 * 
		 * @param gameId - of game
		 * @throws CardGameException
		 */
		public void addPlayersTo(String gameId) throws CardGameException {

		}

		/**
		 * Player with given nick
		 * 
		 * @param nick - of player
		 * @return player
		 */
		public TestPlayer getPlayerWithNick(String nick) {
			return null;
		}

		/**
		 * Player currently with turn
		 * 
		 * @return nickWithTurn
		 */
		public TestPlayer getPlayerWithTurn() {
			return null;
		}

		/**
		 * Process events in all test players. Invoke this method before using player's
		 * state.
		 * 
		 * @throws CardGameException
		 */
		public void processAllEvents() throws CardGameException {

		}

		/**
		 * Suit of the first cards played in this round. May be null if no card played
		 * yet.
		 * 
		 * @return suit or null
		 */
		public CardSuit getSuitToFollow() {
			return null;
		}

		public Iterator<TestPlayer> iterator() {
			return null;
		}
	}

	static Manager manager;

	RealManagerTest() {

	}

	@BeforeAll
	static void setUpBeforeClass() throws Exception {

	}

	@BeforeEach
	void setUp() throws Exception {

	}

	@AfterAll
	static void cleanUpAfterClass() throws Exception {

	}

	/**
	 * Check that an instance is available
	 */
	@Test
	void testGetInstance() {

	}

	/**
	 * Check if the names of the implemented games are reported by getGameNames()
	 */
	@Test
	void testGetGameNames() {

	}

	/**
	 * Check if an empty list of games is reported
	 */
	@Test
	void testGetAvailableGames() {

	}

	/**
	 * Create several games and check that number of available games increases
	 * accordingly
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testGetAvailableGames_afterCreateGame() throws CardGameException {

	}

	/**
	 * Check that game creation returns a game id
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testCreateGame() throws CardGameException {

	}

	/**
	 * Check that an invalid game name raises an exception
	 */
	@Test
	void testCreateGame_invalid() {

	}

	/**
	 * Check adding a player, without registering, with invalid password
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testAddPlayer() throws CardGameException {

	}

	/**
	 * @throws CardGameException
	 */
	@Test
	void testAddBot() throws CardGameException {

	}

	/**
	 * Create a game but ignore the id when adding a bot. It should raise an
	 * exception.
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testAddBot_invalid() throws CardGameException {

	}

	/**
	 * Check that players added to a game receive events from that game.
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testGetRecentEvents() throws CardGameException {

	}

	/**
	 * Test playing WAR a do a invalid move:3 cards as first play
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testPlayCards_WAR_invalidPlay() throws CardGameException {

	}

	/**
	 * Test playing HEARTS to the end
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testPlayCards_HEARTS() throws CardGameException {

	}
}

package wcg.main;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wcg.WebCardGameTest;
import wcg.games.CardCollection;
import wcg.main.ManagerTest.TestManagerPlayers.TestManagerPlayer;
import wcg.shared.CardGameException;
import wcg.shared.GameInfo;
import wcg.shared.cards.Card;
import wcg.shared.cards.CardSuit;
import wcg.shared.events.GameEndEvent;
import wcg.shared.events.GameEvent;
import wcg.shared.events.RoundConclusionEvent;
import wcg.shared.events.RoundUpdateEvent;
import wcg.shared.events.SendCardsEvent;

/**
 * This test class is incomplete. You have to code it! You may (and should) add
 * other tests and define auxiliary methods and even inner classes.
 */
class ManagerTest extends WebCardGameTest {

	/**
	 * A class similar to TestPlayers but using manager rather than GameMaster.
	 * Structure from wcg.main.RealManagerTest and based on
	 * test/wcg.games.TestPlayers
	 */
	class TestManagerPlayers implements Iterable<TestManagerPlayer> {

		/**
		 * Class to collect data from events
		 *
		 */
		class TestManagerPlayer {
			String nick;
			String password;
			CardCollection hand = new CardCollection();
			String nickWithTurn;
			String mode;
			Map<String, List<Card>> cardsOnTable = new HashMap<>();
			boolean gameEnded = false;
			int roundsCompleted;
			Map<String, Integer> points;
			CardSuit suitToFollow = null;
			String winner;

			TestManagerPlayer(String nick, String password) {
				this.nick = nick;
				this.password = password;
			}

			/**
			 * Cycles through Manager's getRecentEvents() for the specific player and sets
			 * values as needed
			 * 
			 * @throws CardGameException
			 */
			void processEvents() throws CardGameException {
				List<GameEvent> events = manager.getRecentEvents(nick, password);
				for (GameEvent event : events) {
					if (event instanceof SendCardsEvent) {
						hand.addAllCards(((SendCardsEvent) event).getCards());
					}
					if (event instanceof RoundUpdateEvent) {
						Map<String, List<Card>> eventOnTable = ((RoundUpdateEvent) event).getCardsOnTable();

						// When there isn't a suit to follow yet, and the first card is played, set the
						// suit to follow
						if (suitToFollow == null && eventOnTable.size() == 1) {
							Card card = null;
							for (String nick : eventOnTable.keySet())
								card = eventOnTable.get(nick).get(0);

							suitToFollow = card.getSuit();
						}

						cardsOnTable = eventOnTable;
						nickWithTurn = ((RoundUpdateEvent) event).getNickWithTurn();
						mode = ((RoundUpdateEvent) event).getMode();
						roundsCompleted = ((RoundUpdateEvent) event).getRoundsCompleted();
					}
					if (event instanceof RoundConclusionEvent) {
						cardsOnTable = ((RoundConclusionEvent) event).getCardsOnTable();
						points = ((RoundConclusionEvent) event).getPoints();
						roundsCompleted = ((RoundConclusionEvent) event).getRoundsCompleted();
						suitToFollow = null;
					}
					if (event instanceof GameEndEvent) {
						winner = ((GameEndEvent) event).getWinner();
						gameEnded = true;
						roundsCompleted = ((GameEndEvent) event).getRoundsCompleted();
					}
				}
			}

			/**
			 * Gets the player's nick
			 * 
			 * @return nick - of player
			 */
			public String getNick() {
				return nick;
			}

			/**
			 * Gets the player's password
			 * 
			 * @return password - of player
			 */
			public String getPassword() {
				return password;
			}

			/**
			 * Gets the player's mode - all player's should have the same
			 * 
			 * @return mode - of player
			 */
			public String getMode() {
				return mode;
			}

			/**
			 * Returns a flag about whether the game has ended or not. Only true if a
			 * GameEndEvent has been sent and processed
			 * 
			 * @return True if the game has Ended. False otherwise
			 */
			public boolean hasEnded() {
				return gameEnded;
			}

			/**
			 * Returns the player's hand
			 * 
			 * @return hand - of player
			 */
			public CardCollection getHand() {
				return hand;
			}

			/**
			 * Returns the game's winner. Only set when a GameEndEvent is received
			 * 
			 * @return winner - of game
			 */
			public String getWinner() {
				return winner;
			}
		}

		List<TestManagerPlayer> players = new ArrayList<>();

		/**
		 * Create a collection of n test players
		 * 
		 * @param n - number of test players
		 */
		public TestManagerPlayers(int n) {
			for (int p = 0; p < n; p++)
				players.add(new TestManagerPlayer("P" + p, "P" + p));
		}

		/**
		 * Player with given index
		 * 
		 * @param index - of player
		 * @return player
		 */
		public TestManagerPlayer getPlayer(int index) {
			return players.get(index);
		}

		/**
		 * Add all players to given game.
		 * 
		 * @param gameId - of game
		 * @throws CardGameException
		 */
		public void addPlayersTo(String gameId) throws CardGameException {
			for (TestManagerPlayer player : players) {
				manager.registerPlayer(player.getNick(), player.getPassword());
				manager.addPlayer(gameId, player.getNick(), player.getPassword());
			}
		}

		/**
		 * Player with given nick
		 * 
		 * @param nick - of player
		 * @return player
		 */
		public TestManagerPlayer getPlayerWithNick(String nick) {
			for (TestManagerPlayer player : players)
				if (player.getNick().equals(nick))
					return player;

			throw new RuntimeException("no player found with nick " + nick);
		}

		/**
		 * Player currently with turn
		 * 
		 * @return nickWithTurn
		 */
		public TestManagerPlayer getPlayerWithTurn() {
			for (TestManagerPlayer player : players)
				if (player.getNick().equals(player.nickWithTurn))
					return player;

			return null;
		}

		/**
		 * Process events in all test players. Invoke this method before using player's
		 * state.
		 * 
		 * @throws CardGameException
		 */
		public void processAllEvents() throws CardGameException {
			for (TestManagerPlayer player : players) {
				player.processEvents();
			}
		}

		/**
		 * Suit of the first cards played in this round. May be null if no card played
		 * yet.
		 * 
		 * @return suit or null
		 */
		public CardSuit getSuitToFollow() {
			return players.get(0).suitToFollow;
		}

		@Override
		public Iterator<TestManagerPlayer> iterator() {
			return players.iterator();
		}
	}

	static Manager manager;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		manager = Manager.getInstance();
	}

	@BeforeEach
	void setUp() throws Exception {
		manager.reset();
	}

	@AfterAll
	static void cleanUpAfterClass() throws Exception {
		manager.reset();
	}

	/**
	 * Check that an instance is available
	 */
	@Test
	void testGetInstance() {
		assertNotNull(manager, "instance of manager expected");
	}

	/**
	 * Check if the names of the implemented games are reported by getGameNames()
	 */
	@Test
	void testGetGameNames() {
		List<String> games = manager.getGameNames();

		assertAll(() -> assertNotNull(games, "a list of games was returned"),
				() -> assertTrue(games.contains("WAR"), "WAR is available"),
				() -> assertTrue(games.contains("HEARTS"), "HEARTS is available"));
	}

	/**
	 * Check if an empty list of games is reported
	 */
	@Test
	void testGetAvailableGames() {
		List<GameInfo> gameInfos = manager.getAvailableGameInfos();
		assertEquals(0, gameInfos.size(), "an empty list of game infos should be returned");
	}

	/**
	 * Create several games and check that number of available games increases
	 * accordingly
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testGetAvailableGames_afterCreateGame() throws CardGameException {
		for (int i = 1; i <= 10; i++) {
			if (i % 2 == 0)
				manager.createGame("WAR");
			else
				manager.createGame("HEARTS");
			List<GameInfo> gameInfos = manager.getAvailableGameInfos();
			assertEquals(i, gameInfos.size(), "The number of game infos should increase when adding games");
		}
	}

	/**
	 * Check that game creation returns a game id
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testCreateGame() throws CardGameException {
		for (String gameName : manager.getGameNames()) {
			String gameId = manager.createGame(gameName);
			assertNotNull(gameId, "should return a game id");
		}
	}

	/**
	 * Check that an invalid game name raises an exception
	 */
	@Test
	void testCreateGame_invalid() {
		assertThrows(CardGameException.class, () -> manager.createGame("invalidGame"));
	}

	/**
	 * Check adding a player, without registering, with invalid password
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testAddPlayer() throws CardGameException {
		String gameId = manager.createGame("WAR");

		assertThrows(CardGameException.class, () -> manager.addPlayer(gameId, "NICK", null));
	}

	/**
	 * Test adding multiple players, verifying that GameInfo informs the correct
	 * number of players.
	 */
	@Test
	void testAddMultiplePlayers() throws CardGameException {
		for (int i = 1; i <= 3; i++) {
			String gameId = manager.createGame("HEARTS");
			TestManagerPlayers testPlayers = new TestManagerPlayers(i);
			testPlayers.addPlayersTo(gameId);

			List<GameInfo> gameInfos = manager.getAvailableGameInfos();
			GameInfo gameInfo = gameInfos.get(0);
			assertEquals(i, gameInfo.getPlayersCount(), "The number of players should be " + i + ".");

			manager.reset();
		}
	}

	/**
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testAddBot() throws CardGameException {
		String gameId = manager.createGame("HEARTS");
		manager.addBotPlayer(gameId);

		List<GameInfo> gameInfos = manager.getAvailableGameInfos();
		GameInfo gameInfo = gameInfos.get(0);

		assertEquals(1, gameInfo.getPlayersCount(), "There should be one player in gameInfo after adding");
	}

	/**
	 * Create a game but ignore the id when adding a bot. It should raise an
	 * exception.
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testAddBot_invalid() throws CardGameException {
		assertThrows(CardGameException.class, () -> manager.addBotPlayer(null));
	}

	/**
	 * Check that players added to a game receive events from that game.
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testGetRecentEvents() throws CardGameException {
		TestManagerPlayers testPlayers = new TestManagerPlayers(2);

		String gameId = manager.createGame("WAR");

		// Two players are registered and added to WAR game, so the game should start
		testPlayers.addPlayersTo(gameId);

		/**
		 * As the game already started, the two events received should be a
		 * SendCardsEvent and a RoundUpdateEvent
		 */
		for (TestManagerPlayer testPlayer : testPlayers) {
			List<GameEvent> recentEvents = manager.getRecentEvents(testPlayer.getNick(), testPlayer.getPassword());

			assertEquals(2, recentEvents.size(), "just two events received");
			assertTrue(recentEvents.get(0) instanceof SendCardsEvent, "first event received must be SendCardsEvent");
			assertTrue(recentEvents.get(1) instanceof RoundUpdateEvent,
					"second event received must be RoundUpdateEvent");

			recentEvents = manager.getRecentEvents(testPlayer.getNick(), testPlayer.getPassword());
			assertEquals(0, recentEvents.size(), "recent events have been cleared");
		}
	}

	/**
	 * Test playing WAR a do a invalid move:3 cards as first play
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testPlayCards_WAR_invalidPlay() throws CardGameException {
		TestManagerPlayers testPlayers = new TestManagerPlayers(2);
		String gameId = manager.createGame("WAR");

		// Two players are registered and added to WAR game, so the game should start,
		// and each player should have 2 events to process, SendCardsEvent and
		// RoundUpdateEvent
		{
			testPlayers.addPlayersTo(gameId);

			testPlayers.processAllEvents();
		}

		CardCollection cardsToPlay = testPlayers.getPlayer(0).getHand().takeFirstCards(3);

		// Can't play 3 cards on the first round
		assertThrows(CardGameException.class, () -> manager.playCards(gameId, testPlayers.getPlayer(0).getNick(),
				testPlayers.getPlayer(0).getPassword(), cardsToPlay.asList()));
	}

	/**
	 * Test playing WAR for a few rounds (or the end of the game) Based on
	 * test/wcg.games.war.WarGameMasterTest
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testPlayCards_WAR() throws CardGameException {
		TestManagerPlayers testPlayers = new TestManagerPlayers(2);
		String gameId = manager.createGame("WAR");

		// Two players are registered and added to WAR game, so the game should start,
		// and each player should have 2 events to process, SendCardsEvent and
		// RoundUpdateEvent
		{
			testPlayers.addPlayersTo(gameId);

			testPlayers.processAllEvents();
		}

		// Performs tests on each round of the game
		for (int count = 0; !testPlayers.getPlayer(0).gameEnded && count < REPETITIONS; count++) {
			int cardCount = 0;
			String mode = testPlayers.getPlayer(0).getMode();

			assertEquals(testPlayers.getPlayer(0).getMode(), testPlayers.getPlayer(1).getMode(),
					"equal mode for both players");

			for (TestManagerPlayer player : testPlayers) {
				String nick = player.getNick();
				String password = player.getPassword();
				CardCollection hand = player.getHand();

				cardCount += hand.size();

				if (mode == null)
					manager.playCards(gameId, nick, password, Arrays.asList(hand.takeFirstCard()));
				else if ("WAR".equals(mode.toUpperCase()))
					manager.playCards(gameId, nick, password, hand.takeFirstCards(3).asList());
				else
					fail("unexpected mode: " + mode);

				testPlayers.processAllEvents();
			}

			if (mode == null)
				assertEquals(52, cardCount, "unless in a war, card count remains constant");
		}
	}

	/**
	 * Test playing HEARTS to the end Based on
	 * test/wcg.games.hearts.HeartsGameMasterTest
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testPlayCards_HEARTS() throws CardGameException {
		TestManagerPlayers testPlayers = new TestManagerPlayers(4);
		String gameId = manager.createGame("HEARTS");

		testPlayers.addPlayersTo(gameId);

		testPlayers.processAllEvents();

		// Attempts to play a game until the end, asserting at the end of which one that
		// the game shouldn't have ended, unless it's the 13th round completed
		for (int round = 0; round < 13; round++) {
			for (int p = 0; p < 4; p++) {
				TestManagerPlayer player = testPlayers.getPlayerWithTurn();
				CardSuit suit = testPlayers.getSuitToFollow();
				CardCollection hand = player.getHand();
				CardCollection nonHearts = hand.getCardsNotFromSuit(CardSuit.HEARTS);
				CardCollection nonHeartsPossible = nonHearts.isEmpty() ? hand : nonHearts;
				CardCollection validCards = round < 2 ? nonHeartsPossible : hand;

				validCards = suit == null ? validCards : validCards.getCardsFromSuit(suit);

				validCards = validCards.isEmpty() ? nonHeartsPossible : validCards;

				Card card = validCards.getHighestCard();

				hand.takeCard(card);

				manager.playCards(gameId, player.getNick(), player.getPassword(), Arrays.asList(card));

				testPlayers.processAllEvents();
			}

			if (round < 12)
				assertFalse(testPlayers.getPlayer(0).hasEnded(), "game not yet completed");
			else
				assertTrue(testPlayers.getPlayer(0).hasEnded(), "13 rounds completed");
		}

		assertNotNull(testPlayers.getPlayer(0).getWinner(), "some winner expected");

		int totalPoints = 0;
		int maxPoints = Integer.MIN_VALUE;

		String winner = null;
		Map<String, Integer> playerPoints = testPlayers.getPlayer(0).points;

		for (TestManagerPlayer player : testPlayers) {
			String nick = player.getNick();
			int points = playerPoints.get(nick);

			assertTrue(points <= 0, "hearts count as negative points");

			totalPoints += points;
			if (points > maxPoints) {
				maxPoints = points;
				winner = nick;
			}
		}

		assertEquals(-13, totalPoints, "all hearts are accounted for");

		assertEquals(winner, testPlayers.getPlayer(0).getWinner(), "winner must be player with less hearts");
	}
}

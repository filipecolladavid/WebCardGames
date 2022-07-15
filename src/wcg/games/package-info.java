
/**
Package wcg.games Description
Classes common to all card games. Each game is defined by a sub-packages containing classes specific to a single game such as WAR or HEARTS.
A core class is this package is GameMaster. This is an abstract class to support the definition of every game master supported by WCG. For instance, the package {link wcg.games.war} contains an specialization of this class for the WAR card game.

The creation of game masters for different games follows the Factory method design pattern and this package contains also the its abstract component.

This package contains also GameBot, whose instances provide automated players for all games. This class uses the Strategy design pattern to support different ways the play and the the package includes the required interface. Concrete strategies are contained in the games's sub-package.

Communication between game masters and players, either humans players or bots, is based on the Observer design pattern. This package provides most of the abstract component. Since some of the events will need to be propagated to clients their classes are defined elsewhere, in wcg.shared.events.

The class CardCollection, heavily used by game masters and bots, provides several methods to manipulate collections of cards, from static methods to produce a deck cards, to methods shuffle, select cards by suite, or select the highest or lowest cards from a collection.

Several manipulations depend of a specific car order, and games may have a specific order. The class provides a DefaultCardComparator, based in a comparators of suits and values, that may be redefined by specific games. For example, the class WarCardComparator extends it and redefines methods, and provides a specialized comparator for WAR games is provided by its game master.

Glossary
Card
A piece of thin cardboard characterized by a suit and a value.
Deck
A collection of usually 52 different cards, each with a suit and value.
Shuffle
Shuffling is the process of bringing the cards of a pack into a random order.
Eldest hand
the player who enjoys greatest priority and is the first to receive cards in the deal.
Suit
One of the 4 card categories: diamonds (♦), clubs (♣), hearts (♥) and spades (♠)
Trick (or round)
Unit of a trick-taking game.
Trick-taking game
A trick-taking game is a card or tile-based game in which play of a hand centers on a series of finite rounds or units of play, called tricks, which are each evaluated to determine a winner or taker of that trick.
Trumps
Trump cards are a set of one or more cards in the deck that, when played, are of higher value than the suit led. If a trick contains any trump cards, it is won by the highest-value trump card played, not the highest-value card of the suit led.
From wikipedia: Card game Card player Playing_card_suit Trick-taking_game
 */
package wcg.games;


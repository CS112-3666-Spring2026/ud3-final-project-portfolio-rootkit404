package war.models;

import java.util.ArrayList;
import java.util.List;

/**
 * WarGame.java - Core game logic for the War card game.
 * Manages the deck, both players, the pot of cards in play, and the
 * turn-by-turn flow of the game. Supports round-limited modes (20/50)
 * and the standard unlimited mode.
 *
 * MVC role: Model Controller. Holds game state and applies rules.
 * Has no JavaFX dependencies. The GUI Controller calls these methods
 * and uses the returned RoundResult to update the View.
 *
 * @author Nathan Tshishimbi
 * @version 1.3 (UD3) / (05/09/2026)
 */
public class WarGame {

    //*** CONSTANTS ***//

    /** Sentinel value for "no round limit" (used in maxRounds). */
    public static final int UNLIMITED_ROUNDS = 0;

    /** Number of face-down cards each player stakes during a war. */
    public static final int WAR_STAKE = 3;

    //*** INSTANCE VARIABLES ***//
    private final Player player1;
    private final Player player2;
    private Deck deck;
    private final ArrayList<PlayingCard> warPile;
    private int roundCount;
    private int player1RoundWins;
    private int player2RoundWins;
    private final int maxRounds;
    private boolean endedEarly;
    private boolean isWarPending;
    private int totalCardsInPlay;

    //*** CONSTRUCTORS ***//

    /**
     * Full Constructor - creates a game with both players and a round limit.
     *
     * @param p1 the first player (typically the HumanPlayer)
     * @param p2 the second player (typically the AIPlayer)
     * @param maxRounds the maximum number of rounds (use UNLIMITED_ROUNDS for no limit)
     */
    public WarGame(Player p1, Player p2, int maxRounds) {
        this.player1 = p1;
        this.player2 = p2;
        this.maxRounds = Math.max(maxRounds, UNLIMITED_ROUNDS);
        this.deck = new Deck();
        this.warPile = new ArrayList<>();
        this.roundCount = 0;
        this.player1RoundWins = 0;
        this.player2RoundWins = 0;
        this.endedEarly = false;
        this.isWarPending = false;
    }

    /**
     * Two-Player Constructor - creates a standard unlimited game.
     *
     * @param p1 the first player
     * @param p2 the second player
     */
    public WarGame(Player p1, Player p2) {
        this(p1, p2, UNLIMITED_ROUNDS);
    }

    /**
     * Default Constructor - creates a game with a HumanPlayer vs AIPlayer
     * and no round limit. Useful for quick testing.
     */
    public WarGame() {
        this(new HumanPlayer("Player 1"), new AIPlayer("CPU"), UNLIMITED_ROUNDS);
    }

    //*** GAME SETUP ***//

    /**
     * Starts a new game: rebuilds and shuffles the deck, clears both
     * players' hands and the war pile, deals 26 cards to each player,
     * and resets all counters.
     */
    public void startGame() {
        clearPlayerHands();
        warPile.clear();
        deck = new Deck();
        deck.shuffle();

        List<PlayingCard> hand1 = new ArrayList<>();
        List<PlayingCard> hand2 = new ArrayList<>();
        deck.deal(hand1, hand2);
        player1.addCards(hand1);
        player2.addCards(hand2);

        roundCount = 0;
        player1RoundWins = 0;
        player2RoundWins = 0;
        endedEarly = false;
        isWarPending = false;
    }

    /**
     * Helper - empties both players' hands by removing cards one at a time.
     * Used at startGame() so a fresh deal isn't combined with leftover cards.
     */
    private void clearPlayerHands() {
        while (player1.hasCards()) {
            player1.removeCard();
        }
        while (player2.hasCards()) {
            player2.removeCard();
        }
    }

    //*** GAME LOGIC ***//

    /**
     * Plays a single round of War. Each player plays their top card;
     * higher value wins both cards. On a tie, a war is triggered: this
     * method returns a RoundResult with isWarPending() == true, and the
     * caller must invoke resolveWar() to settle it.
     *
     * @return the RoundResult describing what happened this round
     * @throws EmptyDeckException if either player has no cards to play
     */
    public RoundResult playRound() throws EmptyDeckException {
        if (!player1.hasCards()) {
            throw new EmptyDeckException(player1.getName() + " has no cards left.");
        }
        if (!player2.hasCards()) {
            throw new EmptyDeckException(player2.getName() + " has no cards left.");
        }

        PlayingCard c1 = player1.chooseCard();
        PlayingCard c2 = player2.chooseCard();
        warPile.add(c1);
        warPile.add(c2);
        roundCount++;

        if (c1.getValue() > c2.getValue()) {
            return awardPotTo(player1, c1, c2, false);
        }
        else if (c2.getValue() > c1.getValue()) {
            return awardPotTo(player2, c1, c2, false);
        }
        else {
            // Equal values -> WAR
            isWarPending = true;
            return new RoundResult(roundCount, null, c1, c2, true, warPile.size(), new ArrayList<>(warPile));
        }
    }

    /**
     * Resolves a pending war. Each player stakes WAR_STAKE face-down cards
     * (or all remaining minus one, if they have fewer) plus one face-up card.
     * The higher face-up card wins everything in the pot. If the face-up
     * cards tie again, this method recursively resolves another war.
     *
     * @return the RoundResult describing the war's outcome
     * @throws EmptyDeckException if a player runs out of cards mid-war
     * @throws IllegalStateException if no war is currently pending
     */
    public RoundResult resolveWar() throws EmptyDeckException {
        if (!isWarPending) {
            throw new IllegalStateException("No war pending to resolve.");
        }

        // Each player puts as many face-down cards as they can spare
        // (we need at least one card left for the face-up comparison)
        int p1FaceDown = Math.min(WAR_STAKE, player1.getHandSize() - 1);
        int p2FaceDown = Math.min(WAR_STAKE, player2.getHandSize() - 1);
        p1FaceDown = Math.max(p1FaceDown, 0);
        p2FaceDown = Math.max(p2FaceDown, 0);

        // Stake the face-down cards (still on top of the warPile)
        for (int i = 0; i < p1FaceDown; i++) {
            warPile.add(player1.removeCard());
        }
        for (int i = 0; i < p2FaceDown; i++) {
            warPile.add(player2.removeCard());
        }

        // If a player can't even play a face-up card, they lose immediately
        if (!player1.hasCards()) {
            isWarPending = false;
            throw new EmptyDeckException(
                    player1.getName() + " ran out of cards during war.");
        }
        if (!player2.hasCards()) {
            isWarPending = false;
            throw new EmptyDeckException(
                    player2.getName() + " ran out of cards during war.");
        }

        // Play one face-up card each
        PlayingCard c1 = player1.chooseCard();
        PlayingCard c2 = player2.chooseCard();
        warPile.add(c1);
        warPile.add(c2);

        if (c1.getValue() > c2.getValue()) {
            return awardPotTo(player1, c1, c2, true);
        }
        else if (c2.getValue() > c1.getValue()) {
            return awardPotTo(player2, c1, c2, true);
        }
        else {
            // Tied again - recurse
            return resolveWar();
        }
    }

    /**
     * Helper - awards every card in the warPile to the winning player,
     * updates round-wins counters, clears the pile, and constructs a
     * RoundResult representing the outcome.
     */
    private RoundResult awardPotTo(Player winner, PlayingCard p1Card,
                                   PlayingCard p2Card, boolean wasWar) {
        ArrayList<PlayingCard> won = new ArrayList<>(warPile);
        int cardsWon = won.size();
        winner.addCards(won);
        warPile.clear();
        isWarPending = false;

        if (winner == player1) {
            player1RoundWins++;
        } else {
            player2RoundWins++;
        }

        return new RoundResult(roundCount, winner, p1Card, p2Card,
                false, cardsWon,
                wasWar ? won : null);
    }

    //*** GAME STATE CHECKS ***//
    /**
     * Checks whether the game has ended. The game ends if:
     *   - Either player has no cards remaining
     *   - The round limit has been reached
     *   - endEarly() was called (used for time-attack mode)
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        if (endedEarly) {
            return true;
        }
        if (!player1.hasCards() || !player2.hasCards()) {
            return true;
        }
        return maxRounds > 0 && roundCount >= maxRounds;
    }

    /**
     * Determines the winner based on the current state.
     * Whoever has more cards in their hand wins. If the game is
     * still in progress or both players have the same number of
     * cards, returns null.
     *
     * @return the winning Player, or null if tied or game in progress
     */
    public Player getWinner() {
        if (!isGameOver()) {
            return null;
        }
        if (player1.getHandSize() > player2.getHandSize()) {
            return player1;
        }
        if (player2.getHandSize() > player1.getHandSize()) {
            return player2;
        }
        return null;
    }

    /**
     * Returns a human-readable string describing the game's outcome.
     *
     * @return a sentence like "Player 1 wins with 35 cards!" or
     *         "Game in progress." if the game hasn't ended
     */
    public String declareWinner() {
        if (!isGameOver()) {
            return "Game in progress.";
        }
        Player winner = getWinner();
        if (winner == null) {
            return "It's a tie! Both players have "
                    + player1.getHandSize() + " cards.";
        }
        return winner.getName() + " wins with "
                + winner.getHandSize() + " cards!";
    }

    /**
     * Forces the game to end immediately. Used by the GUI for the
     * time-attack mode when the countdown timer expires.
     */
    public void endEarly() {
        this.endedEarly = true;
    }

    //*** GETTERS ***//

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public int getCurrentRound() {
        return roundCount;
    }

    public int getPlayer1RoundWins() {
        return player1RoundWins;
    }

    public int getPlayer2RoundWins() {
        return player2RoundWins;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public int getPotSize() {
        return warPile.size();
    }

    public boolean isWarPending() {
        return isWarPending;
    }

    public int getTotalCardsInPlay() {
        return player1.getHandSize() + player2.getHandSize() + warPile.size();
    }

    //*** TOSTRING ***//

    @Override
    public String toString() {
        return "WarGame [Round " + roundCount
                + (maxRounds > 0 ? "/" + maxRounds : "")
                + " | " + player1 + " | " + player2
                + " | Pot: " + warPile.size() + "]";
    }
}
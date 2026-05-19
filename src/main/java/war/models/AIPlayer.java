package war.models;

/**
 * AIPlayer.java - Represents the CPU opponent in the War card game.
 * Extends the abstract Player class and implements chooseCard().
 *
 * In standard War, card selection is deterministic (top card), identical
 * to HumanPlayer. But, this class includes pickHighestCard() as a
 * private helper to demonstrate that AIPlayer COULD use a different
 * strategy — this is the key polymorphism: WarGame calls chooseCard()
 * on a Player reference without knowing which subclass handles it.
 *
 * @author Nathan Tshishimbi
 * @version 1.3 (UD3) / (05/09/2026)
 */

import java.util.List;

public class AIPlayer extends Player {

    //*** CONSTRUCTORS ***//

    /**
     * Full Constructor - creates an AI player with the given name.
     *
     * @param name the AI player's display name
     */
    public AIPlayer(String name) {
        super(name);
    }

    /**
     * Copy Constructor - creates an independent copy of another AIPlayer.
     *
     * @param original the AIPlayer to copy; must not be null
     */
    public AIPlayer(AIPlayer original) {
        super(original);
    }

    /**
     * Default Constructor - creates an AI player with a default name.
     */
    public AIPlayer() {
        super("CPU");
    }

    //*** OVERRIDDEN METHODS ***//

    /**
     * Chooses a card to play this round.
     * Standard behavior: plays the top card of the hand (same as War rules).
     * The card is flipped face-up when played.
     *
     * @return the top PlayingCard from the hand, or null if hand is empty
     */
    @Override
    public PlayingCard chooseCard() {
        PlayingCard card = removeCard();
        if (card != null) {
            card.setFaceUp(true);
        }
        return card;
    }

    //*** PRIVATE HELPER METHODS ***//

    /**
     * Finds the highest-ranked card in the AI's hand without removing it.
     * This method exists to demonstrate that AIPlayer could use a different
     * strategy than HumanPlayer — even though standard War rules don't
     * allow choosing which card to play, this shows the subclass has
     * the capability for a different algorithm if game rules change.
     *
     * @return the highest-ranked PlayingCard in the hand, or null if empty
     */
    private PlayingCard pickHighestCard() {
        List<PlayingCard> hand = getHand();
        if (hand.isEmpty()) {
            return null;
        }
        PlayingCard highest = hand.get(0);
        for (PlayingCard card : hand) {
            if (card.getValue() > highest.getValue()) {
                highest = card;
            }
        }
        return highest;
    }

    /**
     * Returns a string representation of this AI player.
     *
     * @return a human-readable summary
     */
    @Override
    public String toString() {
        return getName() + " (CPU) [" + getHandSize() + " cards]";
    }
}
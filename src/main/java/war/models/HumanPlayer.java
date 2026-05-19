package war.models;

/*
 * HumanPlayer.java - Represents the human player in the War card game.
 * Extends the abstract Player class and implements chooseCard().
 *
 * In War, card selection is deterministic (always the top card),
 * so chooseCard() simply removes and returns the top card from the hand.
 * The GUI drives the timing of when chooseCard() is called (on button click).
 *
 * @author Nathan Tshishimbi
 * @version 2.0 (UD3) / (05/18/2026)
 */
public class HumanPlayer extends Player {

    //*** CONSTRUCTORS ***//

    /**
     * Full Constructor - creates a human player with the given name.
     *
     * @param name the player's display name
     */
    public HumanPlayer(String name) {
        super(name);
    }

    /**
     * Copy Constructor - creates an independent copy of another HumanPlayer.
     *
     * @param original the HumanPlayer to copy; must not be null
     */
    public HumanPlayer(HumanPlayer original) {
        super(original);
    }

    /**
     * Default Constructor - creates a human player with a default name.
     */
    public HumanPlayer() {
        super("Player 1");
    }

    //*** OVERRIDDEN METHODS ***//

    /**
     * Chooses a card to play this round.
     * In War, the human player always plays the top card of their hand.
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

    /**
     * Returns a string representation of this human player.
     *
     * @return a human-readable summary
     */
    @Override
    public String toString() {
        return getName() + " (Human) [" + getHandSize() + " cards]";
    }
}
package war.models;

/*
  Player.java - Abstract base class representing a player in the War card game.
  Holds shared state (name, hand) and shared behavior (addCard, getHandSize)
  that both HumanPlayer and AIPlayer inherit. The abstract method chooseCard()
  forces each subclass to define how a card is selected during a round.

  Design rationale: Player is abstract (not an interface) because it holds
  real state that all players share. An interface cannot store instance fields
  like hand or name. Polymorphism is achieved through the chooseCard() method —
  WarGame calls chooseCard() on Player references without knowing the subtype.

  @author Nathan Tshishimbi
 * @version 2.0 (UD3) / (05/18/2026)
 */

import java.util.ArrayList;
import java.util.List;

public abstract class Player {

    //*** INSTANCE VARIABLES ***//
    private String name;
    private ArrayList<PlayingCard> hand;

    //*** CONSTRUCTORS ***//

    /**
     * Full Constructor - creates a player with the given name
     * and an empty hand.
     *
     * @param name the player's display name
     */
    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    /**
     * Copy Constructor - creates an independent copy of another Player.
     * Copies the name and duplicates every card in the hand.
     *
     * @param original the Player to copy; must not be null
     */
    public Player(Player original) {
        if (original != null) {
            this.name = original.name;
            this.hand = new ArrayList<>();
            for (PlayingCard card : original.hand) {
                this.hand.add(new PlayingCard(card));
            }
        } else {
            System.out.println("ERROR: Cannot copy null Player. Exiting.");
            System.exit(0);
        }
    }

    /**
     * Default Constructor - creates a player with a default name
     * and an empty hand.
     */
    public Player() {
        this.name = "Unknown Player";
        this.hand = new ArrayList<>();
    }

    //*** GETTERS ***//

    /**
     * Gets the player's display name.
     *
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the player's current hand as a list.
     * Returns a copy to prevent external modification.
     *
     * @return a new ArrayList containing the player's cards
     */
    public List<PlayingCard> getHand() {
        return new ArrayList<>(hand);
    }

    /**
     * Gets the number of cards currently in the player's hand.
     *
     * @return the hand size
     */
    public int getHandSize() {
        return hand.size();
    }

    /**
     * Checks whether the player still has cards to play.
     *
     * @return true if the hand is not empty, false otherwise
     */
    public boolean hasCards() {
        return !hand.isEmpty();
    }

    //*** SETTERS ***//

    /**
     * Sets the player's display name.
     *
     * @param name the new name to assign
     */
    public void setName(String name) {
        this.name = name;
    }

    //*** OTHER METHODS ***//

    /**
     * Adds a single card to the bottom of the player's hand.
     *
     * @param card the PlayingCard to add; ignored if null
     */
    public void addCard(PlayingCard card) {
        if (card != null) {
            hand.add(card);
        }
    }

    /**
     * Adds a list of cards to the bottom of the player's hand.
     * Used when a player wins a round and collects all played cards.
     *
     * @param cards the list of PlayingCards to add; ignored if null
     */
    public void addCards(List<PlayingCard> cards) {
        if (cards != null) {
            hand.addAll(cards);
        }
    }

    /**
     * Removes and returns the top card from the player's hand.
     * The "top" is index 0, simulating drawing from the top of a pile.
     *
     * @return the top PlayingCard, or null if the hand is empty
     */
    public PlayingCard removeCard() {
        if (hand.isEmpty()) {
            return null;
        }
        return hand.remove(0);
    }

    /**
     * Removes a specific card from the player's hand.
     *
     * @param card the PlayingCard to remove
     * @return true if the card was found and removed, false otherwise
     */
    public boolean removeCard(PlayingCard card) {
        return hand.remove(card);
    }

    //*** OTHER METHODS ***//

    /**
     * Abstract method - defines how this player selects a card to play.
     * HumanPlayer and AIPlayer each implement this differently.
     * This is the core of the polymorphism in the War game:
     * WarGame calls chooseCard() on a Player reference and gets
     * the correct behavior regardless of the actual subclass.
     *
     * @return the PlayingCard chosen for this round
     */
    public abstract PlayingCard chooseCard();

    /**
     * Compares this Player to another object for equality.
     * Two Players are equal if they have the same name and hand size.
     *
     * @param o the object to compare against
     * @return true if the players are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return name.equals(player.name) && hand.equals(player.hand);
    }

    /**
     * Returns a string representation of the player showing
     * their name and current hand size.
     *
     * @return a human-readable summary of the player
     */
    @Override
    public String toString() {
        return name + " [" + hand.size() + " cards]";
    }
}
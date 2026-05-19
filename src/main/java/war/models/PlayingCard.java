package war.models;
/*
  PlayingCard.java - Represents the foundational class for game, with Card values,
  attributes,

  @author Nathan Tshishimbi <nathanltish@gmail.com>
  @version 2.0 (UD3) / (05/18/2026)
 */

public class PlayingCard {

    //*** INSTANCE VARIABLES ***//
    private Suit suit;
    private Rank rank;
    private boolean isFaceUp; // Whether the card is currently revealed to the player

    //*** CONSTRUCTORS ***//
    /**
     * Full Constructor - initializes all attributes of the card.
     *
     * @param suit the suit to assign (Hearts, Diamonds, Clubs, Spades)
     * @param rank the rank to assign (Two through Ace)
     * @param isFaceUp true if card starts revealed, false if hidden
     */
    public PlayingCard(Suit suit, Rank rank, boolean isFaceUp) {
        this.suit = suit;
        this.rank = rank;
        this.isFaceUp = isFaceUp;
    }

    /**
     * Copy Constructor - creates an independent duplicate of another PlayingCard.
     *
     * @param original the PlayingCard to copy
     * @throws IllegalArgumentException if original is null
     */
    public PlayingCard(PlayingCard original) {
        if (original == null) {
            throw new IllegalArgumentException("Cannot copy a null PlayingCard");
        }
        this.suit = original.getSuit();
        this.rank = original.getRank();
        this.isFaceUp = original.isFaceUp;
    }

    /**
     * Default Constructor - creates a default card to prevent null references.
     * Defaults to Ace of Spades, face down.
     */
    public PlayingCard() {
        this.suit = Suit.SPADE;
        this.rank = Rank.ACE;
        this.isFaceUp = false;
    }


    //*** GETTERS ***//
    /**
     * Gets the suit of this card.
     *
     * @return the card's suit (Hearts, Diamonds, Clubs, or Spades)
     */
    public Suit getSuit() {
        return suit;
    }

    /**
     * Gets the rank of this card.
     *
     * @return the card's rank (Two through Ace)
     */
    public Rank getRank() {
        return rank;
    }

    /**
     * Gets the numeric value of this card's rank for comparison purposes.
     * Values: Two=2, Three=3, ..., Ten=10, Jack=11, Queen=12, King=13, Ace=14.
     * Used by the War game logic to determine which card wins a round.
     *
     * @return the numeric rank value (2 through 14)
     */
    public int getValue() {
        return rank.getValue();
    }

    /**
     * Checks whether the card is currently face-up (revealed).
     *
     * @return true if the card is revealed, false if hidden
     */
    public boolean isFaceUp() {
        return isFaceUp;
    }

    //*** SETTERS ***//
    /**
     * Sets a new suit for this card.
     *
     * @param suit the new suit to assign
     */
    public void setSuit(Suit suit) {
        this.suit = suit;
    }

    /**
     * Sets a new rank for this card.
     *
     * @param rank the new rank to assign
     */
    public void setRank(Rank rank) {
        this.rank = rank;
    }

    /**
     * Sets whether this card is face-up or face-down.
     *
     * @param faceUp true to reveal the card, false to hide it
     */
    public void setFaceUp(boolean faceUp) {
        isFaceUp = faceUp;
    }

    //*** OTHER METHODS ***//

    /**
     * Compares this card's rank against another card's rank
     * using their numeric values (Two=2 through Ace=14).
     * This is the core comparison method for the War game:
     * higher rank wins the round.
     *
     * @param other the card to compare against; must not be null
     * @return true if this card outranks the other card
     */
    public boolean beats(PlayingCard other) {
        return this.rank.getValue() > other.rank.getValue();
    }

    /**
     * Toggles the face-up status of the card.
     * If the card was hidden, it becomes revealed and vice versa.
     * Helper method for GUI "onButtonClick" when a player clicks to reveal a card
     */
    public void flipCard() {
        this.isFaceUp = !this.isFaceUp;
    }

    /**
     * Compares this PlayingCard to another object for equality.
     * Two PlayingCards are considered equal if they have the same suit,
     * the same rank, and the same face-up status.
     *
     * @param o the object to compare against
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayingCard that = (PlayingCard) o;
        return isFaceUp == that.isFaceUp && suit == that.suit && rank == that.rank;
    }

    /**
     * Returns a string representation of the card.
     * If the card is face-down, it returns "[Hidden Card]".
     * If face-up, it returns the rank and suit (e.g., "ACE of SPADE").
     *
     * @return a human-readable string for this card
     */
    @Override
    public String toString() {
        if (!isFaceUp) {
            return "[Hidden Card]";
        }
        return rank + " of " + suit;
    }
}
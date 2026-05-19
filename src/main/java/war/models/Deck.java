package war.models;

/*
  Deck.java - Represents a deck of PlayingCards for the War card game.
  Handles building a standard 52-card deck, shuffling, drawing, and
  adding cards back. Uses an ArrayList as the underlying data structure.

  @author Nathan Tshishimbi
  @version 2.0 (UD3) / (05/18/2026)
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    //*** INSTANCE VARIABLES ***//
    private final ArrayList<PlayingCard> cards;

    //*** CONSTRUCTORS ***//

    /**
     * Default Constructor - builds a standard 52-card deck;
     * one card for every combination of Suit and Rank.
     * (All cards are created face-down by default).
     */
    public Deck() {
        cards = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new PlayingCard(suit, rank, false));
            }
        }
    }

    /**
     * Multi-Deck Constructor - builds multiple standard 52-card decks
     * combined into one. Useful if the game rules call for a larger pool.
     *
     * @param numDecks the number of standard decks to combine (must be >= 1)
     */
    public Deck(int numDecks) {
        cards = new ArrayList<>();
        if (numDecks < 1) {
            numDecks = 1;
        }
        for (int d = 0; d < numDecks; d++) {
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) {
                    cards.add(new PlayingCard(suit, rank, false));
                }
            }
        }
    }

    /**
     * Custom Constructor - builds a deck from an existing list of cards.
     * Creates copies of each card to prevent external modification.
     *
     * @param cards the list of PlayingCards to populate the deck with
     */
    public Deck(List<PlayingCard> cards) {
        this.cards = new ArrayList<>();
        if (cards != null) {
            for (PlayingCard card : cards) {
                this.cards.add(new PlayingCard(card)); // uses copy constructor
            }
        }
    }

    //*** GETTERS ***//

    /**
     * Returns a copy of the cards currently in the deck.
     * Returns a copy to prevent external code from modifying the deck directly.
     *
     * @return a new ArrayList containing copies of the deck's cards
     */
    public List<PlayingCard> getCards() {
        return new ArrayList<>(cards);
    }

    /**
     * Returns the number of cards remaining in the deck.
     *
     * @return the current size of the deck
     */
    public int size() {
        return cards.size();
    }

    /**
     * Checks whether the deck has no cards remaining.
     *
     * @return true if the deck is empty, false otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    //*** OTHER METHODS ***//

    /**
     * Shuffles the deck into a random order using Collections.shuffle().
     * Should be called before dealing at the start of a new game.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Draws (removes and returns) the top card from the deck.
     * The "top" card is the last element in the ArrayList for
     * efficient O(1) removal.
     *
     * @return the top PlayingCard from the deck
     * @throws EmptyDeckException if the deck is empty
     */
    public PlayingCard draw() throws EmptyDeckException {
        if (cards.isEmpty()) {
            throw new EmptyDeckException("Cannot draw from an empty deck.");
        }
        return cards.remove(cards.size() - 1);
    }

    /**
     * Adds a card to the bottom of the deck.
     * Used when a player wins cards then players go back into play.
     *
     * @param card the PlayingCard to add to the deck
     */
    public void addCard(PlayingCard card) {
        if (card != null) {
            cards.add(0, card);
        }
    }

    /**
     * Deals the entire deck evenly between two players' hands.
     * The deck should be shuffled before calling this method.
     * Each player receives alternating cards until the deck is empty.
     *
     * @param hand1 the first player's hand to deal into
     * @param hand2 the second player's hand to deal into
     */
    public void deal(List<PlayingCard> hand1, List<PlayingCard> hand2) {
        boolean toFirstPlayer = true;
        while (!cards.isEmpty()) {
            try {
                PlayingCard card = draw();
                if (toFirstPlayer) {
                    hand1.add(card);
                } else {
                    hand2.add(card);
                }
                toFirstPlayer = !toFirstPlayer;
            } catch (EmptyDeckException ede) {
                System.out.println("Unexpected empty deck during deal: " + ede.getMessage());
                break;
            }
        }
    }

    /**
     * Compares this Deck to another object for equality.
     * Two Decks are equal if they contain the same cards in the same order.
     *
     * @param o the object to compare against
     * @return true if the decks are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deck deck = (Deck) o;
        return cards.equals(deck.cards);
    }

    /**
     * Returns a string representation of the deck showing
     * the number of cards and a preview of the top few cards.
     *
     * @return a human-readable summary of the deck
     */
    @Override
    public String toString() {
        if (cards.isEmpty()) {
            return "Deck [empty]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Deck [").append(cards.size()).append(" cards] Top cards: ");
        int preview = Math.min(3, cards.size());
        for (int i = cards.size() - 1; i >= cards.size() - preview; i--) {
            PlayingCard card = cards.get(i);
            card.setFaceUp(true); // temporarily reveal for display
            sb.append(card);
            card.setFaceUp(false);
            if (i > cards.size() - preview) {
                sb.append(", ");
            }
        }
        sb.append("...");
        return sb.toString();
    }
}
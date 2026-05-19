package war.models;

/*
 * EmptyDeckException.java - Custom checked exception thrown when a player
 * attempts to draw or play a card from an empty hand or deck.
 *
 * Design rationale: A generic RuntimeException or IndexOutOfBoundsException
 * gives no context about what went wrong in the game. EmptyDeckException
 * carries a descriptive message identifying which player's hand was empty
 * and at what point in the game it happened, making debugging easier and
 * allowing WarGame to catch it explicitly and handle the end-of-game
 * condition gracefully rather than crashing.
 *
 * @author Nathan Tshishimbi
 * @version 2.0 (UD3) / (05/18/2026)
 */
public class EmptyDeckException extends Exception {

    //*** CONSTRUCTORS ***//

    /**
     * Full Constructor - creates the exception with a descriptive message.
     *
     * @param message a description of what triggered the exception
     *                (e.g., "Player 1 has no cards remaining during war")
     */
    public EmptyDeckException(String message) {
        super(message);
    }

    /**
     * Default Constructor - creates the exception with a generic message.
     */
    public EmptyDeckException() {
        super("Cannot draw from an empty deck or hand.");
    }

    /**
     * Chained Constructor - wraps another exception that caused this one.
     * Useful when an underlying error (like IndexOutOfBoundsException)
     * is caught and re-thrown as a more meaningful EmptyDeckException.
     *
     * @param message a description of what triggered the exception
     * @param cause   the underlying exception that caused this one
     */
    public EmptyDeckException(String message, Throwable cause) {
        super(message, cause);
    }
}
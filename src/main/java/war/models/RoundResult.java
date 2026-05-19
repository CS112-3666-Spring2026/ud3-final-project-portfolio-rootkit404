package war.models;

/*
  RoundResult.java - Bundles together and handles all information from a single
  round of War so the GameController knows exactly what happened and how to
  update the UI.

  Returned by WarGame.playRound() and WarGame.resolveWar().

  @author Nathan Tshishimbi
  @version 2.0 (UD3) / (05/18/2026)
 */

import java.util.ArrayList;

public class RoundResult {

    //*** INSTANCE VARIABLES ***//
    private final int roundCount;
    private final Player winner; // will be null if war is pending
    private final PlayingCard player1Card;
    private final PlayingCard player2Card;
    private final boolean isWar;
    private final int warPileSize;
    private final ArrayList<PlayingCard> warPileSnapshot;

    //*** CONSTRUCTOR ***//

    /**
     * Full Constructor
     *
     * @param roundCount       current round number
     * @param winner           the Player who won this round; null if it's a war
     * @param player1Card      the card Player 1 played
     * @param player2Card      the card Player 2 played
     * @param isWar            true if the round ended in a tie (war)
     * @param warPileSize      number of cards currently staked in the war pile
     * @param warPileSnapshot  snapshot copy of the war pile at time of result
     */
    public RoundResult(int roundCount, Player winner,
                       PlayingCard player1Card, PlayingCard player2Card,
                       boolean isWar, int warPileSize,
                       ArrayList<PlayingCard> warPileSnapshot) {
        this.roundCount = roundCount;
        this.winner = winner;
        this.player1Card = player1Card;
        this.player2Card = player2Card;
        this.isWar = isWar;
        this.warPileSize = warPileSize;
        this.warPileSnapshot = warPileSnapshot;
    }

    //*** GETTERS ***//

    public int getCurrentRound() {
        return roundCount;
    }

    public Player getWinner() {
        return winner;
    }
    public PlayingCard getPlayer1Card() {
        return player1Card;
    }

    public PlayingCard getPlayer2Card() {
        return player2Card;
    }

    public boolean isWar() {
        return isWar;
    }

    public boolean isGameOver() {
        return !isWar && winner != null;
    }
    public int getWarPileSize() {
        return warPileSize;
    }

    public ArrayList<PlayingCard> getWarPileSnapshot() {
        return warPileSnapshot;
    }

    //*** OTHER METHODS ***//

    /**
     * Returns a human-readable message of the result of the given round
     * (Used by GameController.java to update the status label in the GUI)
     */
    public String getMessage() {
        return toString();
    }

    /**
     * Returns a human-readable summary of this round result.
     * Used by the GameController to populate the status label.
     *
     * @return a descriptive string of the round outcome
     */
    @Override
    public String toString() {
        if (isWar) {
            return "Round " + roundCount + ": WAR! Both played "
                    + player1Card.getRank() + " — " + warPileSize + " cards at stake.";
        }
        if (winner != null) {
            return "Round " + roundCount + ": " + winner.getName()
                    + " wins! (" + player1Card + " vs " + player2Card + ")";
        }
        return "Round " + roundCount + ": No result.";
    }
}
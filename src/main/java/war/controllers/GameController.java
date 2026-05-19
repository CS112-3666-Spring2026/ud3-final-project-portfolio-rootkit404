package war.controllers;

/*
  GameController.java - JavaFX controller that connects the WarGame model
  to the GUI. Handles all button events, updates the view after each round,
  and manages the war popup dialog flow.

  MVC role: This is the Controller — it knows about both the Model (WarGame)
  and the View (FXML labels/buttons), but the Model knows nothing about JavaFX.

  @author Nathan Tshishimbi
 * @version 2.0 (UD3) / (05/18/2026)
 */

//*** IMPORTS SECTION ***//
import war.models.AIPlayer;
import war.models.EmptyDeckException;
import war.models.PlayingCard;
import war.models.Rank;
import war.models.HumanPlayer;
import war.models.Player;
import war.models.WarGame;
import war.models.RoundResult;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.application.Platform;
import java.io.IOException;
import java.net.URL;


public class GameController {

    //*** FXML-INJECTED UI COMPONENTS ***//

    // Human player info labels
    @FXML
    private Label p1NameLabel;
    @FXML
    private Label p1CardCountLabel;
    @FXML
    private ImageView p1CardImageView;

    // CPU player info labels
    @FXML
    private Label p2NameLabel;
    @FXML
    private Label p2CardCountLabel;
    @FXML
    private ImageView p2CardImageView;

    // Game Status
    @FXML
    private Label statusLabel;
    @FXML
    private Label roundLabel;
    @FXML
    private Label p1WinsLabel;
    @FXML
    private Label p2WinsLabel;
    @FXML
    private Label timerLabel;
    @FXML
    private Image cardBackImage;

    // Buttons
    @FXML
    private Button drawButton;
    @FXML
    private Button resolveWarButton;
    @FXML
    private Button newGameButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button loadButton;
    @FXML
    private Button pauseButton;


    //*** INSTANCE VARIABLES ***//
    private WarGame game;
    private int p1RoundsWon = 0;
    private int p2RoundsWon = 0;

    // Max rounds setting — updated by the title screen when it's built.
    // 0 = unlimited, 20 = quick game, 50 = standard game
    private int selectedMaxRounds = 0;

    // Controls clock with time attack modes
    private Timeline countdownTimer;
    private int remainingSeconds = 0;


    //*** INITIALIZATION ***//

    /**
     * Called automatically by JavaFX after the FXML is loaded.
     * Sets the initial disabled/enabled state of all buttons.
     */
    @FXML
    public void initialize() {
        drawButton.setDisable(true);
        resolveWarButton.setDisable(true);
        statusLabel.setText("Press 'New Game' to start!");
        System.out.println("GameController initialized.");

        cardBackImage = loadImage("back-navy" + ".png");
        p1CardImageView.setImage(cardBackImage);
        p2CardImageView.setImage(cardBackImage);
    }

    //*** EVENT HANDLERS ***//

    /**
     * Starts a new game. Creates fresh HumanPlayer and AIPlayer instances,
     * instantiates WarGame with the selected round limit, deals the deck,
     * and resets all UI labels.
     */
    @FXML
    private void handleNewGame() {
        try {
            // Load and show the setup dialog
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/war/game-setup.fxml"));
            Parent setupRoot = loader.load();
            GameSetupController setupController = loader.getController();

            Stage setupStage = new Stage();
            setupStage.setTitle("Game Setup");
            setupStage.setScene(new Scene(setupRoot));
            setupStage.initModality(Modality.APPLICATION_MODAL);
            setupStage.showAndWait(); // blocks until user picks a mode

            // Read selections from the setup controller
            int maxRounds = setupController.getSelectedMaxRounds();
            int timeSeconds = setupController.getSelectedTimeSeconds();

            // Build the game with selected settings
            HumanPlayer human = new HumanPlayer("Player 1");
            AIPlayer cpu = new AIPlayer("CPU");
            game = new WarGame(human, cpu, maxRounds);
            game.startGame();

            // Reset counters and UI
            p1RoundsWon = 0;
            p2RoundsWon = 0;
            p1CardImageView.setImage(cardBackImage);
            p2CardImageView.setImage(cardBackImage);
            drawButton.setDisable(false);
            resolveWarButton.setDisable(true);
            updateAllLabels(null);
            statusLabel.setText("Game started! Click 'Draw Card' to play.");

            // Handle timer setup
            stopTimer(); // clear any previous timer
            if (timeSeconds > 0) {
                timerLabel.setText(formatTime(timeSeconds));
                pauseButton.setDisable(false);
                pauseButton.setText("Pause");
                startTimer(timeSeconds);
            } else {
                timerLabel.setText("");
                pauseButton.setDisable(true);
            }

            System.out.println("New game — Rounds: " +
                    (maxRounds == 0 ? "Unlimited" : maxRounds) +
                    " | Timer: " + (timeSeconds == 0 ? "None" : timeSeconds + "s"));

        } catch (IOException e) {
            statusLabel.setText("Error loading setup screen.");
            e.printStackTrace();
        }
    }

    /**
     * Handles the Draw Card button. Calls game.playRound() and routes the
     * result to the appropriate handler — normal win, war, or game over.
     */
    @FXML
    private void handleDraw() {
        if (game == null || game.isGameOver()) return;
        try {
            RoundResult result = game.playRound();
            if (result.getPlayer1Card() != null) {
                flipCardImage(p1CardImageView, getCardImage(result.getPlayer1Card()));
            }
            if (result.getPlayer2Card() != null) {
                flipCardImage(p2CardImageView, getCardImage(result.getPlayer2Card()));
            }
            updateAllLabels(result);

            if (result.isWar()) {
                drawButton.setDisable(true);
                resolveWarButton.setDisable(false);
                showWarFlow(result);

            } else if (game.isGameOver()) {
                trackRoundWin(result);
                handleGameOver();

            } else {
                trackRoundWin(result);
            }
        } catch (EmptyDeckException ede) {
            statusLabel.setText(ede.getMessage());
            handleGameOver();
        }
    }

    /**
     * Backup Resolve War button — manually triggers war resolution
     * if the popup flow doesn't cover an edge case.
     */
    @FXML
    private void handleResolveWar() {
        if (game == null || !game.isWarPending()) return;
        try {
            RoundResult result = game.resolveWar();
            updateAllLabels(result);

            if (!result.isWar()) {
                resolveWarButton.setDisable(true);
                drawButton.setDisable(false);
                trackRoundWin(result);
            }
            if (result.isGameOver()) handleGameOver();

        } catch (EmptyDeckException e) {
            statusLabel.setText(e.getMessage());
            handleGameOver();
        }
    }

    //*** FUTURE-USE METHODS ***//
    /**
     * Save button handler — stub until File I/O is implemented.
     * (Excluded for UD3)
     */
    @FXML
    private void handleSave() {
        statusLabel.setText("Save coming soon!");
        System.out.println("Save clicked.");
    }

    /**
     * Load button handler — stub until File I/O is implemented.
     * (Excluded for UD3)
     */
    @FXML
    private void handleLoad() {
        statusLabel.setText("Load coming soon!");
        System.out.println("Load clicked.");
    }

    //*** WAR POPUP FLOW ***//

    /**
     * Handles the full war sequence using a modal Alert popup.
     * Shows a popup describing the war, waits for the user to click OK,
     * then calls resolveWar(). If another tie occurs, the loop shows
     * another popup automatically. When the war is finally settled,
     * the popup chain ends and the main window updates with the result.
     *
     * @param initialResult the RoundResult that triggered the war
     */
    private void showWarFlow(RoundResult initialResult) {
        RoundResult current = initialResult;

        while (current.isWar()) {
            // Build the war popup
            Alert warAlert = new Alert(Alert.AlertType.INFORMATION);
            warAlert.setTitle("WAR!");
            warAlert.setHeaderText("It's a WAR!");
            warAlert.setContentText(
                    "Both players played " + current.getPlayer1Card().getRank() + "!\n\n" +
                            current.getWarPileSize() + " cards are at stake.\n" +
                            "Each player will stake 3 face-down cards + 1 face-up war card.\n\n" +
                            "Click OK to reveal the war cards."
            );

            warAlert.showAndWait(); // blocks until user clicks OK — popup auto-closes

            // Resolve war after popup closes
            try {
                current = game.resolveWar();
                if (current.getPlayer1Card() != null) {
                    flipCardImage(p1CardImageView, getCardImage(current.getPlayer1Card()));
                }
                if (current.getPlayer2Card() != null) {
                    flipCardImage(p2CardImageView, getCardImage(current.getPlayer2Card()));
                }
                updateAllLabels(current); // update main window between wars

            } catch (EmptyDeckException e) {
                // Player ran out of cards mid-war
                statusLabel.setText(e.getMessage());
                drawButton.setDisable(true);
                resolveWarButton.setDisable(true);
                handleGameOver();
                return;
            }
        }

        // War fully resolved — update buttons and track result
        resolveWarButton.setDisable(true);
        trackRoundWin(current);

        if (game.isGameOver()) {
            drawButton.setDisable(true);
            handleGameOver();
        } else {
            drawButton.setDisable(false);
        }
    }

    //*** GAME OVER ***//

    /**
     * Locks the UI, displays the winner, and shows a game-over Alert.
     */
    private void handleGameOver() {
        stopTimer();
        drawButton.setDisable(true);
        resolveWarButton.setDisable(true);

        Player winner = game.getWinner();
        String resultMessage;

        if (winner == null) {
            resultMessage = "It's a tie! Both players had the same number of cards.";
        } else {
            resultMessage = winner.getName() + " WINS with " + winner.getHandSize() + " cards!";
        }

        statusLabel.setText(resultMessage);

        Alert gameOverAlert = new Alert(Alert.AlertType.INFORMATION);
        gameOverAlert.setTitle("Game Over!");
        gameOverAlert.setHeaderText(resultMessage);
        gameOverAlert.setContentText(
                game.getPlayer1().getName() + " finished with " + game.getPlayer1().getHandSize() + " cards\n" +
                        game.getPlayer2().getName() + " finished with " + game.getPlayer2().getHandSize() + " cards\n" +
                        "Total rounds played: " + game.getCurrentRound() + "\n" +
                        "Rounds won — " + game.getPlayer1().getName() + ": " + p1RoundsWon +
                        " | " + game.getPlayer2().getName() + ": " + p2RoundsWon
        );
        gameOverAlert.showAndWait();

        System.out.println("Game over! " + resultMessage);
        System.out.println("Total cards in play: " + game.getTotalCardsInPlay()); // invariant check
    }

    //*** HELPER METHODS ***//

    /**
     * Refreshes all UI labels from the current game state.
     * Safe to call with a null result (just refreshes counters/names).
     *
     * @param result the latest RoundResult; null to refresh counters only
     */
    private void updateAllLabels(RoundResult result) {
        if (game == null) return;

        Player p1 = game.getPlayer1();
        Player p2 = game.getPlayer2();

        // Name labels
        p1NameLabel.setText(p1.getName());
        p2NameLabel.setText(p2.getName());

        // Hand size counters — these are the card counters that track winning/losing
        p1CardCountLabel.setText("Cards: " + p1.getHandSize());
        p2CardCountLabel.setText("Cards: " + p2.getHandSize());

        // Round counter (shows max if applicable)
        String roundText = "Round: " + game.getCurrentRound();
        if (game.getMaxRounds() > 0) {
            roundText += " / " + game.getMaxRounds();
        }
        roundLabel.setText(roundText);

        // Rounds won in score bar
        p1WinsLabel.setText(p1.getName() + " Rounds Won: " + p1RoundsWon);
        p2WinsLabel.setText(p2.getName() + " Rounds Won: " + p2RoundsWon);

        // Card display and status from the round result
        if (result != null) {
            statusLabel.setText(result.getMessage());
        }
    }

    /**
     * Increments the round win counter for the round's winner.
     * Called after every resolved round (including war resolutions).
     *
     * @param result the resolved RoundResult to track
     */
    private void trackRoundWin(RoundResult result) {
        if (result == null || result.getWinner() == null) return;
        if (result.getWinner() == game.getPlayer1()) p1RoundsWon++;
        else p2RoundsWon++;

        // Update score bar immediately
        p1WinsLabel.setText(game.getPlayer1().getName() + " Rounds Won: " + p1RoundsWon);
        p2WinsLabel.setText(game.getPlayer2().getName() + " Rounds Won: " + p2RoundsWon);
    }

    /**
     * Sets the max rounds for the next new game.
     * Called by the title screen controller before the main game loads.
     * 0 = unlimited, 20 = quick game, 50 = standard.
     *
     * @param maxRounds the round limit to apply
     */
    public void setSelectedMaxRounds(int maxRounds) {
        this.selectedMaxRounds = maxRounds;
    }

    /**
     * Starts the countdown timer for time attack mode.
     * Ticks every second, updates the timer label, and triggers
     * game over when it reaches zero.
     *
     * @param totalSeconds the countdown duration in seconds
     */
    private void startTimer(int totalSeconds) {
        remainingSeconds = totalSeconds;
        countdownTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            remainingSeconds--;
            timerLabel.setText(formatTime(remainingSeconds));

            if (remainingSeconds <= 0) {
                countdownTimer.stop();
                game.endEarly();
                Platform.runLater(() -> {
                    statusLabel.setText("Time's up!");
                    handleGameOver();
                });
            }
        }));
        countdownTimer.setCycleCount(totalSeconds);
        countdownTimer.play();
    }

    /**
     * Formats a time in seconds into MM:SS display format.
     *
     * @param totalSeconds the number of seconds to format
     * @return a string like "02:30" for 150 seconds
     */
    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Stops and clears the current timer if one is running.
     * Called when a new game starts to prevent stale timers.
     */
    private void stopTimer() {
        if (countdownTimer != null) {
            countdownTimer.stop();
            countdownTimer = null;
        }
        remainingSeconds = 0;
    }

    /**
     * Handles the Pause/Resume button click.
     * Pauses the timer if running, resumes it if paused.
     * Also disables the Draw button while paused to prevent
     * playing cards while time is frozen.
     */
    @FXML
    private void handlePause() {
        if (countdownTimer == null) return;

        if (countdownTimer.getStatus() == Animation.Status.RUNNING) {
            countdownTimer.pause();
            pauseButton.setText("Resume");
            drawButton.setDisable(true);
            statusLabel.setText("Game paused.");
        } else {
            countdownTimer.play();
            pauseButton.setText("Pause");
            drawButton.setDisable(false);
            statusLabel.setText("Game resumed!");
        }
    }

    //*** CARD GUI HELPER METHODS ***//

    /**
     * Loads an image from the war/images/ resource folder.
     * Logs a warning to the console if the file is not found.
     *
     * @param filename the image file name (e.g. "back-color.png", "spade_#.png")
     * @return the loaded Image, or null if not found
     */
    private Image loadImage(String filename) {
        URL url = getClass().getResource("/war/images/2x/" + filename);
        if (url == null) {
            System.err.println("Image not found: /war/images/" + filename);
            return cardBackImage; // fall back to back image
        }
        return new Image(url.toExternalForm());
    }

    /**
     * Builds and loads the card image for a given PlayingCard.
     * Filename format matches the htdebeer/SVG-cards convention:
     * {suit}_{rank}.png  e.g. "spade_2.png", "heart_10.png"
     *
     * @param card the PlayingCard to get an image for
     * @return the card's face image, or the back image if card is null
     */
    private Image getCardImage(PlayingCard card) {
        if (card == null) return cardBackImage;
        String suit = card.getSuit().name().toLowerCase();
        String rank = getRankFileName(card.getRank());
        return loadImage(suit + "_" + rank + ".png");
    }

    /**
     * Maps a Rank enum value to the filename fragment used in the
     * htdebeer/SVG-cards PNG naming convention.
     * Numbers use digits (2–10); face cards and ace use lowercase words.
     *
     * @param rank the Rank to map
     * @return the filename fragment for that rank
     */
    private String getRankFileName(Rank rank) {
        return switch (rank) {
            case TWO   -> "2";
            case THREE -> "3";
            case FOUR  -> "4";
            case FIVE  -> "5";
            case SIX   -> "6";
            case SEVEN -> "7";
            case EIGHT -> "8";
            case NINE  -> "9";
            case TEN   -> "10";
            case JACK  -> "jack";
            case QUEEN -> "queen";
            case KING  -> "king";
            case ACE   -> "ace";
        };
    }

    /**
     * "Animates" a card flip on the given ImageView.
     * Phase 1: Shrinks the ImageView to zero on the X axis (card turns edge-on).
     * Midpoint: Swaps the image (back → face, or previous card → new card).
     * Phase 2: Expands back to full width (new card face is revealed).
     *
     * This simulates a physical card flip without any 3D library.
     * Total animation duration: 300ms (150ms shrink + 150ms expand).
     *
     * @param imageView the ImageView to animate
     * @param newImage  the card face image to reveal at the midpoint
     */
    @SuppressWarnings("JavadocBlankLines")
    private void flipCardImage(ImageView imageView, Image newImage) {
        // Phase 1 — shrink to zero (card goes "edge-on")
        ScaleTransition shrink = new ScaleTransition(Duration.millis(150), imageView);
        shrink.setFromX(1.0);
        shrink.setToX(0.0);

        // Phase 2 — expand back to full (new image now showing)
        ScaleTransition expand = new ScaleTransition(Duration.millis(150), imageView);
        expand.setFromX(0.0);
        expand.setToX(1.0);

        // Swap image at the midpoint when the card is invisible (edge-on)
        shrink.setOnFinished(e -> {
            imageView.setImage(newImage);
            expand.play();
        });

        shrink.play();
    }
}
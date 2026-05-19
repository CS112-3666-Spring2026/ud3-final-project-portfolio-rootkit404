package war.controllers;

/*
  GameSetupController.java - Controls the game setup dialog.
  Shown when the player clicks "New Game." The user selects either
  a round-limited mode or a time attack mode. Once a button is clicked,
  the dialog closes and GameController reads the selections.

  @author Nathan Tshishimbi
 * @version 2.0 (UD3) / (05/18/2026)
 */

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class GameSetupController {

    @FXML private Button rounds20Button;
    @FXML private Button rounds50Button;
    @FXML private Button unlimitedButton;
    @FXML private Button time2Button;
    @FXML private Button time5Button;
    @FXML private Button time10Button;

    //*** SELECTED VALUES ***//
    // Read by GameController after the dialog closes.
    // maxRounds: 0 = unlimited, 20 or 50 for limited modes
    // timeSeconds: 0 = no timer, otherwise the countdown duration
    private int selectedMaxRounds = 0;
    private int selectedTimeSeconds = 0;

    //*** ROUND MODE HANDLERS ***//

    @FXML
    private void handleRounds20() {
        selectedMaxRounds = 20;
        selectedTimeSeconds = 0;
        closeDialog();
    }

    @FXML
    private void handleRounds50() {
        selectedMaxRounds = 50;
        selectedTimeSeconds = 0;
        closeDialog();
    }

    @FXML
    private void handleUnlimited() {
        selectedMaxRounds = 0;
        selectedTimeSeconds = 0;
        closeDialog();
    }

    //*** TIME ATTACK HANDLERS ***//

    @FXML
    private void handleTime2() {
        selectedMaxRounds = 0;       // no round limit — timer ends the game
        selectedTimeSeconds = 120;   // 2 minutes
        closeDialog();
    }

    @FXML
    private void handleTime5() {
        selectedMaxRounds = 0;
        selectedTimeSeconds = 300;   // 5 minutes
        closeDialog();
    }

    @FXML
    private void handleTime10() {
        selectedMaxRounds = 0;
        selectedTimeSeconds = 600;   // 10 minutes
        closeDialog();
    }

    //*** HELPER ***//

    /**
     * Closes the setup dialog window.
     * Called after any mode button is clicked.
     */
    private void closeDialog() {
        Stage stage = (Stage) rounds20Button.getScene().getWindow();
        stage.close();
    }

    //*** GETTERS (read by GameController after dialog closes) ***//

    public int getSelectedMaxRounds()   { return selectedMaxRounds; }
    public int getSelectedTimeSeconds() { return selectedTimeSeconds; }
}
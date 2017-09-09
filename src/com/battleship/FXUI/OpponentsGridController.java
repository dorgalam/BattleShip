package com.battleship.FXUI;

import com.battleship.Logic.Board;
import com.battleship.Logic.Game;
import com.battleship.Logic.Point;
import com.battleship.Logic.Ship;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;


public class OpponentsGridController extends GridBase {

    @FXML
    public Pane opponentsGrid;

    @FXML
    Text attackOutcome;

    @FXML
    protected void initialize() {
        super.initialize();
        this.hitStyle = "hit-opponent";
        this.gridNum = 1;
        opponentsGrid.opacityProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue.equals(0.0)) {
                populateGrid();
            }
        });
    }



    @Override
    void populateGrid() {
        super.populateGrid();
        addDestroyedShipStyles();
    }

    void moveToCenter() {
        super.moveToCenter();
        double windowSize = Context.getInstance().getWindowSize();
        attackOutcome.setLayoutY(windowSize / 2 + 9);
        attackOutcome.setTextAlignment(TextAlignment.CENTER);
        attackOutcome.setLayoutX(0);
        attackOutcome.setWrappingWidth(windowSize);
    }

    @FXML
    void handleGridClick(MouseEvent e) {
        try {
            if (grid.getStyleClass().indexOf("turn-made") > -1) {
                return;
            }
            Node source = (Node)e.getTarget() ;
            int colIndex = GridPane.getColumnIndex(source);
            int rowIndex = GridPane.getRowIndex(source);
            final int result = game.makeTurn(colIndex - 1, rowIndex - 1);
            String textToWrite, classToAdd = "";
            switch (result) {
                case 1:
                    textToWrite = "Hit! still your turn";
                    source.getStyleClass().clear();
                    classToAdd = "hit-opponent";
                    break;
                case 0:
                    textToWrite = "Miss.. switching players";
                    source.getStyleClass().clear();
                    classToAdd = "miss";
                    break;
                case -1:
                    return;
                case -2:
                    textToWrite = "Mine hit!! your cell took damage";
                    source.getStyleClass().clear();
                    classToAdd = "mine-hit";
                    break;
                default:
                    if (game.isGameFinished()) {
                        Context.getInstance();
                    }
                    textToWrite = "Great hit! ship is destroyed, still your turn";
                    source.getStyleClass().clear();
                    addDestroyedShipStyles();
                    break;
            }
            source.getStyleClass().add(classToAdd);
            grid.setDisable(true);
            grid.setOpacity(0.2);
            attackOutcome.setText(textToWrite);
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.millis(result == 1 ? 500 : 2000),
                    ae -> {
                        attackOutcome.setText("");
                        if (result < 1) {
                            populateGrid();
                        }
                        TransitionEffects.fadeEffect(grid, 0.2, 500).setOnFinished(finished -> {
                            grid.setDisable(false);
                        });
                    }));
            timeline.play();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    void addDestroyedShipStyles() {
        game.getDestroyedShips().forEach(this::addShipStyles);
    }
}
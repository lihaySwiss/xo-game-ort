package com.example.xoo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class GameWindowController {
    private int size;
    private String name;

    private Game game;

    @FXML
    private Label mynameLLabel;

    private Label waitingLabel = new Label("Waiting for game...");

    @FXML
    private Label opponentNameLabel;

    @FXML
    private VBox mainVbox;

    @FXML
    private Circle moveIndicator;

    private GridPane pane;

    Client client;

    MainGameController mainWindow;

    //put the waiting for game prompt
    public void initialize(){
        waitingLabel.setFont(new Font(30));
    }

    public void initParams(int size, String name){
        this.size = size;
        this.name = name;
    }

    public void initGame(GameDetails gd) {
        mainVbox.getChildren().remove(waitingLabel);

        this.game = new Game(gd);

        this.pane = new GridPane();
        int size = game.getN();

        for (int r = 0; r < size; r++) {
            RowConstraints rc = new RowConstraints();
            rc.setVgrow(Priority.ALWAYS);
            rc.setFillHeight(true);
            pane.getRowConstraints().add(rc);
        }
        for (int c = 0; c < size; c++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS); // allow column to grow
            cc.setFillWidth(true); // ask nodes to fill space for column

            pane.getColumnConstraints().add(cc);
        }
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++) {
                Button button = new Button();
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                button.setOnAction(event -> btnClick(event));
                button.setUserData(new Pair<Integer, Integer>(r, c));
                pane.add(button, c, r);
            }

        pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(pane, Priority.ALWAYS);
        mainVbox.getChildren().add(pane);

        mynameLLabel.setText(gd.name());
        opponentNameLabel.setText(gd.opponentName());

        if (gd.firstMove()){
            moveIndicator.setFill(Color.GREEN);
            enableButtons();
        }

        else {
            moveIndicator.setFill(Color.YELLOW);
            disableButtons();
        }
    }

    private void disableButtons(){
        for(Node node : pane.getChildren()){
            Button button = (Button) node;
            button.setOnAction(null);
        }
    }

    private void enableButtons(){
        for(Node node : pane.getChildren()){
            Button button = (Button) node;
            button.setOnAction(this::btnClick);
        }
    }

    private void btnClick(ActionEvent event) {
        Button button = (Button) event.getSource();
        Pair<Integer, Integer> pair = (Pair<Integer, Integer>) button.getUserData();
        if (pair == null)
            return;

        Move move = new Move(pair.getKey(), pair.getValue());

        //update GUI
        disableButtons();
        moveIndicator.setFill(Color.YELLOW);
        button.setText(String.valueOf((char)(game.getMark())));

        //pass move to network handler
        client.processMove(move);

        button.setUserData(null);
    }

    private Button locateCell(int x, int y) throws RuntimeException {
        var ch = pane.getChildren();

        for (Node node : ch) {
            Pair<Integer, Integer> pair = (Pair<Integer, Integer>) node.getUserData();

            if (pair == null)
                continue;

            if (pair.getKey() == x && pair.getValue() == y)
                return (Button) node;
        }
        //throw new RuntimeException("couldn't find node");
        //continue;
        return null;
    }

    public void handleOpponentMove(Move move) {
        //update game structure
        game.opponentMark(move.x(), move.y());

        //update GUI
        Button target = locateCell(move.x(), move.y());
        if(target != null) {
            target.setText(String.valueOf((char) game.getGameDetails().opponentMark()));
            target.setUserData(null);
            enableButtons();
        }
        moveIndicator.setFill(Color.GREEN);
    }


    public void displayIllegal(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.show();
    }


    public void waitForGame() throws IOException, ClassNotFoundException {
        mainVbox.getChildren().add(waitingLabel);
        client = new Client(size, name, this);
        new Thread(client).start();
    }

    public void handleTermination(Move terminateMove) throws IOException{
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if(terminateMove.win()) {
            alert.setContentText("You won");
        } else if(!terminateMove.win())
            alert.setContentText("You lost");
        else
            alert.setContentText("Draw");

        alert.showAndWait();


        Stage stage = (Stage) pane.getScene().getWindow();

        //Platform.exit();
        //start new main window
        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("mainWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        mainWindow = (MainGameController) fxmlLoader.getController();
        mainWindow.initialize();

        stage.setScene(scene);

    }
}

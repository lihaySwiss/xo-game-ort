package com.example.xoo;

import java.sql.Timestamp;
import com.example.database.viewModel.GameDB;
import com.example.database.Model.GameModel;
import com.example.database.viewModel.PlayerDB;
import com.example.database.Model.PlayerModel;

public class HandlerSQL {
    private final PlayerDB playerDB = new PlayerDB();
    private final GameDB gameDB = new GameDB();
    private PlayerModel playerOne;
    private PlayerModel playerTwo;
    private GameModel gameModel;

    public HandlerSQL(String username1, String username2, int boardSize, Timestamp startTime) {
        this.playerOne = new PlayerModel(username1);
        if (this.playerDB.selectByName(username1).isEmpty()) {
            this.playerDB.insert(this.playerOne);
        } else {
            this.playerOne = playerDB.selectByName(username1).getFirst();
        }
        this.playerDB.saveChanges();
        this.playerTwo = new PlayerModel(username2);
        if (this.playerDB.selectByName(username2).isEmpty()) {
            this.playerDB.insert(this.playerTwo);
        } else {
            this.playerTwo = playerDB.selectByName(username2).getFirst();
        }
        this.playerDB.saveChanges();
        if(this.playerOne == null || this.playerTwo == null)
        {
            System.out.println("players failed\n");
        }
        this.gameModel = new GameModel(this.playerOne, this.playerTwo, boardSize, startTime);
        this.gameModel.setResult(GameModel.Result.ACTIVE);
        this.gameDB.insert(this.gameModel);
        this.gameDB.saveChanges();
        System.out.println(gameModel.toString());
        if(this.gameModel instanceof GameModel)
        {
            System.out.println("yes");
        }
    }

    public PlayerModel getPlayerOne() {
        return this.playerOne;
    }

    public PlayerModel getPlayerTwo() {
        return this.playerTwo;
    }

    public void win(PlayerModel winner, Timestamp endTime) {
        this.gameModel.setResult(GameModel.Result.WIN);
        this.gameModel.setWinner(winner);
        this.gameModel.setEndTime(endTime);
        this.gameDB.update(this.gameModel);
        this.gameDB.saveChanges();
    }

    public void draw(Timestamp endTime) {
        this.gameModel.setResult(GameModel.Result.DRAW);
        this.gameModel.setEndTime(endTime);
        this.gameDB.update(this.gameModel);
        this.gameDB.saveChanges();
    }
}

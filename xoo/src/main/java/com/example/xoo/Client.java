package com.example.xoo;

import com.example.database.viewModel.BaseDB;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class Client implements Runnable{

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8888;

    private String name;
    private int size;
    private ObjectOutputStream os;
    private ObjectInputStream is;

    private GameWindowController controller;

    private Socket socket;

    private Move playerMove = null;
    private Lock moveLock = new ReentrantLock();
    private Condition moveSendCondition = moveLock.newCondition();

    private static final Logger logger = Logger.getLogger(Client.class.getName());

    private BaseDB dataBase;

    public void processMove(Move playerMove) {
        System.out.println("client: processing move");
        moveLock.lock();
        this.playerMove = playerMove;
        moveSendCondition.signalAll();
        System.out.println("client: signalled");
        moveLock.unlock();
    }


    //initialize connection to the host
    public Client(int size, String name, GameWindowController controller) {
        this.controller = controller;
        this.size = size;
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println("client: running");
        Request req = new Request(size, name);

        boolean success = false;
        while (!success) {
            try {
                //acquire connection
                socket = new Socket(HOST, PORT);
                is = new ObjectInputStream(socket.getInputStream());
                os = new ObjectOutputStream(socket.getOutputStream());
                os.flush();

                //initiate request
                os.writeObject(req);
                Response resp = (Response) is.readObject();
                if (!resp.getType().equals(Response.Type.GAME_FOUND))
                    throw new IOException("Failed to find game");

                //game found
                GameDetails gd = (GameDetails) resp.getData();
                Platform.runLater(() -> controller.initGame(gd));

                if (gd.firstMove())
                    waitForMove();

                else
                    waitForOpponent();

                success = true;

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    //send the given move to host
    private void sendMove(Move move) throws IOException, ClassNotFoundException {
        System.out.println("client: sending move");
        os.writeObject(move);

        System.out.println("client: sent move, waiting for response");
        Response resp = (Response) is.readObject();
        if(!resp.getType().equals(Response.Type.ACCEPTED_MOVE)) {
            controller.displayIllegal(resp.toString());
        }
    }

    //wait for the next move from the opponent
    private void waitForOpponent() throws IOException, ClassNotFoundException {
        System.out.println("client: waiting for opponent");
        Move move = (Move) is.readObject();
        updateGUI(move);
        if(move.terminate())
            terminateGame(move);

        waitForMove();
    }

    private void waitForMove() throws IOException, ClassNotFoundException {
        System.out.println("client: waiting for move from GUI");
        moveLock.lock();
        try {
            while (playerMove == null) {
                System.out.println("move null, going to sleep");
                moveSendCondition.await();
            }

            System.out.println("client: wake up, send move");
            sendMove(playerMove);
            playerMove = null;
            System.out.println("client: move sent");
            waitForOpponent();
        }

        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        finally{
            moveLock.unlock();
        }
    }

    private void updateGUI(Move move){
        Platform.runLater(()->controller.handleOpponentMove(move));
    }

    private void terminateGame(Move terminateMove) throws IOException {
        Platform.runLater(()-> {
            try {
                controller.handleTermination(terminateMove);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        closeConnection();
    }

    public void closeConnection() throws IOException {
        is.close();
        os.close();
        socket.close();
    }
}

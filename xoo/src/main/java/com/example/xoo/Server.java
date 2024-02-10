package com.example.xoo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static final int PORT = 8888;
    static ServerSocket socket;

    private final ExecutorService pool = Executors.newCachedThreadPool();
    private Map<Integer, ServerRequest> requests = new HashMap<>();

    public Server() throws IOException {
        socket = new ServerSocket(PORT);

        Socket playerSocket;
        while (true) {
            try {
                playerSocket = socket.accept();
                System.out.println("server: accepted connection " + playerSocket.toString());
                Connection conn = new Connection(playerSocket);

                ServerRequest req = waitForRequest(conn);
                processRequest(req);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws IOException {
        new Server();
    }

    private ServerRequest waitForRequest(Connection connection) throws IOException, ClassNotFoundException {

        System.out.println("server: waiting for request");
        Request req = (Request) connection.getIs().readObject();
        System.out.println("server: received game request from " + connection.toString());
        ServerRequest request = new ServerRequest(req.getSize(), req.getName(), connection);

        return request;
    }

    private void processRequest(ServerRequest request) throws IOException {
        System.out.println("server: processing request");
        if (requests.containsKey(request.getSize())) {
            ServerRequest second = requests.remove(request.getSize());
            requests.remove(request.getSize());
            initiateGame(request, second);
        } else
            requests.put(request.getSize(), request);
    }

    private void initiateGame(ServerRequest req1, ServerRequest req2) throws IOException {
        System.out.println("server: initiating game");
        pool.submit(new Session(req1, req2));
    }

    public class Session implements Runnable {
        ServerRequest req1;
        ServerRequest req2;
        Game game;

        ObjectOutputStream os1, os2;
        ObjectInputStream is1, is2;

        private GameDetails gd1;

        public Session(ServerRequest req1, ServerRequest req2) {
            this.req1 = req1;
            this.req2 = req2;

            gd1 = new GameDetails(req1.getSize(), req1.getName(),
                    req2.getName(), (byte)'x', (byte)'o', true);
        }

        @Override
        public void run() {
            try {
                System.out.println("session: running");
                initSession();

                Response acc = new Response(Response.Type.ACCEPTED_MOVE);
                Response decl = new Response(Response.Type.ILLEGAL_MOVE);

                Move move;

                while(true){
                    //  ping pong moves back and forth until win
                    move = (Move) is1.readObject();

                    if(checkMove(move))
                        os1.writeObject(acc);

                    game.mark(move.x(), move.y());
                    if(game.checkWin(move.x(), move.y()))
                        terminateGame(move, os1, os2);

                    os2.writeObject(move);

                    move = (Move) is2.readObject();

                    if(checkMove(move))
                        os2.writeObject(acc);

                    game.opponentMark(move.x(), move.y());

                    if(game.checkOpponentWin(move.x(), move.y())){
                        terminateGame(move, os2, os1);
                    }

                    os1.writeObject(move);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }


        private void initSession() throws IOException {
            System.out.println("session: initiating with" + req1.getConnection() + " " + req2.getConnection());

            os1 = req1.getConnection().getOs();
            os2 = req2.getConnection().getOs();
            is1 = req1.getConnection().getIs();
            is2 = req2.getConnection().getIs();

            Response resp1 = new Response(Response.Type.GAME_FOUND);
            Response resp2 = new Response(Response.Type.GAME_FOUND);

            resp1.setData(gd1);

            resp2.setData(new GameDetails(req2.getSize(), req2.getName(),
                    req1.getName(), (byte)'o', (byte)'x', false));

            game = new Game(gd1);

            System.out.println("session: responding about a new game");
            os1.writeObject(resp1);
            os2.writeObject(resp2);
            System.out.println("session: responded");
        }

        private boolean checkMove(Move move){
            //TO DO
            return true;
        }

        private void terminateGame(Move move, ObjectOutputStream osWin, ObjectOutputStream osLose) throws IOException {
            osWin.writeObject(new Move(move.x(), move.y(), true, true));
            osLose.writeObject(new Move(move.x(), move.y(), true, false));
        }

        private void close() throws IOException {
            os1.close();
            os2.close();
            is1.close();
            is2.close();
            Thread.currentThread().interrupt();
        }
    }
}

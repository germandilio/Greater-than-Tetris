package ru.hse.germandilio.tetris.server;

import ru.hse.germandilio.tetris.commands.GameStatus;
import ru.hse.germandilio.tetris.server.generator.BricksRandomGenerator;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;

public class Server implements AutoCloseable, CallbackProvider {
    private static final int DEFAULT_PORT = 5000;

    private final boolean isSingle;
    private final int maxClients;
    private final long timeout;
    private final ServerSocket socket;

    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    private volatile boolean gameStarted;

    public Server(int maxClients, long maxTime) throws IOException {
        if (maxClients < 1 || maxClients > 2) {
            throw new IllegalArgumentException("MaxClients number should be greater than 1");
        }
        if (maxTime <= 0) {
            throw new IllegalArgumentException("Max timeout should be greater than 0 milliseconds");
        }

        socket = new ServerSocket(DEFAULT_PORT);

        isSingle = maxClients == 1;

        this.maxClients = maxClients;
        this.timeout = maxTime;
    }

    public void launch() throws IOException {
        new Thread(() -> {
            try(socket) {
                ForkJoinPool executor = new ForkJoinPool(maxClients);

                for (int i = 0; i < maxClients; i++) {
                    var acceptedSocket = socket.accept();
                    var handler = new ClientHandler(this, acceptedSocket, timeout);

                    clients.add(handler);
                    executor.execute(handler);
                }

            } catch(IllegalArgumentException | IOException ex) {
                throw new IllegalStateException("Cannot create executor environment.", ex);
            }
        }).start();
    }

    @Override
    public void close() throws Exception {
        if (!socket.isClosed()) {
            socket.close();
        }
    }

    @Override
    public synchronized void statusChanged() {
        if (isSingle) {
            singleStatusChanged();
            return;
        }

        boolean startGame = hasStatus(GameStatus.READY_TO_START_GAME);
        if (startGame && !gameStarted) {
            gameStarted = true;
            handleStartGame();
        }

        if (clients.size() == 2) {
            if (clients.get(0).getUserStatus() == GameStatus.DISCONNECTED) {
                clients.get(0).endGame(clients.get(1), true);
                return;
            }
            if (clients.get(1).getUserStatus() == GameStatus.DISCONNECTED) {
                clients.get(1).endGame(clients.get(0), true);
                return;
            }
        }

        boolean endGame = hasStatus(GameStatus.END_GAME);
        if (endGame && gameStarted) {
            gameStarted = false;
            handleEndGame();
        }
    }

    private void singleStatusChanged() {
        if (clients.get(0).getUserStatus() == GameStatus.READY_TO_START_GAME && !gameStarted) {
            var generator = new BricksRandomGenerator();

            gameStarted = true;
            clients.get(0).startGame(generator);
            return;
        }

        if (clients.get(0).getUserStatus() == GameStatus.END_GAME && gameStarted) {
            gameStarted = false;
            clients.get(0).endGame();
        }
    }

    private boolean hasStatus(GameStatus gameStatus) {
        boolean status = true;
        for (ClientHandler client : clients) {
            status &= (client.getUserStatus() == gameStatus);
        }
        return status;
    }

    private void handleStartGame() {
        var generator = new BricksRandomGenerator();

        for (int i = 0; i < clients.size(); i++) {
            var opponent = clients.get(clients.size() - 1 - i);
            clients.get(i).startGame(opponent, generator);
        }
    }

    private void handleEndGame() {
        for (int i = 0; i < clients.size(); i++) {
            var opponent = clients.get(clients.size() - 1 - i);
            clients.get(i).endGame(opponent, false);
        }
    }
}

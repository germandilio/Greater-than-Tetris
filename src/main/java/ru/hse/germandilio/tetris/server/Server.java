package ru.hse.germandilio.tetris.server;

import ru.hse.germandilio.tetris.server.clienthandling.ClientHandler;
import ru.hse.germandilio.tetris.server.game.ServerGameManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;

public class Server implements AutoCloseable {
    public static final int DEFAULT_PORT = 5000;

    private final ServerSocket socket;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    private final ServerGameManager serverGame;

    public Server(int maxClients, long maxTime) throws IOException {
        if (maxClients < 1 || maxClients > 2) {
            throw new IllegalArgumentException("MaxClients number should be greater than 1");
        }
        if (maxTime <= 0) {
            throw new IllegalArgumentException("Max timeout should be greater than 0 milliseconds");
        }

        socket = new ServerSocket(DEFAULT_PORT);

        serverGame = new ServerGameManager(maxClients, maxTime);
    }

    public void launch() {
        new Thread(() -> {
            try(socket) {
                ForkJoinPool executor = new ForkJoinPool();

                for (int i = 0; i < serverGame.getMaxUsersNumber(); i++) {
                    var acceptedSocket = socket.accept();
                    var handler = new ClientHandler(acceptedSocket, serverGame);

                    clients.add(handler);
                    System.out.println("Client connected with InetAddress=" + acceptedSocket.getInetAddress());

                    executor.execute(handler);
                }

            } catch(IllegalArgumentException | IOException ex) {
                throw new IllegalStateException("Cannot create executor environment or accept client.", ex);
            }
        }).start();
    }

    @Override
    public void close() throws Exception {
        if (!socket.isClosed()) {
            socket.close();
        }
    }
}

package net.advancius.communication;

import lombok.Data;
import lombok.SneakyThrows;
import net.advancius.AdvanciusLogger;
import net.advancius.AdvanciusSpigot;

import java.io.IOException;
import java.net.Socket;

@Data
public class CommunicationConnector extends Thread {

    private final CommunicationManager communicationManager;

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            if (!isCurrentlyConnected()) {
                try {
                    AdvanciusLogger.info("Attempting to establish a connection to the AdvanciusBungee server.");
                    Socket socket = communicationManager.attemptConnection();

                    AdvanciusLogger.info("Successfully established connection to server " + socket.getRemoteSocketAddress() + ":" + socket.getPort());
                } catch (IOException exception) {
                    AdvanciusLogger.warn("Failed to establish connection to server, trying again in ten seconds.");
                    this.sleep(10 * 1000);
                }
            }
        }

    }

    private boolean isCurrentlyConnected() {
        Connection currentConnection = communicationManager.getClientConnection();
        return currentConnection != null && !currentConnection.getSocket().isClosed() &&
        !currentConnection.getSocket().isInputShutdown() && !currentConnection.getSocket().isOutputShutdown();
    }
}

package net.advancius.communication;

import lombok.Data;
import net.advancius.AdvanciusLogger;
import net.advancius.AdvanciusSpigot;

import java.io.EOFException;
import java.io.IOException;

@Data
public class CommunicationReader extends Thread {

    private final Connection connection;

    @Override
    public void run() {
        while (true) {
            try {
                if (connection.getSocket().isClosed()) {
                    AdvanciusLogger.warn("Client was closed.");
                    connection.disconnect();
                    break;
                }
                CommunicationPacket communicationPacket = connection.readPacket();
                AdvanciusSpigot.getInstance().getCommunicationManager().handleReadPacket(communicationPacket);
            } catch (EOFException exception) {
                connection.disconnect();
              break;
            } catch (IOException exception) {}
        }
    }
}

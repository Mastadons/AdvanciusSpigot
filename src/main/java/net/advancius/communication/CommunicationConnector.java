package net.advancius.communication;

import lombok.Data;
import lombok.SneakyThrows;
import net.advancius.AdvanciusClient;
import net.advancius.AdvanciusLogger;

import java.util.concurrent.TimeUnit;

@Data
public class CommunicationConnector extends Thread {

    private final AdvanciusClient client;

    @SneakyThrows
    @Override
    public void run() {
        while (true) {

            if (!client.hasPacketConnection()) {
                try {
                    AdvanciusLogger.info("Attempting to establish a packet connection to the AdvanciusBungee server.");
                    client.establishPacketConnection().get(5, TimeUnit.SECONDS);
                    AdvanciusLogger.info("Successfully established packet connection!");
                } catch (Exception exception) {
                    AdvanciusLogger.info("Failed to establish packet connection!");
                }
            }
            if (!client.hasSocketConnection()) {
                try {
                    AdvanciusLogger.info("Attempting to establish a socket connection to the AdvanciusBungee server.");
                    client.establishSocketConnection().get(5, TimeUnit.SECONDS);
                    AdvanciusLogger.info("Successfully established socket connection!");
                } catch (Exception exception) {
                    AdvanciusLogger.info("Failed to establish socket connection!");
                }
            }

            Thread.sleep(10000);
        }
    }
}

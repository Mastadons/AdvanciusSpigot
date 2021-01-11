package net.advancius.communication;

import lombok.Data;
import net.advancius.AdvanciusClient;
import net.advancius.AdvanciusSpigot;
import net.advancius.commons.Identifier;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;

import java.io.IOException;

@Data
@FlagManager.FlaggedClass
public class CommunicationManager {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 5)
    private static void loadChannelManager() throws IOException {
        CommunicationManager instance = new CommunicationManager();
        instance.startCommunication();

        AdvanciusSpigot.getInstance().setCommunicationManager(instance);
    }

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_SAVE, priority = 5)
    private static void saveChannelManager() throws IOException, InterruptedException {
        CommunicationManager instance = AdvanciusSpigot.getInstance().getCommunicationManager();
        instance.stopCommunication();
    }

    private AdvanciusClient client;
    private CommunicationConnector connector;

    public void startCommunication() {
        Identifier identifier = Identifier.fromString(CommunicationConfiguration.getInstance().identifier);
        String authenticationToken = CommunicationConfiguration.getInstance().authenticationToken;

        client = AdvanciusClient.Builder(identifier, authenticationToken)
                .packetAddress(CommunicationConfiguration.getInstance().packetAddress)
                .socketAddress(CommunicationConfiguration.getInstance().socketAddress)
                .build();

        connector = new CommunicationConnector(client);
        connector.start();
    }

    public void stopCommunication() {
        connector.stop();

        client.destroySocketConnection();
        client.destroyPacketConnection();
    }
}
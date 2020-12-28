package net.advancius.communication;

import lombok.Data;
import net.advancius.AdvanciusLogger;
import net.advancius.AdvanciusSpigot;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.protocol.Protocol;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

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

    private final Map<UUID, AtomicReference<CommunicationPacket>> requestMap = new HashMap<>();
    private final List<CommunicationListener> listenerList = new ArrayList<>();

    private final List<UUID> usedIds = Collections.synchronizedList(new ArrayList<>());

    private Connection clientConnection;
    private CommunicationReader communicationReader;

    private CommunicationConnector communicationConnector;

    private SecretKey encryptionKey;
    private byte[] salt;

    public void startCommunication() {
        AdvanciusLogger.info("Starting communication connector.");
        communicationConnector = new CommunicationConnector(this);
        communicationConnector.start();
    }

    public Socket attemptConnection() throws IOException {
        Socket socket = new Socket(CommunicationConfiguration.getInstance().host, CommunicationConfiguration.getInstance().port);

        clientConnection = new Connection(socket);

        communicationReader = new CommunicationReader(clientConnection);
        communicationReader.start();
        return socket;
    }

    public void stopCommunication() throws IOException, InterruptedException {
        communicationConnector.stop();

        if (clientConnection != null) clientConnection.getSocket().close();
        if (communicationReader != null) communicationReader.stop();
    }

    public void handleReadPacket(CommunicationPacket communicationPacket) {
        if (packetExists(communicationPacket)) return;
        if (handleRequest(communicationPacket)) return;

        AdvanciusLogger.log(Level.INFO, "[Network] Incoming packet(%s) with code %d",
                communicationPacket.getId().toString(), communicationPacket.getCode());

        listenerList.forEach(listener -> listener.getListenerMethods(communicationPacket.getCode()).forEach(method -> method.executeMethod(communicationPacket)));
    }

    public boolean sendPacket(CommunicationPacket communicationPacket) {
        try {
            clientConnection.sendPacket(communicationPacket);
            AdvanciusLogger.log(Level.INFO, "[Network] Successfully sent packet(%s) with code %d",
                    communicationPacket.getId().toString(), communicationPacket.getCode());
            return true;
        } catch (IOException exception) {
            AdvanciusLogger.log(Level.INFO, "[Network] Failed to send packet(%s) with code %d",
                    communicationPacket.getId().toString(), communicationPacket.getCode());
            if(exception.getMessage().contains("Broken pipe")) clientConnection = null;
            return false;
        }
    }

    private boolean packetExists(CommunicationPacket communicationPacket) {
        if (usedIds.contains(communicationPacket.getId())) return true;
        usedIds.add(communicationPacket.getId());
        return false;
    }

    private boolean handleRequest(CommunicationPacket communicationPacket) {
        if (communicationPacket.getRespondingTo() == null) return false;
        requestMap.forEach((requestId, reference) -> {
            if (!communicationPacket.getRespondingTo().equals(requestId)) return;
            AdvanciusLogger.log(Level.INFO, "[Network] Handling incoming response(%s) with code %d",
                    communicationPacket.getId().toString(), communicationPacket.getCode());
            reference.set(communicationPacket);
        });
        return requestMap.remove(communicationPacket.getRespondingTo()) != null;
    }

    public AtomicReference<CommunicationPacket> awaitResponse(CommunicationPacket communicationPacket) {
        AtomicReference<CommunicationPacket> reference = new AtomicReference<>();
        requestMap.put(communicationPacket.getId(), reference);
        return reference;
    }

    public void registerListener(CommunicationListener listener) {
        listenerList.add(listener);
    }

    public void unregisterListener(CommunicationListener listener) {
        listenerList.remove(listener);
    }
}

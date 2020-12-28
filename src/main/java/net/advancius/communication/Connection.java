package net.advancius.communication;

import lombok.Data;
import net.advancius.AdvanciusSpigot;
import net.advancius.encryption.SymmetricEncryption;

import javax.crypto.SecretKey;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Data
public class Connection {

    private final Socket socket;

    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public CommunicationPacket readPacket() throws IOException {
        CommunicationManager communicationManager = AdvanciusSpigot.getInstance().getCommunicationManager();
        if (socket.isClosed()) throw new IOException("Socket is closed");

        String data = inputStream.readUTF();
        if (communicationManager.getEncryptionKey() != null) {
            byte[] encryptedData = Base64.getDecoder().decode(data);
            String decryptedData0 = SymmetricEncryption.decrypt(encryptedData, communicationManager.getEncryptionKey(), communicationManager.getSalt());
            return AdvanciusSpigot.GSON.fromJson(decryptedData0, CommunicationPacket.class);
        }
        return AdvanciusSpigot.GSON.fromJson(data, CommunicationPacket.class);
    }

    public void sendPacket(CommunicationPacket communicationPacket) throws IOException {
        CommunicationManager communicationManager = AdvanciusSpigot.getInstance().getCommunicationManager();
        if (socket.isClosed()) throw new IOException("Socket is closed");

        String data = AdvanciusSpigot.GSON.toJson(communicationPacket);

        if (communicationManager.getEncryptionKey() != null) {
            byte[] encryptedData = SymmetricEncryption.encrypt(data, communicationManager.getEncryptionKey(), communicationManager.getSalt());
            String encryptedData0 = Base64.getEncoder().encodeToString(encryptedData);
            outputStream.writeUTF(encryptedData0);
            return;
        }
        outputStream.writeUTF(data);
    }

    public void disconnect() {
        try {
            socket.close();
            inputStream.close();
            outputStream.close();
        } catch (IOException exception) {}
    }
}

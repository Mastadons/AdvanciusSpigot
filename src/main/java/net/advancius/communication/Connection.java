package net.advancius.communication;

import lombok.Data;
import net.advancius.AdvanciusSpigot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
        if (socket.isClosed()) throw new IOException("Socket is closed");

        String data = inputStream.readUTF();
        return AdvanciusSpigot.GSON.fromJson(data, CommunicationPacket.class);
    }

    public void sendPacket(CommunicationPacket communicationPacket) throws IOException {
        if (socket.isClosed()) throw new IOException("Socket is closed");

        String data = AdvanciusSpigot.GSON.toJson(communicationPacket);
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

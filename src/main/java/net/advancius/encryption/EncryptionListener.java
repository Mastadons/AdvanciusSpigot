package net.advancius.encryption;

import net.advancius.AdvanciusLogger;
import net.advancius.AdvanciusSpigot;
import net.advancius.communication.CommunicationConfiguration;
import net.advancius.communication.CommunicationConnector;
import net.advancius.communication.CommunicationHandler;
import net.advancius.communication.CommunicationListener;
import net.advancius.communication.CommunicationManager;
import net.advancius.communication.CommunicationPacket;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.protocol.Protocol;

import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@FlagManager.FlaggedClass
public class EncryptionListener implements CommunicationListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    public static void load() {
        AdvanciusSpigot.getInstance().getCommunicationManager().registerListener(new EncryptionListener());
    }

    @CommunicationHandler(code = Protocol.SERVER_ASYMMETRIC_ENCRYPTION)
    public void onServerAsymmetricEncryption(CommunicationPacket communicationPacket) {
        try {
            AdvanciusLogger.info("Received server encryption public key.");
            CommunicationManager communicationManager = AdvanciusSpigot.getInstance().getCommunicationManager();

            String publicBase64 = communicationPacket.getMetadata().getMetadata("public_key");
            PublicKey publicKey = AsymmetricEncryption.decodePublicKey(publicBase64);

            byte[] salt = SymmetricEncryption.generateRandomSalt();
            SecretKey encryptionKey = SymmetricEncryption.decodeSecretKey(Base64.getDecoder().decode(CommunicationConfiguration.getInstance().encryption));
            AdvanciusLogger.info("Generated unique symmetric encryption key.");

            byte[] saltEncrypted = AsymmetricEncryption.encrypt(salt, publicKey);
            byte[] keyEncrypted = AsymmetricEncryption.encrypt(encryptionKey.getEncoded(), publicKey);

            String saltEncryptedBase64 = Base64.getEncoder().encodeToString(saltEncrypted);
            String keyEncryptedBase64 = Base64.getEncoder().encodeToString(keyEncrypted);
            AdvanciusLogger.info("Encrypted unique symmetric encryption key.");

            CommunicationPacket encryptionResponse = CommunicationPacket.generatePacket(Protocol.CLIENT_ENCRYPTION);
            encryptionResponse.getMetadata().setMetadata("encryption_key", keyEncryptedBase64);
            encryptionResponse.getMetadata().setMetadata("salt", saltEncryptedBase64);
            communicationManager.sendPacket(encryptionResponse);
            AdvanciusLogger.info("Sent unique symmetric encryption key to server.");

            communicationManager.setEncryptionKey(encryptionKey);
            communicationManager.setSalt(salt);
        } catch (Exception exception) { exception.printStackTrace(); }
    }

    @CommunicationHandler(code = Protocol.SERVER_ACCEPT_ENCRYPTION)
    public void onServerAcceptEncryption(CommunicationPacket ignored) {
        AdvanciusLogger.info("Server successfully received symmetric encryption key.");
        CommunicationPacket communicationPacket = CommunicationPacket.generatePacket(Protocol.CLIENT_CREDENTIALS);
        communicationPacket.getMetadata().setMetadata("key", CommunicationConfiguration.getInstance().key);

        AdvanciusLogger.info("Sending my credentials...");
        AdvanciusSpigot.getInstance().getCommunicationManager().sendPacket(communicationPacket);
    }

    private List<Byte> convertByteArray(byte[] byteArray) {
        List<Byte> list = new ArrayList<>();
        for (int i = 0; i < byteArray.length; i++) list.add(byteArray[i]);
        return list;
    }
}

package net.advancius.communication;

import lombok.Data;
import net.advancius.utils.Metadata;

import java.util.UUID;

@Data
public class CommunicationPacket {

    private final UUID id;
    private final int  code;
    private final long timestamp;

    private UUID respondingTo;

    private final Metadata metadata = new Metadata();

    public static CommunicationPacket generatePacket(int code) {
        return new CommunicationPacket(UUID.randomUUID(), code, System.currentTimeMillis());
    }
}

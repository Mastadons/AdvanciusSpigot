package net.advancius.placeholder;

import net.advancius.AdvanciusSpigot;
import net.advancius.communication.CommunicationPacket;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.protocol.Protocol;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

@FlagManager.FlaggedClass
public class PlaceholderExpansion extends me.clip.placeholderapi.expansion.PlaceholderExpansion {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 50)
    private static void loadPlaceholderExpansion() {
        new PlaceholderExpansion().register();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (Bukkit.getOnlinePlayers().size() == 0) return null;

        final Object synchronizer = new Object();

        String[] components = identifier.split("_");
        if (components[0].equals("statistic")) {
            synchronized (synchronizer) {
                CommunicationPacket communicationPacket = CommunicationPacket.generatePacket(Protocol.CLIENT_STATISTIC);
                communicationPacket.getMetadata().setMetadata("id", player.getUniqueId());
                communicationPacket.getMetadata().setMetadata("namespace", components[1]);
                communicationPacket.getMetadata().setMetadata("name", components[2]);

                AtomicReference<CommunicationPacket> communicationResponse = AdvanciusSpigot.getInstance().getCommunicationManager().awaitResponse(communicationPacket);
                AdvanciusSpigot.getInstance().getCommunicationManager().sendPacket(communicationPacket);
                int attempts = 100;
                while (communicationResponse.get() == null && attempts-- > 0) {
                    try {
                        synchronizer.wait(10);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }
                if (communicationResponse.get() == null) return "Could not connect to server.";
                if (!communicationResponse.get().getMetadata().hasMetadata("score")) return null;
                return communicationResponse.get().getMetadata().getMetadata("score").toString();
            }
        }
        else if (components[0].equalsIgnoreCase("dump")) {
            synchronized (synchronizer) {
                CommunicationPacket communicationPacket = CommunicationPacket.generatePacket(Protocol.CLIENT_DUMP_REQUEST);
                communicationPacket.getMetadata().setMetadata("person", player.getUniqueId());
                communicationPacket.getMetadata().setMetadata("path", components[1]);

                AtomicReference<CommunicationPacket> communicationResponse = AdvanciusSpigot.getInstance().getCommunicationManager().awaitResponse(communicationPacket);
                AdvanciusSpigot.getInstance().getCommunicationManager().sendPacket(communicationPacket);
                int attempts = 100;
                while (communicationResponse.get() == null && attempts-- > 0) {
                    try {
                        synchronizer.wait(10);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }
                if (communicationResponse.get() == null) return "Could not connect to server.";

                if (!communicationResponse.get().getMetadata().hasMetadata("dump")) return null;
                return communicationResponse.get().getMetadata().getMetadata("dump").toString();
            }
        }
        return null;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "advancius";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Mastadons";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0-SNAPSHOT";
    }
}

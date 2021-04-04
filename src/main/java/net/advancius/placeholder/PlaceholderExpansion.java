package net.advancius.placeholder;

import net.advancius.AdvanciusSpigot;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.packet.Packet;
import net.advancius.packet.PacketResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@FlagManager.FlaggedClass
public class PlaceholderExpansion extends me.clip.placeholderapi.expansion.PlaceholderExpansion {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 50)
    private static void loadPlaceholderExpansion() {
        new PlaceholderExpansion().register();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (Bukkit.getOnlinePlayers().size() == 0) return null;

        String[] components = identifier.split("_");
        if (components[0].equals("statistic")) {
            return getStatistic(player.getUniqueId(), components[1], components[2]);
        } else if (components[0].equalsIgnoreCase("dump")) {
            return getDumpResponse(player.getUniqueId(), components[1]);
        }
        return null;
    }

    private String getStatistic(UUID id, String namespace, String statistic) {
        Packet packet = Packet.generatePacket("statistic");
        packet.getMetadata().setMetadata("id", id);
        packet.getMetadata().setMetadata("namespace", namespace);
        packet.getMetadata().setMetadata("name", statistic);

        try {
            CompletableFuture<PacketResponse> futureResponse = AdvanciusSpigot.getInstance().getCommunicationManager().getClient().sendPacket(packet);
            PacketResponse response = futureResponse.get(3, TimeUnit.SECONDS);

            return response.getMetadata().getMetadata("score");
        } catch (TimeoutException exception) {
            return "remote_timeout";
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private String getDumpResponse(UUID id, String path) {
        Packet packet = Packet.generatePacket("dump_request");
        packet.getMetadata().setMetadata("person", id);
        packet.getMetadata().setMetadata("path", path);

        try {
            CompletableFuture<PacketResponse> futureResponse = AdvanciusSpigot.getInstance().getCommunicationManager().getClient().sendPacket(packet);
            PacketResponse response = futureResponse.get(3, TimeUnit.SECONDS);

            return response.getMetadata().getMetadata("dump");
        } catch (TimeoutException exception) {
            return "remote_timeout";
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
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
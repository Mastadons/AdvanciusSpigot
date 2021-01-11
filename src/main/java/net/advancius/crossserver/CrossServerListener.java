package net.advancius.crossserver;

import net.advancius.AdvanciusSpigot;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.packet.Packet;
import net.advancius.packet.PacketHandler;
import net.advancius.packet.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@FlagManager.FlaggedClass
public class CrossServerListener implements PacketListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    public static void load() {
        AdvanciusSpigot.getInstance().getCommunicationManager().getClient().registerListener(new CrossServerListener());
    }

    @PacketHandler(packetType = "execute_command")
    public void onExecuteCommand(Packet packet) {
        String command = packet.getMetadata().getMetadata("command");

        Bukkit.getScheduler().scheduleSyncDelayedTask(AdvanciusSpigot.getInstance(), () -> {
            if (packet.getMetadata().hasMetadata("player")) {
                Player player = Bukkit.getPlayer(UUID.fromString(packet.getMetadata().getMetadata("player")));

                Bukkit.dispatchCommand(player, command);
            }
            else Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        });
    }
}

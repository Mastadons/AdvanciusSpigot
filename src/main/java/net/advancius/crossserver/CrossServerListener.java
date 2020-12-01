package net.advancius.crossserver;

import net.advancius.AdvanciusSpigot;
import net.advancius.communication.CommunicationHandler;
import net.advancius.communication.CommunicationListener;
import net.advancius.communication.CommunicationPacket;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.protocol.Protocol;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@FlagManager.FlaggedClass
public class CrossServerListener implements CommunicationListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    public static void load() {
        AdvanciusSpigot.getInstance().getCommunicationManager().registerListener(new CrossServerListener());
    }

    @CommunicationHandler(code = Protocol.SERVER_CROSS_COMMAND)
    public void onCrossServerCommand(CommunicationPacket communicationPacket) {
        String command = communicationPacket.getMetadata().getMetadata("command");

        Bukkit.getScheduler().scheduleSyncDelayedTask(AdvanciusSpigot.getInstance(), () -> {
            if (communicationPacket.getMetadata().hasMetadata("person")) {
                Player player = Bukkit.getPlayer(UUID.fromString(communicationPacket.getMetadata().getMetadata("person")));

                Bukkit.dispatchCommand(player, command);
            }
            else Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        });
    }
}

package net.advancius.crossserver;

import net.advancius.AdvanciusSpigot;
import net.advancius.command.BasicCommandListener;
import net.advancius.command.CommandManager;
import net.advancius.command.flag.CommandFlagList;
import net.advancius.communication.CommunicationPacket;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.protocol.Protocol;
import net.advancius.utils.ColorUtils;
import org.bukkit.entity.Player;

@FlagManager.FlaggedClass
public class CrossServerCommand extends BasicCommandListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 5)
    public static void load() {
        CommandManager.registerCommand(AdvanciusSpigot.getInstance(), new CrossServerCommand());
    }

    public CrossServerCommand() {
        super("crosscommand", "advancius.crosscommand", "description", "usageMessage", new String[]{"csc"});
    }

    @CommandHandler
    public void onPlayerCommand(Player player, CommandFlagList flagList) throws Exception {
        CommunicationPacket communicationPacket = CommunicationPacket.generatePacket(Protocol.CLIENT_CROSS_COMMAND);

        String server = flagList.getFlag("server").getData();
        String command = flagList.getFlag("command").getData();

        communicationPacket.getMetadata().setMetadata("server", server);
        communicationPacket.getMetadata().setMetadata("command", command);

        AdvanciusSpigot.getInstance().getCommunicationManager().sendPacket(communicationPacket);
        player.sendMessage(ColorUtils.translateColor(String.format("&3Me &8-> &3%s &7» &f%s", server, command)));
    }
}

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
import org.bukkit.command.CommandSender;
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
    public void onPlayerCommand(CommandSender sender, CommandFlagList flagList) throws Exception {
        CommunicationPacket communicationPacket = CommunicationPacket.generatePacket(Protocol.CLIENT_CROSS_COMMAND);

        String server = flagList.getFlag("server").getData();
        String command = flagList.getFlag("command").getData();

        communicationPacket.getMetadata().setMetadata("server", server);
        communicationPacket.getMetadata().setMetadata("command", command);
        communicationPacket.getMetadata().setMetadata("sender", sender.getName());

        AdvanciusSpigot.getInstance().getCommunicationManager().sendPacket(communicationPacket);
    }
}

package net.advancius.crossserver;

import net.advancius.AdvanciusLogger;
import net.advancius.AdvanciusSpigot;
import net.advancius.command.BasicCommandListener;
import net.advancius.command.CommandManager;
import net.advancius.command.flag.CommandFlagList;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.packet.Packet;
import org.bukkit.command.CommandSender;

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
        Packet packet = Packet.generatePacket("cross_command");

        String server = flagList.getFlag("server").getData();
        String command = flagList.getFlag("command").getData();

        packet.getMetadata().setMetadata("server", server);
        packet.getMetadata().setMetadata("command", command);
        packet.getMetadata().setMetadata("sender", sender.getName());

        AdvanciusLogger.info("Sending cross command to " + server + ": " + command);
        AdvanciusSpigot.getInstance().getCommunicationManager().getClient().sendPacket(packet, null);
    }
}

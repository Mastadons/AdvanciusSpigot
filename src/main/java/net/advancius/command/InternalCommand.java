package net.advancius.command;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class InternalCommand extends Command {

	/**
	 * The CommandListener that this command belongs to.
	 */
	@Getter private final CommandListener listener;

	protected InternalCommand(CommandListener listener) {
		super(listener.getName(), listener.getDescription(), listener.getUsageMessage(), Arrays.asList(listener.getAliases()));
		this.listener = listener;
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] arguments) {
		if (arguments.length > 0) {
			for (CommandListener sublistener : listener.getSublisteners())
				if (sublistener.hasName(arguments[0]))
					return new InternalCommand(sublistener).execute(sender, label, Arrays.copyOfRange(arguments, 1, arguments.length));
		}

		if (listener.getPermission() != null && !sender.hasPermission(listener.getPermission())) {
			listener.onPermissionDenied(sender);
			return false;
		}
		for (CommandMethod method : listener.getCommandMethods()) {
			if (!method.canSendCommand(sender)) continue;
			return method.execute(sender, arguments);
		}
		return false;
	}

}

package net.advancius.command;

import net.advancius.command.flag.CommandFlagList;
import net.advancius.command.flag.CommandFlagParser;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommandMethod {

	/**
	 * The internal java method that this CommandMethod should call
	 */
	private Method method;
	
	/**
	 * The CommandListener that this CommandMethod belongs to.
	 */
	private CommandListener listener;
	
	/**
	 * The valid CommandSender class that can call this CommandMethod
	 */
	private Class<? extends CommandSender> senderType;
	
	public CommandMethod(Method method, CommandListener listener, Class<? extends CommandSender> senderType) {
		this.method = method;
		this.listener = listener;
		this.senderType = senderType;
	}
	
	/**
	 * Checks if the provided CommandSender instance matches the parameter type of the command method.
	 * @param sender The sender to verify.
	 * @return Whether the CommandSender instance is an instance of sender type
	 */
	public boolean canSendCommand(CommandSender sender) {
		return senderType.isInstance(sender);
	}
	
	/**
	 * Internally executes the method that underlies the command.
	 * @param sender The CommandSender who executed the command.
	 * @param arguments The arguments the command sender provided.
	 * @return Whether the command was executed successfully or not.
	 */
	public boolean execute(CommandSender sender, String[] arguments) {
		if (!canSendCommand(sender)) return false;
		try {
			if (method.getParameterTypes()[1] == CommandFlagList.class) {
				method.invoke(listener, sender, CommandFlagParser.getCommandFlags(String.join(" ", arguments)));
			}
			else method.invoke(listener, sender, arguments);
			return true;
		} catch (Throwable e) {
			if (e instanceof InvocationTargetException) e = ((InvocationTargetException) e).getTargetException();
			listener.onCommandException(sender, arguments, e);
			if (CommandManager.PRINT_COMMAND_EXCEPTIONS) e.printStackTrace();
			return false;
		}
	}
}

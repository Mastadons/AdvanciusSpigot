package net.advancius.command;

import net.advancius.command.flag.CommandFlagList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This interface should be extended when you want to make a custom command.<br>
 * See the abstract class {@link net.advancius.command.BasicCommandListener}

 * @author Mastadons
 */
public interface CommandListener {

	/**
	 * The name of the command, what is used to reference to the command in-game.
	 * @return The name of the command
	 */
	public String getName();
	
	/**
	 * The permission for the command, is needed to access the command in-game.
	 * @return The permission for the command
	 */
	public String getPermission();
	
	/**
	 * Secondary commands that the command can be referenced by in-game.
	 * @return The aliases for the command
	 */
	public String[] getAliases();
	
	/**
	 * The description of the command, can be used by Bukkit to provide information for the command in-game
	 * @return The description of the command
	 */
	public String getDescription();
	
	/**
	 * The usage message of the command, can be used by Bukkit to provide information for the command in-game
	 * @return The usage message of the command
	 */
	public String getUsageMessage();
	
	/**
	 * This method will be called when a sender calls a command, and an exception is thrown.
	 * @param sender The CommandSender instance which attempted to call the command
	 * @param arguments The arguments that the command sender provided
	 * @param exception The exception that occured when processing the commmand
	 */
	public default void onCommandException(CommandSender sender, String[] arguments, Throwable exception) {
		sender.sendMessage(ChatColor.RED + "Encountered uncaught " + exception.getClass().getSimpleName() + ": " + exception.getMessage());
	}
	
	/**
	 * This method will be called when a sender calls a command, but the CommandListener does not have any
	 * method that is designated to handle that CommandSender.
	 * @param sender The CommandSender instance which attempted to call the command
	 */
	public default void onUncaughtCommand(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "You cannot perform this command thtrough this interface.");
	}
	
	/**
	 * This method will be called when a sender calls a command, but does not have permission
	 * @param sender The CommandSender instance which attempted to call the command
	 */
	public default void onPermissionDenied(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "You cannot perform this command!");
	}
	
	/**
	 * Gets all of the CommandListeners that act as subcommands of this command
	 * Subcommands can be called in-game through /<command> <subcommand> <subcommand....>
	 * @return a list of CommandListener
	 */
	public List<CommandListener> getSublisteners();
	
	/**
	 * Calculates all of the valid methods annotated with CommandHandler inside of this CommandListener instance
	 * @return a list of CommandMethod
	 */
	@SuppressWarnings("unchecked")
	public default List<CommandMethod> getCommandMethods() {
		List<CommandMethod> methods = new ArrayList<>();
		for (Method method : this.getClass().getMethods()) {
			if (!method.isAnnotationPresent(CommandHandler.class)) continue;
			if (method.getParameterCount() != 2) continue;
			if (!isCommandSenderClass(method.getParameterTypes()[0])) continue;
			if (!isArgumentClass(method.getParameterTypes()[1])) continue;
			methods.add(new CommandMethod(method, this, (Class<? extends CommandSender>) method.getParameterTypes()[0]));
		}
		return methods;
	}

	/**
	 * Whether the provided class is String array or {@link CommandFlagList}
	 * These two types are the only valid types that can be used as the second parameter in a CommandHandler method.
	 * @param clazz The class to check
	 * @return Whether the class is valid for the condition
	 */
	public static boolean isArgumentClass(Class<?> clazz) {
		return clazz.equals(String[].class) || clazz.equals(CommandFlagList.class);
	}

	/**
	 * Whether the provided class is an instance of {@link CommandSender}
	 * @param clazz The class to check
	 * @return Whether the class can be casted to a CommandSender or not
	 */
	public static boolean isCommandSenderClass(Class<?> clazz) {
		try { 
			@SuppressWarnings({ "unused", "unchecked" })
			Class<? extends CommandSender> commandSenderClass = (Class<? extends CommandSender>) clazz;
			return true;
		} catch (ClassCastException e) { return false; }
	}
	
	/**
	 * Check if this command listener's name or one of its aliases is equal to the provided name
	 * @param name The name to look for
	 * @return Whether the command listener can be referenced by that name
	 */
	public default boolean hasName(String name) {
		if (getName().equalsIgnoreCase(name)) return true;
		for (String alias : getAliases()) 
			if (alias.equalsIgnoreCase(name)) return true;
		return false;
	}
	
	/**
	 * Annotation used to annotate what methods in the listener should be made into command endpoints.
	 * @author Mastadons
	 */
	@Retention(RUNTIME)
	@Target(METHOD)
	@interface CommandHandler {};
}

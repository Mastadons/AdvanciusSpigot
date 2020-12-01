package net.advancius.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main singleton class for registering and handling custom commands.
 * @author Mastadons
 */
public class CommandManager {

	/**
	 * Whether the stacktrace of exceptions encountered while executing a command should be printed to the console
	 */
	public static boolean PRINT_COMMAND_EXCEPTIONS = false;
	
	private static Map<Plugin, List<InternalCommand>> commands = new HashMap<>();
	
	/**  
	 * Will register a command so that it can be used on the server.
	 * @param plugin The plugin that the command should be registered under
	 * @param listener The listener of the command to register
	 */
	public static void registerCommand(Plugin plugin, CommandListener listener) {
		if (getInternalCommand(listener) != null) return;
		
		CommandMap commandMap = getCommandMap();
		InternalCommand internal = new InternalCommand(listener);
		commandMap.register(listener.getName(), plugin.getName(), internal);
		for (String alias : listener.getAliases())
			commandMap.register(alias, plugin.getName(), internal);
		
		List<InternalCommand> pluginCommandList = commands.get(plugin);
		if (pluginCommandList == null) {
			pluginCommandList = new ArrayList<>();
			commands.put(plugin, pluginCommandList);
		}
		pluginCommandList.add(internal);
	}
	
	/**
	 * Grab the internal command for the specified listener, used to synthetically call a Bukkit command.
	 * @param listener The listener to get the internal command of
	 * @return The internal command for the specified listener, or null if not found.
	 */
	public static InternalCommand getInternalCommand(CommandListener listener) {
		for (List<InternalCommand> pluginCommandList : commands.values()) {
			for (InternalCommand pluginCommand : pluginCommandList) {
				if (pluginCommand.getListener().equals(listener)) return pluginCommand;
			}
		}
		return null;
	}
	
	/**
	 * Internal util method to find the CommandMap of the server, so that the CommandManager can register commands.
	 * @return The Bukkit server's CommandMap instance
	 * @throws IllegalStateException The CommandMap could not be found, something on the server is foreign.
	 */
	private static CommandMap getCommandMap() throws IllegalStateException {
		try {	
			Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			commandMapField.setAccessible(true);
			CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
			commandMapField.setAccessible(false);
			return commandMap;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}
}

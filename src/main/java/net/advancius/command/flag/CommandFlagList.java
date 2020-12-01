package net.advancius.command.flag;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CommandFlagList {

	private Set<CommandFlag> commandFlags = new HashSet<>();
	
	public CommandFlag getFlag(String name) {
		for (CommandFlag commandFlag : commandFlags) {
			if (commandFlag.getName().equalsIgnoreCase(name)) return commandFlag;
		}
		return null;
	}
	
	public void add(String name, String data) {
		try {
			commandFlags.add(new CommandFlag(name, data));
		} catch (Exception e) {
		}
	}

	public Collection<CommandFlag> getFlags() {
		return commandFlags;
	}

	public boolean hasFlag(String name) {
		return getFlag(name) != null;
	}

	public boolean hasFlags(String... names) {
		for (String name : names) if (!hasFlag(name)) return false;
		return true;
	}
}

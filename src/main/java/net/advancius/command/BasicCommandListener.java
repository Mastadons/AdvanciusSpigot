package net.advancius.command;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class BasicCommandListener implements CommandListener {

	@Getter private final String name;
	@Getter private final String permission;
	@Getter private final String description;
	@Getter private final String usageMessage;
	@Getter private final String[] aliases;
	@Getter private final List<CommandListener> sublisteners = new ArrayList<>();

	public BasicCommandListener(String name, String permission, String description, String usageMessage, String... aliases) {
		this.name = name;
		this.permission = permission;
		this.description = description;
		this.usageMessage = usageMessage;
		this.aliases = aliases;
	}
	
	public BasicCommandListener(String name, String permission, String... aliases) {
		this(name, permission, null, null, aliases);
	}

	public void addSubCommandListener(CommandListener listener) {
		if (listener.equals(this)) return;
		if (listener.getSublisteners().contains(this)) return;
		sublisteners.add(listener);
	}

}

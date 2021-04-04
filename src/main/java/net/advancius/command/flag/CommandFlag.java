package net.advancius.command.flag;

import lombok.Data;

@Data
public class CommandFlag {
	
	private String name;
	private String data;
	
	public CommandFlag(String name, String data) throws Exception {
		if (name.contains("=") || data.contains("=")) throw new Exception("Command Flag Name or Data cannot contain Equal Sign.");

		this.name = name;
		this.data = data.replace("\"", "");
	}

	@Override
	public String toString() {
		return "CommandFlag [name=" + name + ", data=" + data + "]";
	}
	
	public double asDouble() {
		return Double.valueOf(data);
	}
	
	public float asFloat() {
		return Float.valueOf(data);
	}
	
	public int asInteger() {
		return Integer.valueOf(data);
	}
}
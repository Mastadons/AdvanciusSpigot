package net.advancius.command.flag;

public class CommandFlagParser {

	public static CommandFlagList getCommandFlags(String command) {
		CommandFlagList commandFlagList = new CommandFlagList();
		char[] characters = command.toCharArray();
		for (int i=0; i<characters.length; i++) {
			if (characters[i] == '=') {
				commandFlagList.add(getFlagName(command, i), getFlagData(command, i));
			}
		}
		return commandFlagList;
	}
	
	private static String getFlagName(String command, int equalSign) {
		boolean string = false;
		char[] characters = command.toCharArray();
		int start = 0;
		for (int i=equalSign; i>=0; i--) {
			if (characters[i] == '"') {
				string = !string;
				continue;
			}
			if (string) continue;
		
			if (characters[i] == ' ') {
				start = i;
				break;
			}
		}
		return command.substring(start, equalSign).trim();
	}
	
	private static String getFlagData(String command, int equalSign) {
		boolean string = false;
		char[] characters = command.toCharArray();
		int end = command.length();
		for (int i=equalSign; i<characters.length; i++) {
			if (characters[i] == '"') {
				string = !string;
				continue;
			}
			if (string) continue;
		
			if (characters[i] == ' ') {
				end = i;
				break;
			}
		}
		return command.substring(equalSign+1, end);
	}
}

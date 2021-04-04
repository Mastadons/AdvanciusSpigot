package net.advancius.command.flag;

public class CommandFlagParser {

	public static CommandFlagList getCommandFlags(String command) {
		command = command.replace("\\=", "<EQUALSIGN>");
		command = command.replace("\\\"", "<QUOTESIGN>");

		CommandFlagList commandFlagList = new CommandFlagList();
		char[] characters = command.toCharArray();
		for (int i=0; i<characters.length; i++) {
			if (characters[i] == '=') {
				commandFlagList.add(getFlagName(command, i), getFlagData(command, i));
			}
		}
		commandFlagList.getFlags().forEach(commandFlag -> {
			commandFlag.setData(commandFlag.getData().replace("<EQUALSIGN>", "="));
			commandFlag.setData(commandFlag.getData().replace("<QUOTESIGN>", "\""));
		});
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

		String content = command.substring(start, equalSign).trim();
		if (content.startsWith("\"") && content.endsWith("\"")) {
			if (content.length() > 2) content = content.substring(1, content.length()-1);
			else content = "";
		}
		return content;
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

		String content = command.substring(equalSign + 1, end);
		if (content.startsWith("\"") && content.endsWith("\"")) {
			if (content.length() > 2) content = content.substring(1, content.length()-1);
			else content = "";
		}
		return content;
	}
}

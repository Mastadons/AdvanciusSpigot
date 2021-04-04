import net.advancius.command.flag.CommandFlag;
import net.advancius.command.flag.CommandFlagList;
import net.advancius.command.flag.CommandFlagParser;

import java.util.Base64;
import java.util.UUID;

public class AdvanciusTest {

    public static void main(String[] arguments) {
        String command = "/csc server=Bungee command=\"discordmessage webhook\\=internal content\\=test\"";

        System.out.println(command);
        CommandFlagList commandFlags = CommandFlagParser.getCommandFlags(command);

        for (CommandFlag flag : commandFlags.getFlags()) {
            System.out.println(flag);
        }
    }
}

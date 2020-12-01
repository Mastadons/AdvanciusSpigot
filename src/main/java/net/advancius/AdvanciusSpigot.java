package net.advancius;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import net.advancius.communication.CommunicationManager;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvanciusSpigot extends JavaPlugin {

    public static Gson GSON = new Gson();

    @Getter @Setter private CommunicationManager communicationManager;

    @Getter private static AdvanciusSpigot instance;

    @Override
    public void onEnable() {
        super.onEnable();
        AdvanciusSpigot.instance = this;

        FlagManager.runFlaggedMethods("net.advancius", DefinedFlag.PLUGIN_LOAD);
    }

    @Override
    public void onDisable() {
        FlagManager.runFlaggedMethods("net.advancius", DefinedFlag.PLUGIN_SAVE);

        super.onDisable();
        AdvanciusSpigot.instance = null;
    }
}

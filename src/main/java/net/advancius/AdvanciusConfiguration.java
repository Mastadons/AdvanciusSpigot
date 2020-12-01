package net.advancius;

import lombok.Data;
import lombok.Getter;
import net.advancius.file.FileManager;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

@FlagManager.FlaggedClass
@Data
public class AdvanciusConfiguration {

    @Getter private static AdvanciusConfiguration instance;

    @Getter private static Yaml configurationYaml;
    @Getter private static File configurationFile;

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 0)
    public static void load() throws FileNotFoundException {
        AdvanciusLogger.info("Loading configuration...");

        configurationYaml = new Yaml(new CustomClassLoaderConstructor(AdvanciusConfiguration.class.getClassLoader()));
        configurationFile = FileManager.getServerFile("configuration.yml", "configuration.yml");
        FileReader configurationReader = new FileReader(configurationFile);

        instance = configurationYaml.loadAs(configurationReader, AdvanciusConfiguration.class);
        AdvanciusLogger.info("Loaded configuration!");
        FlagManager.runFlaggedMethods("net.advancius", DefinedFlag.POST_CONFIGURATION_LOAD);
    }

    private boolean debugExceptions;
    private List<String> serverProcessing;

    public boolean defaultFormatWorkaround;
}

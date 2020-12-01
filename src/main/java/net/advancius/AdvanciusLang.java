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

@FlagManager.FlaggedClass
@Data
public class AdvanciusLang {

    @Getter private static AdvanciusLang instance;

    @Getter private static Yaml configurationYaml;
    @Getter private static File configurationFile;

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = -5)
    public static void load() throws FileNotFoundException {
        AdvanciusLogger.info("Loading language...");

        configurationYaml = new Yaml(new CustomClassLoaderConstructor(AdvanciusLang.class.getClassLoader()));
        configurationFile = FileManager.getServerFile("language.yml", "language.yml");
        FileReader configurationReader = new FileReader(configurationFile);

        instance = configurationYaml.loadAs(configurationReader, AdvanciusLang.class);
        AdvanciusLogger.info("Loaded language!");
    }
}

package net.advancius.communication;

import lombok.Data;
import lombok.Getter;
import net.advancius.AdvanciusLogger;
import net.advancius.file.FileManager;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.introspector.BeanAccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

@FlagManager.FlaggedClass
@Data
public class CommunicationConfiguration {

    @Getter private static CommunicationConfiguration instance;

    @Getter private static Yaml configurationYaml;
    @Getter private static File configurationFile;

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 0)
    public static void load() throws FileNotFoundException {
        AdvanciusLogger.info("Loading communication configuration...");

        configurationYaml = new Yaml(new CustomClassLoaderConstructor(CommunicationConfiguration.class.getClassLoader()));
        configurationYaml.setBeanAccess(BeanAccess.FIELD);
        configurationFile = FileManager.getServerFile("communication.yml", "communication.yml");
        FileReader configurationReader = new FileReader(configurationFile);

        instance = configurationYaml.loadAs(configurationReader, CommunicationConfiguration.class);
        AdvanciusLogger.info("Loaded communication configuration!");
    }

    public String authenticationToken;
    public String identifier;

    public String packetAddress;
    public String socketAddress;

    public boolean redirectChat;
}

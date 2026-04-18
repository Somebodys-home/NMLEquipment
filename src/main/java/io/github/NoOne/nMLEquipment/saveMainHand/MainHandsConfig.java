package io.github.NoOne.nMLEquipment.saveMainHand;

import io.github.NoOne.nMLEquipment.NMLEquipment;
import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public class MainHandsConfig {
    private NMLEquipment nmlEquipment;
    private File file;
    private String fileName;
    private FileConfiguration config = new YamlConfiguration();

    public MainHandsConfig(NMLEquipment nmlEquipment, String filename) {
        this.nmlEquipment = nmlEquipment;
        this.fileName = filename;
        file = new File(nmlEquipment.getDataFolder(), filename + ".yml");
    }

    public void loadConfig() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            nmlEquipment.saveResource(fileName + ".yml", false);
        } try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }
}

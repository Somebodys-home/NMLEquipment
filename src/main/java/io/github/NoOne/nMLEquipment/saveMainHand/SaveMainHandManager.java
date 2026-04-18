package io.github.NoOne.nMLEquipment.saveMainHand;

import io.github.NoOne.nMLEquipment.NMLEquipment;
import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SaveMainHandManager {
    private HashMap<UUID, ItemStack> mainHands = new HashMap<>();
    private FileConfiguration config;

    public SaveMainHandManager(NMLEquipment nmlEquipment) {
        config = nmlEquipment.getMainHandsConfig().getConfig();
    }

    public void addPlayer(Player player) {
        mainHands.put(player.getUniqueId(), player.getInventory().getItemInMainHand());
    }

    public void loadMainHandsFromConfig() {
        for (String id : config.getConfigurationSection("").getKeys(false)) {
            mainHands.put(UUID.fromString(id), config.getItemStack(id));
        }
    }

    public void saveMainHandsToConfig() {
        for (UUID uuid : mainHands.keySet()) {
            config.set(uuid.toString(), mainHands.get(uuid));
        }
    }

    public HashMap<UUID, ItemStack> getMainHands() {
        return mainHands;
    }
}
package io.github.NoOne.nMLEquipment;

import com.comphenix.protocol.ProtocolLibrary;
import io.github.NoOne.menuSystem.MenuListener;
import io.github.NoOne.nMLEquipment.events.PlayerDropItemSlotHandler;
import io.github.NoOne.nMLEquipment.listeners.EquipmentMenuListener;
import io.github.NoOne.nMLEquipment.listeners.ItemStatListener;
import io.github.NoOne.nMLEquipment.saveMainHand.MainHandsConfig;
import io.github.NoOne.nMLEquipment.saveMainHand.SaveMainHandListener;
import io.github.NoOne.nMLEquipment.saveMainHand.SaveMainHandManager;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.NMLItems;
import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLSkills.NMLSkills;
import io.github.NoOne.nMLSkills.skillSetSystem.SkillSetManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class NMLEquipment extends JavaPlugin {
    private ProfileManager profileManager;
    private SkillSetManager skillSetManager;
    private EquipmentTracker equipmentTracker;
    private MainHandsConfig mainHandsConfig;
    private SaveMainHandManager saveMainHandManager;
    private ItemSystem itemSystem;

    @Override
    public void onEnable() {
        profileManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getProfileManager();
        skillSetManager = JavaPlugin.getPlugin(NMLSkills.class).getSkillSetManager();
        itemSystem = JavaPlugin.getPlugin(NMLItems.class).getItemSystem(); // you good dawg??

        mainHandsConfig = new MainHandsConfig(this, "mainHands");
        mainHandsConfig.loadConfig();

        saveMainHandManager = new SaveMainHandManager(this);
        saveMainHandManager.loadMainHandsFromConfig();

        equipmentTracker = new EquipmentTracker(this);
        equipmentTracker.setPreviousHeldItems(saveMainHandManager.getMainHands());
        new BukkitRunnable() {
            @Override
            public void run() {
                equipmentTracker.startTracker();
            }
        }.runTaskLater(this, 1L);

        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getCommand("equipment").setExecutor(new EquipmentCommand(this));
        getServer().getPluginManager().registerEvents(new EquipmentMenuListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemStatListener(this), this);
        getServer().getPluginManager().registerEvents(new SaveMainHandListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItemSlotHandler(ProtocolLibrary.getProtocolManager(), this), this);
    }

    @Override
    public void onDisable() {
        equipmentTracker.stopTracker();
        saveMainHandManager.saveMainHandsToConfig();
        mainHandsConfig.saveConfig();
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public SkillSetManager getSkillSetManager() {
        return skillSetManager;
    }

    public ItemSystem getItemSystem() {
        return itemSystem;
    }

    public SaveMainHandManager getSaveMainHandManager() {
        return saveMainHandManager;
    }

    public MainHandsConfig getMainHandsConfig() {
        return mainHandsConfig;
    }
}

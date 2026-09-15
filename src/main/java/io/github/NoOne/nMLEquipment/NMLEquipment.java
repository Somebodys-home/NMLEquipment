package io.github.NoOne.nMLEquipment;

import io.github.NoOne.menuSystem.MenuListener;
import io.github.NoOne.nMLEquipment.listeners.EquipmentListener;
import io.github.NoOne.nMLEquipment.saveMainHand.MainHandsConfig;
import io.github.NoOne.nMLEquipment.saveMainHand.SaveMainHandListener;
import io.github.NoOne.nMLEquipment.saveMainHand.SaveMainHandManager;
import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class NMLEquipment extends JavaPlugin {
    private ProfileManager profileManager;
    private EquipmentTracker equipmentTracker;
    private MainHandsConfig mainHandsConfig;
    private SaveMainHandManager saveMainHandManager;

    @Override
    public void onEnable() {
        profileManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getProfileManager();

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

        getCommand("equipment").setExecutor(new EquipmentCommand(this));
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new EquipmentListener(this), this);
        getServer().getPluginManager().registerEvents(new SaveMainHandListener(this), this);
    }

    @Override
    public void onDisable() {
        equipmentTracker.stopTracker();
        saveMainHandManager.saveMainHandsToConfig();
        mainHandsConfig.saveConfig();
    }

    // used in listener and menu
    public static void sendUnusableItemWarning(Player player) {
        player.sendMessage("§c⚠ You are too inexperienced for this item!§r§c ⚠");
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public SaveMainHandManager getSaveMainHandManager() {
        return saveMainHandManager;
    }

    public MainHandsConfig getMainHandsConfig() {
        return mainHandsConfig;
    }
}

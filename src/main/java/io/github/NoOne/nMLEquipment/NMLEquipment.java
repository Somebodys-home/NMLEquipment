package io.github.NoOne.nMLEquipment;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.NoOne.menuSystem.MenuListener;
import io.github.NoOne.nMLEquipment.events.PlayerDropItemSlotHandler;
import io.github.NoOne.nMLEquipment.listeners.EquipmentMenuListener;
import io.github.NoOne.nMLEquipment.listeners.ItemStatListener;
import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLSkills.NMLSkills;
import io.github.NoOne.nMLSkills.skillSetSystem.SkillSetManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class NMLEquipment extends JavaPlugin {
    private EquipmentTracker equipmentTracker;
    private ProfileManager profileManager;
    private SkillSetManager skillSetManager;

    @Override
    public void onEnable() {
        profileManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getProfileManager();
        skillSetManager = JavaPlugin.getPlugin(NMLSkills.class).getSkillSetManager();

        equipmentTracker = new EquipmentTracker(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                equipmentTracker.startTracker();
            }
        }.runTaskLater(this, 1L);

        getCommand("equipment").setExecutor(new EquipmentCommand());
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new EquipmentMenuListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemStatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItemSlotHandler(ProtocolLibrary.getProtocolManager(), this), this);
    }

    @Override
    public void onDisable() {
        equipmentTracker.stopTracker();
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public SkillSetManager getSkillSetManager() {
        return skillSetManager;
    }
}

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

public final class NMLEquipment extends JavaPlugin {
    private static NMLEquipment instance;
    private ProtocolManager protocolManager;
    private ProfileManager profileManager;
    private SkillSetManager skillSetManager;

    @Override
    public void onEnable() {
        instance = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        profileManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getProfileManager();
        skillSetManager = JavaPlugin.getPlugin(NMLSkills.class).getSkillSetManager();

        getCommand("equipment").setExecutor(new EquipmentCommand());
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new EquipmentMenuListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemStatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItemSlotHandler(protocolManager), this);
    }

    public static NMLEquipment getInstance() {
        return instance;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public SkillSetManager getSkillSetManager() {
        return skillSetManager;
    }
}

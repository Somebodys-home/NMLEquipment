package io.github.NoOne.nMLEquipment;

import io.github.NoOne.menuSystem.MenuListener;
import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLEquipment extends JavaPlugin {
    private ProfileManager profileManager;

    @Override
    public void onEnable() {
        profileManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getProfileManager();

        getCommand("equipment").setExecutor(new EquipmentCommand());
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new EquipmentMenuListener(this), this);
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }
}

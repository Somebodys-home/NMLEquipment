package io.github.NoOne.nMLEquipment;

import io.github.NoOne.menuSystem.MenuListener;
import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLEquipment extends JavaPlugin {
    private NMLPlayerStats nmlPlayerStats;

    @Override
    public void onEnable() {
        nmlPlayerStats = JavaPlugin.getPlugin(NMLPlayerStats.class);

        getCommand("equipment").setExecutor(new EquipmentCommand());
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new EquipmentMenuListener(this), this);
    }

    public NMLPlayerStats getNmlPlayerStats() {
        return nmlPlayerStats;
    }
}

package io.github.NoOne.nMLEquipment;

import io.github.NoOne.menuSystem.MenuListener;
import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLEquipment extends JavaPlugin {
    private NMLEquipment instance;
    private NMLPlayerStats nmlPlayerStats;

    @Override
    public void onEnable() {
        instance = this;
        nmlPlayerStats = JavaPlugin.getPlugin(NMLPlayerStats.class);

        getCommand("equipment").setExecutor(new EquipmentCommand(this));
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new EquipmentMenuListener(this), this);
    }

    public NMLEquipment getInstance() {
        return instance;
    }

    public NMLPlayerStats getNmlPlayerStats() {
        return nmlPlayerStats;
    }
}

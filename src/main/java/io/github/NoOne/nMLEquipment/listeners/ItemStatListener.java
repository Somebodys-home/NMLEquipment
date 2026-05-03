package io.github.NoOne.nMLEquipment.listeners;

import io.github.NoOne.nMLEquipment.NMLEquipment;
import io.github.NoOne.nMLEquipment.events.EquipmentChangeEvent;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class ItemStatListener implements Listener {
    private NMLEquipment nmlEquipment;
    private ItemSystem itemSystem;

    public ItemStatListener(NMLEquipment nmlEquipment) {
        this.nmlEquipment = nmlEquipment;
        itemSystem = nmlEquipment.getItemSystem();
    }

    @EventHandler
    public void onEquipmentChange(EquipmentChangeEvent event) {
        HashMap<String, Double> doffedArmorStats = itemSystem.convertItemStatsToPlayerStats(event.getDoffedEquipment());
        HashMap<String, Double> donnedArmorStats = itemSystem.convertItemStatsToPlayerStats(event.getDonnedEquipment());
        Stats stats = nmlEquipment.getProfileManager().getPlayerProfile(event.getPlayer().getUniqueId()).getStats();

        for (Map.Entry<String, Double> entry : doffedArmorStats.entrySet()) {
            stats.removeFromStat(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Double> entry : donnedArmorStats.entrySet()) {
            stats.add2Stat(entry.getKey(), entry.getValue());
        }
    }
}

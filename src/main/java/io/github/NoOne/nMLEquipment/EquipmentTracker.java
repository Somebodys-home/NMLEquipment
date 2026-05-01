package io.github.NoOne.nMLEquipment;

import io.github.NoOne.nMLAbilities.abilitySystem.AbilityItemManager;
import io.github.NoOne.nMLEquipment.events.EquipmentChangeEvent;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.enums.ItemType;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EquipmentTracker {
    private NMLEquipment nmlEquipment;
    private ProfileManager profileManager;
    private ItemSystem itemSystem;
    private BukkitTask tracker;
    private HashMap<UUID, ItemStack> previousHeldItems = new HashMap<>();

    public EquipmentTracker(NMLEquipment nmlEquipment) {
        this.nmlEquipment = nmlEquipment;
        profileManager = nmlEquipment.getProfileManager();
        itemSystem = nmlEquipment.getItemSystem();
    }

    public void startTracker() {
        tracker = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    ItemStack heldItem = player.getInventory().getItemInMainHand();
                    ItemStack previousHeldItem = previousHeldItems.get(uuid);

                    if (!areEquals(heldItem, previousHeldItem) && !AbilityItemManager.isAnAbility(heldItem)) {
                        Stats stats = profileManager.getPlayerProfile(uuid).getStats();

                        if ((itemSystem.isItemType(heldItem, ItemType.SHIELD) || itemSystem.isWeapon(heldItem))) {
                            for (Map.Entry<String, Double> statEntry : itemSystem.convertItemStatsToPlayerStats(heldItem).entrySet()) {
                                stats.add2Stat(statEntry.getKey(), statEntry.getValue());
                            }                        }
                        if ((itemSystem.isItemType(previousHeldItem, ItemType.SHIELD) || itemSystem.isWeapon(previousHeldItem))) {
                            for (Map.Entry<String, Double> statEntry : itemSystem.convertItemStatsToPlayerStats(previousHeldItem).entrySet()) {
                                stats.removeFromStat(statEntry.getKey(), statEntry.getValue());
                            }
                        }
                    }

                    previousHeldItems.put(uuid, heldItem.clone());
                }
            }
        }.runTaskTimer(nmlEquipment, 0, 1);
    }

    public void stopTracker() {
        tracker.cancel();
    }

    private boolean areEquals(ItemStack a, ItemStack b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;

        return a.isSimilar(b);
    }

    public void setPreviousHeldItems(HashMap<UUID, ItemStack> previousHeldItems) {
        this.previousHeldItems = previousHeldItems;
    }
}

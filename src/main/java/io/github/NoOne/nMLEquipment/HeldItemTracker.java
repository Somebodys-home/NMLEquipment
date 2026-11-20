package io.github.NoOne.nMLEquipment;

import io.github.NoOne.nMLAbilities.abilitySystem.AbilityItemManager;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.enums.ItemType;
import io.github.NoOne.nMLPlayerStats.StatConfigManager;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class HeldItemTracker {
    private NMLEquipment nmlEquipment;
    private StatConfigManager statConfigManager;
    private HashMap<UUID, ItemStack> previousHeldItems = new HashMap<>();

    public HeldItemTracker(NMLEquipment nmlEquipment) {
        this.nmlEquipment = nmlEquipment;
        statConfigManager = nmlEquipment.getStatConfigManager();
    }

    public void startTracker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    ItemStack heldItem = player.getInventory().getItemInMainHand();
                    ItemStack previousHeldItem = previousHeldItems.get(uuid);

                    if (!areEquals(heldItem, previousHeldItem) && !AbilityItemManager.isAnAbility(heldItem)) {
                        Stats stats = statConfigManager.getStats(player.getUniqueId());
                        ItemStack offhand = player.getInventory().getItemInOffHand();

                        // add the stats of the weapon or shield were holding in main hand
                        if ((ItemSystem.isItemType(heldItem, ItemType.SHIELD) || ItemSystem.isWeapon(heldItem))) {
                            stats.addToStats(ItemSystem.convertItemStatsToPlayerStats(heldItem));

                            // if were adding the stats of a bow, also add the stats of the quiver too
                            if (ItemSystem.isItemType(heldItem, ItemType.BOW) && ItemSystem.isItemType(offhand, ItemType.QUIVER)) {
                                stats.addToStats(ItemSystem.convertItemStatsToPlayerStats(offhand));
                            }
                        }

                        // remove the stats of the weapon or shield we were holding in main hand
                        if ((ItemSystem.isItemType(previousHeldItem, ItemType.SHIELD) || ItemSystem.isWeapon(previousHeldItem))) {
                            stats.removeFromStats(ItemSystem.convertItemStatsToPlayerStats(previousHeldItem));

                            // if were removing the stats of a bow, also remove the stats of the quiver too
                            if (ItemSystem.isItemType(previousHeldItem, ItemType.BOW) && ItemSystem.isItemType(offhand, ItemType.QUIVER)) {
                                stats.removeFromStats(ItemSystem.convertItemStatsToPlayerStats(offhand));
                            }
                        }
                    }

                    previousHeldItems.put(uuid, heldItem.clone());
                }
            }
        }.runTaskTimer(nmlEquipment, 0, 1);
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

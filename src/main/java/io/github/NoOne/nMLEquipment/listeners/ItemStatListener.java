package io.github.NoOne.nMLEquipment.listeners;

import io.github.NoOne.expertiseStylePlugin.abilitySystem.AbilityItemManager;
import io.github.NoOne.nMLEquipment.NMLEquipment;
import io.github.NoOne.nMLEquipment.events.PlayerDropItemSlotEvent;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.ItemType;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Objects;

public class ItemStatListener implements Listener {
    private NMLEquipment nmlEquipment;
    private ProfileManager profileManager;

    public ItemStatListener(NMLEquipment nmlEquipment) {
        this.nmlEquipment = nmlEquipment;
        profileManager = nmlEquipment.getProfileManager();
    }

    @EventHandler
    public void updateItemStatsOnHold(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());

        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (!AbilityItemManager.isAnAbility(newItem)) {
            if (ItemSystem.isItemType(newItem, ItemType.SHIELD) || ItemSystem.isWeapon(newItem)) {
                addItemStatsToPlayer(player, newItem);
            }
            if (ItemSystem.isItemType(oldItem, ItemType.SHIELD) || ItemSystem.isWeapon(newItem)) {
                removeItemStatsFromPlayer(player, oldItem);
            }
        }
    }

    @EventHandler
    public void updateItemStatsOnInventoryMove(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerInventory playerInventory = player.getInventory();
        int clickedSlot = event.getSlot();
        int heldItemSlot = playerInventory.getHeldItemSlot();
        ItemStack triggeringItem = event.getCurrentItem();
        ItemStack previouslyHeldItem = playerInventory.getItem(heldItemSlot);

        switch (event.getClick()) {
            case SHIFT_LEFT, SHIFT_RIGHT -> {
                if (player.getGameMode() == GameMode.CREATIVE) return;
                if (clickedSlot == heldItemSlot) { // shift clicking item out of hand
                    if (ItemSystem.isItemType(triggeringItem, ItemType.SHIELD) || ItemSystem.isWeapon(triggeringItem)) {
                        removeItemStatsFromPlayer(player, triggeringItem);
                        return;
                    }
                }

                if (ItemSystem.isItemType(triggeringItem, ItemType.SHIELD) || ItemSystem.isWeapon(triggeringItem)) { // maybe shift clicking item into hand
                    ItemStack triggeringItemClone = triggeringItem.clone();

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ItemStack newMainHand = player.getInventory().getItem(heldItemSlot);

                            if (triggeringItemClone.isSimilar(newMainHand)) {
                                addItemStatsToPlayer(player, triggeringItemClone);
                            }
                        }
                    }.runTask(nmlEquipment);
                }
            }
            case NUMBER_KEY -> {
                if (player.getGameMode() == GameMode.CREATIVE) return;

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ItemStack newMainHand = playerInventory.getItem(heldItemSlot);

                        if (ItemSystem.isItemType(previouslyHeldItem, ItemType.SHIELD) || ItemSystem.isWeapon(previouslyHeldItem)) {
                            if (!Objects.requireNonNull(previouslyHeldItem).isSimilar(newMainHand)) {
                                removeItemStatsFromPlayer(player, previouslyHeldItem);
                            }
                        }


                        if (ItemSystem.isItemType(newMainHand, ItemType.SHIELD) || ItemSystem.isWeapon(triggeringItem)) {
                            if (!Objects.requireNonNull(previouslyHeldItem).isSimilar(newMainHand)) {
                                addItemStatsToPlayer(player, newMainHand);
                            }
                        }
                    }
                }.runTask(nmlEquipment);
            }
            // moving items manually normally
            default -> {
                if (player.getGameMode() == GameMode.CREATIVE) return;
                if (clickedSlot == heldItemSlot) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ItemStack cursorItem = event.getCursor();
                            ItemStack newMainHandItem = player.getInventory().getItemInMainHand();

                            if (ItemSystem.isItemType(newMainHandItem, ItemType.SHIELD) || ItemSystem.isWeapon(newMainHandItem)) {
                                addItemStatsToPlayer(player, newMainHandItem);
                            }
                            if (ItemSystem.isItemType(cursorItem, ItemType.SHIELD) || ItemSystem.isWeapon(cursorItem)) {
                                removeItemStatsFromPlayer(player, cursorItem);
                            }
                        }
                    }.runTask(nmlEquipment);
                }
            }
        }
    }

    @EventHandler
    public void updateItemStatsWhenDropped(PlayerDropItemSlotEvent event) {
        Player player = event.getPlayer();
        ItemStack droppedItem = event.getItemStack();

        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (ItemSystem.isItemType(droppedItem, ItemType.SHIELD) || ItemSystem.isWeapon(droppedItem)) {
            if (event.getSlot() == player.getInventory().getHeldItemSlot()) {
                removeItemStatsFromPlayer(player, droppedItem);
            }
        }
    }

    @EventHandler
    public void updateItemStatsOnPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;

        ItemStack pickedUpItem = event.getItem().getItemStack();
        PlayerInventory playerInventory = player.getInventory();
        ItemStack oldHand = playerInventory.getItemInMainHand();

        if (locateItemInInventory(playerInventory, pickedUpItem) == playerInventory.getHeldItemSlot()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    ItemStack newHand = playerInventory.getItemInMainHand();

                    if (ItemSystem.isItemType(newHand, ItemType.SHIELD) || ItemSystem.isWeapon(newHand)) {
                        addItemStatsToPlayer(player, newHand);
                    }
                    if (ItemSystem.isItemType(oldHand, ItemType.SHIELD) || ItemSystem.isWeapon(oldHand)) {
                        removeItemStatsFromPlayer(player, oldHand);
                    }
                }
            }.runTaskLater(nmlEquipment, 1L);
        }
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        PlayerInventory playerInventory = player.getInventory();
        GameMode prevGameMode = player.getGameMode();
        ItemStack mainHand = playerInventory.getItemInMainHand();

        if (ItemSystem.isItemType(mainHand, ItemType.SHIELD) || ItemSystem.isWeapon(mainHand)) {
            if (prevGameMode == GameMode.SURVIVAL && event.getNewGameMode() == GameMode.CREATIVE) {
                removeItemStatsFromPlayer(player, mainHand);
            } else if (prevGameMode == GameMode.CREATIVE && event.getNewGameMode() == GameMode.SURVIVAL) {
                addItemStatsToPlayer(player, mainHand);
            }
        }
    }

    private void addItemStatsToPlayer(Player player, ItemStack itemStack) {
        Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();

        for (Map.Entry<String, Double> statEntry : ItemSystem.convertItemStatsToPlayerStats(itemStack).entrySet()) {
            stats.add2Stat(statEntry.getKey(), statEntry.getValue());
        }
    }

    private void removeItemStatsFromPlayer(Player player, ItemStack itemStack) {
        Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();

        for (Map.Entry<String, Double> statEntry : ItemSystem.convertItemStatsToPlayerStats(itemStack).entrySet()) {
            stats.removeFromStat(statEntry.getKey(), statEntry.getValue());
        }
    }

    /// returns the first slot of an itemstack in the player's inventory
    /// or -1 if it can't find it
    private int locateItemInInventory(PlayerInventory playerInventory, ItemStack itemStack) {
        int slot = -1;

        for (int i = 0; i < playerInventory.getSize(); i++) {
            ItemStack stack = playerInventory.getItem(i);
            if (stack != null && stack.isSimilar(itemStack) && stack.getAmount() < stack.getMaxStackSize()) {
                slot = i;
            }
        }

        if (slot == -1) {
            slot = playerInventory.firstEmpty();
        }

        return slot;
    }
}

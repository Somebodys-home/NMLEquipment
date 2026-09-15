package io.github.NoOne.nMLEquipment.listeners;

import io.github.NoOne.nMLEquipment.EquipmentMenu;
import io.github.NoOne.nMLEquipment.NMLEquipment;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLPlayerStats.statSystem.StatChangeEvent;
import io.github.NoOne.nMLSkills.skillSystem.SkillChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

import static io.github.NoOne.nMLItems.enums.ItemType.SHIELD;

public class EquipmentListener implements Listener {
    private NMLEquipment nmlEquipment;

    public EquipmentListener(NMLEquipment nmlEquipment) {
        this.nmlEquipment = nmlEquipment;
    }

    @EventHandler
    public void onEquipmentChange(EquipmentChangeEvent event) {
        HashMap<String, Double> doffedArmorStats = ItemSystem.convertItemStatsToPlayerStats(event.getDoffedEquipment());
        HashMap<String, Double> donnedArmorStats = ItemSystem.convertItemStatsToPlayerStats(event.getDonnedEquipment());

        for (Map.Entry<String, Double> entry : doffedArmorStats.entrySet()) {
            Bukkit.getPluginManager().callEvent(new StatChangeEvent(event.getPlayer(), entry.getKey(), -entry.getValue()));
        }

        for (Map.Entry<String, Double> entry : donnedArmorStats.entrySet()) {
            Bukkit.getPluginManager().callEvent(new StatChangeEvent(event.getPlayer(), entry.getKey(), entry.getValue()));
        }
    }

    @EventHandler()
    public void openEquipmentMenuOnInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        if (!(event.getClickedInventory() instanceof PlayerInventory)) {
            return;
        }

        // clicking on armor/offhand slots
        if ((event.getSlotType() == InventoryType.SlotType.ARMOR || event.getSlot() == 40)) {
            event.setCancelled(true);

            if (!cursorItem.getType().isAir()) { // for clicking on slots with an item in cursor
                if (ItemSystem.isItemUsable(cursorItem, player)) {
                    player.getInventory().addItem(cursorItem);
                    event.setCursor(null);
                } else {
                    NMLEquipment.sendUnusableItemWarning(player);
                    return; // so the menu isn't opened using unusable equipment
                }
            }

            Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> new EquipmentMenu(nmlEquipment, player).open(), 1L);
            return;
        }

        // shift clicking equipment
        if (event.getClick().isShiftClick() && ItemSystem.isEquippable(clickedItem)) {
            event.setCancelled(true);

            if (ItemSystem.isItemUsable(clickedItem, player)) {
                Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> new EquipmentMenu(nmlEquipment, player).open(), 1L);
            } else {
                NMLEquipment.sendUnusableItemWarning(player);
            }
        }
    }

    @EventHandler
    public void usingEquipmentInYourMainHand(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerInventory playerInventory = player.getInventory();
        ItemStack triggeringItem = event.getItem();
        Action actionType = event.getAction();

        if ((actionType == Action.RIGHT_CLICK_AIR || actionType == Action.RIGHT_CLICK_BLOCK) &&
            (triggeringItem != null && playerInventory.getItem(playerInventory.getHeldItemSlot()) != null &&
            playerInventory.getItem(playerInventory.getHeldItemSlot()).isSimilar(triggeringItem))) {

            if (ItemSystem.isEquippable(triggeringItem) && !ItemSystem.isItemType(triggeringItem, SHIELD)) {
                event.setCancelled(true);

                if (ItemSystem.isItemUsable(triggeringItem, player)) {
                    Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> new EquipmentMenu(nmlEquipment, player).open(), 1L);
                } else {
                    NMLEquipment.sendUnusableItemWarning(player);
                }
            }
        }
    }

    @EventHandler
    public void dontSwapEquipmentToOffhand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if (ItemSystem.isEquippable(event.getOffHandItem())) {
            event.setCancelled(true);

            if (ItemSystem.isItemUsable(event.getOffHandItem(), player)) {
                Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> new EquipmentMenu(nmlEquipment, player).open(), 1L);
            } else {
                NMLEquipment.sendUnusableItemWarning(player);
            }
        }
    }

    @EventHandler()
    public void levelCheck(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItem(event.getNewSlot());

        assert heldItem != null;
        if (ItemSystem.hasLevelKey(heldItem) && ItemSystem.hasOriginalNameKey(heldItem)) {
            ItemSystem.usableItemCheck(heldItem, player);
        }
    }

    @EventHandler
    public void levelCheckOnLevelUp(SkillChangeEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (ItemSystem.hasLevelKey(heldItem)) {
            ItemSystem.usableItemCheck(heldItem, player);
        }
    }
}

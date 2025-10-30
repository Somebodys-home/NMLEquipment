package io.github.NoOne.nMLEquipment;

import io.github.NoOne.menuSystem.MenuSystem;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static io.github.NoOne.nMLItems.ItemType.*;

public class EquipmentMenuListener implements Listener {
    private NMLEquipment nmlEquipment;

    public EquipmentMenuListener(NMLEquipment nmlEquipment) {
        this.nmlEquipment = nmlEquipment;
    }

    @EventHandler
    public void onEquipmentChange(EquipmentChangeEvent event) {
        HashMap<String, Double> doffedArmorStats = ItemSystem.convertItemStatsToPlayerStats(event.getDoffedEquipment());
        HashMap<String, Double> donnedArmorStats = ItemSystem.convertItemStatsToPlayerStats(event.getDonnedEquipment());
        Stats stats = nmlEquipment.getProfileManager().getPlayerProfile(event.getPlayer().getUniqueId()).getStats();

        for (Map.Entry<String, Double> entry : doffedArmorStats.entrySet()) {
            stats.removeFromStat(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Double> entry : donnedArmorStats.entrySet()) {
            stats.add2Stat(entry.getKey(), entry.getValue());
        }
    }

    @EventHandler()
    public void openMenuWhenManuallyWithArmor(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack cursorItem = event.getCursor();
        int clickedSlot = event.getSlot();

        if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT) {
            if (clickedSlot >= 36 && clickedSlot <= 40 && (event.getSlotType() == InventoryType.SlotType.ARMOR || event.getSlotType() == InventoryType.SlotType.QUICKBAR)) {
                event.setCancelled(true);

                if (cursorItem == null || cursorItem.getType() == Material.AIR) {
                    Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> {
                        new EquipmentMenu(MenuSystem.getPlayerMenuUtility(player)).open();
                    }, 1L);
                } else if (ItemSystem.isItemUsable(cursorItem, player)) {
                    player.getInventory().addItem(cursorItem);
                    player.setItemOnCursor(null);

                    Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> {
                        new EquipmentMenu(MenuSystem.getPlayerMenuUtility(player)).open();
                    }, 1L);
                }
            }
        }
    }

    @EventHandler()
    public void openMenuWhenShiftEquippingEquipment(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        ClickType clickType = event.getClick();
        int clickedSlot = event.getSlot();

        if (clickType.isShiftClick()) {
            if (clickedSlot >= 36 && clickedSlot <= 40) { // potentially armor off
                event.setCancelled(true);

                Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> {
                    new EquipmentMenu(MenuSystem.getPlayerMenuUtility(player)).open();
                }, 1L);
            } else { // putting armor on
                if ((ItemSystem.isEquippable(currentItem)) && ItemSystem.isItemUsable(currentItem, player)) {
                    event.setCancelled(true);

                    Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> {
                        new EquipmentMenu(MenuSystem.getPlayerMenuUtility(player)).open();
                    }, 1L);
                }
            }
        }
    }

    @EventHandler
    public void usingEquipmentInYourMainHand(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack triggeredItem = event.getItem();
        Action actionType = event.getAction();

        if ((actionType == Action.RIGHT_CLICK_AIR || actionType == Action.RIGHT_CLICK_BLOCK) &&
            (triggeredItem != null && player.getInventory().getItem(player.getInventory().getHeldItemSlot()) != null && player.getInventory().getItem(player.getInventory().getHeldItemSlot()).isSimilar(triggeredItem))) {

            if (ItemSystem.isEquippable(triggeredItem) && ItemSystem.getItemType(triggeredItem) != SHIELD) {
                event.setCancelled(true);

                Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> {
                    new EquipmentMenu(MenuSystem.getPlayerMenuUtility(player)).open();
                }, 1L);
            }
        }
    }

    @EventHandler
    public void dontSwapEquipmentToOffhand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack mainhand = event.getOffHandItem();

        if (ItemSystem.isEquippable(mainhand)) {
            event.setCancelled(true);

            Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> {
                new EquipmentMenu(MenuSystem.getPlayerMenuUtility(player)).open();
            }, 1L);
        }
    }

    @EventHandler()
    public void armorLevelCheck(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItem(event.getNewSlot());
        boolean usable = ItemSystem.isItemUsable(heldItem, player);

        if (heldItem == null || heldItem.getType() == Material.AIR) { return; }
        if (!heldItem.hasItemMeta()) { return; }
        if (ItemSystem.getItemType(heldItem) == null) { return; }
        if (!usable) {
            player.sendMessage("§c⚠ §nYou are too inexperienced for this item!§r§c ⚠");
        }

        ItemSystem.updateUnusableItemName(heldItem, usable);
    }

    @EventHandler
    public void unusableArmorCheck(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ClickType click = event.getClick();
        InventoryAction action = event.getAction();
        ItemStack armor = event.getCursor();
        int slot = event.getSlot();

        if (click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT) {
            if (ItemSystem.isEquippable(armor) && !ItemSystem.isItemUsable(armor, player)) {
                player.sendMessage("§c⚠ §nYou are too inexperienced for this item!§r§c ⚠");
                event.setCancelled(true);
                ItemSystem.updateUnusableItemName(armor, false);
                return;
            }
        }

        if ((slot >= 36 && slot <= 40) && (action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE || action == InventoryAction.PLACE_SOME)) {
            if (ItemSystem.isEquippable(armor) && !ItemSystem.isItemUsable(armor, player)) {
                player.sendMessage("§c⚠ §nYou are too inexperienced for this item!§r§c ⚠");
                event.setCancelled(true);
                ItemSystem.updateUnusableItemName(armor, false);
            }
        }
    }

    @EventHandler
    public void blockRightClickEquippingUnusableArmorFromHand(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();

            if (ItemSystem.isEquippable(item) && !ItemSystem.isItemUsable(item, player)) {
                ItemSystem.updateUnusableItemName(item, false);
                event.setCancelled(true);
                player.sendMessage("§c⚠ §nYou are too inexperienced for this item!§r§c ⚠");
            }
        }
    }
}

package io.github.NoOne.nMLEquipment.listeners;

import io.github.NoOne.menuSystem.MenuSystem;
import io.github.NoOne.nMLEquipment.EquipmentMenu;
import io.github.NoOne.nMLEquipment.NMLEquipment;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLSkills.skillSetSystem.SkillSetManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.github.NoOne.nMLItems.enums.ItemType.*;

public class EquipmentMenuListener implements Listener {
    private NMLEquipment nmlEquipment;
    private SkillSetManager skillSetManager;

    public EquipmentMenuListener(NMLEquipment nmlEquipment) {
        this.nmlEquipment = nmlEquipment;
        skillSetManager = nmlEquipment.getSkillSetManager();
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
    public void levelCheck(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItem(event.getNewSlot());

        if (heldItem == null || heldItem.getType() == Material.AIR || !heldItem.hasItemMeta() || !ItemSystem.hasLevelKey(heldItem)) {
            return;
        }

        PersistentDataContainer pdc = heldItem.getItemMeta().getPersistentDataContainer();
        boolean usable = true;

        if (ItemSystem.isItemType(heldItem, HOE)) {
            int farmingLevel = skillSetManager.getSkillSet(player.getUniqueId()).getSkills().getFarmingLevel();

            usable = farmingLevel >= ItemSystem.getLevel(heldItem);
        } else if (ItemSystem.isWeapon(heldItem) || ItemSystem.isEquippable(heldItem)){
            int combatLevel = skillSetManager.getSkillSet(player.getUniqueId()).getSkills().getCombatLevel();

            usable = combatLevel >= ItemSystem.getLevel(heldItem);
        }

        if (!usable) {
            player.sendMessage("§c⚠ §nYou are too inexperienced for this item!§r§c ⚠");
        }

        if (pdc.has(ItemSystem.getOriginalNameKey())) {
            ItemSystem.updateUnusableItemName(heldItem, usable);
        }
    }

    @EventHandler
    public void unusableArmorCheck(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ClickType click = event.getClick();
        InventoryAction action = event.getAction();
        ItemStack armor = event.getCursor();
        int slot = event.getSlot();

        if (click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT) {
            if (armor.hasItemMeta() && ItemSystem.isEquippable(armor) && !ItemSystem.isItemUsable(armor, player)) {
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

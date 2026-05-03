package io.github.NoOne.nMLEquipment.listeners;

import io.github.NoOne.menuSystem.MenuSystem;
import io.github.NoOne.nMLEquipment.EquipmentMenu;
import io.github.NoOne.nMLEquipment.NMLEquipment;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLSkills.skillSetSystem.SkillSetManager;
import io.github.NoOne.nMLSkills.skillSystem.SkillChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
import org.bukkit.persistence.PersistentDataContainer;

import static io.github.NoOne.nMLItems.enums.ItemType.*;

public class EquipmentListener implements Listener {
    private NMLEquipment nmlEquipment;
    private ItemSystem itemSystem;

    public EquipmentListener(NMLEquipment nmlEquipment) {
        this.nmlEquipment = nmlEquipment;
        itemSystem = nmlEquipment.getItemSystem();
    }

    @EventHandler()
    public void openEquipmentMenuOnInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        // clicking on armor/offhand slots
        if ((event.getSlotType() == InventoryType.SlotType.ARMOR || event.getSlot() == 40)) {
            event.setCancelled(true);

            if (!cursorItem.getType().isAir()) { // for clicking on slots with an item in cursor
                if (itemSystem.isItemUsable(cursorItem, player)) {
                    player.getInventory().addItem(cursorItem);
                    event.setCursor(null);
                } else {
                    NMLEquipment.sendUnusableItemWarning(player);
                    return; // so the menu isn't opened using unusable equipment
                }
            }

            Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> new EquipmentMenu(nmlEquipment, MenuSystem.getPlayerMenuUtility(player)).open(), 1L);
            return;
        }

        // shift clicking equipment
        if (event.getClick().isShiftClick() && itemSystem.isEquippable(clickedItem)) {
            event.setCancelled(true);

            if (itemSystem.isItemUsable(clickedItem, player)) {
                Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> new EquipmentMenu(nmlEquipment, MenuSystem.getPlayerMenuUtility(player)).open(), 1L);
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

            if (itemSystem.isEquippable(triggeringItem) && itemSystem.getItemType(triggeringItem) != SHIELD) {
                event.setCancelled(true);

                if (itemSystem.isItemUsable(triggeringItem, player)) {
                    Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> new EquipmentMenu(nmlEquipment, MenuSystem.getPlayerMenuUtility(player)).open(), 1L);
                } else {
                    NMLEquipment.sendUnusableItemWarning(player);
                }
            }
        }
    }

    @EventHandler
    public void dontSwapEquipmentToOffhand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if (itemSystem.isEquippable(event.getOffHandItem())) {
            event.setCancelled(true);

            if (itemSystem.isItemUsable(event.getOffHandItem(), player)) {
                Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> new EquipmentMenu(nmlEquipment, MenuSystem.getPlayerMenuUtility(player)).open(), 1L);
            } else {
                NMLEquipment.sendUnusableItemWarning(player);
            }
        }
    }

    @EventHandler()
    public void levelCheck(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItem(event.getNewSlot());
        boolean usable = itemSystem.isItemUsable(heldItem, player);

        if (itemSystem.hasLevelKey(heldItem)) {
            itemSystem.updateUnusableItemName(heldItem, usable);

            if (!usable) {
                NMLEquipment.sendUnusableItemWarning(player);
            }
        }
    }

    @EventHandler
    public void levelCheckOnLevelUp(SkillChangeEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (itemSystem.hasLevelKey(heldItem)) {
            itemSystem.updateUnusableItemName(heldItem, itemSystem.isItemUsable(heldItem, player));
        }
    }
}

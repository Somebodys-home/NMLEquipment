package io.github.NoOne.nMLEquipment;

import io.github.NoOne.menuSystem.MenuSystem;
import io.github.NoOne.nMLArmor.ArmorChangeEvent;
import io.github.NoOne.nMLItems.ItemSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import static io.github.NoOne.nMLItems.ItemType.*;

public class EquipmentMenuListener implements Listener {
    private NMLEquipment nmlEquipment;

    public EquipmentMenuListener(NMLEquipment nmlEquipment) {
        this.nmlEquipment = nmlEquipment;
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
                        new EquipmentMenu(MenuSystem.getPlayerMenuUtility(player), nmlEquipment).open();
                    }, 1L);
                } else if (ItemSystem.isItemUsable(cursorItem, player)) {
                    player.getInventory().addItem(cursorItem);
                    player.setItemOnCursor(null);

                    Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> {
                        new EquipmentMenu(MenuSystem.getPlayerMenuUtility(player), nmlEquipment).open();
                    }, 1L);
                }
            }
        }
    }

    @EventHandler()
    public void openMenuWhenShiftEquippingArmor(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerInventory playerInventory = player.getInventory();
        ItemStack currentItem = event.getCurrentItem();
        ClickType clickType = event.getClick();
        int clickedSlot = event.getSlot();

        if (clickType.isShiftClick()) {
            if (clickedSlot >= 36 && clickedSlot <= 40) { // potentially armor off
                event.setCancelled(true);
                playerInventory.setItem(clickedSlot, null);
                playerInventory.addItem(currentItem);

                Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> {
                    new EquipmentMenu(MenuSystem.getPlayerMenuUtility(player), nmlEquipment).open();
                }, 1L);

                Bukkit.getPluginManager().callEvent(new ArmorChangeEvent(player, currentItem, new ItemStack(Material.AIR)));
            } else { // putting armor on
                if (currentItem.hasItemMeta() && (ItemSystem.getItemType(currentItem) == HELMET || ItemSystem.getItemType(currentItem) == CHESTPLATE ||
                        ItemSystem.getItemType(currentItem) == LEGGINGS || ItemSystem.getItemType(currentItem) == BOOTS ||
                        ItemSystem.getItemType(currentItem) == SHIELD) && ItemSystem.isItemUsable(currentItem, player)) {

                    Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> {
                        new EquipmentMenu(MenuSystem.getPlayerMenuUtility(player), nmlEquipment).open();
                    }, 1L);

                    Bukkit.getPluginManager().callEvent(new ArmorChangeEvent(player, new ItemStack(Material.AIR), currentItem));
                }
            }
        }
    }

    @EventHandler
    public void usingArmorInYourMainHand(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = event.getItem();
        Action actionType = event.getAction();

        if (actionType == Action.RIGHT_CLICK_AIR || actionType == Action.RIGHT_CLICK_BLOCK) {
            if (heldItem != null && (ItemSystem.getItemType(heldItem) == HELMET || ItemSystem.getItemType(heldItem) == CHESTPLATE ||
                ItemSystem.getItemType(heldItem) == LEGGINGS || ItemSystem.getItemType(heldItem) == BOOTS)) {

                switch (ItemSystem.getItemType(heldItem)) {
                    case HELMET -> Bukkit.getPluginManager().callEvent(new ArmorChangeEvent(player, player.getInventory().getHelmet(), heldItem));
                    case CHESTPLATE -> Bukkit.getPluginManager().callEvent(new ArmorChangeEvent(player, player.getInventory().getChestplate(), heldItem));
                    case LEGGINGS -> Bukkit.getPluginManager().callEvent(new ArmorChangeEvent(player, player.getInventory().getLeggings(), heldItem));
                    case BOOTS -> Bukkit.getPluginManager().callEvent(new ArmorChangeEvent(player, player.getInventory().getBoots(), heldItem));
                }

                Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> {
                    new EquipmentMenu(MenuSystem.getPlayerMenuUtility(player), nmlEquipment).open();
                }, 1L);
            }
        }
    }

    @EventHandler
    public void dontSwapArmorToOffhand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack mainhand = event.getOffHandItem();

        if (ItemSystem.getItemType(mainhand) == HELMET || ItemSystem.getItemType(mainhand) == CHESTPLATE ||
                ItemSystem.getItemType(mainhand) == LEGGINGS || ItemSystem.getItemType(mainhand) == BOOTS
                || ItemSystem.getItemType(mainhand) == SHIELD) {

            event.setCancelled(true);
            Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> {
                new EquipmentMenu(MenuSystem.getPlayerMenuUtility(player), nmlEquipment).open();
            }, 1L);
        }
    }
}

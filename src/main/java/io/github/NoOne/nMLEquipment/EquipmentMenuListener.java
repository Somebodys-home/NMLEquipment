package io.github.NoOne.nMLEquipment;

import io.github.NoOne.menuSystem.MenuSystem;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.ItemType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
            if (clickedSlot >= 36 && clickedSlot <= 40) {
                event.setCancelled(true);
                if (ItemSystem.isItemUsable(cursorItem, player) || cursorItem == null || cursorItem.getType() == Material.AIR) {
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
        ItemStack cursorItem = event.getCursor();
        ClickType clickType = event.getClick();
        int clickedSlot = event.getSlot();

        switch (clickType) {
            case SHIFT_LEFT, SHIFT_RIGHT -> {
                if (clickedSlot >= 36 && clickedSlot <= 40) { // potentially armor off
                    event.setCancelled(true);
                    playerInventory.setItem(clickedSlot, null);
                    playerInventory.addItem(currentItem);

                    Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> {
                        new EquipmentMenu(MenuSystem.getPlayerMenuUtility(player), nmlEquipment).open();
                    }, 1L);
                } else { // putting armor on
                    if (currentItem.hasItemMeta() && (ItemSystem.getItemTypeFromItemStack(currentItem) == ItemType.HELMET || ItemSystem.getItemTypeFromItemStack(currentItem) == ItemType.CHESTPLATE ||
                            ItemSystem.getItemTypeFromItemStack(currentItem) == ItemType.LEGGINGS || ItemSystem.getItemTypeFromItemStack(currentItem) == ItemType.BOOTS)
                            && ItemSystem.isItemUsable(currentItem, player)) {

                        Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> {
                            new EquipmentMenu(MenuSystem.getPlayerMenuUtility(player), nmlEquipment).open();
                        }, 1L);
                    }
                }
            }
        }
    }

    @EventHandler
    public void usingArmorInYourMainHand(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = event.getItem();
        Action actionType = event.getAction();

        switch (actionType) {
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                if (ItemSystem.getItemTypeFromItemStack(heldItem) == ItemType.HELMET || ItemSystem.getItemTypeFromItemStack(heldItem) == ItemType.CHESTPLATE ||
                    ItemSystem.getItemTypeFromItemStack(heldItem) == ItemType.LEGGINGS || ItemSystem.getItemTypeFromItemStack(heldItem) == ItemType.BOOTS) {

                    Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> {
                        new EquipmentMenu(MenuSystem.getPlayerMenuUtility(player), nmlEquipment).open();
                    }, 1L);
                }
            }
        }
    }

    @EventHandler
    public void dontSwapArmorToOffhand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack mainhand = event.getOffHandItem();

        if (ItemSystem.getItemTypeFromItemStack(mainhand) == ItemType.HELMET || ItemSystem.getItemTypeFromItemStack(mainhand) == ItemType.CHESTPLATE ||
                ItemSystem.getItemTypeFromItemStack(mainhand) == ItemType.LEGGINGS || ItemSystem.getItemTypeFromItemStack(mainhand) == ItemType.BOOTS) {

            event.setCancelled(true);
            Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> {
                new EquipmentMenu(MenuSystem.getPlayerMenuUtility(player), nmlEquipment).open();
            }, 1L);
        }
    }
}

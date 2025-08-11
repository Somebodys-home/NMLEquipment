package io.github.NoOne.nMLEquipment;

import io.github.NoOne.menuSystem.MenuSystem;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.ItemType;
import org.bukkit.Bukkit;
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

import javax.swing.*;

public class EquipmentMenuListener implements Listener {
    private NMLEquipment nmlEquipment;

    public EquipmentMenuListener(NMLEquipment nmlEquipment) {
        this.nmlEquipment = nmlEquipment;
    }

    @EventHandler()
    public void openMenuWhenInteractingWithArmor(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerInventory playerInventory = player.getInventory();
        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        ClickType clickType = event.getClick();
        int clickedSlot = event.getSlot();

        switch (clickType) {
            case LEFT, RIGHT -> {
                if (clickedSlot >= 36 && clickedSlot <= 40) {
                    event.setCancelled(true);

                    switch (clickedSlot) {
                        case 39 -> {
                            if (ItemSystem.getItemTypeFromItemStack(cursorItem) == ItemType.HELMET) {
                                ItemStack prevHelm = playerInventory.getHelmet();

                                if (prevHelm != null) {
                                    playerInventory.addItem(prevHelm);
                                }

                                playerInventory.setHelmet(cursorItem);
                                player.setItemOnCursor(null);
                            }
                        }
                        case 38 -> {
                            if (ItemSystem.getItemTypeFromItemStack(cursorItem) == ItemType.CHESTPLATE) {
                                ItemStack prevChest = playerInventory.getChestplate();

                                if (prevChest != null) {
                                    playerInventory.addItem(prevChest);
                                }

                                playerInventory.setChestplate(cursorItem);
                                player.setItemOnCursor(null);
                            }
                        }
                        case 37 -> {
                            if (ItemSystem.getItemTypeFromItemStack(cursorItem) == ItemType.LEGGINGS) {
                                ItemStack prevLeg = playerInventory.getLeggings();

                                if (prevLeg != null) {
                                    playerInventory.addItem(prevLeg);
                                }

                                playerInventory.setLeggings(cursorItem);
                                player.setItemOnCursor(null);
                            }
                        }
                        case 36 -> {
                            if (ItemSystem.getItemTypeFromItemStack(cursorItem) == ItemType.BOOTS) {
                                ItemStack prevBoot = playerInventory.getBoots();

                                if (prevBoot != null) {
                                    playerInventory.addItem(prevBoot);
                                }

                                playerInventory.setBoots(cursorItem);
                                player.setItemOnCursor(null);
                            }
                        }
                    }

                    Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> {
                        new EquipmentMenu(MenuSystem.getPlayerMenuUtility(player), nmlEquipment).open();
                    }, 1L);
                }
            }
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
                        ItemSystem.getItemTypeFromItemStack(currentItem) == ItemType.LEGGINGS || ItemSystem.getItemTypeFromItemStack(currentItem) == ItemType.BOOTS)) {

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

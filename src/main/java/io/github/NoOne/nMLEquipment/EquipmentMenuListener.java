package io.github.NoOne.nMLEquipment;

import io.github.NoOne.menuSystem.MenuSystem;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.ItemType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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

//        player.sendMessage("current item: " + currentItem.getType());
//        player.sendMessage("cursor item: " + cursorItem.getType());

        switch (clickType) {
            case LEFT, RIGHT -> {
                if (clickedSlot >= 36 && clickedSlot <= 40) {
                    event.setCancelled(true);

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
                    if (ItemSystem.getItemTypeFromItemStack(currentItem) == ItemType.HELMET || ItemSystem.getItemTypeFromItemStack(currentItem) == ItemType.CHESTPLATE ||
                        ItemSystem.getItemTypeFromItemStack(currentItem) == ItemType.LEGGINGS || ItemSystem.getItemTypeFromItemStack(currentItem) == ItemType.BOOTS) {

                        Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> {
                            new EquipmentMenu(MenuSystem.getPlayerMenuUtility(player), nmlEquipment).open();
                        }, 1L);
                    }
                }
            }
        }
    }
}

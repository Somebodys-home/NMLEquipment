package io.github.NoOne.nMLEquipment;

import io.github.NoOne.menuSystem.Menu;
import io.github.NoOne.menuSystem.PlayerMenuUtility;
import io.github.NoOne.nMLItems.ItemStat;
import io.github.NoOne.nMLItems.ItemSystem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EquipmentMenu extends Menu { // todo: finalize menu when all the stats are finalized
    private NMLEquipment nmlEquipment;
    private Player player;
    private PlayerInventory playerInventory;
    private ItemStack nothingItem;
    private ItemStack statsItem;

    public EquipmentMenu(PlayerMenuUtility playerMenuUtility, NMLEquipment nmlEquipment) {
        super(playerMenuUtility);
        this.nmlEquipment = nmlEquipment;

        player = playerMenuUtility.getOwner();
        playerInventory = player.getInventory();

        nothingItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta nothingMeta = nothingItem.getItemMeta();
        nothingMeta.setDisplayName("§8...Nothing?");
        nothingItem.setItemMeta(nothingMeta);

        statsItem = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        ItemMeta statsMeta = statsItem.getItemMeta();
        statsMeta.setDisplayName("§b§lYour Armor's Total Stats:");
        statsItem.setItemMeta(statsMeta);
    }

    @Override
    public String getMenuName() {
        return "             §5§lYour Drip";
    }

    @Override
    public int getSlots() {
        return 9 * 6;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
    }

    @Override
    public void setMenuItems() {
        updateNothingItem(1);
        inventory.setItem(11, nothingItemCheck(playerInventory.getHelmet()));
        updateNothingItem(2);
        inventory.setItem(20, nothingItemCheck(playerInventory.getChestplate()));
        updateNothingItem(3);
        inventory.setItem(29, nothingItemCheck(playerInventory.getLeggings()));
        updateNothingItem(4);
        inventory.setItem(38, nothingItemCheck(playerInventory.getBoots()));
        updateNothingItem(5);
        inventory.setItem(21, nothingItemCheck(playerInventory.getItemInMainHand()));
        updateNothingItem(6);
        inventory.setItem(30, nothingItemCheck(playerInventory.getItemInOffHand()));

        updateStatsItem();

        for (Map.Entry<ItemStat, Double> entry : ItemSystem.getAllStats(statsItem).entrySet()) {
            player.sendMessage(entry.getKey().toString() + " " + entry.getValue());
        }

        inventory.setItem(14, statsItem);
        inventory.setItem(15, statsItem);
        inventory.setItem(23, statsItem);
        inventory.setItem(24, statsItem);
        inventory.setItem(32, statsItem);
        inventory.setItem(33, statsItem);
        inventory.setItem(41, statsItem);
        inventory.setItem(42, statsItem);
    }

    public ItemStack nothingItemCheck(ItemStack itemStack) {
        if (itemStack == null) {
            return nothingItem;
        }
        if (itemStack.getType() == Material.AIR) {
            return nothingItem;
        }

        return itemStack;
    }

    public void updateNothingItem(int situation) {
        ItemMeta meta = nothingItem.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("");

        if (situation == 1) { // helmet
            lore.add("§7§oYou can move helmets in");
            lore.add("§7§oand out of this slot, ya know.");
        } else if (situation == 2) { // chestplate
            lore.add("§7§oYou can move chestplates in");
            lore.add("§7§oand out of this slot, ya know.");
        } else if (situation == 3) { // leggings
            lore.add("§7§oYou can move leggings in");
            lore.add("§7§oand out of this slot, ya know.");
        } else if (situation == 4) { // boots
            lore.add("§7§oYou can move boots in");
            lore.add("§7§oand out of this slot, ya know.");
        } else if (situation == 5) { // mainhand
            lore.add("§7§oOk you can't actually move items outa here");
        } else if (situation == 6) { // offhand
            lore.add("§7§oor here either.");
        }

        meta.setLore(lore);
        nothingItem.setItemMeta(meta);
    }

    public void updateStatsItem() {
        HashMap<ItemStat, Double> total = getAllDefensesOfPlayerArmor();

        for (Map.Entry<ItemStat, Double> defenseEntry : total.entrySet()) {
            ItemSystem.setStat(statsItem, defenseEntry.getKey(), defenseEntry.getValue());
        }

        ItemSystem.updateLoreWithItemStats(statsItem);
    }

    public HashMap<ItemStat, Double> getAllDefensesOfPlayerArmor() {
        HashMap<ItemStat, Double> helmet = ItemSystem.getAllStats(playerInventory.getHelmet());
        HashMap<ItemStat, Double> chestplate = ItemSystem.getAllStats(playerInventory.getChestplate());
        HashMap<ItemStat, Double> leggings = ItemSystem.getAllStats(playerInventory.getLeggings());
        HashMap<ItemStat, Double> boots = ItemSystem.getAllStats(playerInventory.getBoots());
        HashMap<ItemStat, Double> total = helmet;

        total.forEach((key, value) -> chestplate.merge(key, value, (oldValue, newValue) -> oldValue + newValue));
        total.forEach((key, value) -> leggings.merge(key, value, (oldValue, newValue) -> oldValue + newValue));
        total.forEach((key, value) -> boots.merge(key, value, (oldValue, newValue) -> oldValue + newValue));

        return total;
    }
}

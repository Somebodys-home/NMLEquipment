package io.github.NoOne.nMLEquipment;

import io.github.NoOne.menuSystem.Menu;
import io.github.NoOne.menuSystem.PlayerMenuUtility;
import io.github.NoOne.nMLItems.ItemStat;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.ItemType;
import io.github.NoOne.nMLPlayerStats.statSystem.StatChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EquipmentMenu extends Menu {
    private Player player;
    private PlayerInventory playerInventory;
    private ItemStack nothingItem;
    private ItemStack statsItem;

    public EquipmentMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);

        player = playerMenuUtility.getOwner();
        playerInventory = player.getInventory();

        nothingItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta nothingMeta = nothingItem.getItemMeta();
        nothingMeta.setDisplayName("§8...Nothing?");
        nothingItem.setItemMeta(nothingMeta);

        statsItem = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
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
        event.setCancelled(true);

        switch (event.getSlot()) {
            case 11 -> { // take off helmet
                ItemStack helmet = playerInventory.getHelmet();
                if (helmet == null) helmet = new ItemStack(Material.AIR);
                playerInventory.addItem(helmet);
                playerInventory.setHelmet(new ItemStack(Material.AIR));
            }
            case 20 -> { // take off chestplate
                ItemStack chestplate = playerInventory.getChestplate();
                if (chestplate == null) chestplate = new ItemStack(Material.AIR);
                playerInventory.addItem(chestplate);
                playerInventory.setChestplate(new ItemStack(Material.AIR));
            }
            case 29 -> { // take off leggings
                ItemStack leggings = playerInventory.getLeggings();
                if (leggings == null) leggings = new ItemStack(Material.AIR);
                playerInventory.addItem(leggings);
                playerInventory.setLeggings(new ItemStack(Material.AIR));
            }
            case 38 -> { // take off boots
                ItemStack boots = playerInventory.getBoots();
                if (boots == null) boots = new ItemStack(Material.AIR);
                playerInventory.addItem(boots);
                playerInventory.setBoots(new ItemStack(Material.AIR));
            }
            case 30 -> { // take off offhand
                ItemStack offhand = playerInventory.getItemInOffHand();
                if (offhand == null) offhand = new ItemStack(Material.AIR);

                if (ItemSystem.getItemType(offhand) != ItemType.GLOVE) {
                    HashMap<String, Double> prevOffhandStats = ItemSystem.convertItemStatsToPlayerStats(offhand);

                    for (Map.Entry<String, Double> statEntry : prevOffhandStats.entrySet()) {
                        Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, statEntry.getKey(), -statEntry.getValue()));
                    }
                    
                    playerInventory.addItem(offhand);
                    playerInventory.setItemInOffHand(new ItemStack(Material.AIR));
                }
            }
        }

        setMenuItems();
    }

    @Override
    public void handlePlayerMenu(InventoryClickEvent event) {
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (ItemSystem.isItemUsable(clickedItem, player)) {
            switch (ItemSystem.getItemType(clickedItem)) {
                case HELMET -> { // swapping helmets
                    ItemStack helmet = playerInventory.getHelmet();
                    playerInventory.setHelmet(clickedItem);
                    playerInventory.setItem(event.getSlot(), helmet);
                }
                case CHESTPLATE -> { // swapping chestplates
                    ItemStack chestplate = playerInventory.getChestplate();
                    playerInventory.setChestplate(clickedItem);
                    playerInventory.setItem(event.getSlot(), chestplate);
                }
                case LEGGINGS -> { // swapping leggings
                    ItemStack leggings = playerInventory.getLeggings();
                    playerInventory.setLeggings(clickedItem);
                    playerInventory.setItem(event.getSlot(), leggings);
                }
                case BOOTS -> { // swapping boots
                    ItemStack boots = playerInventory.getBoots();
                    playerInventory.setBoots(clickedItem);
                    playerInventory.setItem(event.getSlot(), boots);
                }
                case SHIELD, QUIVER -> { // swapping offhand
                    ItemStack offhand = playerInventory.getItemInOffHand();
                    HashMap<String, Double> prevOffhandStats = ItemSystem.convertItemStatsToPlayerStats(offhand);
                    HashMap<String, Double> clickedItemStats = ItemSystem.convertItemStatsToPlayerStats(clickedItem);

                    for (Map.Entry<String, Double> statEntry : prevOffhandStats.entrySet()) {
                        Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, statEntry.getKey(), -statEntry.getValue()));
                    }

                    for (Map.Entry<String, Double> statEntry : clickedItemStats.entrySet()) {
                        Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, statEntry.getKey(), statEntry.getValue()));
                    }

                    playerInventory.setItemInOffHand(clickedItem);
                    playerInventory.setItem(event.getSlot(), offhand);
                }
            }
        } else {
            player.sendMessage("§c⚠ §nYou are too inexperienced for this item!§r§c ⚠");
        }

        setMenuItems();
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
        if (itemStack == null || itemStack.getType() == Material.AIR) {
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
            lore.add("§7§oOk you can't actually move your");
            lore.add("§7§omainhand item from here.");
        } else if (situation == 6) { // offhand
            lore.add("§7§oYou can move your offhand item");
            lore.add("§7§oin and out of this slot, ya know.");
        }

        meta.setLore(lore);
        nothingItem.setItemMeta(meta);
    }

    public HashMap<ItemStat, Double> getAllDefensesOfPlayerArmor() {
        HashMap<ItemStat, Double> helmet = ItemSystem.getAllStats(playerInventory.getHelmet());
        HashMap<ItemStat, Double> chestplate = ItemSystem.getAllStats(playerInventory.getChestplate());
        HashMap<ItemStat, Double> leggings = ItemSystem.getAllStats(playerInventory.getLeggings());
        HashMap<ItemStat, Double> boots = ItemSystem.getAllStats(playerInventory.getBoots());
        HashMap<ItemStat, Double> offhand = ItemSystem.getAllStats(playerInventory.getItemInOffHand());
        HashMap<ItemStat, Double> total = helmet;

        chestplate.forEach((key, value) -> total.merge(key, value, Double::sum));
        leggings.forEach((key, value) -> total.merge(key, value, Double::sum));
        boots.forEach((key, value) -> total.merge(key, value, Double::sum));
        offhand.forEach((key, value) -> total.merge(key, value, Double::sum));

        return total;
    }

    public void updateStatsItem() {
        HashMap<ItemStat, Double> total = getAllDefensesOfPlayerArmor();
        ItemMeta statsMeta = statsItem.getItemMeta();
        ArrayList<String> statsLore = new ArrayList<>();
        statsMeta.setDisplayName("§bYour Armor's Total Stats:");
        statsLore.add("");
        statsMeta.setLore(statsLore);
        statsItem.setItemMeta(statsMeta);

        if (total.isEmpty()) {
            ItemSystem.clearStats(statsItem);
            ItemMeta meta = statsItem.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§7§oCongrats! You've either reached true");
            lore.add("§7§oequilibrium or you're completely nude.");
            meta.setLore(lore);
            statsItem.setItemMeta(meta);
        } else {
            ItemSystem.clearStats(statsItem);
            ItemSystem.setStats(statsItem, total);
        }

        ItemSystem.updateLoreWithStats(statsItem);
    }
}

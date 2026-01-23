package io.github.NoOne.nMLEquipment;

import io.github.NoOne.menuSystem.Menu;
import io.github.NoOne.menuSystem.PlayerMenuUtility;
import io.github.NoOne.nMLEquipment.events.EquipmentChangeEvent;
import io.github.NoOne.nMLItems.enums.ItemStat;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.enums.ItemType;
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
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack mainHand;
    private ItemStack offHand;
    private ItemStack nothingItem;
    private ItemStack statsItem;

    public EquipmentMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);

        player = playerMenuUtility.getOwner();
        playerInventory = player.getInventory();
        helmet = playerInventory.getHelmet();
        chestplate = playerInventory.getChestplate();
        leggings = playerInventory.getLeggings();
        boots = playerInventory.getBoots();
        mainHand = playerInventory.getItemInMainHand();
        offHand = playerInventory.getItemInOffHand();

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
                if (helmet == null) helmet = new ItemStack(Material.AIR);

                playerInventory.addItem(helmet);
                playerInventory.setHelmet(new ItemStack(Material.AIR));
                Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, helmet, null));
                new EquipmentMenu(playerMenuUtility).open();
            }
            case 20 -> { // take off chestplate
                if (chestplate == null) chestplate = new ItemStack(Material.AIR);

                playerInventory.addItem(chestplate);
                playerInventory.setChestplate(new ItemStack(Material.AIR));
                Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, chestplate, null));
                new EquipmentMenu(playerMenuUtility).open();
            }
            case 29 -> { // take off leggings
                if (leggings == null) leggings = new ItemStack(Material.AIR);

                playerInventory.addItem(leggings);
                playerInventory.setLeggings(new ItemStack(Material.AIR));
                Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, leggings, null));
                new EquipmentMenu(playerMenuUtility).open();
            }
            case 38 -> { // take off boots
                if (boots == null) boots = new ItemStack(Material.AIR);

                playerInventory.addItem(boots);
                playerInventory.setBoots(new ItemStack(Material.AIR));
                Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, boots, null));
                new EquipmentMenu(playerMenuUtility).open();
            }
            case 21 -> { // stop taking off other glove (why?)
                if (ItemSystem.getItemType(mainHand) == ItemType.GLOVE) {
                    playerInventory.setItemInOffHand(offHand);
                    return;
                }
            }
            case 30 -> { // take off offHand
                boolean changedOffhand = true;
                int foundSlot = -1;

                if (ItemSystem.getItemType(offHand) == ItemType.GLOVE) { // idk why i need to do this
                    playerInventory.setItemInOffHand(offHand);
                    return;
                }

                playerInventory.addItem(offHand);
                playerInventory.setItemInOffHand(new ItemStack(Material.AIR));

                /// where did that item go?
                for (int slot = 0; slot < playerInventory.getSize(); slot++) {
                    ItemStack stack = playerInventory.getItem(slot);
                    if (stack != null && stack.isSimilar(offHand)) {
                        foundSlot = slot;
                        break;
                    }
                }

                if (foundSlot == playerInventory.getHeldItemSlot() && ItemSystem.isItemType(offHand, ItemType.SHIELD)) {
                    changedOffhand = false;
                }

                if (changedOffhand) {
                    Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, offHand, null));
                }

                new EquipmentMenu(playerMenuUtility).open();
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
                    playerInventory.setHelmet(clickedItem);
                    playerInventory.setItem(event.getSlot(), helmet);
                    Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, helmet, clickedItem));
                    new EquipmentMenu(playerMenuUtility).open();
                }
                case CHESTPLATE -> { // swapping chestplates
                    playerInventory.setChestplate(clickedItem);
                    playerInventory.setItem(event.getSlot(), chestplate);
                    Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, chestplate, clickedItem));
                    new EquipmentMenu(playerMenuUtility).open();
                }
                case LEGGINGS -> { // swapping leggings
                    playerInventory.setLeggings(clickedItem);
                    playerInventory.setItem(event.getSlot(), leggings);
                    Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, leggings, clickedItem));
                    new EquipmentMenu(playerMenuUtility).open();
                }
                case BOOTS -> { // swapping boots
                    playerInventory.setBoots(clickedItem);
                    playerInventory.setItem(event.getSlot(), boots);
                    Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, boots, clickedItem));
                    new EquipmentMenu(playerMenuUtility).open();
                }
                case SHIELD, QUIVER -> { // swapping offHand
                    boolean changedOffhand = true;

                    if (ItemSystem.isItemType(clickedItem, ItemType.SHIELD) && clickedItem.isSimilar(mainHand)) {
                        changedOffhand = false;
                    }

                    if (changedOffhand) {
                        Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, offHand, clickedItem));
                    }

                    playerInventory.setItemInOffHand(clickedItem);
                    playerInventory.setItem(event.getSlot(), offHand);
                    new EquipmentMenu(playerMenuUtility).open();
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
        inventory.setItem(11, nothingItemCheck(helmet));
        updateNothingItem(2);
        inventory.setItem(20, nothingItemCheck(chestplate));
        updateNothingItem(3);
        inventory.setItem(29, nothingItemCheck(leggings));
        updateNothingItem(4);
        inventory.setItem(38, nothingItemCheck(boots));
        updateNothingItem(5);
        inventory.setItem(21, nothingItemCheck(mainHand));
        updateNothingItem(6);
        inventory.setItem(30, nothingItemCheck(offHand));

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
        } else if (situation == 6) { // offHand
            lore.add("§7§oYou can move your offHand item");
            lore.add("§7§oin and out of this slot, ya know.");
        }

        meta.setLore(lore);
        nothingItem.setItemMeta(meta);
    }

    public HashMap<ItemStat, Double> getAllDefensesOfPlayerArmor() {
        HashMap<ItemStat, Double> helmetStats = ItemSystem.getAllStats(helmet);
        HashMap<ItemStat, Double> chestplateStats = ItemSystem.getAllStats(chestplate);
        HashMap<ItemStat, Double> leggingsStats = ItemSystem.getAllStats(leggings);
        HashMap<ItemStat, Double> bootsStats = ItemSystem.getAllStats(boots);
        HashMap<ItemStat, Double> offhandStats = ItemSystem.getAllStats(offHand);
        HashMap<ItemStat, Double> total = helmetStats;

        chestplateStats.forEach((key, value) -> total.merge(key, value, Double::sum));
        leggingsStats.forEach((key, value) -> total.merge(key, value, Double::sum));
        bootsStats.forEach((key, value) -> total.merge(key, value, Double::sum));
        offhandStats.forEach((key, value) -> total.merge(key, value, Double::sum));

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

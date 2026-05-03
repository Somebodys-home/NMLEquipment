package io.github.NoOne.nMLEquipment;

import io.github.NoOne.menuSystem.Menu;
import io.github.NoOne.menuSystem.MenuSystem;
import io.github.NoOne.menuSystem.PlayerMenuUtility;
import io.github.NoOne.nMLEquipment.events.EquipmentChangeEvent;
import io.github.NoOne.nMLItems.ItemCreator;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EquipmentMenu extends Menu {
    private NMLEquipment nmlEquipment;
    private ItemSystem itemSystem;
    private Player player;
    private PlayerInventory playerInventory;
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack mainHand;
    private ItemStack offHand;

    public EquipmentMenu(NMLEquipment nmlEquipment, PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);

        this.nmlEquipment = nmlEquipment;
        itemSystem = nmlEquipment.getItemSystem();
        player = playerMenuUtility.getOwner();
        playerInventory = player.getInventory();
        helmet = playerInventory.getHelmet();
        chestplate = playerInventory.getChestplate();
        leggings = playerInventory.getLeggings();
        boots = playerInventory.getBoots();
        mainHand = playerInventory.getItemInMainHand();
        offHand = playerInventory.getItemInOffHand();
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
        if (event.isCancelled()) return;

        event.setCancelled(true);

        switch (event.getSlot()) {
            case 11 -> { // take off helmet
                playerInventory.addItem(helmet);
                playerInventory.setHelmet(null);
                Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, helmet, null));
                new EquipmentMenu(nmlEquipment, playerMenuUtility).open();
            }
            case 20 -> { // take off chestplate
                playerInventory.addItem(chestplate);
                playerInventory.setChestplate(null);
                Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, chestplate, null));
                new EquipmentMenu(nmlEquipment, playerMenuUtility).open();
            }
            case 29 -> { // take off leggings
                playerInventory.addItem(leggings);
                playerInventory.setLeggings(null);
                Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, leggings, null));
                new EquipmentMenu(nmlEquipment, playerMenuUtility).open();
            }
            case 38 -> { // take off boots
                playerInventory.addItem(boots);
                playerInventory.setBoots(null);
                Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, boots, null));
                new EquipmentMenu(nmlEquipment, playerMenuUtility).open();
            }
            case 30 -> { // take off offhand
                if (!itemSystem.isItemType(offHand, ItemType.GLOVE)) {
                    playerInventory.addItem(offHand);
                    playerInventory.setItemInOffHand(null);
                    Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, offHand, null));
                    new EquipmentMenu(nmlEquipment, playerMenuUtility).open();
                }
            }
            case 53 -> playerInventory.close(); // exit
        }
    }

    @Override
    public void handlePlayerMenu(InventoryClickEvent event) {
        if (event.isCancelled()) return;

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();

        if (itemSystem.isItemUsable(clickedItem, player)) {
            switch (itemSystem.getItemType(clickedItem)) {
                case HELMET -> { // swapping helmets
                    playerInventory.setHelmet(clickedItem);
                    playerInventory.setItem(event.getSlot(), helmet);
                    Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, helmet, clickedItem));
                    Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> new EquipmentMenu(nmlEquipment, playerMenuUtility).open(), 1L);
                }
                case CHESTPLATE -> { // swapping chestplates
                    playerInventory.setChestplate(clickedItem);
                    playerInventory.setItem(event.getSlot(), chestplate);
                    Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, chestplate, clickedItem));
                    Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> new EquipmentMenu(nmlEquipment, playerMenuUtility).open(), 1L);
                }
                case LEGGINGS -> { // swapping leggings
                    playerInventory.setLeggings(clickedItem);
                    playerInventory.setItem(event.getSlot(), leggings);
                    Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, leggings, clickedItem));
                    Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> new EquipmentMenu(nmlEquipment, playerMenuUtility).open(), 1L);
                }
                case BOOTS -> { // swapping boots
                    playerInventory.setBoots(clickedItem);
                    playerInventory.setItem(event.getSlot(), boots);
                    Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, boots, clickedItem));
                    Bukkit.getScheduler().runTaskLater(nmlEquipment, () -> new EquipmentMenu(nmlEquipment, playerMenuUtility).open(), 1L);
                }
                case SHIELD, QUIVER -> { // swapping offhand
                    boolean changedOffhand = true;

                    if (itemSystem.isItemType(clickedItem, ItemType.SHIELD) && clickedItem.isSimilar(mainHand)) {
                        changedOffhand = false;
                    }

                    if (changedOffhand) {
                        Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(player, offHand, clickedItem));
                    }

                    playerInventory.setItemInOffHand(clickedItem);
                    playerInventory.setItem(event.getSlot(), offHand);
                    new EquipmentMenu(nmlEquipment, playerMenuUtility).open();
                }
            }
        } else {
            NMLEquipment.sendUnusableItemWarning(player);
        }
    }

    @Override
    public void setMenuItems() {
        ItemStack statsItem = setStatsItem();

        inventory.setItem(11, nothingItemCheck(helmet, 1));
        inventory.setItem(20, nothingItemCheck(chestplate, 2));
        inventory.setItem(29, nothingItemCheck(leggings, 3));
        inventory.setItem(38, nothingItemCheck(boots, 4));
        inventory.setItem(21, nothingItemCheck(mainHand, 5));
        inventory.setItem(30, nothingItemCheck(offHand, 6));
        inventory.setItem(14, statsItem);
        inventory.setItem(15, statsItem);
        inventory.setItem(23, statsItem);
        inventory.setItem(24, statsItem);
        inventory.setItem(32, statsItem);
        inventory.setItem(33, statsItem);
        inventory.setItem(41, statsItem);
        inventory.setItem(42, statsItem);
        inventory.setItem(53, ItemCreator.createItem(Material.BARRIER, 1, "§cExit", List.of()));
    }

    public ItemStack nothingItemCheck(ItemStack itemStack, int equipmentSlot) {
        List<String> lore = new ArrayList<>(List.of(""));

        if (equipmentSlot == 1) { // helmet
            lore.add("§7§oYou can move helmets in");
            lore.add("§7§oand out of this slot, ya know.");
        } else if (equipmentSlot == 2) { // chestplate
            lore.add("§7§oYou can move chestplates in");
            lore.add("§7§oand out of this slot, ya know.");
        } else if (equipmentSlot == 3) { // leggings
            lore.add("§7§oYou can move leggings in");
            lore.add("§7§oand out of this slot, ya know.");
        } else if (equipmentSlot == 4) { // boots
            lore.add("§7§oYou can move boots in");
            lore.add("§7§oand out of this slot, ya know.");
        } else if (equipmentSlot == 5) { // mainhand
            lore.add("§7§oOk you can't actually move your");
            lore.add("§7§omainhand item from here.");
        } else if (equipmentSlot == 6) { // offHand
            lore.add("§7§oYou can move your offHand item");
            lore.add("§7§oin and out of this slot, ya know.");
        }
        
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return ItemCreator.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "§8...Nothing?", lore);
        } else {
            return itemStack;
        }
    }

    public ItemStack setStatsItem() {
        ItemStack statsItem = ItemCreator.createItem(Material.PURPLE_STAINED_GLASS_PANE, 1, "§d§nYour Drip's Total Stats:", List.of());
        HashMap<ItemStat, Double> total = getAllDefensesOfPlayerArmor();

        if (total.isEmpty()) {
            ItemMeta statsMeta = statsItem.getItemMeta();
            List<String> statsLore = List.of(
                    "",
                    "§7§oCongrats! You've either reached absolute",
                    "§7§oequilibrium or you're completely nude."
            );

            statsMeta.setLore(statsLore);
            statsItem.setItemMeta(statsMeta);
        } else {
            for (Map.Entry<ItemStat, Double> entry : total.entrySet()) {
                itemSystem.setStat(statsItem, entry.getKey(), entry.getValue());
            }

            itemSystem.updateLoreWithStats(statsItem);
        }

        return statsItem;
    }

    private HashMap<ItemStat, Double> getAllDefensesOfPlayerArmor() {
        HashMap<ItemStat, Double> helmetStats = itemSystem.getAllStats(helmet);
        HashMap<ItemStat, Double> chestplateStats = itemSystem.getAllStats(chestplate);
        HashMap<ItemStat, Double> leggingsStats = itemSystem.getAllStats(leggings);
        HashMap<ItemStat, Double> bootsStats = itemSystem.getAllStats(boots);
        HashMap<ItemStat, Double> offhandStats = itemSystem.getAllStats(offHand);
        HashMap<ItemStat, Double> total = helmetStats;

        chestplateStats.forEach((key, value) -> total.merge(key, value, Double::sum));
        leggingsStats.forEach((key, value) -> total.merge(key, value, Double::sum));
        bootsStats.forEach((key, value) -> total.merge(key, value, Double::sum));
        offhandStats.forEach((key, value) -> total.merge(key, value, Double::sum));

        return total;
    }
}

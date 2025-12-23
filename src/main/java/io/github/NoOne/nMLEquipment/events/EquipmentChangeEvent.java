package io.github.NoOne.nMLEquipment.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EquipmentChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final ItemStack doffedEquipment;
    private final ItemStack donnedEquipment;

    public EquipmentChangeEvent(@NotNull Player player, ItemStack doffedEquipment, ItemStack donnedEquipment) {
        this.player = player;
        this.doffedEquipment = doffedEquipment;
        this.donnedEquipment = donnedEquipment;
    }

    @Override
    public HandlerList getHandlers() { return handlers; }

    public static HandlerList getHandlerList() { return handlers; } // deleting this breaks things, apparently

    public Player getPlayer() { return player; }

    public ItemStack getDoffedEquipment() { return doffedEquipment; }

    public ItemStack getDonnedEquipment() { return donnedEquipment; }
}
package io.github.NoOne.nMLEquipment.saveMainHand;

import io.github.NoOne.nMLEquipment.NMLEquipment;
import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class SaveMainHandListener implements Listener {
    private SaveMainHandManager saveMainHandManager;

    public SaveMainHandListener(NMLEquipment nmlEquipment) {
        saveMainHandManager = nmlEquipment.getSaveMainHandManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        HashMap<UUID, ItemStack> mainHands = saveMainHandManager.getMainHands();

        if (!mainHands.containsKey(player.getUniqueId())) {
            saveMainHandManager.addPlayer(player);
        }
    }
}

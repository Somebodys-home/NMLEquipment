package io.github.NoOne.nMLEquipment;

import io.github.NoOne.menuSystem.MenuSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EquipmentCommand implements CommandExecutor {
    private NMLEquipment nMLEquipment;

    public EquipmentCommand(NMLEquipment nMLEquipment) {
        this.nMLEquipment = nMLEquipment;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player player) {
            new EquipmentMenu(nMLEquipment, MenuSystem.getPlayerMenuUtility(player)).open();
        }

        return true;
    }
}

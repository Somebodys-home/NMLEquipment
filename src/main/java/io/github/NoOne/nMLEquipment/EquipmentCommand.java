package io.github.NoOne.nMLEquipment;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EquipmentCommand implements CommandExecutor {
    private NMLEquipment nmlEquipment;

    public EquipmentCommand(NMLEquipment nmlEquipment) {
        this.nmlEquipment = nmlEquipment;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player player) {
            new EquipmentMenu(nmlEquipment, player).open();
        }

        return true;
    }
}

package com.balzikz.smartpet.command;

import com.balzikz.smartpet.manager.PetManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PetCommand implements CommandExecutor {

    private final PetManager petManager;

    public PetCommand(PetManager petManager) {
        this.petManager = petManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("smartpet.use")) {
            sender.sendMessage(ChatColor.RED + "У вас нет прав для использования этой команды.");
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Игрок " + args[1] + " не найден.");
                return true;
            }
            petManager.removePet(target);
            sender.sendMessage(ChatColor.GREEN + "Питомец игрока " + target.getName() + " был удален.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Использование: /pet <игрок> <Cat/Allay/Rabbit> или /pet remove <игрок>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Игрок " + args[0] + " не найден.");
            return true;
        }

        EntityType petType;
        try {
            petType = EntityType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Неверный тип питомца. Доступные: Cat, Allay, Rabbit.");
            return true;
        }

        if (petType != EntityType.CAT && petType != EntityType.ALLAY && petType != EntityType.RABBIT) {
            sender.sendMessage(ChatColor.RED + "Этот тип моба не может быть питомцем. Доступные: Cat, Allay, Rabbit.");
            return true;
        }

        petManager.createPet(target, petType);
        sender.sendMessage(ChatColor.GREEN + "Вы создали питомца (" + petType.name() + ") для игрока " + target.getName());

        return true;
    }
}

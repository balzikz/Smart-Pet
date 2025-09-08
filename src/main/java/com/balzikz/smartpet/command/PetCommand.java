package com.balzikz.smartpet.command;

import com.balzikz.smartpet.manager.PetManager;
import com.balzikz.smartpet.service.GeminiService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class PetCommand implements CommandExecutor {

    private final PetManager petManager;
    private final GeminiService geminiService;

    public PetCommand(PetManager petManager, GeminiService geminiService) {
        this.petManager = petManager;
        this.geminiService = geminiService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("smartpet.use")) {
            player.sendMessage(ChatColor.RED + "У вас нет прав для использования этой команды.");
            return true;
        }

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "spawn":
                handleSpawn(player, Arrays.copyOfRange(args, 1, args.length));
                break;
            case "remove":
                handleRemove(player, Arrays.copyOfRange(args, 1, args.length));
                break;
            case "talk":
                handleTalk(player, Arrays.copyOfRange(args, 1, args.length));
                break;
            default:
                sendUsage(player);
                break;
        }

        return true;
    }

    private void handleSpawn(Player sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Использование: /pet spawn <игрок> <Cat/Allay/Rabbit>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Игрок " + args[0] + " не найден.");
            return;
        }

        EntityType petType;
        try {
            petType = EntityType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Неверный тип питомца. Доступные: Cat, Allay, Rabbit.");
            return;
        }

        if (petType != EntityType.CAT && petType != EntityType.ALLAY && petType != EntityType.RABBIT) {
            sender.sendMessage(ChatColor.RED + "Этот тип моба не может быть питомцем.");
            return;
        }

        petManager.createPet(target, petType);
        sender.sendMessage(ChatColor.GREEN + "Вы создали питомца (" + petType.name() + ") для " + target.getName());
    }

    private void handleRemove(Player sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Использование: /pet remove <игрок>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Игрок " + args[0] + " не найден.");
            return;
        }

        petManager.removePet(target);
        sender.sendMessage(ChatColor.GREEN + "Питомец игрока " + target.getName() + " был удален.");
    }

    private void handleTalk(Player player, String[] args) {
        if (!petManager.hasPet(player)) {
            player.sendMessage(ChatColor.RED + "У вас нет питомца, чтобы с ним говорить!");
            return;
        }
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Использование: /pet talk <сообщение...>");
            return;
        }

        String message = String.join(" ", args);
        player.sendMessage(ChatColor.GRAY + "Вы говорите питомцу: " + message);

        geminiService.getResponse(message).thenAccept(response -> {
            petManager.makePetTalk(player, response);
        }).exceptionally(ex -> {
            player.sendMessage(ChatColor.DARK_RED + "Произошла ошибка при общении с питомцем.");
            return null;
        });
    }

    private void sendUsage(Player player) {
        player.sendMessage(ChatColor.GOLD + "--- Smart-Pet Помощь ---");
        player.sendMessage(ChatColor.YELLOW + "/pet spawn <игрок> <тип> - Создать питомца");
        player.sendMessage(ChatColor.YELLOW + "/pet remove <игрок> - Удалить питомца");
        player.sendMessage(ChatColor.YELLOW + "/pet talk <сообщение...> - Поговорить с питомцем");
    }
}

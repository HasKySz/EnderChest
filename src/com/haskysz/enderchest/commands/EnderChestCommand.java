package com.haskysz.enderchest.commands;

import com.haskysz.enderchest.EnderChestPlugin;
import com.haskysz.enderchest.objects.EnderChest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Objects;

public class EnderChestCommand implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return false;

        Player player = (Player) commandSender;

        if (strings.length == 0) {

            EnderChest enderChest = EnderChestPlugin.getEnderChestManager().find(player.getName());

            if (enderChest == null) {

                player.sendMessage("§cVocê não está em nosso banco de dados, relogue e tente novamente.");

                return false;

            }

            if (enderChest.getDelay() > System.currentTimeMillis()) {

                player.sendMessage("§cAguarde alguns segundos para abrir o seu Baú do Fim novamente.");

                return false;

            }

            if (enderChest.getEnderChest().getSize() != EnderChestPlugin.getEnderChestManager().getEnderChestRows(player)) {

                Inventory oldEnderChest = enderChest.getEnderChest();

                Inventory newEnderChest = EnderChestPlugin.getEnderChestManager().createEnderChest(player);

                Arrays.stream(oldEnderChest.getContents())
                        .filter(Objects::nonNull)
                        .forEach(content -> {

                            if (newEnderChest.firstEmpty() != -1)
                                newEnderChest.addItem(content);

                            else
                                player.getWorld().dropItemNaturally(player.getLocation(), content);

                        });

                enderChest.setEnderChest(newEnderChest);

            }

            player.openInventory(enderChest.getEnderChest());

            return true;

        }

        if (strings.length == 1) {

            if (!commandSender.hasPermission("syrenmc.gerente")) {

                commandSender.sendMessage("§cVocê precisa do grupo Gerente ou superior para executar este comando.");

                return false;

            }

            EnderChest enderChest = EnderChestPlugin.getEnderChestManager().find(strings[0]);
            Player target = Bukkit.getPlayer(strings[0]);

            if (target == null) {

                Bukkit.getScheduler().runTaskAsynchronously(EnderChestPlugin.getInstance(), () -> {

                    if (!EnderChestPlugin.getEnderChestRepository().enderChestExists(strings[0])) {

                        commandSender.sendMessage("§cEste jogador não existe em nosso banco de dados.");

                        return;

                    }

                    player.openInventory(EnderChestPlugin.getEnderChestRepository().getEnderChest(strings[0]));

                });

                return false;

            }

            player.openInventory(enderChest.getEnderChest());

            return true;

        }

        return false;

    }
}

package com.haskysz.enderchest.managers;

import com.haskysz.enderchest.EnderChestPlugin;
import com.haskysz.enderchest.objects.EnderChest;
import com.haskysz.enderchest.utils.InventorySerialize;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class EnderChestManager {

    @Getter private List<EnderChest> enderChestList;
    private EnderChestPlugin plugin;

    public EnderChestManager(EnderChestPlugin plugin, List<EnderChest> enderChestList) {

        this.plugin = plugin;
        this.enderChestList = enderChestList;

    }

    public void load(Player player) {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                enderChestList.add(EnderChest
                        .builder()
                        .owner(player.getName())
                        .enderChest(EnderChestPlugin.getEnderChestRepository().enderChestExists(player.getName()) ?
                                EnderChestPlugin.getEnderChestRepository().getEnderChest(player.getName()) : createEnderChest(player))
                        .delay(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10))
                        .build()));

    }

    public void unload(Player player) {

        EnderChest enderChest = find(player.getName());

        enderChestList.remove(enderChest);

        if (enderChest != null)
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                    EnderChestPlugin.getEnderChestRepository().insertOrUpate(player.getName(), InventorySerialize.toJsonObject(enderChest.getEnderChest()).toString()));
    }

    public EnderChest find(String owner) {

        return enderChestList.stream()
                .filter(enderChest -> enderChest.getOwner().equalsIgnoreCase(owner))
                .findFirst()
                .orElse(null);

    }

    public int getEnderChestRows(Player player) {

        if (player.hasPermission("syrenmc.orion")) return 9*6;

        if (player.hasPermission("syrenmc.sirius")) return 9*5;

        if (player.hasPermission("syrenmc.sirius")) return 9*4;

        return 9*3;

    }

    public Inventory createEnderChest(Player player) {

        return Bukkit.createInventory(null, getEnderChestRows(player), "Baú do Fim de " + player.getName());

    }

}

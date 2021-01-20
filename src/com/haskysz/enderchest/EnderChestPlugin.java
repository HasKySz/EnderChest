package com.haskysz.enderchest;

import com.google.common.collect.Lists;
import com.haskysz.enderchest.commands.EnderChestCommand;
import com.haskysz.enderchest.listenres.EnderChestListeners;
import com.haskysz.enderchest.managers.EnderChestManager;
import com.haskysz.enderchest.repository.EnderChestRepository;
import com.haskysz.enderchest.utils.InventorySerialize;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderChestPlugin extends JavaPlugin {

    @Getter private static EnderChestPlugin instance;
    @Getter private static EnderChestRepository enderChestRepository;
    @Getter private static EnderChestManager enderChestManager;

    public void onEnable() {

        saveDefaultConfig();

        instance = this;

        enderChestRepository = new EnderChestRepository(
                getConfig().getString("MySQL.url") + "?autoReconnect=true",
                getConfig().getString("MySQL.username"),
                getConfig().getString("MySQL.password")
        );

        enderChestManager = new EnderChestManager(
                this,
                Lists.newArrayList()
        );

        getCommand("enderchest").setExecutor(new EnderChestCommand());
        Bukkit.getPluginManager().registerEvents(new EnderChestListeners(), this);

    }

    public void onDisable() {

        Bukkit.getScheduler().runTaskAsynchronously(this, () ->
                enderChestManager.getEnderChestList().forEach(enderChest ->
                        enderChestRepository.insertOrUpate(enderChest.getOwner(), InventorySerialize.toJsonObject(enderChest.getEnderChest()).toString())));

    }
}

package com.haskysz.enderchest.repository;

import com.haskysz.enderchest.utils.InventorySerialize;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.inventory.Inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnderChestRepository {

    public HikariDataSource dataSource;
    private Connection connection;

    public EnderChestRepository(String url, String username, String password) {

        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(hikariConfig);

        try { connection = dataSource.getConnection(); }

        catch (SQLException ex) { ex.printStackTrace(); }

        createTables();

    }

    private void createTables() {

        try  {

            PreparedStatement enderChestStatement = connection.prepareStatement("create table if not exists EnderChests (" +
                    "id int auto_increment primary key," +
                    "owner varchar(16) not null," +
                    "enderChest longtext not null," +
                    "constraint EnderChests_uindex unique (id)," +
                    "constraint EnderChests_name_uindex unique (owner)" +
                    ")" +
                    "engine=InnoDB;");

            enderChestStatement.execute();
            enderChestStatement.close();

        }

        catch (SQLException ex) { ex.printStackTrace(); }

    }

    public void insertOrUpate(String owner, String enderChest) {

        try {

            String QUERY = "insert into EnderChests (`owner`, `enderChest`) values(?,?) on duplicate key update `enderChest`=values(enderChest);";
            PreparedStatement statement = connection.prepareStatement(QUERY);

            statement.setString(1, owner);
            statement.setString(2, enderChest);
            statement.executeUpdate();

        }

        catch (SQLException ex) { ex.printStackTrace(); }

    }

    public boolean enderChestExists(String owner) {

        try {

            String QUERY = "select `id` from EnderChests where `owner`=?";
            PreparedStatement statement = connection.prepareStatement(QUERY);

            statement.setString(1, owner);

            if (statement.executeQuery().next()) return true;

        }

        catch (SQLException ex) { ex.printStackTrace(); }

        return false;

    }

    public Inventory getEnderChest(String owner) {

        try {

            String QUERY = "select `enderChest` from `EnderChests` WHERE `owner`=?";
            PreparedStatement statement = connection.prepareStatement(QUERY);

            statement.setString(1, owner);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next())
                return InventorySerialize.toInventory(resultSet.getString("enderChest"));

        }

        catch (SQLException ex) { ex.printStackTrace(); }

        return null;

    }
}

package com.haskysz.enderchest.objects;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;

@Builder
public class EnderChest {

    @Getter private String owner;
    @Getter @Setter private Inventory enderChest;
    @Getter @Setter private long delay;

}

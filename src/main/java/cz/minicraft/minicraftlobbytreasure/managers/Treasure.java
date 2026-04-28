package cz.minicraft.minicraftlobbytreasure.managers;

import org.bukkit.Location;

public record Treasure(int id, Location location, TreasureType type) {
}

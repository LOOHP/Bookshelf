package com.loohp.bookshelf.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class EnchantmentTableUtils {
	
	public static List<Block> getBookshelves(Block enchantmentTableBlock) {
		List<Block> blocks = new ArrayList<Block>();
		List<Block> returnlist = new ArrayList<Block>();
		Location center = enchantmentTableBlock.getLocation();
		
		for (int y = 0; y <= 1; y++) {
			for (int x = -2; x <= 2; x++) {
				Location loc = new Location(center.getWorld(), center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + 2);
				if (loc.getBlock().getType().equals(Material.BOOKSHELF)) {
					blocks.add(loc.getBlock());
				}
			}
			for (int x = -2; x <= 2; x++) {
				Location loc = new Location(center.getWorld(), center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() - 2);
				if (loc.getBlock().getType().equals(Material.BOOKSHELF)) {
					blocks.add(loc.getBlock());
				}
			}
			for (int z = -2; z <= 2; z++) {
				Location loc = new Location(center.getWorld(), center.getBlockX() + 2, center.getBlockY() + y, center.getBlockZ() + z);
				if (loc.getBlock().getType().equals(Material.BOOKSHELF)) {
					blocks.add(loc.getBlock());
				}
			}
			for (int z = -2; z <= 2; z++) {
				Location loc = new Location(center.getWorld(), center.getBlockX() - 2, center.getBlockY() + y, center.getBlockZ() + z);
				if (loc.getBlock().getType().equals(Material.BOOKSHELF)) {
					blocks.add(loc.getBlock());
				}
			}
		}
		
		for (Block block : blocks) {
			if (isAirBetween(block.getLocation().clone().add(0.5, 0.5, 0.5), center.clone().add(0.5, 0.5, 0.5)) == true) {
				returnlist.add(block);
			}
		}
		
		return returnlist;
	}
	
	public static boolean isAirBetween(Location loc1, Location loc2) {
		Vector vector = genVec(loc1, loc2).multiply(0.5);
		Location loc = loc1.clone();
		while (true) {
			loc.add(vector);
			if (loc.getBlock().equals(loc1.getBlock())) {
				continue;
			}
			if (!loc.getBlock().getType().equals(Material.AIR)) {
				if (loc.getBlock().equals(loc2.getBlock())) {
					break;
				} else {
					return false;
				}
			}
		}
		return true;
	}
	
	public static Vector genVec(Location a, Location b) {
        double dX = a.getX() - b.getX();
        double dY = a.getY() - b.getY();
        double dZ = a.getZ() - b.getZ();
        double yaw = Math.atan2(dZ, dX);
        double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);

        Vector vector = new Vector(x, z, y);
        vector.normalize();

        return vector;
    }

}

package com.loohp.bookshelf.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.loohp.bookshelf.objectholders.BlockPosition;

public class BookshelfUtils {
	
	public static String locKey(Location loc) {
		return loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
	}
	
	public static String posKey(BlockPosition pos) {
		return pos.getX() + "_" + pos.getY() + "_" + pos.getZ();
	}
	
	public static Location keyLoc(World world, String key) {
		String[] breakdown = key.split("_");
		int x = Integer.parseInt(breakdown[0]);
		int y = Integer.parseInt(breakdown[1]);
		int z = Integer.parseInt(breakdown[2]);
		return new Location(world, x, y, z);
	}
	
	public static BlockPosition keyPos(World world, String key) {
		String[] breakdown = key.split("_");
		int x = Integer.parseInt(breakdown[0]);
		int y = Integer.parseInt(breakdown[1]);
		int z = Integer.parseInt(breakdown[2]);
		return new BlockPosition(world, x, y, z);
	}
	
	public static String toBase64(Inventory inventory) throws IllegalStateException {
	    try {
	    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
	            
	        // Write the size of the inventory
	        dataOutput.writeInt(inventory.getSize());
	            
	        // Save every element in the list
	        for (int i = 0; i < inventory.getSize(); i++) {
	            dataOutput.writeObject(inventory.getItem(i));
	        }
	            
	        // Serialize that array
	        dataOutput.close();
	        return Base64Coder.encodeLines(outputStream.toByteArray());
	    } catch (Exception e) {
	        throw new IllegalStateException("Unable to save item stacks.", e);
        }
	}
	    
	public static Inventory fromBase64(String data, String title, InventoryHolder holder) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
	        Inventory inventory = null;
	        if (title.equals("")) {
	       	 	inventory = Bukkit.getServer().createInventory(holder, dataInput.readInt());
	        } else {
	       	 	inventory = Bukkit.getServer().createInventory(holder, dataInput.readInt(), title);
	        }
	   
	        // Read the serialized inventory
	        for (int i = 0; i < inventory.getSize(); i++) {
	            inventory.setItem(i, (ItemStack) dataInput.readObject());
	        }
	            
	        dataInput.close();
	        return inventory;
	    } catch (ClassNotFoundException e) {
	        throw new IOException("Unable to decode class type.", e);
	    }
	}
}

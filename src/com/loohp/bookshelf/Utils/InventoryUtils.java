package com.loohp.bookshelf.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
	public static boolean hasAvaliableSlot(Inventory inv, Material material){
	    for (ItemStack item: inv.getContents()) {
	        if (item == null) {
	        	return true;
	        }
	        if (item.getType().equals(material)) {
	        	if (item.getAmount() < item.getType().getMaxStackSize()) {
	        		return true;
	        	}
	        }
	    }
	    return false;
	}
}

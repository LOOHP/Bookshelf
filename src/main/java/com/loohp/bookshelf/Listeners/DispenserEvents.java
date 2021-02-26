package com.loohp.bookshelf.Listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.BookshelfManager;
import com.loohp.bookshelf.Utils.BlockLockerUtils;
import com.loohp.bookshelf.Utils.BookshelfUtils;
import com.loohp.bookshelf.Utils.DropperUtils;
import com.loohp.bookshelf.Utils.InventoryUtils;
import com.loohp.bookshelf.Utils.LWCUtils;

public class DispenserEvents implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDropper(BlockDispenseEvent event) {
		if (!Bookshelf.enableDropperSupport) {
			return;
		}
		if (event.isCancelled()) {
			return;
		}
		if (!event.getBlock().getType().equals(Material.DROPPER)) {
			return;
		}
		Block relative = DropperUtils.getDropperRelative(event.getBlock());
		if (!relative.getType().equals(Material.BOOKSHELF)) {
			return;
		}
		String key = BookshelfUtils.locKey(relative.getLocation());
		if (!Bookshelf.keyToContentMapping.containsKey(key)) {
			if (!BookshelfManager.contains(key)) {
				String bsTitle = Bookshelf.title;
				Bookshelf.addBookshelfToMapping(key , Bukkit.createInventory(null, (int) (Bookshelf.bookShelfRows * 9), bsTitle));
				BookshelfManager.setTitle(key, bsTitle);
				BookshelfUtils.saveBookShelf(key);
			} else {
				BookshelfUtils.loadBookShelf(key);
			}
		}
		if (Bookshelf.lwcHook) {
			if (!LWCUtils.checkHopperFlagIn(relative)) {
				event.setCancelled(true);
				return;
			}
		}
		if (Bookshelf.blockLockerHook) {
			if (!BlockLockerUtils.canRedstone(relative)) {
				event.setCancelled(true);
				return;
			}
		}
		event.setCancelled(true);
		Inventory bookshelf = Bookshelf.keyToContentMapping.get(key);
		org.bukkit.block.Dropper d = (org.bukkit.block.Dropper) event.getBlock().getState();
		Inventory dropper = d.getInventory();
		List<ItemStack> newList = new ArrayList<ItemStack>();
		newList.addAll(Arrays.asList(dropper.getContents()));
		newList.add(event.getItem());
		Collections.shuffle(newList);
		for (ItemStack each : newList) {
			if (each == null) {
				continue;
			}
			if (Bookshelf.useWhitelist) {
				if (!Bookshelf.whitelist.contains(each.getType().toString().toUpperCase())) {
					continue;
				}
			}
			if (!InventoryUtils.stillHaveSpace(bookshelf, each.getType())) {
				continue;
			}
			ItemStack additem = each.clone();
			additem.setAmount(1);
			bookshelf.addItem(additem);
			boolean removed = false;
			for (int i = 0; i < dropper.getSize(); i = i + 1) {
				ItemStack removeitem = dropper.getItem(i);
            	if (removeitem == null) {
            		continue;
            	}
            	if (removeitem.equals(each)) {
            		removeitem.setAmount(removeitem.getAmount() - 1);
            		dropper.setItem(i, removeitem);
            		removed = true;
            		break;
            	}
			}
			if (!removed) {
				new BukkitRunnable() {
					public void run() {
						for (int i = 0; i < dropper.getSize(); i = i + 1) {
							ItemStack removeitem = dropper.getItem(i);
			            	if (removeitem == null) {
			            		continue;
			            	}
			            	if (removeitem.equals(each)) {
			            		removeitem.setAmount(removeitem.getAmount() - 1);
			            		dropper.setItem(i, removeitem);
			            		break;
			            	}
						}
					}
				}.runTaskLater(Bookshelf.plugin, 1);
			}
			Bookshelf.bookshelfSavePending.add(key);
			if (!Bookshelf.version.isLegacy()) {
				event.getBlock().getWorld().playSound(event.getBlock().getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
			}
			return;
		}
		if (!Bookshelf.version.isLegacy()) {
			event.getBlock().getWorld().playSound(event.getBlock().getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1, 1);
		}
	}

}

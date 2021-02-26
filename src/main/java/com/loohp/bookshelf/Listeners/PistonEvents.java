package com.loohp.bookshelf.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.inventory.Inventory;

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.BookshelfManager;
import com.loohp.bookshelf.Utils.BookshelfUtils;
import com.loohp.bookshelf.Utils.CustomListUtils;

public class PistonEvents implements Listener {
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPistonExtend(BlockPistonExtendEvent event) {
		if (event.isCancelled()) {
			return;
		}
		List<Block> bookshelves = new ArrayList<Block>();
		for (Block block : event.getBlocks()) {
			if (block.getType().equals(Material.BOOKSHELF)) {
				String key = BookshelfUtils.locKey(block.getLocation());
				if (!Bookshelf.keyToContentMapping.containsKey(key)) {
					if (BookshelfManager.contains("BookShelfData." + key)) {
						BookshelfUtils.loadBookShelf(key);
						bookshelves.add(block);
					}
				} else {
					bookshelves.add(block);
				}
			}
		}
		
		if (bookshelves.isEmpty()) {
			return;
		}
		
		BlockFace dir = event.getDirection();
		for (Block bookshelf : CustomListUtils.reverse(bookshelves)) {
			String key = BookshelfUtils.locKey(bookshelf.getLocation());
			Inventory inv = Bookshelf.keyToContentMapping.get(key);
			Location newLoc = bookshelf.getRelative(dir).getLocation().clone();
			String newKey = BookshelfUtils.locKey(newLoc);
			String bsTitle = Bookshelf.title;
			if (BookshelfManager.getTitle(key) != null) {
				bsTitle = BookshelfManager.getTitle(key);
			}
			BookshelfUtils.safeRemoveBookself(key);
			
			Bookshelf.addBookshelfToMapping(newKey, inv);
			BookshelfManager.setTitle(newKey, bsTitle);
			
			BookshelfUtils.saveBookShelf(newKey);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPistonRetract(BlockPistonRetractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		List<Block> bookshelves = new ArrayList<Block>();
		for (Block block : event.getBlocks()) {
			if (block.getType().equals(Material.BOOKSHELF)) {
				String key = BookshelfUtils.locKey(block.getLocation());
				if (!Bookshelf.keyToContentMapping.containsKey(key)) {
					if (BookshelfManager.contains(key)) {
						BookshelfUtils.loadBookShelf(key);
						bookshelves.add(block);
					}
				} else {
					bookshelves.add(block);
				}
			}
		}
		
		if (bookshelves.isEmpty()) {
			return;
		}
		
		BlockFace dir = event.getDirection();
		for (Block bookshelf : CustomListUtils.reverse(bookshelves)) {
			String key = BookshelfUtils.locKey(bookshelf.getLocation());
			Inventory inv = Bookshelf.keyToContentMapping.get(key);
			Location newLoc = bookshelf.getRelative(dir).getLocation().clone();
			String newKey = BookshelfUtils.locKey(newLoc);
			String bsTitle = Bookshelf.title;
			if (BookshelfManager.getTitle(key) != null) {
				bsTitle = BookshelfManager.getTitle(key);
			}
			BookshelfUtils.safeRemoveBookself(key);
			
			Bookshelf.addBookshelfToMapping(newKey, inv);
			BookshelfManager.setTitle(newKey, bsTitle);
			
			BookshelfUtils.saveBookShelf(newKey);
		}
	}

}

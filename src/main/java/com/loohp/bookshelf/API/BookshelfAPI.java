package com.loohp.bookshelf.API;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.BookshelfManager;
import com.loohp.bookshelf.Utils.BookshelfUtils;

public class BookshelfAPI {
	
	public static int getBookshelfSize() {
		return Bookshelf.BookShelfRows * 9;
	}
	
	public static String getKeyFromInventory(Inventory inventory) {
		return Bookshelf.contentToKeyMapping.get(inventory);
	}
	
	public static boolean isBookshelf(Block block) {
		if (block.getType().equals(Material.BOOKSHELF)) {
			return true;
		}
		return false;
	}
	
	public static Block getPlayerOpeningBookshelf(Player player) {
		Inventory inventory = player.getOpenInventory().getTopInventory();
		if (inventory == null) {
			return null;
		}
		String key = Bookshelf.contentToKeyMapping.get(inventory);
		if (key != null) {
			Location loc = BookshelfUtils.keyLoc(key);
			return loc.getBlock();
		}
		return null;
	}

	public static Inventory getBookshelfInventory(Location location) {
		String key = BookshelfUtils.locKey(location);
		return Bookshelf.keyToContentMapping.get(key);
	}
	
	public static Inventory getBookshelfInventory(String key) {
		return Bookshelf.keyToContentMapping.get(key);
	}
	
	public static Inventory loadBookshelfFromStorage(Location location) {
		String key = BookshelfUtils.locKey(location);
		String hash = BookshelfManager.getInventoryHash(key);
		if (hash == null) {
			return null;
		}
		Inventory inv = null;
		String bsTitle = BookshelfManager.getTitle(key);
		try {
			inv = BookshelfUtils.fromBase64(hash, bsTitle);
		} catch (IOException e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Bookshelf] Unable to load bookshelf inventory");
			e.printStackTrace();
		}
		Bookshelf.addBookshelfToMapping(key, inv);
		return Bookshelf.keyToContentMapping.get(key);
	}
	
	public static Inventory loadBookshelfFromStorage(String key) {
		String hash = BookshelfManager.getInventoryHash(key);
		if (hash == null) {
			return null;
		}
		Inventory inv = null;
		String bsTitle = BookshelfManager.getTitle(key);
		try {
			inv = BookshelfUtils.fromBase64(hash, bsTitle);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Bookshelf.addBookshelfToMapping(key, inv);
		return Bookshelf.keyToContentMapping.get(key);
	}
	
	public static String getBookshelfTitle(Location location) {
		String key = BookshelfUtils.locKey(location);
		if (BookshelfManager.getTitle(key) != null) {
			return BookshelfManager.getTitle(key);
		}
		return null;
	}
	
	public static String getBookshelfTitle(String key) {
		if (BookshelfManager.getTitle(key) != null) {
			return BookshelfManager.getTitle(key);
		}
		return null;
	}
	
	public static boolean createBookshelf(Location location) {
		String key = BookshelfUtils.locKey(location);
		if (!Bookshelf.keyToContentMapping.containsKey(key)) {
			if (!BookshelfManager.contains(key)) {
				String bsTitle = Bookshelf.Title;
				Bookshelf.addBookshelfToMapping(key , Bukkit.createInventory(null, (int) (Bookshelf.BookShelfRows * 9), bsTitle));
				BookshelfManager.setTitle(key, bsTitle);
				BookshelfUtils.saveBookShelf(key);
				return true;
			}
		}
		return false;
	}
	
	public static boolean createBookshelf(String key) {
		if (!Bookshelf.keyToContentMapping.containsKey(key)) {
			if (!BookshelfManager.contains(key)) {
				String bsTitle = Bookshelf.Title;
				Bookshelf.addBookshelfToMapping(key , Bukkit.createInventory(null, (int) (Bookshelf.BookShelfRows * 9), bsTitle));
				BookshelfManager.setTitle(key, bsTitle);
				BookshelfUtils.saveBookShelf(key);
				return true;
			}
		}
		return false;
	}
	
	public static boolean isBookshelfInMemory(Location location) {
		String key = BookshelfUtils.locKey(location);
		if (BookshelfManager.contains(key)) {
			return true;
		}
		return false;
	}
	
	public static boolean isBookshelfInMemory(String key) {
		if (BookshelfManager.contains(key)) {
			return true;
		}
		return false;
	}
	
	public static boolean isBookshelfLoaded(Location location) {
		String key = BookshelfUtils.locKey(location);
		if (Bookshelf.keyToContentMapping.containsKey(key)) {
			return true;
		}
		return false;
	}
	
	public static boolean isBookshelfLoaded(String key) {
		if (Bookshelf.keyToContentMapping.containsKey(key)) {
			return true;
		}
		return false;
	}
	
	public static String convertLocationToKey(Location location) {
		return BookshelfUtils.locKey(location);
	}
	
	public static Location convertKeyToLocation(String key) {
		return BookshelfUtils.keyLoc(key);
	}
	
	@Deprecated
	public static void deleteBookshelfDataFromStorage(String key) {
		BookshelfManager.removeShelf(key);
	}

}

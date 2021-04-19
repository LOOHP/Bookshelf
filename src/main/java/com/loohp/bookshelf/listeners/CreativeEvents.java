package com.loohp.bookshelf.listeners;

import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.BookshelfManager;
import com.loohp.bookshelf.objectholders.BlockPosition;
import com.loohp.bookshelf.objectholders.BookshelfHolder;
import com.loohp.bookshelf.utils.BookshelfUtils;
import com.loohp.bookshelf.utils.MCVersion;
import com.loohp.bookshelf.utils.MaterialUtils;
import com.loohp.bookshelf.utils.NBTUtils;

public class CreativeEvents implements Listener {
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCreativePickBlock(InventoryCreativeEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event.getClick().equals(ClickType.CREATIVE)) {
			Player player = (Player) event.getWhoClicked();
			ItemStack item = event.getCursor();
			if (item.getType().equals(Material.BOOKSHELF) && player.getGameMode().equals(GameMode.CREATIVE) && player.isSneaking() && player.hasPermission("bookshelf.copynbt")) {
				Block block = null;
				if (Bookshelf.version.isNewerOrEqualTo(MCVersion.V1_14)) {
					block = player.getTargetBlockExact(10, FluidCollisionMode.NEVER);
				} else {
					block = player.getTargetBlock(MaterialUtils.getNonSolidSet(), 10);
				}
				if (block.getType().equals(Material.BOOKSHELF)) {
					BookshelfHolder bookshelf = BookshelfManager.getBookshelfManager(player.getWorld()).getOrCreateBookself(new BlockPosition(block), Bookshelf.title);
					String hash = BookshelfUtils.toBase64(bookshelf.getInventory());
					String title = bookshelf.getTitle();
					item = NBTUtils.set(item, hash, "BookshelfContent");
					item = NBTUtils.set(item, title, "BookshelfTitle");
					event.setCursor(item);
				}
			}
		}
	}

}

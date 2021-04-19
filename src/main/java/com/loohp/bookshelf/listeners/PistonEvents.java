package com.loohp.bookshelf.listeners;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.BookshelfManager;
import com.loohp.bookshelf.objectholders.BlockPosition;
import com.loohp.bookshelf.utils.CustomListUtils;

public class PistonEvents implements Listener {
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPistonExtend(BlockPistonExtendEvent event) {
		if (event.isCancelled()) {
			return;
		}
		BookshelfManager manager = BookshelfManager.getBookshelfManager(event.getBlock().getWorld());
		Map<Block, BlockPosition> position = new LinkedHashMap<>();
		List<Block> order = new ArrayList<>();
		for (Block block : event.getBlocks()) {
			if (block.getType().equals(Material.BOOKSHELF)) {
				position.put(block, manager.getOrCreateBookself(new BlockPosition(block), Bookshelf.title).getPosition());
				order.add(block);
			}
		}
		
		if (order.isEmpty()) {
			return;
		}
		
		BlockFace dir = event.getDirection();
		for (Block block : CustomListUtils.reverse(order)) {
			Location newLoc = block.getRelative(dir).getLocation().clone();
			manager.move(position.get(block), newLoc.getBlockX(), newLoc.getBlockY(), newLoc.getBlockZ());
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPistonRetract(BlockPistonRetractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		BookshelfManager manager = BookshelfManager.getBookshelfManager(event.getBlock().getWorld());
		Map<Block, BlockPosition> position = new LinkedHashMap<>();
		List<Block> order = new ArrayList<>();
		for (Block block : event.getBlocks()) {
			if (block.getType().equals(Material.BOOKSHELF)) {
				position.put(block, manager.getOrCreateBookself(new BlockPosition(block), Bookshelf.title).getPosition());
				order.add(block);
			}
		}
		
		if (order.isEmpty()) {
			return;
		}
		
		BlockFace dir = event.getDirection();
		for (Block block : CustomListUtils.reverse(order)) {
			Location newLoc = block.getRelative(dir).getLocation().clone();
			manager.move(position.get(block), newLoc.getBlockX(), newLoc.getBlockY(), newLoc.getBlockZ());
		}
	}

}

package com.loohp.bookshelf.objectholders;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class BookshelfHolder implements InventoryHolder {
	
	private BlockPosition position;
	private String title;
	private Inventory inventory;
	
	public BookshelfHolder(BlockPosition position, String title, Inventory inventory) {
		this.position = position;
		this.title = title;
		this.inventory = inventory;
	}

	public BlockPosition getPosition() {
		return position;
	}

	/**
	 * <b>Dangerous, non-deterministic behavior if not used correctly</b>
	 */
	@Deprecated
	public void setPosition(BlockPosition position) {
		this.position = position;
	}

	public String getTitle() {
		return title;
	}
	
	/**
	 * <b>Dangerous, non-deterministic behavior if not used correctly</b>
	 */
	@Deprecated
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * <b>Dangerous, non-deterministic behavior if not used correctly</b>
	 */
	@Deprecated
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
 
}

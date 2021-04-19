package com.loohp.bookshelf.api.events;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.loohp.bookshelf.objectholders.BookshelfHolder;

public class PlayerCloseBookshelfEvent extends Event{
	
	private Player player;
	private Block block;
	private Location location;
	private BookshelfHolder bookshelf;
	
	public PlayerCloseBookshelfEvent (Player player, BookshelfHolder bookshelf) {
		this.player = player;
		this.location = bookshelf.getPosition().getLocation();
		this.block = location.getBlock();
		this.bookshelf = bookshelf;
	}

	public Player getPlayer() {
		return player;
	}

	public Block getBlock() {
		return block;
	}

	public Location getLocation() {
		return location;
	}

	public BookshelfHolder getBookshelf() {
		return bookshelf;
	}

	private static final HandlerList HANDLERS = new HandlerList();

	@Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}

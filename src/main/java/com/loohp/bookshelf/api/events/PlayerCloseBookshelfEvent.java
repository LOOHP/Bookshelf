package com.loohp.bookshelf.api.events;

import com.loohp.bookshelf.objectholders.BookshelfHolder;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerCloseBookshelfEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Block block;
    private final Location location;
    private final BookshelfHolder bookshelf;

    public PlayerCloseBookshelfEvent(Player player, BookshelfHolder bookshelf) {
        this.player = player;
        this.location = bookshelf.getPosition().getLocation();
        this.block = location.getBlock();
        this.bookshelf = bookshelf;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
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

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}

package com.loohp.bookshelf.objectholders;

import org.bukkit.block.BlockFace;

public class LWCRequestOpenData {
	
	private BookshelfHolder bookshelf;
	private BlockFace blockface;
	private boolean cancelled;
	
	public LWCRequestOpenData(BookshelfHolder bookshelf, BlockFace blockface, boolean cancelled) {
		this.bookshelf = bookshelf;
		this.cancelled = cancelled;
		this.blockface = blockface;
	}

	public BookshelfHolder getBookshelf() {
		return bookshelf;
	}
	
	public BlockFace getBlockFace() {
		return blockface;
	}

	public boolean isCancelled() {
		return cancelled;
	}

}

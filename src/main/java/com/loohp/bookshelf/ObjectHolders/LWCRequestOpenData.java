package com.loohp.bookshelf.ObjectHolders;

import org.bukkit.block.BlockFace;

public class LWCRequestOpenData {
	
	String key;
	BlockFace blockface;
	boolean cancelled;
	
	public LWCRequestOpenData(String key, BlockFace blockface, boolean cancelled) {
		this.key = key;
		this.cancelled = cancelled;
		this.blockface = blockface;
	}

	public String getKey() {
		return key;
	}
	
	public BlockFace getBlockFace() {
		return blockface;
	}

	public boolean isCancelled() {
		return cancelled;
	}

}

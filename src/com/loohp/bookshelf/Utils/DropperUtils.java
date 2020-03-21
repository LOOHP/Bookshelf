package com.loohp.bookshelf.Utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dropper;

import com.loohp.bookshelf.Bookshelf;

public class DropperUtils {
	public static Block getDropperRelative(Block block) {
		if (!block.getType().equals(Material.DROPPER)) {
			return null;
		}
		Block relativeBlock = null;
		if (!Bookshelf.version.contains("legacy")) {
			BlockFace face = ((org.bukkit.block.data.type.Dispenser) block.getBlockData()).getFacing();
	        Block relative = block.getRelative(face);
	        relativeBlock = relative;
        } else {
        	Dropper dropper = (Dropper) block.getState();
    		@SuppressWarnings("deprecation")
			int data = dropper.getRawData();
    		BlockFace relative = BlockFace.DOWN;
    		switch(data) {
    			case 0:
    				relative = BlockFace.DOWN;
    				break;
    			case 1:
    				relative = BlockFace.UP;
    				break;
    			case 2:
    				relative = BlockFace.NORTH;
    				break;
    			case 3:
    				relative = BlockFace.SOUTH;
    				break;
    			case 4:
    				relative = BlockFace.WEST;
    				break;
    			case 5:
    				relative = BlockFace.EAST;
    				break;
    			case 8:
    				relative = BlockFace.DOWN;
    				break;
    			case 9:
    				relative = BlockFace.UP;
    				break;
    			case 10:
    				relative = BlockFace.NORTH;
    				break;
    			case 11:
    				relative = BlockFace.SOUTH;
    				break;
    			case 12:
    				relative = BlockFace.WEST;
    				break;
    			case 13:
    				relative = BlockFace.EAST;
    				break;       				     		  
    			default:
    				relative = BlockFace.DOWN;       			
    		}
    		relativeBlock = block.getRelative(relative);
        }		
		return relativeBlock;
	}
}

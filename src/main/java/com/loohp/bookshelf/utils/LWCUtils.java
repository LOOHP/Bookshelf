package com.loohp.bookshelf.utils;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Flag.Type;
import com.griefcraft.model.Protection;

public class LWCUtils {
	public static boolean checkHopperFlagIn(Entity entity) {
		int hash = 50000 + entity.getUniqueId().hashCode();
		Protection protection = LWC.getInstance().getPlugin().getLWC().findProtection(entity.getWorld(), hash, hash ,hash);
		
		if (protection == null) {
			return true;
		}
		
		if (protection.getFlag(Type.HOPPERIN) != null) {
			return true;
		}
		
		if (protection.getFlag(Type.HOPPER) != null) {
			return true;
		}
		return false;
	}
	
	public static boolean checkHopperFlagOut(Entity entity) {
		int hash = 50000 + entity.getUniqueId().hashCode();
		Protection protection = LWC.getInstance().getPlugin().getLWC().findProtection(entity.getWorld(), hash, hash ,hash);
		
		if (protection == null) {
			return true;
		}
		
		if (protection.getFlag(Type.HOPPEROUT) != null) {
			return true;
		}
		
		if (protection.getFlag(Type.HOPPER) != null) {
			return true;
		}
		return false;
	}
	public static boolean checkHopperFlagIn(Block block) {
		Protection protection = LWC.getInstance().getPlugin().getLWC().findProtection(block);
		
		if (protection == null) {
			return true;
		}
		
		if (protection.getFlag(Type.HOPPERIN) != null) {
			return true;
		}
		
		if (protection.getFlag(Type.HOPPER) != null) {
			return true;
		}
		return false;
	}
	
	public static boolean checkHopperFlagOut(Block block) {
		Protection protection = LWC.getInstance().getPlugin().getLWC().findProtection(block);
		
		if (protection == null) {
			return true;
		}
		
		if (protection.getFlag(Type.HOPPEROUT) != null) {
			return true;
		}
		
		if (protection.getFlag(Type.HOPPER) != null) {
			return true;
		}
		return false;
	}
}

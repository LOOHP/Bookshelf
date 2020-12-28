package com.loohp.bookshelf.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.lishid.openinv.OpenInv;
import com.loohp.bookshelf.Bookshelf;

public class OpenInvUtils {
	
	private static OpenInv openInvInstance = null;
	
	private static OpenInv getOpenInvInstance() {
		if (openInvInstance == null) {
			openInvInstance = (OpenInv) Bukkit.getPluginManager().getPlugin("OpenInv");
		}
		return openInvInstance;
	}
	
	public static boolean isSlientChest(Player player) {
		if (!Bookshelf.OpenInvHook) {
			return false;
		}
		OpenInv openinv = getOpenInvInstance();
		boolean isSilent = openinv.getPlayerSilentChestStatus(player);
		if (isSilent) {
			return true;
		}
		return false;
	}

}

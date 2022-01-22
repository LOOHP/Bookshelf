package com.loohp.bookshelf.utils;

import com.lishid.openinv.OpenInv;
import com.loohp.bookshelf.Bookshelf;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class OpenInvUtils {

    private static OpenInv openInvInstance = null;

    private static OpenInv getOpenInvInstance() {
        if (openInvInstance == null) {
            openInvInstance = (OpenInv) Bukkit.getPluginManager().getPlugin("OpenInv");
        }
        return openInvInstance;
    }

    public static boolean isSlientChest(Player player) {
        if (!Bookshelf.openInvHook) {
            return false;
        }
        OpenInv openinv = getOpenInvInstance();
        boolean isSilent = openinv.getPlayerSilentChestStatus(player);
        return isSilent;
    }

}

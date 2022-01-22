package com.loohp.bookshelf.utils;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.loohp.bookshelf.Bookshelf;
import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VanishUtils {

    public static boolean isVanished(Player player) {
        if (Bookshelf.vanishHook) {
            if (VanishAPI.isInvisible(player)) {
                return true;
            }
        }
        if (Bookshelf.cmiHook) {
            CMIUser user = CMI.getInstance().getPlayerManager().getUser(player);
            if (user.isVanished()) {
                return true;
            }
        }
        if (Bookshelf.essentialsHook) {
            Essentials ess3 = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            User user = ess3.getUser(player);
            return user.isVanished();
        }
        return false;
    }

}

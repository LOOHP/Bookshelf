/*
 * This file is part of Bookshelf.
 *
 * Copyright (C) 2025. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2025. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.loohp.bookshelf.utils;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Flag.Type;
import com.griefcraft.model.Protection;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class LWCUtils {

    public static boolean checkHopperFlagIn(Entity entity) {
        int hash = 50000 + entity.getUniqueId().hashCode();
        Protection protection = LWC.getInstance().getPlugin().getLWC().findProtection(entity.getWorld(), hash, hash, hash);

        if (protection == null) {
            return true;
        }

        if (protection.getFlag(Type.HOPPERIN) != null) {
            return true;
        }

        return protection.getFlag(Type.HOPPER) != null;
    }

    public static boolean checkHopperFlagOut(Entity entity) {
        int hash = 50000 + entity.getUniqueId().hashCode();
        Protection protection = LWC.getInstance().getPlugin().getLWC().findProtection(entity.getWorld(), hash, hash, hash);

        if (protection == null) {
            return true;
        }

        if (protection.getFlag(Type.HOPPEROUT) != null) {
            return true;
        }

        return protection.getFlag(Type.HOPPER) != null;
    }

    public static boolean checkHopperFlagIn(Block block) {
        Protection protection = LWC.getInstance().getPlugin().getLWC().findProtection(block);

        if (protection == null) {
            return true;
        }

        if (protection.getFlag(Type.HOPPERIN) != null) {
            return true;
        }

        return protection.getFlag(Type.HOPPER) != null;
    }

    public static boolean checkHopperFlagOut(Block block) {
        Protection protection = LWC.getInstance().getPlugin().getLWC().findProtection(block);

        if (protection == null) {
            return true;
        }

        if (protection.getFlag(Type.HOPPEROUT) != null) {
            return true;
        }

        return protection.getFlag(Type.HOPPER) != null;
    }

}

package com.loohp.bookshelf.utils;

import org.bukkit.block.BlockFace;

public class RedstoneUtils {

    public static BlockFace getOppositeFace(BlockFace face) {
        if (face.equals(BlockFace.SOUTH)) {
            return BlockFace.NORTH;
        } else if (face.equals(BlockFace.NORTH)) {
            return BlockFace.SOUTH;
        } else if (face.equals(BlockFace.WEST)) {
            return BlockFace.EAST;
        } else {
            return BlockFace.WEST;
        }
    }

}

/*
 * This file is part of Bookshelf.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
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

package com.loohp.bookshelf.objectholders;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

public class ChunkPosition {

    private final World world;
    private final int x;
    private final int z;

    public ChunkPosition(World world, int chunkX, int chunkZ) {
        this.world = world;
        this.x = chunkX;
        this.z = chunkZ;
    }

    public ChunkPosition(Location location) {
        this(location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    public ChunkPosition(Chunk chunk) {
        this(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    public World getWorld() {
        return world;
    }

    public int getChunkX() {
        return x;
    }

    public int getChunkZ() {
        return z;
    }

    public Chunk getChunk() {
        return getWorld().getChunkAt(x, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkPosition that = (ChunkPosition) o;
        return x == that.x && z == that.z && Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, z);
    }
}

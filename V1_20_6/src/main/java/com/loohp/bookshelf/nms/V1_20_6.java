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

package com.loohp.bookshelf.nms;

import com.loohp.bookshelf.objectholders.BookshelfState;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.Containers;
import net.minecraft.world.item.component.ItemContainerContents;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R4.util.CraftChatMessage;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class V1_20_6 extends NMSWrapper {

    @Override
    public Component getItemDisplayName(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        IChatBaseComponent displayName = nmsItemStack.a(DataComponents.g);
        return displayName == null ? null : GsonComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(displayName));
    }

    @Override
    public BookshelfState getStoredBookshelfState(ItemStack itemStack, int slots) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        ItemContainerContents contents = nmsItemStack.a(DataComponents.aa);
        Component title = getItemDisplayName(itemStack);
        if (contents == null) {
            return title == null ? null : new BookshelfState(title, null);
        }
        ItemStack[] items = contents.b().map(i -> itemOrNull(CraftItemStack.asBukkitCopy(i))).toArray(ItemStack[]::new);
        return new BookshelfState(getItemDisplayName(itemStack), items);
    }

    @Override
    public ItemStack withStoredBookshelfState(ItemStack itemStack, BookshelfState bookshelfState) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        DataComponentPatch.a builder = DataComponentPatch.a();
        if (bookshelfState.getTitle() != null) {
            builder.a(DataComponents.g, CraftChatMessage.fromJSON(GsonComponentSerializer.gson().serialize(bookshelfState.getTitle())));
        }
        if (bookshelfState.getContents() != null) {
            List<net.minecraft.world.item.ItemStack> items = Arrays.stream(bookshelfState.getContents()).map(i -> CraftItemStack.asNMSCopy(itemNonNull(i))).collect(Collectors.toList());
            ItemContainerContents contents = ItemContainerContents.a(items);
            builder.a(DataComponents.aa, contents);
        }
        nmsItemStack.a(builder.a());
        return CraftItemStack.asCraftMirror(nmsItemStack);
    }

    @Override
    public void sendBookshelfWindowOpen(Player player, Inventory inventory, Component title) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        Container container = new CraftContainer(inventory, entityPlayer, entityPlayer.nextContainerCounter());
        Containers<?> windowType = CraftContainer.getNotchInventoryType(inventory);
        entityPlayer.c.b(new PacketPlayOutOpenWindow(container.j, windowType, CraftChatMessage.fromJSON(GsonComponentSerializer.gson().serialize(title))));
        entityPlayer.cb = container;
        entityPlayer.a(container);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void spawnDustParticle(Location location, int count, Object dustOptions) {
        location.getWorld().spawnParticle(Particle.DUST, location, count, (Particle.DustOptions) dustOptions);
    }

    @Override
    public EntityType getHopperMinecartEntityType() {
        return EntityType.HOPPER_MINECART;
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getWorldNamespacedKey(World world) {
        NamespacedKey key = world.getKey();
        return Key.key(key.getNamespace(), key.getKey());
    }

}

/*
 * This file is part of Bookshelf.
 *
 * Copyright (C) 2024. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2024. Contributors
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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.Containers;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

@SuppressWarnings("unused")
public class V1_17 extends NMSWrapper {

    @Override
    public Component getItemDisplayName(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbttagcompound = nmsItemStack.b("display");
        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("Name", 8)) {
            try {
                String displayName = nbttagcompound.getString("Name");
                if (displayName != null) {
                    return GsonComponentSerializer.gson().deserialize(displayName);
                }
            } catch (Exception ignore) {
            }
        }
        return null;
    }

    @Override
    public BookshelfState getStoredBookshelfState(ItemStack itemStack, int slots) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbt = nmsItemStack.getTag();
        NBTTagCompound blockState = nbt == null ? null : nbt.getCompound("BlockEntityTag");
        if (blockState == null) {
            return null;
        }
        Component customName = null;
        if (blockState.hasKey("CustomName")) {
            customName = GsonComponentSerializer.gson().deserialize(blockState.getString("CustomName"));
        }
        ItemStack[] items = null;
        if (blockState.hasKey("Items")) {
            NonNullList<net.minecraft.world.item.ItemStack> contents = NonNullList.a(slots, CraftItemStack.asNMSCopy(AIR));
            ContainerUtil.b(blockState, contents);
            items = contents.stream().map(i -> itemOrNull(CraftItemStack.asBukkitCopy(i))).toArray(ItemStack[]::new);
        }
        return new BookshelfState(customName, items);
    }

    @Override
    public ItemStack withStoredBookshelfState(ItemStack itemStack, BookshelfState bookshelfState) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbt = nmsItemStack.save(new NBTTagCompound());
        NBTTagCompound merge = new NBTTagCompound();
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound blockState = new NBTTagCompound();
        if (bookshelfState.getTitle() != null) {
            blockState.setString("CustomName", GsonComponentSerializer.gson().serialize(bookshelfState.getTitle()));
        }
        if (bookshelfState.getContents() != null) {
            net.minecraft.world.item.ItemStack[] items = Arrays.stream(bookshelfState.getContents()).map(i -> CraftItemStack.asNMSCopy(itemNonNull(i))).toArray(net.minecraft.world.item.ItemStack[]::new);
            ContainerUtil.a(blockState, NonNullList.a(null, items));
        }
        tag.set("BlockEntityTag", blockState);
        merge.set("tag", tag);
        nbt.a(merge);
        net.minecraft.world.item.ItemStack modifiedNmsItemStack = net.minecraft.world.item.ItemStack.a(nbt);
        return CraftItemStack.asCraftMirror(modifiedNmsItemStack);
    }

    @Override
    public void sendBookshelfWindowOpen(Player player, Inventory inventory, Component title) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        Container container = new CraftContainer(inventory, entityPlayer, entityPlayer.nextContainerCounter());
        Containers<?> windowType = CraftContainer.getNotchInventoryType(inventory);
        entityPlayer.b.sendPacket(new PacketPlayOutOpenWindow(container.j, windowType, CraftChatMessage.fromJSON(GsonComponentSerializer.gson().serialize(title))));
        entityPlayer.bV = container;
        entityPlayer.initMenu(container);
    }

}
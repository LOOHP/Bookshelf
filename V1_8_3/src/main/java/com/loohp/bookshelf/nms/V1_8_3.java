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
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.server.v1_8_R2.Container;
import net.minecraft.server.v1_8_R2.EntityPlayer;
import net.minecraft.server.v1_8_R2.IChatBaseComponent;
import net.minecraft.server.v1_8_R2.NBTTagCompound;
import net.minecraft.server.v1_8_R2.NBTTagList;
import net.minecraft.server.v1_8_R2.PacketPlayOutOpenWindow;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R2.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("unused")
public class V1_8_3 extends NMSWrapper {

    @Override
    public Component getItemDisplayName(ItemStack itemStack) {
        net.minecraft.server.v1_8_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        if (nmsItemStack.getTag() != null && nmsItemStack.getTag().hasKeyOfType("display", 10)) {
            NBTTagCompound nbttagcompound = nmsItemStack.getTag().getCompound("display");
            if (nbttagcompound.hasKeyOfType("Name", 8)) {
                return LegacyComponentSerializer.legacySection().deserialize(nbttagcompound.getString("Name"));
            }
        }
        return null;
    }

    @Override
    public BookshelfState getStoredBookshelfState(ItemStack itemStack, int slots) {
        net.minecraft.server.v1_8_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbt = nmsItemStack.getTag();
        NBTTagCompound blockState = nbt == null ? null : nbt.getCompound("BlockEntityTag");
        if (blockState == null) {
            return null;
        }
        Component customName = null;
        if (blockState.hasKey("CustomName")) {
            customName = LegacyComponentSerializer.legacySection().deserialize(blockState.getString("CustomName"));
        }
        ItemStack[] items = null;
        if (blockState.hasKey("Items")) {
            items = new ItemStack[slots];
            NBTTagList nbttaglist = blockState.getList("Items", 10);
            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound itemNbt = nbttaglist.get(i);
                int j = itemNbt.getByte("Slot") & 255;
                if (j < items.length) {
                    net.minecraft.server.v1_8_R2.ItemStack item = net.minecraft.server.v1_8_R2.ItemStack.createStack(itemNbt);
                    items[j] = itemOrNull(CraftItemStack.asBukkitCopy(item));
                }
            }
        }
        return new BookshelfState(customName, items);
    }

    @Override
    public ItemStack withStoredBookshelfState(ItemStack itemStack, BookshelfState bookshelfState) {
        net.minecraft.server.v1_8_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbt = nmsItemStack.save(new NBTTagCompound());
        NBTTagCompound merge = new NBTTagCompound();
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound blockState = new NBTTagCompound();
        if (bookshelfState.getTitle() != null) {
            blockState.setString("CustomName", LegacyComponentSerializer.legacySection().serialize(bookshelfState.getTitle()));
        }
        if (bookshelfState.getContents() != null) {
            ItemStack[] items = bookshelfState.getContents();
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < items.length; ++i) {
                if (items[i] != null) {
                    NBTTagCompound itemNbt = new NBTTagCompound();
                    itemNbt.setByte("Slot", (byte) i);
                    CraftItemStack.asNMSCopy(items[i]).save(itemNbt);
                    nbttaglist.add(itemNbt);
                }
            }
            blockState.set("Items", nbttaglist);
        }
        tag.set("BlockEntityTag", blockState);
        merge.set("tag", tag);
        nbt.a(merge);
        net.minecraft.server.v1_8_R2.ItemStack modifiedNmsItemStack = net.minecraft.server.v1_8_R2.ItemStack.createStack(nbt);
        return CraftItemStack.asCraftMirror(modifiedNmsItemStack);
    }

    @Override
    public void sendBookshelfWindowOpen(Player player, Inventory inventory, Component title) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        Container container = new CraftContainer(inventory, player, entityPlayer.nextContainerCounter());
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(container.windowId, "minecraft:container", IChatBaseComponent.ChatSerializer.a(GsonComponentSerializer.gson().serialize(title)), inventory.getSize()));
        entityPlayer.activeContainer = container;
        entityPlayer.activeContainer.addSlotListener(entityPlayer);
    }

    @Override
    public void spawnDustParticle(Location location, int count, Object dustOptions) {
        //do nothing
    }

    @Override
    public EntityType getHopperMinecartEntityType() {
        return EntityType.MINECART_HOPPER;
    }

}

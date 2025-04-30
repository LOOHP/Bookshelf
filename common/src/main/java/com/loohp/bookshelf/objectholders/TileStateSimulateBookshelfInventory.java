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

package com.loohp.bookshelf.objectholders;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class TileStateSimulateBookshelfInventory implements Inventory {

    private final BookshelfHolder holder;

    public TileStateSimulateBookshelfInventory(BookshelfHolder holder) {
        this.holder = holder;
    }

    @Override
    @Nullable
    public Location getLocation() {
        return holder.getInventory().getLocation();
    }

    @Override
    @NotNull
    public ListIterator<ItemStack> iterator(int index) {
        return holder.getInventory().iterator(index);
    }

    @Override
    @NotNull
    public ListIterator<ItemStack> iterator() {
        return holder.getInventory().iterator();
    }

    @Override
    @Nullable
    public InventoryHolder getHolder() {
        return holder.getInventory().getHolder();
    }

    @Nullable
    public InventoryHolder getHolder(boolean useSnapshot) {
        try {
            Inventory inventory = holder.getInventory();
            Method method = inventory.getClass().getMethod("getHolder", boolean.class);
            return (InventoryHolder) method.invoke(inventory, useSnapshot);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @NotNull
    public InventoryType getType() {
        return holder.getInventory().getType();
    }

    @Override
    @NotNull
    public List<HumanEntity> getViewers() {
        return holder.getInventory().getViewers();
    }

    @MightOverride
    public String getTitle() {
        return LegacyComponentSerializer.legacySection().serialize(holder.getTitle());
    }

    @Override
    public void clear() {
        holder.getInventory().clear();
    }

    @Override
    public void clear(int index) {
        holder.getInventory().clear(index);
    }

    @Override
    public void remove(@NotNull ItemStack item) {
        holder.getInventory().remove(item);
    }

    @Override
    public void remove(@NotNull Material material) throws IllegalArgumentException {
        holder.getInventory().remove(material);
    }

    @Override
    public boolean isEmpty() {
        return holder.getInventory().isEmpty();
    }

    @Override
    public int firstEmpty() {
        return holder.getInventory().firstEmpty();
    }

    @MightOverride
    public void remove(int materialId) {
        Arrays.stream(Material.values()).filter(m -> m.getId() == materialId).findFirst().ifPresent(m -> {
            holder.getInventory().remove(m);
        });
    }

    @Override
    public int first(@NotNull ItemStack item) {
        return holder.getInventory().first(item);
    }

    @Override
    public int first(@NotNull Material material) throws IllegalArgumentException {
        return holder.getInventory().first(material);
    }

    @Override
    @NotNull
    public HashMap<Integer, ? extends ItemStack> all(@Nullable ItemStack item) {
        return holder.getInventory().all(item);
    }

    @MightOverride
    public int first(int materialId) {
        return Arrays.stream(Material.values()).filter(m -> m.getId() == materialId).findFirst().map(m -> {
            return holder.getInventory().first(m);
        }).orElse(-1);
    }

    @Override
    @NotNull
    public HashMap<Integer, ? extends ItemStack> all(@NotNull Material material) throws IllegalArgumentException {
        return holder.getInventory().all(material);
    }

    @Override
    @Contract("null, _ -> false")
    public boolean containsAtLeast(@Nullable ItemStack item, int amount) {
        return holder.getInventory().containsAtLeast(item, amount);
    }

    @MightOverride
    public HashMap<Integer, ? extends ItemStack> all(int materialId) {
        return Arrays.stream(Material.values()).filter(m -> m.getId() == materialId).findFirst().map(m -> {
            return holder.getInventory().all(m);
        }).orElseGet(() -> new HashMap<>());
    }

    @Override
    @Contract("null, _ -> false")
    public boolean contains(@Nullable ItemStack item, int amount) {
        return holder.getInventory().contains(item, amount);
    }

    @Override
    public boolean contains(@NotNull Material material, int amount) throws IllegalArgumentException {
        return holder.getInventory().contains(material, amount);
    }

    @Override
    @Contract("null -> false")
    public boolean contains(@Nullable ItemStack item) {
        return holder.getInventory().contains(item);
    }

    @MightOverride
    public boolean contains(int materialId, int amount) {
        return Arrays.stream(Material.values()).filter(m -> m.getId() == materialId).findFirst().map(m -> {
            return holder.getInventory().contains(m, amount);
        }).orElse(false);
    }

    @Override
    public boolean contains(@NotNull Material material) throws IllegalArgumentException {
        return holder.getInventory().contains(material);
    }

    @Override
    public void setStorageContents(@Nullable ItemStack @NotNull [] items) throws IllegalArgumentException {
        holder.getInventory().setStorageContents(items);
    }

    @Override
    @Nullable
    public ItemStack @NotNull [] getStorageContents() {
        return holder.getInventory().getStorageContents();
    }

    @Override
    public void setContents(@Nullable ItemStack @NotNull [] items) throws IllegalArgumentException {
        holder.getInventory().setContents(items);
    }

    @MightOverride
    public boolean contains(int materialId) {
        return Arrays.stream(Material.values()).filter(m -> m.getId() == materialId).findFirst().map(m -> {
            return holder.getInventory().contains(m);
        }).orElse(false);
    }

    @Override
    @Nullable
    public ItemStack @NotNull [] getContents() {
        return holder.getInventory().getContents();
    }

    @Override
    @NotNull
    public HashMap<Integer, ItemStack> removeItem(@NotNull ItemStack... items) throws IllegalArgumentException {
        return holder.getInventory().removeItem(items);
    }

    @Override
    @NotNull
    public HashMap<Integer, ItemStack> addItem(@NotNull ItemStack... items) throws IllegalArgumentException {
        return holder.getInventory().addItem(items);
    }

    @Override
    public void setItem(int index, @Nullable ItemStack item) {
        holder.getInventory().setItem(index, item);
    }

    @Override
    @Nullable
    public ItemStack getItem(int index) {
        return holder.getInventory().getItem(index);
    }

    @Override
    public void setMaxStackSize(int size) {
        holder.getInventory().setMaxStackSize(size);
    }

    @MightOverride
    public String getName() {
        return LegacyComponentSerializer.legacySection().serialize(holder.getTitle());
    }

    @Override
    public int getMaxStackSize() {
        return holder.getInventory().getMaxStackSize();
    }

    @Override
    public int getSize() {
        return holder.getInventory().getSize();
    }
}

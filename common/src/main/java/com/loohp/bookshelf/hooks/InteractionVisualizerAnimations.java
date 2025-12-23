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

package com.loohp.bookshelf.hooks;

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.api.BookshelfAPI;
import com.loohp.bookshelf.api.events.PlayerCloseBookshelfEvent;
import com.loohp.bookshelf.api.events.PlayerOpenBookshelfEvent;
import com.loohp.bookshelf.utils.InventoryUtils;
import com.loohp.bookshelf.utils.OpenInvUtils;
import com.loohp.bookshelf.utils.VanishUtils;
import com.loohp.interactionvisualizer.api.InteractionVisualizerAPI;
import com.loohp.interactionvisualizer.api.InteractionVisualizerAPI.Modules;
import com.loohp.interactionvisualizer.api.VisualizerDisplay;
import com.loohp.interactionvisualizer.entityholders.Item;
import com.loohp.interactionvisualizer.managers.PacketManager;
import com.loohp.interactionvisualizer.objectholders.EntryKey;
import com.loohp.platformscheduler.Scheduler;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class InteractionVisualizerAnimations implements Listener, VisualizerDisplay {

    public static final EntryKey KEY = new EntryKey(Bookshelf.plugin, "bookshelf");

    private final Map<Player, Location> playermap = new ConcurrentHashMap<>();
    private final Map<Player, List<Item>> link = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUseBookshelf(InventoryClickEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Location location = playermap.get(player);

        if (location == null) {
            return;
        }

        if (VanishUtils.isVanished(player)) {
            return;
        }
        if (OpenInvUtils.isSilentChest(player)) {
            return;
        }
        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            return;
        }

        if (event.getClick().equals(ClickType.MIDDLE) && !player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }

        boolean isIn = true;
        boolean isMove = false;
        ItemStack itemstack = null;

        if (event.getRawSlot() >= 0 && event.getRawSlot() < BookshelfAPI.getBookshelfSize()) {

            itemstack = event.getCurrentItem();
            if (itemstack != null) {
                if (itemstack.getType().equals(Material.AIR)) {
                    itemstack = null;
                } else {
                    isIn = false;
                    isMove = true;
                }
            }
            if (itemstack == null) {
                itemstack = event.getCursor();
                if (itemstack != null) {
                    if (itemstack.getType().equals(Material.AIR)) {
                        itemstack = null;
                    } else {
                        isMove = true;
                    }
                }
            } else {
                if (event.getCursor() != null) {
                    if (event.getCursor().getType().equals(itemstack.getType())) {
                        isIn = true;
                    }
                }
            }
            if (itemstack == null) {
                if (event.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD) || event.getAction().equals(InventoryAction.HOTBAR_SWAP)) {
                    itemstack = player.getInventory().getItem(event.getHotbarButton());
                    if (itemstack != null) {
                        if (itemstack.getType().equals(Material.AIR)) {
                            itemstack = null;
                        } else {
                            isMove = true;
                        }
                    }
                }
            }
        }

        if (itemstack == null) {
            if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                itemstack = event.getCurrentItem();
                if (itemstack != null) {
                    if (itemstack.getType().equals(Material.AIR)) {
                        itemstack = null;
                    } else {
                        isMove = true;
                    }
                }
            }
        }

        if (event.isShiftClick() && event.getView().getItem(event.getRawSlot()) != null) {
            if (isIn) {
                if (!InventoryUtils.stillHaveSpace(event.getView().getTopInventory(), event.getView().getItem(event.getRawSlot()).getType())) {
                    return;
                }
            } else {
                if (!InventoryUtils.stillHaveSpace(player.getInventory(), event.getView().getItem(event.getRawSlot()).getType())) {
                    return;
                }
            }
        }
        if (event.getCursor() != null) {
            if (!event.getCursor().getType().equals(Material.AIR)) {
                if (event.getCurrentItem() != null) {
                    if (!event.getCurrentItem().getType().equals(Material.AIR)) {
                        if (event.getCurrentItem().getType().equals(event.getCursor().getType())) {
                            if (event.getCurrentItem().getAmount() >= event.getCurrentItem().getType().getMaxStackSize()) {
                                return;
                            }
                        }
                    }
                }
            }
        }

        if (isMove) {
            PacketManager.sendHandMovement(InteractionVisualizerAPI.getPlayers(), player);
            if (itemstack != null) {
                Item item = new Item(location.clone());
                Vector offset = new Vector(0.0, 0.15, 0.0);
                Vector vector = location.clone().toVector().subtract(player.getEyeLocation().clone().add(0.0, -0.5, 0.0).toVector()).multiply(-0.15).add(offset);
                item.setVelocity(vector);
                if (isIn) {
                    item.teleport(player.getEyeLocation());
                    vector = location.clone().toVector().subtract(player.getEyeLocation().clone().toVector()).multiply(0.15).add(offset);
                    item.setVelocity(vector);
                }
                PacketManager.sendItemSpawn(InteractionVisualizerAPI.getPlayerModuleList(Modules.ITEMDROP, KEY), item);
                item.setItemStack(itemstack);
                item.setPickupDelay(32767);
                item.setGravity(true);
                PacketManager.updateItem(item);
                if (!link.containsKey(player)) {
                    link.put(player, new ArrayList<Item>());
                }
                List<Item> list = link.get(player);
                list.add(item);
                boolean finalIsIn = isIn;
                Scheduler.runTaskLater(Bookshelf.plugin, () -> {
                    if (finalIsIn) {
                        item.teleport(location.clone());
                    } else {
                        item.teleport(player.getEyeLocation().add(0.0, -0.5, 0.0));
                    }
                    item.setVelocity(new Vector(0.0, 0.0, 0.0));
                    item.setGravity(false);
                    PacketManager.updateItem(item);
                }, 7);
                Scheduler.runTaskLater(Bookshelf.plugin, () -> {
                    PacketManager.removeItem(InteractionVisualizerAPI.getPlayers(), item);
                    list.remove(item);
                }, 20);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDragChest(InventoryDragEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Location location = playermap.get(player);

        if (location == null) {
            return;
        }

        if (VanishUtils.isVanished(player)) {
            return;
        }
        if (OpenInvUtils.isSilentChest(player)) {
            return;
        }
        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            return;
        }

        boolean ok = false;
        for (Entry<Integer, ItemStack> entry : event.getNewItems().entrySet()) {
            ItemStack item = event.getView().getItem(entry.getKey());
            if (item == null) {
                ok = true;
                break;
            }
            if (item.getType().equals(Material.AIR)) {
                ok = true;
                break;
            }
            if (!item.getType().equals(entry.getValue().getType())) {
                continue;
            }
            if (item.getAmount() < item.getType().getMaxStackSize()) {
                ok = true;
                break;
            }
        }
        if (!ok) {
            return;
        }

        for (int slot : event.getRawSlots()) {
            if (slot >= 0 && slot < BookshelfAPI.getBookshelfSize()) {
                PacketManager.sendHandMovement(InteractionVisualizerAPI.getPlayers(), player);

                ItemStack itemstack = event.getOldCursor();
                if (itemstack != null) {
                    if (itemstack.getType().equals(Material.AIR)) {
                        itemstack = null;
                    }
                }

                if (itemstack != null) {
                    Item item = new Item(player.getEyeLocation());
                    Vector offset = new Vector(0.0, 0.15, 0.0);
                    Vector vector = location.clone().toVector().subtract(player.getEyeLocation().clone().toVector()).multiply(0.15).add(offset);
                    item.setVelocity(vector);
                    PacketManager.sendItemSpawn(InteractionVisualizerAPI.getPlayerModuleList(Modules.ITEMDROP, KEY), item);
                    item.setItemStack(itemstack);
                    item.setCustomName(System.currentTimeMillis() + "");
                    item.setPickupDelay(32767);
                    item.setGravity(true);
                    PacketManager.updateItem(item);
                    if (!link.containsKey(player)) {
                        link.put(player, new ArrayList<Item>());
                    }
                    List<Item> list = link.get(player);
                    list.add(item);
                    Scheduler.runTaskLater(Bookshelf.plugin, () -> {
                        item.teleport(location.clone());
                        item.setVelocity(new Vector(0.0, 0.0, 0.0));
                        item.setGravity(false);
                        PacketManager.updateItem(item);
                    }, 7);
                    Scheduler.runTaskLater(Bookshelf.plugin, () -> {
                        PacketManager.removeItem(InteractionVisualizerAPI.getPlayers(), item);
                        list.remove(item);
                    }, 20);
                }
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onOpenBookshelf(PlayerOpenBookshelfEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        if (VanishUtils.isVanished(player)) {
            return;
        }
        if (OpenInvUtils.isSilentChest(player)) {
            return;
        }

        playermap.put(player, getBlockFaceCenterLocation(event.getBlock(), event.getClickedBlockFace(), 0.3));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCloseBookshelf(PlayerCloseBookshelfEvent event) {
        playermap.remove(event.getPlayer());

        if (!link.containsKey(event.getPlayer())) {
            return;
        }
        List<Item> list = link.get(event.getPlayer());
        for (Item item : list) {
            PacketManager.removeItem(InteractionVisualizerAPI.getPlayers(), item);
        }

        link.remove(event.getPlayer());
    }

    private Location getBlockFaceCenterLocation(Block block, BlockFace face, double offset) {
        Block other = block.getRelative(face);
        Vector vector = other.getLocation().add(0.5, 0.5, 0.5).toVector().subtract(block.getLocation().toVector()).multiply(0.5 + offset);
        return block.getLocation().add(vector);
    }

    @Override
    public EntryKey key() {
        return KEY;
    }

}

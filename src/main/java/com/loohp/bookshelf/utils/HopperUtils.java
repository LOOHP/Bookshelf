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

package com.loohp.bookshelf.utils;

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.BookshelfManager;
import com.loohp.bookshelf.objectholders.BlockPosition;
import com.loohp.bookshelf.objectholders.BookshelfHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HopperUtils {

    private static final HashMap<World, Long> PerWorldHopperTransferCurrentTick = new HashMap<World, Long>();
    private static final HashMap<World, Long> PerWorldHopperTransferSpeed = new HashMap<World, Long>();
    private static final HashMap<World, Long> PerWorldHopperTransferAmount = new HashMap<World, Long>();

    public static void hopperMinecartCheck() {
        for (World world : Bukkit.getWorlds()) {
            if (Bukkit.spigot().getConfig().contains("world-settings." + world.getName() + ".ticks-per.hopper-transfer")) {
                long rate = Bukkit.spigot().getConfig().getLong("world-settings." + world.getName() + ".ticks-per.hopper-transfer");
                PerWorldHopperTransferSpeed.put(world, rate);
            }
            if (Bukkit.spigot().getConfig().contains("world-settings." + world.getName() + ".hopper-amount")) {
                long amount = Bukkit.spigot().getConfig().getLong("world-settings." + world.getName() + ".hopper-amount");
                PerWorldHopperTransferAmount.put(world, amount);
            }
        }
        Bookshelf.hopperMinecartTaskID = Bukkit.getScheduler().runTaskTimer(Bookshelf.plugin, () -> {
            long start = System.currentTimeMillis();
            long startNano = System.nanoTime();
            for (World world : BookshelfManager.getWorlds()) {
                BookshelfManager manager = BookshelfManager.getBookshelfManager(world);
                if (manager == null) {
                    continue;
                }
                for (Entity entity : world.getEntities()) {
                    if (entity.getType().equals(EntityType.MINECART_HOPPER)) {
                        HopperMinecart hoppercart = (HopperMinecart) entity;
                        if (!hoppercart.isEnabled()) {
                            continue;
                        }
                        if (hoppercart.getLocation().getBlock().getRelative(BlockFace.UP) == null) {
                            continue;
                        }
                        Block bookshelfBlock = hoppercart.getLocation().getBlock().getRelative(BlockFace.UP);
                        if (!bookshelfBlock.getType().equals(Material.BOOKSHELF)) {
                            continue;
                        }

                        Inventory inventory = hoppercart.getInventory();
                        Inventory bookshelfInv = manager.getOrCreateBookshelf(new BlockPosition(bookshelfBlock), null).getInventory();
                        for (int i = 0; i < bookshelfInv.getSize(); i++) {
                            ItemStack item = bookshelfInv.getItem(i);
                            if (item == null) {
                                continue;
                            }
                            if (InventoryUtils.stillHaveSpace(inventory, item.getType())) {

                                if (Bookshelf.blockLockerHook) {
                                    if (BlockLockerUtils.isLocked(bookshelfBlock)) {
                                        break;
                                    }
                                }
                                if (Bookshelf.lwcHook) {
                                    if (!LWCUtils.checkHopperFlagOut(bookshelfBlock)) {
                                        break;
                                    }
                                    if (!LWCUtils.checkHopperFlagIn(hoppercart)) {
                                        break;
                                    }
                                }

                                ItemStack additem = item.clone();
                                additem.setAmount(1);
                                ItemStack beforeEvent = additem.clone();
                                InventoryMoveItemEvent event = new InventoryMoveItemEvent(inventory, additem, bookshelfInv, true);
                                if (event.isCancelled()) {
                                    break;
                                }
                                additem = event.getItem();
                                if (beforeEvent.equals(additem)) {
                                    item.setAmount(item.getAmount() - 1);
                                    bookshelfInv.setItem(i, item);
                                }
                                inventory.addItem(additem);
                                break;
                            }
                        }
                    }
                }
            }
            long end = System.currentTimeMillis();
            long endNano = System.nanoTime();
            Bookshelf.lastHoppercartTime = endNano - startNano;
            if ((end - start) > 500) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Hopper Minecart Item Move Function took more than 500ms! (" + (end - start) + "ms)");
            }
        }, 0, 1).getTaskId();
    }

    public static void hopperCheck() {
        Bookshelf.hopperTaskID = Bukkit.getScheduler().runTaskTimer(Bookshelf.plugin, () -> {
            long start = System.currentTimeMillis();
            long startNano = System.nanoTime();
            HashMap<World, Long> perform = new HashMap<World, Long>();
            for (World world : Bukkit.getWorlds()) {
                long currentTick = 0;
                if (PerWorldHopperTransferCurrentTick.containsKey(world)) {
                    currentTick = PerWorldHopperTransferCurrentTick.get(world);
                }
                long rate = Bookshelf.hopperTicksPerTransfer;
                if (PerWorldHopperTransferSpeed.containsKey(world)) {
                    rate = PerWorldHopperTransferSpeed.get(world);
                }
                currentTick++;
                if (currentTick > rate) {
                    currentTick = 1;
                }
                PerWorldHopperTransferCurrentTick.put(world, currentTick);
                if (currentTick == 1) {
                    long amount = Bookshelf.hopperAmount;
                    if (PerWorldHopperTransferAmount.containsKey(world)) {
                        amount = PerWorldHopperTransferAmount.get(world);
                    }
                    perform.put(world, amount);
                }
            }
            for (BookshelfHolder bookshelf : BookshelfManager.getAllLoadedBookshelves()) {
                Location loc = bookshelf.getPosition().getLocation();
                if (!perform.containsKey(loc.getWorld())) {
                    continue;
                }
                long amount = perform.get(loc.getWorld());
                Block blockBelow = loc.getBlock().getRelative(BlockFace.DOWN);
                if (!loc.getBlock().getType().equals(Material.BOOKSHELF)) {
                    continue;
                }
                if (blockBelow != null) {
                    if (blockBelow.getType().equals(Material.HOPPER)) {
                        if (!blockBelow.isBlockPowered() && !blockBelow.isBlockIndirectlyPowered()) {
                            org.bukkit.block.Hopper h = (org.bukkit.block.Hopper) blockBelow.getState();
                            Inventory inventory = h.getInventory();
                            Inventory bookshelfInv = bookshelf.getInventory();
                            for (int i = 0; i < bookshelfInv.getSize(); i++) {
                                ItemStack item = bookshelfInv.getItem(i);
                                if (item == null) {
                                    continue;
                                }
                                if (InventoryUtils.stillHaveSpace(inventory, item.getType())) {
                                    if (isAllow(loc.getBlock())) {
                                        ItemStack additem = item.clone();
                                        int num = item.getAmount();
                                        if (num > amount) {
                                            num = (int) amount;
                                        }
                                        additem.setAmount(num);
                                        ItemStack beforeEvent = additem.clone();
                                        InventoryMoveItemEvent event = new InventoryMoveItemEvent(inventory, additem, bookshelfInv, true);
                                        if (event.isCancelled()) {
                                            break;
                                        }
                                        additem = event.getItem();
                                        if (beforeEvent.equals(additem)) {
                                            item.setAmount(item.getAmount() - num);
                                            bookshelfInv.setItem(i, item);
                                        }
                                        inventory.addItem(additem);
                                        break;
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                Block bookshelfBlock = loc.getBlock();
                List<Block> hoppers = getHoppersIn(bookshelfBlock, BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
                for (Block hopper : hoppers) {
                    if (hopper.isBlockPowered() || hopper.isBlockIndirectlyPowered()) {
                        continue;
                    }
                    org.bukkit.block.Hopper h = (org.bukkit.block.Hopper) hopper.getState();
                    Inventory inventory = h.getInventory();
                    Inventory bookshelfInv = bookshelf.getInventory();
                    for (int i = 0; i < inventory.getSize(); i++) {
                        ItemStack item = inventory.getItem(i);
                        if (item == null) {
                            continue;
                        }
                        if (Bookshelf.useWhitelist) {
                            if (Bookshelf.whitelist.contains(item.getType().toString().toUpperCase())) {
                                if (InventoryUtils.stillHaveSpace(bookshelfInv, item.getType())) {
                                    if (Bookshelf.lwcHook) {
                                        if (!LWCUtils.checkHopperFlagOut(hopper)) {
                                            break;
                                        }
                                        if (!LWCUtils.checkHopperFlagIn(bookshelfBlock)) {
                                            break;
                                        }
                                    }
                                    if (Bookshelf.blockLockerHook) {
                                        if (!BlockLockerUtils.canRedstone(bookshelfBlock)) {
                                            break;
                                        }
                                    }
                                    ItemStack additem = item.clone();
                                    int num = item.getAmount();
                                    if (num > amount) {
                                        num = (int) amount;
                                    }
                                    additem.setAmount(num);
                                    ItemStack beforeEvent = additem.clone();
                                    InventoryMoveItemEvent event = new InventoryMoveItemEvent(bookshelfInv, additem, inventory, false);
                                    if (event.isCancelled()) {
                                        break;
                                    }
                                    additem = event.getItem();
                                    if (beforeEvent.equals(additem)) {
                                        item.setAmount(item.getAmount() - num);
                                        inventory.setItem(i, item);
                                    }
                                    bookshelfInv.addItem(additem);
                                    break;
                                }
                            }
                        } else {
                            if (InventoryUtils.stillHaveSpace(bookshelfInv, item.getType())) {
                                if (Bookshelf.lwcHook) {
                                    if (!LWCUtils.checkHopperFlagOut(hopper)) {
                                        break;
                                    }
                                    if (!LWCUtils.checkHopperFlagIn(bookshelfBlock)) {
                                        break;
                                    }
                                }
                                if (Bookshelf.blockLockerHook) {
                                    if (!BlockLockerUtils.canRedstone(bookshelfBlock)) {
                                        break;
                                    }
                                }
                                ItemStack additem = item.clone();
                                int num = item.getAmount();
                                if (num > amount) {
                                    num = (int) amount;
                                }
                                additem.setAmount(num);
                                ItemStack beforeEvent = additem.clone();
                                InventoryMoveItemEvent event = new InventoryMoveItemEvent(bookshelfInv, additem, inventory, false);
                                if (event.isCancelled()) {
                                    break;
                                }
                                additem = event.getItem();
                                if (beforeEvent.equals(additem)) {
                                    item.setAmount(item.getAmount() - num);
                                    inventory.setItem(i, item);
                                }
                                bookshelfInv.addItem(additem);
                                break;
                            }
                        }
                    }
                }
            }
            long end = System.currentTimeMillis();
            long endNano = System.nanoTime();
            //Bukkit.getConsoleSender().sendMessage("(" + (endNano - startNano) + "ns) / (" + (end - start) + "ms)");
            Bookshelf.lastHopperTime = endNano - startNano;
            if ((end - start) > 500) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Hopper Block Item Move Function took more than 500ms! (" + (end - start) + "ms)");
            }
        }, 0, 1).getTaskId();
    }

    @SuppressWarnings("deprecation")
    public static List<Block> getHoppersIn(Block block, BlockFace... faces) {
        List<Block> hoppers = new ArrayList<Block>();
        if (!Bookshelf.version.isLegacy()) {
            for (BlockFace face : faces) {
                Block relative = block.getRelative(face);
                if (relative.getType().equals(Material.HOPPER)) {
                    if (relative.getRelative(((org.bukkit.block.data.type.Hopper) relative.getBlockData()).getFacing()).equals(block)) {
                        hoppers.add(relative);
                    }
                }
            }
            return hoppers;
        } else {
            for (BlockFace face : faces) {
                if (!block.getRelative(face).getType().equals(Material.HOPPER)) {
                    continue;
                }
                Hopper hopper = (Hopper) block.getRelative(face).getState();
                int data = hopper.getRawData();
                BlockFace relative = BlockFace.DOWN;
                switch (data) {
                    case 0:
                        relative = BlockFace.DOWN;
                        break;
                    case 2:
                        relative = BlockFace.NORTH;
                        break;
                    case 3:
                        relative = BlockFace.SOUTH;
                        break;
                    case 4:
                        relative = BlockFace.WEST;
                        break;
                    case 5:
                        relative = BlockFace.EAST;
                        break;
                    case 8:
                        relative = BlockFace.DOWN;
                        break;
                    case 10:
                        relative = BlockFace.NORTH;
                        break;
                    case 11:
                        relative = BlockFace.SOUTH;
                        break;
                    case 12:
                        relative = BlockFace.WEST;
                        break;
                    case 13:
                        relative = BlockFace.EAST;
                        break;
                    default:
                        relative = BlockFace.DOWN;
                }
                if (block.getRelative(face).getRelative(relative).equals(block)) {
                    hoppers.add(block.getRelative(face));
                }
            }
        }
        return hoppers;
    }

    public static boolean isAllow(Block block) {
        if (Bookshelf.blockLockerHook) {
            if (!BlockLockerUtils.canRedstone(block)) {
                return false;
            }
        }
        if (Bookshelf.lwcHook) {
            if (!LWCUtils.checkHopperFlagOut(block)) {
                return false;
            }
            return LWCUtils.checkHopperFlagIn(block.getRelative(BlockFace.DOWN));
        }
        return true;
    }

}

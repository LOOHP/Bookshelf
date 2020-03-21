package com.loohp.bookshelf.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

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
import org.bukkit.scheduler.BukkitRunnable;

import com.loohp.bookshelf.Bookshelf;

public class HopperUtils {
	
	public static void hopperMinecartCheck() {
		Bookshelf.HopperMinecartTaskID = new BukkitRunnable() {
			public void run() {
				long start = System.currentTimeMillis();
				for (World world : Bukkit.getWorlds()) {
					for (Entity entity : world.getEntities()) {
						if (entity.getType().equals(EntityType.MINECART_HOPPER)) {
							HopperMinecart hoppercart = (HopperMinecart) entity;
							if (hoppercart.isEnabled() == false) {
								continue;
							}
							if (hoppercart.getLocation().getBlock().getRelative(BlockFace.UP) == null) {
								continue;
							}
							if (!hoppercart.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.BOOKSHELF)) {
								continue;
							}
							if (Bookshelf.BlockLockerHook == true) {
								if (BlockLockerUtils.isLocked(hoppercart.getLocation().getBlock().getRelative(BlockFace.UP)) == true) {
									continue;
								}
							}
							String key = BookshelfUtils.locKey(hoppercart.getLocation().getBlock().getRelative(BlockFace.UP).getLocation());
							if (!Bookshelf.bookshelfContent.containsKey(key)) {
								continue;
							}							
							if (Bookshelf.LWCHook == true) {
								if (LWCUtils.checkHopperFlagOut(hoppercart.getLocation().getBlock().getRelative(BlockFace.UP)) == false) {
									continue;
								}
							}
							if (Bookshelf.LWCHook == true) {
								if (LWCUtils.checkHopperFlagIn(hoppercart) == false) {
									continue;
								}
							}
							Inventory inventory = hoppercart.getInventory();
							Inventory bookshelfInv = Bookshelf.bookshelfContent.get(key);			           
				            for (int i = 0; i < bookshelfInv.getSize(); i = i + 1) {
				            	ItemStack item = bookshelfInv.getItem(i);
				            	if (item == null) {
				            		continue;
				            	}
				            	if (InventoryUtils.hasAvaliableSlot(inventory, item.getType())) {
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
					            	Bookshelf.bookshelfSavePending.add(key);
					            	break;
				            	}
				            }
						}
					}
				}
				long end = System.currentTimeMillis();
				if ((end - start) > 500) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Hopper Minecart Item Move Function took more than 500ms! (" + (end - start) + "ms)");
				}
			}
		}.runTaskTimer(Bookshelf.plugin, 0, 1).getTaskId();
	}
	
	public static void hopperCheck() {
		Bookshelf.HopperTaskID = new BukkitRunnable() {
			public void run() {	
				long start = System.currentTimeMillis();
				for (Entry<String, Inventory> entry : Bookshelf.bookshelfContent.entrySet()) {
					Location loc = BookshelfUtils.keyLoc(entry.getKey());	
					if (!loc.getBlock().getType().equals(Material.BOOKSHELF)) {
						continue;
					}
					if (Bookshelf.BlockLockerHook == true) {
						if (BlockLockerUtils.canRedstone(loc.getBlock()) == false) {
							continue;
						}
					}
					if (loc.getBlock().getRelative(BlockFace.DOWN) == null) {
						continue;
					}
					if (Bookshelf.LWCHook == true) {
						if (LWCUtils.checkHopperFlagOut(loc.getBlock()) == false) {
							continue;
						}
					}
					if (Bookshelf.LWCHook == true) {
						if (LWCUtils.checkHopperFlagIn(loc.getBlock().getRelative(BlockFace.DOWN)) == false) {
							continue;
						}
					}
					if (loc.getBlock().getRelative(BlockFace.DOWN).isBlockPowered() || loc.getBlock().getRelative(BlockFace.DOWN).isBlockIndirectlyPowered()) {
						continue;
					}
					if (loc.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.HOPPER)) {
						org.bukkit.block.Hopper h = (org.bukkit.block.Hopper) loc.getBlock().getRelative(BlockFace.DOWN).getState();
						Inventory inventory = h.getInventory();
						Inventory bookshelfInv = entry.getValue();			           
			            for (int i = 0; i < bookshelfInv.getSize(); i = i + 1) {
			            	ItemStack item = bookshelfInv.getItem(i);
			            	if (item == null) {
			            		continue;
			            	}
			            	if (InventoryUtils.hasAvaliableSlot(inventory, item.getType())) {
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
				            	Bookshelf.bookshelfSavePending.add(entry.getKey());
				            	break;
			            	}
			            }
					}
				}
				
				for (Entry<String, Inventory> entry : Bookshelf.bookshelfContent.entrySet()) {
					Location loc = BookshelfUtils.keyLoc(entry.getKey());		
					if (Bookshelf.BlockLockerHook == true) {
						if (BlockLockerUtils.canRedstone(loc.getBlock()) == false) {
							continue;
						}
					}
					List<Block> hoppers = getHoppersIn(loc.getBlock(), BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);					
					for (Block hopper : hoppers) {				
						if (hopper.isBlockPowered() || hopper.isBlockIndirectlyPowered()) {
							continue;
						}
						if (Bookshelf.LWCHook == true) {
							if (LWCUtils.checkHopperFlagOut(hopper) == false) {
								continue;
							}
							if (LWCUtils.checkHopperFlagIn(loc.getBlock()) == false) {
								continue;
							}
						}
						org.bukkit.block.Hopper h = (org.bukkit.block.Hopper) hopper.getState();
			            Inventory inventory = h.getInventory();
			            Inventory bookshelfInv = entry.getValue();
			            for (int i = 0; i < inventory.getSize(); i = i + 1) {
			            	ItemStack item = inventory.getItem(i);
			            	if (item == null) {
			            		continue;
			            	}
			            	if (Bookshelf.UseWhitelist == true) {
				            	if (Bookshelf.Whitelist.contains(item.getType().toString().toUpperCase())) {
				            		if (InventoryUtils.hasAvaliableSlot(bookshelfInv, item.getType())) {
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
							            	inventory.setItem(i, item);		
					            		}           
					            		bookshelfInv.addItem(additem);
					            		Bookshelf.bookshelfSavePending.add(entry.getKey());
					            		break;
				            		}
				            	}
			            	} else {
			            		if (InventoryUtils.hasAvaliableSlot(bookshelfInv, item.getType())) {
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
						            	inventory.setItem(i, item);		
				            		}                    
				            		bookshelfInv.addItem(additem);
				            		Bookshelf.bookshelfSavePending.add(entry.getKey());
				            		break;
			            		}
			            	}
			            }
					}
				}
				long end = System.currentTimeMillis();
				//Bukkit.getConsoleSender().sendMessage("(" + (end - start) + "ms)");
				if ((end - start) > 500) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Hopper Item Move Function took more than 500ms! (" + (end - start) + "ms)");
				}
			}
		}.runTaskTimer(Bookshelf.plugin, 0, Bookshelf.HopperTicksPerTransfer).getTaskId();
	}
	
    @SuppressWarnings("deprecation")
	public static List<Block> getHoppersIn(Block block, BlockFace... faces) {
        List<Block> hoppers = new ArrayList<Block>();
        if (!Bookshelf.version.contains("legacy")) {
	        for (BlockFace face : faces) {
	            Block relative = block.getRelative(face);
	            if (relative.getType().equals(Material.HOPPER)) {
	                if (relative.getRelative(((org.bukkit.block.data.type.Hopper) relative.getBlockData()).getFacing()).equals(block)) {
	                    hoppers.add(relative);
	                }
	            }
	        }
        } else {
        	for (BlockFace face : faces) {
        		if (!block.getRelative(face).getType().equals(Material.HOPPER)) {
        			continue;
        		}
        		Hopper hopper = (Hopper) block.getRelative(face).getState();
        		int data = hopper.getRawData();
        		BlockFace relative = BlockFace.DOWN;
        		switch(data) {
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
    
}

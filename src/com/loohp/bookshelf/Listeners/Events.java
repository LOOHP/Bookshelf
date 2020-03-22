package com.loohp.bookshelf.Listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.BookshelfManager;
import com.loohp.bookshelf.Utils.BlockLockerUtils;
import com.loohp.bookshelf.Utils.BookshelfUtils;
import com.loohp.bookshelf.Utils.DropperUtils;
import com.loohp.bookshelf.Utils.InventoryUtils;
import com.loohp.bookshelf.Utils.LWCUtils;
import com.loohp.bookshelf.Utils.ReverseList;

public class Events implements Listener {
	
//	@EventHandler
//	public void onBlockUpdate(BlockPhysicsEvent event) {
//		if (!event.getBlock().getType().equals(Material.COMPARATOR)) {
//			return;
//		}
//		Block bookshelfBlock = null;
//		Comparator comparatorData = (Comparator) event.getBlock().getBlockData();
//		BlockFace face = comparatorData.getFacing();
//		if (event.getBlock().getRelative(face).getType().equals(Material.BOOKSHELF)) {
//			bookshelfBlock = event.getBlock().getRelative(face);
//		} else if (event.getBlock().getRelative(face).getRelative(face).getType().equals(Material.BOOKSHELF)) {
//    		bookshelfBlock = event.getBlock().getRelative(face).getRelative(face);
//		} else {
//			return;
//		}
//		String loc = BookshelfUtils.locKey(bookshelfBlock.getLocation());
//		if (!Bookshelf.bookshelfContent.containsKey(loc)) {
//			return;
//		}
//		Inventory inv = Bookshelf.bookshelfContent.get(loc);
//		double slotFullness = 0.0;
//		for (ItemStack item : inv.getContents()) {
//			if (item != null) {
//				slotFullness = slotFullness + (item.getAmount() / item.getType().getMaxStackSize());
//			}
//		}
//		int signalStrength = (int) Math.floor(1 + (slotFullness / inv.getSize()) * 14);
//		if (signalStrength > 0) {
//			Bukkit.getConsoleSender().sendMessage(signalStrength + "");
//			comparatorData.setPowered(true);
//			event.getBlock().setBlockData(comparatorData);
//			if (event.getBlock().getRelative(RedstoneUtils.getOppositeFace(face)).getType().equals(Material.REDSTONE_WIRE)) {
//				AnaloguePowerable powerable = (AnaloguePowerable) event.getBlock().getRelative(RedstoneUtils.getOppositeFace(face)).getBlockData();
//				powerable.setPower(signalStrength);
//				event.getBlock().getRelative(RedstoneUtils.getOppositeFace(face)).setBlockData(powerable);
//			}
//		} else {
//			
//		}
//	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDropper(BlockDispenseEvent event) {
		if (Bookshelf.EnableDropperSupport == false) {
			return;
		}
		if (event.isCancelled() == true) {
			return;
		}
		if (!event.getBlock().getType().equals(Material.DROPPER)) {
			return;
		}
		Block relative = DropperUtils.getDropperRelative(event.getBlock());
		if (!relative.getType().equals(Material.BOOKSHELF)) {
			return;
		}
		String key = BookshelfUtils.locKey(relative.getLocation());
		if (!Bookshelf.bookshelfContent.containsKey(key)) {
			if (!BookshelfManager.contains(key)) {
				String bsTitle = Bookshelf.Title;
				Bookshelf.bookshelfContent.put(key , Bukkit.createInventory(null, (int) (Bookshelf.BookShelfRows * 9), bsTitle));
				BookshelfManager.setTitle(key, bsTitle);
				BookshelfUtils.saveBookShelf(key);
			} else {
				BookshelfUtils.loadBookShelf(key);
			}
		}
		if (Bookshelf.LWCHook == true) {
			if (LWCUtils.checkHopperFlagIn(relative) == false) {
				event.setCancelled(true);
				return;
			}
		}
		if (Bookshelf.BlockLockerHook == true) {
			if (BlockLockerUtils.canRedstone(relative) == false) {
				event.setCancelled(true);
				return;
			}
		}
		event.setCancelled(true);
		Inventory bookshelf = Bookshelf.bookshelfContent.get(key);
		org.bukkit.block.Dropper d = (org.bukkit.block.Dropper) event.getBlock().getState();
		Inventory dropper = d.getInventory();
		List<ItemStack> newList = Arrays.asList(dropper.getContents());
		Collections.shuffle(newList);
		for (ItemStack each : newList) {
			if (each == null) {
				continue;
			}
			if (Bookshelf.UseWhitelist == true) {
				if (!Bookshelf.Whitelist.contains(each.getType().toString().toUpperCase())) {
					continue;
				}
			}
			if (InventoryUtils.hasAvaliableSlot(bookshelf, each.getType()) == false) {
				continue;
			}
			ItemStack additem = each.clone();
			additem.setAmount(1);
			bookshelf.addItem(additem);
			for (int i = 0; i < dropper.getSize(); i = i + 1) {
				ItemStack removeitem = dropper.getItem(i);
            	if (removeitem == null) {
            		continue;
            	}
            	if (removeitem.equals(each)) {
            		removeitem.setAmount(removeitem.getAmount() - 1);
            		dropper.setItem(i, removeitem);
            	}
			}
			Bookshelf.bookshelfSavePending.add(key);
			if (!Bookshelf.version.contains("legacy")) {
				event.getBlock().getWorld().playSound(event.getBlock().getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
			}
			return;
		}
		if (!Bookshelf.version.contains("legacy")) {
			event.getBlock().getWorld().playSound(event.getBlock().getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1, 1);
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		if (Bookshelf.EnableHopperSupport == false) {
			return;
		}
		Chunk chunk = event.getChunk();
		for (Block block : BookshelfUtils.getAllBookshelvesInChunk(chunk)) {
			String loc = BookshelfUtils.locKey(block.getLocation());
			if (!Bookshelf.bookshelfContent.containsKey(loc)) {
				if (!BookshelfManager.contains(loc)) {
					String bsTitle = Bookshelf.Title;
					Bookshelf.bookshelfContent.put(loc , Bukkit.createInventory(null, (int) (Bookshelf.BookShelfRows * 9), bsTitle));
					BookshelfManager.setTitle(loc, bsTitle);
					BookshelfUtils.saveBookShelf(loc);
				} else {
					BookshelfUtils.loadBookShelf(loc);
				}
			}
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkUnloadEvent event) {
		if (Bookshelf.EnableHopperSupport == false) {
			return;
		}
		Chunk chunk = event.getChunk();
		for (Block block : BookshelfUtils.getAllBookshelvesInChunk(chunk)) {
			String loc = BookshelfUtils.locKey(block.getLocation());
			if (Bookshelf.bookshelfContent.containsKey(loc)) {
				BookshelfUtils.saveBookShelf(loc, true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPistonExtend(BlockPistonExtendEvent event) {
		if (event.isCancelled()) {
			return;
		}
		List<Block> bookshelves = new ArrayList<Block>();
		for (Block block : event.getBlocks()) {
			if (block.getType().equals(Material.BOOKSHELF)) {
				String key = BookshelfUtils.locKey(block.getLocation());
				if (!Bookshelf.bookshelfContent.containsKey(key)) {
					if (BookshelfManager.contains("BookShelfData." + key)) {
						BookshelfUtils.loadBookShelf(key);
						bookshelves.add(block);
					}
				} else {
					bookshelves.add(block);
				}
			}
		}
		
		if (bookshelves.isEmpty()) {
			return;
		}
		
		BlockFace dir = event.getDirection();
		for (Block bookshelf : ReverseList.reversed(bookshelves)) {
			String key = BookshelfUtils.locKey(bookshelf.getLocation());
			Inventory inv = Bookshelf.bookshelfContent.get(key);
			Location newLoc = bookshelf.getRelative(dir).getLocation().clone();
			String newKey = BookshelfUtils.locKey(newLoc);
			String bsTitle = Bookshelf.Title;
			if (BookshelfManager.getTitle(key) != null) {
				bsTitle = BookshelfManager.getTitle(key);
			}
			BookshelfUtils.safeRemoveBookself(key);
			
			Bookshelf.bookshelfContent.put(newKey, inv);
			BookshelfManager.setTitle(newKey, bsTitle);
			
			BookshelfUtils.saveBookShelf(newKey);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPistonRetract(BlockPistonRetractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		List<Block> bookshelves = new ArrayList<Block>();
		for (Block block : event.getBlocks()) {
			if (block.getType().equals(Material.BOOKSHELF)) {
				String key = BookshelfUtils.locKey(block.getLocation());
				if (!Bookshelf.bookshelfContent.containsKey(key)) {
					if (BookshelfManager.contains(key)) {
						BookshelfUtils.loadBookShelf(key);
						bookshelves.add(block);
					}
				} else {
					bookshelves.add(block);
				}
			}
		}
		
		if (bookshelves.isEmpty()) {
			return;
		}
		
		BlockFace dir = event.getDirection();
		for (Block bookshelf : ReverseList.reversed(bookshelves)) {
			String key = BookshelfUtils.locKey(bookshelf.getLocation());
			Inventory inv = Bookshelf.bookshelfContent.get(key);
			Location newLoc = bookshelf.getRelative(dir).getLocation().clone();
			String newKey = BookshelfUtils.locKey(newLoc);
			String bsTitle = Bookshelf.Title;
			if (BookshelfManager.getTitle(key) != null) {
				bsTitle = BookshelfManager.getTitle(key);
			}
			BookshelfUtils.safeRemoveBookself(key);
			
			Bookshelf.bookshelfContent.put(newKey, inv);
			BookshelfManager.setTitle(newKey, bsTitle);
			
			BookshelfUtils.saveBookShelf(newKey);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (!event.getPlayer().hasPermission("bookshelf.use")) {
			return;
		}
		
		if (event.getBlockAgainst().getType().equals(Material.BOOKSHELF)) {
			if (event.getPlayer().isSneaking() == false && Bookshelf.lastBlockFace.containsKey(event.getPlayer())) {
				BlockFace face = Bookshelf.lastBlockFace.get(event.getPlayer());				
				
				if (face.equals(BlockFace.EAST) || face.equals(BlockFace.SOUTH) || face.equals(BlockFace.WEST) || face.equals(BlockFace.NORTH)) {
					event.setCancelled(true);
					return;
				}
			}
		}
		
		if (!event.getBlockPlaced().getType().equals(Material.BOOKSHELF)) {
			return;
		}
		
		String loc = BookshelfUtils.locKey(event.getBlockPlaced().getLocation());
		if (Bookshelf.bookshelfContent.containsKey(loc)) {
			return;
		}
		if (BookshelfManager.contains(loc)) {
			return;
		}
		
		String bsTitle = Bookshelf.Title;
		if (event.getItemInHand().hasItemMeta() == true) {
			if (event.getItemInHand().getItemMeta().hasDisplayName() == true) {
				if (!event.getItemInHand().getItemMeta().getDisplayName().equals("")) {
					bsTitle = event.getItemInHand().getItemMeta().getDisplayName();
				}
			}
		}
		Bookshelf.bookshelfContent.put(loc, Bukkit.createInventory(null, (int) (Bookshelf.BookShelfRows * 9), bsTitle));
		BookshelfManager.setTitle(loc, bsTitle);
		BookshelfUtils.saveBookShelf(loc);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (!event.getBlock().getType().equals(Material.BOOKSHELF)) {
			return;
		}
		
		String loc = BookshelfUtils.locKey(event.getBlock().getLocation());
		if (!Bookshelf.bookshelfContent.containsKey(loc)) {
			if (!BookshelfManager.contains(loc)) {
				return;
			}
			BookshelfUtils.loadBookShelf(loc);
		}
		Inventory inv = Bookshelf.bookshelfContent.get(loc);
		for (ItemStack item : inv.getContents()) {
			if (item != null && !item.getType().equals(Material.AIR)) {
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
			}
		}
		BookshelfUtils.safeRemoveBookself(loc);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onExplode(EntityExplodeEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		List<Block> bookshelves = new ArrayList<Block>();
		for (Block block : event.blockList()) {
			if (block.getType().equals(Material.BOOKSHELF)) {
				String key = BookshelfUtils.locKey(block.getLocation());
				if (!Bookshelf.bookshelfContent.containsKey(key)) {
					if (BookshelfManager.contains(key)) {
						BookshelfUtils.loadBookShelf(key);
						bookshelves.add(block);
					}
				} else {
					bookshelves.add(block);
				}
			}
		}
		
		if (bookshelves.isEmpty()) {
			return;
		}
		
		for (Block bookshelf : bookshelves) {
			String loc = BookshelfUtils.locKey(bookshelf.getLocation());
			if (!Bookshelf.bookshelfContent.containsKey(loc)) {
				if (!BookshelfManager.contains(loc)) {
					return;
				}
				BookshelfUtils.loadBookShelf(loc);
			}
			Inventory inv = Bookshelf.bookshelfContent.get(loc);
			for (ItemStack item : inv.getContents()) {
				if (item != null && !item.getType().equals(Material.AIR)) {
					bookshelf.getWorld().dropItemNaturally(bookshelf.getLocation(), item);
				}
			}
			BookshelfUtils.safeRemoveBookself(loc);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBurn(BlockBurnEvent event) {
		if (event.getBlock().getType().equals(Material.BOOKSHELF)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onUse(InventoryClickEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (event.getRawSlot() == -999) {
			return;
		}
		
		if (event.getView().getType().equals(InventoryType.CREATIVE)) {
			return;
		}
		
		if (event.getView().getTopInventory() == null) {
			return;
		}
		
		Player player = (Player) event.getWhoClicked();
		
		if (Bookshelf.isDonationView.contains(player)) {
			Inventory clicked = event.getClickedInventory();
			if (Bookshelf.bookshelfContent.containsValue(clicked)) {
				if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) || event.getAction().equals(InventoryAction.PICKUP_SOME) || event.getAction().equals(InventoryAction.PICKUP_ALL) || event.getAction().equals(InventoryAction.PICKUP_ONE) || event.getAction().equals(InventoryAction.PICKUP_HALF)) {
					event.setCancelled(true);
					return;
				}
			}
		}
		
		if (Bookshelf.UseWhitelist == false) {
			return;
		}
		if (event.getAction().equals(InventoryAction.NOTHING) || event.getAction().equals(InventoryAction.UNKNOWN) || event.getAction().equals(InventoryAction.DROP_ALL_CURSOR) || event.getAction().equals(InventoryAction.DROP_ALL_SLOT) || event.getAction().equals(InventoryAction.DROP_ONE_CURSOR) || event.getAction().equals(InventoryAction.DROP_ONE_SLOT  )) {
        	return;
        }
		
		if (event.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD) || event.getAction().equals(InventoryAction.HOTBAR_SWAP)) {
			int slot = event.getRawSlot();
			int inventorySize = event.getView().getTopInventory().getSize();
			if (slot < inventorySize) {
				if (event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) != null) {
					if (!Bookshelf.Whitelist.contains(event.getWhoClicked().getInventory().getItem(event.getHotbarButton()).getType().toString().toUpperCase())) { 
						event.setCancelled(true);
						return;
					}
				}
            }
			return;
		}
		
		Inventory inv = event.getView().getTopInventory();
		for (Entry<String, Inventory> entry : Bookshelf.bookshelfContent.entrySet()) {
			if (entry.getValue().equals(inv)) {
				Bookshelf.bookshelfSavePending.add(entry.getKey());
				if (event.getClick().isShiftClick()) {
					ItemStack clickedOn = event.getCurrentItem();

			        if (clickedOn != null) {
			        	if (!clickedOn.getType().equals(Material.AIR)) {
				        	if (!Bookshelf.Whitelist.contains(clickedOn.getType().toString().toUpperCase())) {
				            	event.setCancelled(true);
				            	return;
				            }
			        	}
			        }
			        
			    }
				Inventory clicked = event.getClickedInventory();
			    if (clicked.equals(event.getView().getTopInventory())) {
			        ItemStack onCursor = event.getCursor();

			        if (onCursor != null){
			        	if (!onCursor.getType().equals(Material.AIR)) {
				        	if (!Bookshelf.Whitelist.contains(onCursor.getType().toString().toUpperCase())) {
			            		event.setCancelled(true);
			            		return;
			            	}
			        	}
			        }
			    }
				break;
			}
		}
		boolean putting = false;
		if (event.getClickedInventory().equals(event.getView().getTopInventory())) {
			putting = true;
		}
		if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && event.getClickedInventory().equals(event.getView().getBottomInventory())) {
			putting = true;
		}
		if (event.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD) || event.getAction().equals(InventoryAction.HOTBAR_SWAP)) {
			int slot = event.getRawSlot();
			int inventorySize = event.getView().getTopInventory().getSize();
			if (slot < inventorySize) {
				putting = true;
			}
		}
		
		if (putting == false) {
			return;
		}
		for (Entry<String, Inventory> entry : Bookshelf.bookshelfContent.entrySet()) {
			if (entry.getValue().equals(event.getView().getTopInventory())) {
				Location loc = BookshelfUtils.keyLoc(entry.getKey());
				double random = Math.floor(Math.random() * 3) + 1;
				if (Bookshelf.version.contains("OLD")) {
					event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("HORSE_ARMOR"), 10, 1);
        		} else if (Bookshelf.version.contains("legacy") || Bookshelf.version.equals("1.13") || Bookshelf.version.equals("1.13.1")) {
					event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("ITEM_ARMOR_EQUIP_LEATHER"), 10, 1);
				} else if (Bookshelf.version.equals("1.14") || Bookshelf.version.equals("1.15")) {
					if (random == 1) {
						event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("ITEM_BOOK_PUT"), 10, 1);
					} else if (random == 2) {
						event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("ITEM_ARMOR_EQUIP_LEATHER"), 10, 1);
					} else if (random == 3) {
						event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("ITEM_BOOK_PAGE_TURN"), 10, 1);
					}
				}
				break;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDrag(InventoryDragEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (Bookshelf.UseWhitelist == false) {
			return;
		}
		
		if (event.getView().getTopInventory() == null) {
			return;
		}
		
		Inventory inv = event.getView().getTopInventory();
		for (Entry<String, Inventory> entry : Bookshelf.bookshelfContent.entrySet()) {
			if (entry.getValue().equals(inv)) {
				ItemStack dragged = event.getOldCursor(); // This is the item that is being dragged

			    if (!Bookshelf.Whitelist.contains(dragged.getType().toString().toUpperCase())) {
			        int inventorySize = entry.getValue().getSize();

			        for (int i : event.getRawSlots()) {
			            if (i < inventorySize) {
			                event.setCancelled(true);
			                return;
			            }
			        }
			    }
			    
			    int inventorySize = entry.getValue().getSize();

		        for (int i : event.getRawSlots()) {
		        	if (i < inventorySize) {
		        		Location loc = BookshelfUtils.keyLoc(entry.getKey());
						double random = Math.floor(Math.random() * 3) + 1;
						if (Bookshelf.version.contains("OLD")) {
							event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("HORSE_ARMOR"), 10, 1);
		        		} else if (Bookshelf.version.contains("legacy") || Bookshelf.version.equals("1.13") || Bookshelf.version.equals("1.13.1")) {
							event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("ITEM_ARMOR_EQUIP_LEATHER"), 10, 1);
						} else if (Bookshelf.version.equals("1.14") || Bookshelf.version.equals("1.15")) {
							if (random == 1) {
								event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("ITEM_BOOK_PUT"), 10, 1);
							} else if (random == 2) {
								event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("ITEM_ARMOR_EQUIP_LEATHER"), 10, 1);
							} else if (random == 3) {
								event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("ITEM_BOOK_PAGE_TURN"), 10, 1);
							}
						}
						break;
		        	}
		        }
				break;
			}
		}
		
		
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onOpen(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		
		if (!Bookshelf.version.contains("OLD")) {
			if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
				return;
			}
		}
		
		Player player = event.getPlayer();
		
		if (!player.hasPermission("bookshelf.use")) {
			return;
		}
		
		if (Bookshelf.cancelOpen.contains(event.getPlayer())) {
			return;
		}
		if (player.isSneaking() == true) {
			return;
		}
		if (event.getClickedBlock() == null) {
			return;
		}
		if (!event.getClickedBlock().getType().equals(Material.BOOKSHELF)) {
			return;
		}
		
		Bookshelf.lastBlockFace.put(event.getPlayer(), event.getBlockFace());
		new BukkitRunnable() {
			public void run() {
				Bookshelf.lastBlockFace.remove(event.getPlayer());
			}
		}.runTaskLater(Bookshelf.plugin, 2);

		if (event.getBlockFace().equals(BlockFace.UP) || event.getBlockFace().equals(BlockFace.DOWN)) {
			return;
		}
		
		String loc = BookshelfUtils.locKey(event.getClickedBlock().getLocation());
		if (!Bookshelf.bookshelfContent.containsKey(loc)) {
			if (!BookshelfManager.contains(loc)) {
				String bsTitle = Bookshelf.Title;
				Bookshelf.bookshelfContent.put(loc , Bukkit.createInventory(null, (int) (Bookshelf.BookShelfRows * 9), bsTitle));
				BookshelfManager.setTitle(loc, bsTitle);
				BookshelfUtils.saveBookShelf(loc);
			} else {
				BookshelfUtils.loadBookShelf(loc);
			}
		}
		if (Bookshelf.LWCHook == true) {
			Location blockLoc = BookshelfUtils.keyLoc(loc);
			Protection protection = LWC.getInstance().getPlugin().getLWC().findProtection(blockLoc.getBlock());
			if (protection != null) {
				if (protection.isOwner(player)) {
					Inventory inv = Bookshelf.bookshelfContent.get(loc);
					Bukkit.getScheduler().runTask(Bookshelf.plugin, () -> player.openInventory(inv));
					if (!Bookshelf.bookshelfSavePending.contains(loc)) {
						Bookshelf.bookshelfSavePending.add(loc);
					}
				} else {
					Bookshelf.requestOpen.put(player, loc);
				}
			} else {
				Inventory inv = Bookshelf.bookshelfContent.get(loc);
				Bukkit.getScheduler().runTask(Bookshelf.plugin, () -> player.openInventory(inv));
				if (!Bookshelf.bookshelfSavePending.contains(loc)) {
					Bookshelf.bookshelfSavePending.add(loc);
				}
			}
			return;
		}		
		Inventory inv = Bookshelf.bookshelfContent.get(loc);
		Bukkit.getScheduler().runTask(Bookshelf.plugin, () -> player.openInventory(inv));
		if (!Bookshelf.bookshelfSavePending.contains(loc)) {
			Bookshelf.bookshelfSavePending.add(loc);
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		Inventory inv = event.getView().getTopInventory();
		for (Entry<String, Inventory> entry : Bookshelf.bookshelfContent.entrySet()) {
			if (entry.getValue().equals(inv)) {
				Bookshelf.bookshelfSavePending.add(entry.getKey());
				break;
			}
		}
		while (Bookshelf.isDonationView.contains(event.getPlayer())) {
			Bookshelf.isDonationView.remove(event.getPlayer());
		}
	}	
}
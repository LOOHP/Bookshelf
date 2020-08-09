package com.loohp.bookshelf.Listeners;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.BookshelfManager;
import com.loohp.bookshelf.API.Events.PlayerCloseBookshelfEvent;
import com.loohp.bookshelf.API.Events.PlayerOpenBookshelfEvent;
import com.loohp.bookshelf.ObjectHolders.LWCRequestOpenData;
import com.loohp.bookshelf.Utils.BookshelfUtils;
import com.loohp.bookshelf.Utils.MCVersion;
import com.loohp.bookshelf.Utils.NBTUtils;

public class BookshelfEvents implements Listener {

/*	
	@EventHandler
	public void onBlockUpdate(BlockPhysicsEvent event) {
		if (!event.getBlock().getType().equals(Material.COMPARATOR)) {
			return;
		}
		Block bookshelfBlock = null;
		Comparator comparatorData = (Comparator) event.getBlock().getBlockData();
		BlockFace face = comparatorData.getFacing();
		if (event.getBlock().getRelative(face).getType().equals(Material.BOOKSHELF)) {
			bookshelfBlock = event.getBlock().getRelative(face);
		} else if (event.getBlock().getRelative(face).getRelative(face).getType().equals(Material.BOOKSHELF)) {
    		bookshelfBlock = event.getBlock().getRelative(face).getRelative(face);
		} else {
			return;
		}
		String loc = BookshelfUtils.locKey(bookshelfBlock.getLocation());
		if (!Bookshelf.bookshelfContent.containsKey(loc)) {
			return;
		}
		Inventory inv = Bookshelf.bookshelfContent.get(loc);
		double slotFullness = 0.0;
		for (ItemStack item : inv.getContents()) {
			if (item != null) {
				slotFullness = slotFullness + (item.getAmount() / item.getType().getMaxStackSize());
			}
		}
		int signalStrength = (int) Math.floor(1 + (slotFullness / inv.getSize()) * 14);
		if (signalStrength > 0) {
			Bukkit.getConsoleSender().sendMessage(signalStrength + "");
			comparatorData.setPowered(true);
			event.getBlock().setBlockData(comparatorData);
			if (event.getBlock().getRelative(RedstoneUtils.getOppositeFace(face)).getType().equals(Material.REDSTONE_WIRE)) {
				AnaloguePowerable powerable = (AnaloguePowerable) event.getBlock().getRelative(RedstoneUtils.getOppositeFace(face)).getBlockData();
				powerable.setPower(signalStrength);
				event.getBlock().getRelative(RedstoneUtils.getOppositeFace(face)).setBlockData(powerable);
			}
		} else {
			
 		}
	}
*/	
	
	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		Bookshelf.loadBookshelf(event.getWorld());
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		if (!Bookshelf.EnableHopperSupport) {
			return;
		}
		Chunk chunk = event.getChunk();
		Bookshelf.bookshelfLoadPending.add(chunk);
		while (Bookshelf.bookshelfRemovePending.contains(chunk)) {
			Bookshelf.bookshelfRemovePending.remove(chunk);
		}
	}
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		if (!Bookshelf.EnableHopperSupport) {
			return;
		}
		Chunk chunk = event.getChunk();
		Bookshelf.bookshelfRemovePending.add(chunk);
		while (Bookshelf.bookshelfLoadPending.contains(chunk)) {
			Bookshelf.bookshelfLoadPending.remove(chunk);
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
			if (!event.getPlayer().isSneaking() && Bookshelf.lastBlockFace.containsKey(event.getPlayer())) {
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
		ItemStack item = event.getItemInHand();
		if (Bookshelf.version.isLegacy() || Bookshelf.version.equals(MCVersion.V1_13) || Bookshelf.version.equals(MCVersion.V1_13_1)) {
			if (NBTUtils.contains(item, "BookshelfContent") && NBTUtils.contains(item, "BookshelfTitle")) {
				String title = NBTUtils.getString(item, "BookshelfTitle");
				if (!item.getItemMeta().getDisplayName().equals("")) {
					title = item.getItemMeta().getDisplayName();
				}
				String hash = NBTUtils.getString(item, "BookshelfContent");
				try {
					Bookshelf.addBookshelfToMapping(loc, BookshelfUtils.fromBase64(hash, title));
				} catch (IOException e) {
					e.printStackTrace();
				}		
				BookshelfManager.setTitle(loc, title);
				BookshelfUtils.saveBookShelf(loc);
				return;
			}
		} else {
			ItemMeta meta = item.getItemMeta();
			PersistentDataContainer pdc = meta.getPersistentDataContainer();
			NamespacedKey keyContent = new NamespacedKey(Bookshelf.plugin, "BookshelfContent");
			NamespacedKey keyTitle = new NamespacedKey(Bookshelf.plugin, "BookshelfTitle");
			if (pdc.has(keyContent, PersistentDataType.STRING) && pdc.has(keyTitle, PersistentDataType.STRING)) {
				String hash = pdc.get(keyContent, PersistentDataType.STRING);
				String title = pdc.get(keyTitle, PersistentDataType.STRING);
				try {
					Bookshelf.addBookshelfToMapping(loc, BookshelfUtils.fromBase64(hash, title));
				} catch (IOException e) {
					e.printStackTrace();
				}		
				BookshelfManager.setTitle(loc, title);
				BookshelfUtils.saveBookShelf(loc);
				return;
			}
		}
		
		if (Bookshelf.keyToContentMapping.containsKey(loc)) {
			return;
		}
		if (BookshelfManager.contains(loc)) {
			return;
		}
		
		String bsTitle = Bookshelf.Title;
		if (event.getItemInHand().hasItemMeta()) {
			if (event.getItemInHand().getItemMeta().hasDisplayName()) {
				if (!event.getItemInHand().getItemMeta().getDisplayName().equals("")) {
					bsTitle = event.getItemInHand().getItemMeta().getDisplayName();
				}
			}
		}
		Bookshelf.addBookshelfToMapping(loc, Bukkit.createInventory(null, (int) (Bookshelf.BookShelfRows * 9), bsTitle));
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
		if (!Bookshelf.keyToContentMapping.containsKey(loc)) {
			if (!BookshelfManager.contains(loc)) {
				return;
			}
			BookshelfUtils.loadBookShelf(loc);
		}
		Inventory inv = Bookshelf.keyToContentMapping.get(loc);
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
				if (!Bookshelf.keyToContentMapping.containsKey(key)) {
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
			if (!Bookshelf.keyToContentMapping.containsKey(loc)) {
				if (!BookshelfManager.contains(loc)) {
					return;
				}
				BookshelfUtils.loadBookShelf(loc);
			}
			Inventory inv = Bookshelf.keyToContentMapping.get(loc);
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
			if (Bookshelf.contentToKeyMapping.containsKey(clicked)) {
				if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) || event.getAction().equals(InventoryAction.PICKUP_SOME) || event.getAction().equals(InventoryAction.PICKUP_ALL) || event.getAction().equals(InventoryAction.PICKUP_ONE) || event.getAction().equals(InventoryAction.PICKUP_HALF)) {
					event.setCancelled(true);
					return;
				}
			}
		}
		
		if (!Bookshelf.UseWhitelist) {
			return;
		}
		if (event.getAction().equals(InventoryAction.NOTHING) || event.getAction().equals(InventoryAction.UNKNOWN) || event.getAction().equals(InventoryAction.DROP_ALL_CURSOR) || event.getAction().equals(InventoryAction.DROP_ALL_SLOT) || event.getAction().equals(InventoryAction.DROP_ONE_CURSOR) || event.getAction().equals(InventoryAction.DROP_ONE_SLOT  )) {
        	return;
        }
		
		if (event.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD) || event.getAction().equals(InventoryAction.HOTBAR_SWAP)) {
			if (!Bookshelf.contentToKeyMapping.containsKey(event.getView().getTopInventory())) {
				return;
			}
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
		String key = Bookshelf.contentToKeyMapping.get(inv);
		if (key != null) {
			Bookshelf.bookshelfSavePending.add(key);
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
		}
		boolean putting = false;
		if (event.getClickedInventory().equals(event.getView().getTopInventory())) {
			putting = true;
		}
		if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && event.getClickedInventory().equals(event.getView().getBottomInventory())) {
			putting = true;
		}
		if (event.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD) || event.getAction().equals(InventoryAction.HOTBAR_SWAP)) {
			if (!Bookshelf.contentToKeyMapping.containsKey(event.getView().getTopInventory())) {
				return;
			}
			int slot = event.getRawSlot();
			int inventorySize = event.getView().getTopInventory().getSize();
			if (slot < inventorySize) {
				putting = true;
			}
		}
		
		if (!putting) {
			return;
		}
		
		for (Entry<String, Inventory> entry : Bookshelf.keyToContentMapping.entrySet()) {
			if (entry.getValue().equals(event.getView().getTopInventory())) {
				Location loc = BookshelfUtils.keyLoc(entry.getKey());
				double random = Math.floor(Math.random() * 3) + 1;
				if (Bookshelf.version.isOld()) {
					event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("HORSE_ARMOR"), 10, 1);
        		} else if (Bookshelf.version.isLegacy() || Bookshelf.version.equals(MCVersion.V1_13) || Bookshelf.version.equals(MCVersion.V1_13_1)) {
					event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("ITEM_ARMOR_EQUIP_LEATHER"), 10, 1);
				} else {
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
		
		if (!Bookshelf.UseWhitelist) {
			return;
		}
		
		if (event.getView().getTopInventory() == null) {
			return;
		}
		
		Inventory inv = event.getView().getTopInventory();
		String key = Bookshelf.contentToKeyMapping.get(inv);
		if (key != null) {
			ItemStack dragged = event.getOldCursor(); // This is the item that is being dragged

		    if (!Bookshelf.Whitelist.contains(dragged.getType().toString().toUpperCase())) {
		        int inventorySize = inv.getSize();

		        for (int i : event.getRawSlots()) {
		            if (i < inventorySize) {
		                event.setCancelled(true);
		                return;
		            }
		        }
		    }
		    
		    int inventorySize = inv.getSize();

	        for (int i : event.getRawSlots()) {
	        	if (i < inventorySize) {
	        		Location loc = BookshelfUtils.keyLoc(key);
					double random = Math.floor(Math.random() * 3) + 1;
					if (Bookshelf.version.isOld()) {
						event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("HORSE_ARMOR"), 10, 1);
					} else if (Bookshelf.version.isLegacy() || Bookshelf.version.equals(MCVersion.V1_13) || Bookshelf.version.equals(MCVersion.V1_13_1)) {
						event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("ITEM_ARMOR_EQUIP_LEATHER"), 10, 1);
					} else {
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
		
		if (!Bookshelf.version.isOld()) {
			if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
				return;
			}
		}
		
		Player player = event.getPlayer();
		
		if (player.isSneaking()) {
			return;
		}
		if (event.getClickedBlock() == null) {
			return;
		}
		if (!event.getClickedBlock().getType().equals(Material.BOOKSHELF)) {
			return;
		}
		if (Bookshelf.lwcCancelOpen.contains(event.getPlayer())) {
			return;
		}
		
		Bookshelf.lastBlockFace.put(event.getPlayer(), event.getBlockFace());
		Bukkit.getScheduler().runTaskLater(Bookshelf.plugin, () -> {
			Bookshelf.lastBlockFace.remove(event.getPlayer());
		}, 2);

		if (event.getBlockFace().equals(BlockFace.UP) || event.getBlockFace().equals(BlockFace.DOWN)) {
			return;
		}
		
		boolean cancelled = false;
		
		if (!player.hasPermission("bookshelf.use")) {
			cancelled = true;
		}
		
		String loc = BookshelfUtils.locKey(event.getClickedBlock().getLocation());
		if (!Bookshelf.keyToContentMapping.containsKey(loc)) {
			if (!BookshelfManager.contains(loc)) {
				String bsTitle = Bookshelf.Title;
				Bookshelf.addBookshelfToMapping(loc , Bukkit.createInventory(null, (int) (Bookshelf.BookShelfRows * 9), bsTitle));
				BookshelfManager.setTitle(loc, bsTitle);
				BookshelfUtils.saveBookShelf(loc);
			} else {
				BookshelfUtils.loadBookShelf(loc);
			}
		}
		if (Bookshelf.LWCHook) {
			Location blockLoc = BookshelfUtils.keyLoc(loc);
			Protection protection = LWC.getInstance().getPlugin().getLWC().findProtection(blockLoc.getBlock());
			if (protection != null) {
				if (!protection.isOwner(player)) {
					Bookshelf.requestOpen.put(player, new LWCRequestOpenData(loc, event.getBlockFace(), cancelled));
					return;
				}
			}
		}		
		
		PlayerOpenBookshelfEvent pobe = new PlayerOpenBookshelfEvent(player, loc, event.getBlockFace(), cancelled);
		Bukkit.getPluginManager().callEvent(pobe);
		
		if (pobe.isCancelled()) {
			return;
		}
		
		Inventory inv = Bookshelf.keyToContentMapping.get(loc);
		Bukkit.getScheduler().runTask(Bookshelf.plugin, () -> player.openInventory(inv));
		if (!Bookshelf.bookshelfSavePending.contains(loc)) {
			Bookshelf.bookshelfSavePending.add(loc);
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		Inventory inv = event.getView().getTopInventory();
		String key = Bookshelf.contentToKeyMapping.get(inv);
		if (key != null) {
			PlayerCloseBookshelfEvent pcbe = new PlayerCloseBookshelfEvent((Player) event.getPlayer(), key);
			Bukkit.getPluginManager().callEvent(pcbe);
			
			Bookshelf.bookshelfSavePending.add(key);
		}
		Bookshelf.isDonationView.remove(event.getPlayer());
	}	
}
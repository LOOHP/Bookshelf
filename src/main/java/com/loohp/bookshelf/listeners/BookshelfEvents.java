package com.loohp.bookshelf.listeners;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
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
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.BookshelfManager;
import com.loohp.bookshelf.api.events.PlayerCloseBookshelfEvent;
import com.loohp.bookshelf.api.events.PlayerOpenBookshelfEvent;
import com.loohp.bookshelf.objectholders.BlockPosition;
import com.loohp.bookshelf.objectholders.BookshelfHolder;
import com.loohp.bookshelf.objectholders.LWCRequestOpenData;
import com.loohp.bookshelf.utils.BookshelfUtils;
import com.loohp.bookshelf.utils.MCVersion;
import com.loohp.bookshelf.utils.legacy.LegacyConfigConverter;

import io.github.bananapuncher714.nbteditor.NBTEditor;

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
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		BookshelfManager manager = BookshelfManager.loadWorld(Bookshelf.plugin, event.getWorld());
		File legacyData = new File(Bookshelf.plugin.getDataFolder().getAbsolutePath() + "/bookshelfdata.json");
		if (legacyData.exists()) {
			LegacyConfigConverter.mergeLegacy(legacyData, manager);
		}
	}
	
	@SuppressWarnings("deprecation")
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
		
		BookshelfManager manager = BookshelfManager.getBookshelfManager(event.getBlock().getWorld());
		BookshelfHolder bookshelf = manager.getOrCreateBookself(new BlockPosition(event.getBlock()), null);
		
		ItemStack item = event.getItemInHand();
		if (NBTEditor.contains(item, "BookshelfContent")) {
			String title = NBTEditor.getString(item, "BookshelfTitle");
			if (title != null && !item.getItemMeta().getDisplayName().equals("")) {
				title = item.getItemMeta().getDisplayName();
			}
			String hash = NBTEditor.getString(item, "BookshelfContent");
			try {
				bookshelf.setInventory(BookshelfUtils.fromBase64(hash, title == null ? BookshelfManager.DEFAULT_BOOKSHELF_NAME_PLACEHOLDER : title, bookshelf));
				bookshelf.setTitle(title);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		
		String bsTitle = null;
		if (event.getItemInHand().hasItemMeta()) {
			if (event.getItemInHand().getItemMeta().hasDisplayName()) {
				if (!event.getItemInHand().getItemMeta().getDisplayName().equals("")) {
					bsTitle = event.getItemInHand().getItemMeta().getDisplayName();
				}
			}
		}
		try {
			bookshelf.setInventory(BookshelfUtils.fromBase64(BookshelfUtils.toBase64(bookshelf.getInventory()), bsTitle == null ? BookshelfManager.DEFAULT_BOOKSHELF_NAME_PLACEHOLDER : bsTitle, bookshelf));
			bookshelf.setTitle(bsTitle);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (!event.getBlock().getType().equals(Material.BOOKSHELF)) {
			return;
		}
		
		BookshelfManager manager = BookshelfManager.getBookshelfManager(event.getBlock().getWorld());
		BookshelfHolder bookshelf = manager.getOrCreateBookself(new BlockPosition(event.getBlock()), null);
		Inventory inv = bookshelf.getInventory();
		for (ItemStack item : inv.getContents()) {
			if (item != null && !item.getType().equals(Material.AIR)) {
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
			}
		}
		manager.remove(bookshelf.getPosition());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		BookshelfManager manager = BookshelfManager.getBookshelfManager(event.getLocation().getWorld());
		Map<Block, BookshelfHolder> position = new LinkedHashMap<>();
		List<Block> order = new ArrayList<>();
		for (Block block : event.blockList()) {
			if (block.getType().equals(Material.BOOKSHELF)) {
				position.put(block, manager.getOrCreateBookself(new BlockPosition(block), null));
				order.add(block);
			}
		}
		
		if (order.isEmpty()) {
			return;
		}
		
		for (Block block : order) {
			BookshelfHolder bookshelf = position.get(block);
			Inventory inv = bookshelf.getInventory();
			for (ItemStack item : inv.getContents()) {
				if (item != null && !item.getType().equals(Material.AIR)) {
					block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), item);
				}
			}
			manager.remove(bookshelf.getPosition());
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockExplode(BlockExplodeEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		BookshelfManager manager = BookshelfManager.getBookshelfManager(event.getBlock().getWorld());
		Map<Block, BookshelfHolder> position = new LinkedHashMap<>();
		List<Block> order = new ArrayList<>();
		for (Block block : event.blockList()) {
			if (block.getType().equals(Material.BOOKSHELF)) {
				position.put(block, manager.getOrCreateBookself(new BlockPosition(block), null));
				order.add(block);
			}
		}
		
		if (order.isEmpty()) {
			return;
		}
		
		for (Block block : order) {
			BookshelfHolder bookshelf = position.get(block);
			Inventory inv = bookshelf.getInventory();
			for (ItemStack item : inv.getContents()) {
				if (item != null && !item.getType().equals(Material.AIR)) {
					block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), item);
				}
			}
			manager.remove(bookshelf.getPosition());
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
		
		InventoryHolder holder = event.getView().getTopInventory().getHolder();
		boolean isBookshelf = holder != null && holder instanceof BookshelfHolder;
		if (!isBookshelf) {
			return;
		}
		
		if (Bookshelf.isDonationView.contains(player.getUniqueId())) {
			if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) || event.getAction().equals(InventoryAction.PICKUP_SOME) || event.getAction().equals(InventoryAction.PICKUP_ALL) || event.getAction().equals(InventoryAction.PICKUP_ONE) || event.getAction().equals(InventoryAction.PICKUP_HALF)) {
				event.setCancelled(true);
				return;
			}
		}
		
		if (!Bookshelf.useWhitelist) {
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
					if (!Bookshelf.whitelist.contains(event.getWhoClicked().getInventory().getItem(event.getHotbarButton()).getType().toString().toUpperCase())) { 
						event.setCancelled(true);
						return;
					}
				}
            }
			return;
		}
		
		if (isBookshelf) {
			if (event.getClick().isShiftClick()) {
				ItemStack clickedOn = event.getCurrentItem();

		        if (clickedOn != null) {
		        	if (!clickedOn.getType().equals(Material.AIR)) {
			        	if (!Bookshelf.whitelist.contains(clickedOn.getType().toString().toUpperCase())) {
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
			        	if (!Bookshelf.whitelist.contains(onCursor.getType().toString().toUpperCase())) {
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
			int slot = event.getRawSlot();
			int inventorySize = event.getView().getTopInventory().getSize();
			if (slot < inventorySize) {
				putting = true;
			}
		}
		
		if (!putting) {
			return;
		}
		
		Location loc = ((BookshelfHolder) holder).getPosition().getLocation();
		double random = Math.floor(Math.random() * 3) + 1;
		if (Bookshelf.version.isOld()) {
			event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("HORSE_ARMOR"), 3, 1);
		} else if (Bookshelf.version.isOlderOrEqualTo(MCVersion.V1_13_1)) {
			event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("ITEM_ARMOR_EQUIP_LEATHER"), 3, 1);
		} else {
			if (random == 1) {
				event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("ITEM_BOOK_PUT"), 3, 1);
			} else if (random == 2) {
				event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("ITEM_ARMOR_EQUIP_LEATHER"), 3, 1);
			} else if (random == 3) {
				event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("ITEM_BOOK_PAGE_TURN"), 3, 1);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDrag(InventoryDragEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (!Bookshelf.useWhitelist) {
			return;
		}
		
		if (event.getView().getTopInventory() == null) {
			return;
		}
		
		Inventory inv = event.getView().getTopInventory();
		InventoryHolder holder = inv.getHolder();
		if (holder != null && holder instanceof BookshelfHolder) {
			ItemStack dragged = event.getOldCursor(); // This is the item that is being dragged

		    if (!Bookshelf.whitelist.contains(dragged.getType().toString().toUpperCase())) {
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
	        		Location loc = ((BookshelfHolder) holder).getPosition().getLocation();
					double random = Math.floor(Math.random() * 3) + 1;
					if (Bookshelf.version.isOld()) {
						event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("HORSE_ARMOR"), 3, 1);
					} else if (Bookshelf.version.isOlderOrEqualTo(MCVersion.V1_13_1)) {
						event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("ITEM_ARMOR_EQUIP_LEATHER"), 3, 1);
					} else {
						if (random == 1) {
							event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("ITEM_BOOK_PUT"), 3, 1);
						} else if (random == 2) {
							event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("ITEM_ARMOR_EQUIP_LEATHER"), 3, 1);
						} else if (random == 3) {
							event.getWhoClicked().getWorld().playSound(loc.add(0.5, 0.5, 0.5), Sound.valueOf("ITEM_BOOK_PAGE_TURN"), 3, 1);
						}
					}
					break;
	        	}
	        }
		}
		
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onOpen(PlayerInteractEvent event) {
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
		if (Bookshelf.lwcCancelOpen.contains(event.getPlayer().getUniqueId())) {
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
		
		BookshelfManager manager = BookshelfManager.getBookshelfManager(player.getWorld());
		
		BookshelfHolder bookshelf = manager.getOrCreateBookself(new BlockPosition(event.getClickedBlock()), null);
		if (Bookshelf.lwcHook) {
			Location blockLoc = bookshelf.getPosition().getLocation();
			Protection protection = LWC.getInstance().getPlugin().getLWC().findProtection(blockLoc.getBlock());
			if (protection != null) {
				if (!protection.isOwner(player)) {
					Bookshelf.requestOpen.put(player, new LWCRequestOpenData(bookshelf, event.getBlockFace(), cancelled));
					return;
				}
			}
		}		
		
		PlayerOpenBookshelfEvent pobe = new PlayerOpenBookshelfEvent(player, bookshelf, event.getBlockFace(), cancelled);
		Bukkit.getPluginManager().callEvent(pobe);
		
		if (pobe.isCancelled()) {
			return;
		}
		
		Bukkit.getScheduler().runTask(Bookshelf.plugin, () -> player.openInventory(bookshelf.getInventory()));
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		Inventory inv = event.getView().getTopInventory();
		InventoryHolder holder = inv.getHolder();
		if (holder != null && holder instanceof BookshelfHolder) {
			PlayerCloseBookshelfEvent pcbe = new PlayerCloseBookshelfEvent((Player) event.getPlayer(), (BookshelfHolder) holder);
			Bukkit.getPluginManager().callEvent(pcbe);
		}
		Bookshelf.isDonationView.remove(event.getPlayer().getUniqueId());
	}	
}

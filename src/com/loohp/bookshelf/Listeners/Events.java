package com.loohp.bookshelf.Listeners;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
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
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.BookshelfManager;
import com.loohp.bookshelf.Special;
import com.loohp.bookshelf.Utils.BlockLockerUtils;
import com.loohp.bookshelf.Utils.BookshelfUtils;
import com.loohp.bookshelf.Utils.DropperUtils;
import com.loohp.bookshelf.Utils.EnchantmentTableUtils;
import com.loohp.bookshelf.Utils.InventoryUtils;
import com.loohp.bookshelf.Utils.LWCUtils;
import com.loohp.bookshelf.Utils.MCVersion;
import com.loohp.bookshelf.Utils.MaterialUtils;
import com.loohp.bookshelf.Utils.NBTUtils;
import com.loohp.bookshelf.Utils.CustomListUtils;

public class Events implements Listener {

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

	@SuppressWarnings({ "unchecked", "deprecation" })
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPreEnchantTable(PrepareItemEnchantEvent event) {
		if (Bookshelf.version.isOld() || Bookshelf.version.equals(MCVersion.V1_9) || Bookshelf.version.equals(MCVersion.V1_9_4) || Bookshelf.version.equals(MCVersion.V1_10)) {
			return;
		}
		if (!Bookshelf.enchantmentTable) {
			return;
		}
		
		if (event.isCancelled()) {
			return;
		}
		
		Block eTable = event.getEnchantBlock();
		List<Block> blocks = EnchantmentTableUtils.getBookshelves(eTable);
		Map<Enchantment, HashMap<String, Object>> enchants = new HashMap<Enchantment, HashMap<String, Object>>();
		int totalSlots = (int) (Bookshelf.BookShelfRows * 9 * 15);
		if (blocks.isEmpty()) {
			return;
		}
		for (Block block : blocks) {
			String key = BookshelfUtils.locKey(block.getLocation());
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
			Inventory inv = Bookshelf.bookshelfContent.get(key);
			for (int i = 0; i < inv.getSize(); i++) {
				ItemStack item = inv.getItem(i);
				if (item == null) {
					continue;
				}
				if (!item.getType().equals(Material.ENCHANTED_BOOK)) {
					continue;
				}
				EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
				Map<Enchantment, Integer> map = meta.getStoredEnchants();
				for (Entry<Enchantment, Integer> entry : map.entrySet()) {
					if (!enchants.containsKey(entry.getKey())) {
						HashMap<String, Object> value = new HashMap<String, Object>();
						value.put("Occurance", (int) 1);
						List<Integer> lvl = new ArrayList<Integer>();
						lvl.add(entry.getValue());
						value.put("Level", lvl);
						enchants.put(entry.getKey(), value);
					} else {
						HashMap<String, Object> value = enchants.get(entry.getKey());
						value.put("Occurance", (int) value.get("Occurance") + 1);
						List<Integer> lvl = (List<Integer>) value.get("Level");
						lvl.add(entry.getValue());
						value.put("Level", lvl);
					}
				}
			}
		}

		if (enchants.isEmpty()) {
			return;
		}
		HashMap<Enchantment, HashMap<String, Integer>> list = new HashMap<Enchantment, HashMap<String, Integer>>();
		int totalOccurance = 0;
		for (Entry<Enchantment, HashMap<String, Object>> entry : enchants.entrySet()) {
			int occurance = (int) entry.getValue().get("Occurance");
			totalOccurance = totalOccurance + occurance;
			List<Integer> levels = (List<Integer>) entry.getValue().get("Level");
			int sum = 0;
			for (int each : levels) {
				sum = sum + each;
			}
			int level = (int) Math.floor((double) sum / (double) levels.size());
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			map.put("Occurance", occurance);
			map.put("Level", level);
			list.put(entry.getKey(), map);
		}
		if (list.isEmpty()) {
			return;
		}
		List<Object> pick = new ArrayList<Object>();
		for (Entry<Enchantment, HashMap<String, Integer>> entry : list.entrySet()) {
			if (entry.getKey().equals(Enchantment.MENDING)) {
				continue;
			}
			if (entry.getKey().equals(Enchantment.FROST_WALKER)) {
				continue;
			}
			if (entry.getKey().equals(Enchantment.BINDING_CURSE)) {
				continue;
			}
			if (entry.getKey().equals(Enchantment.VANISHING_CURSE)) {
				continue;
			}
			if (!entry.getKey().canEnchantItem(event.getItem()) && !event.getItem().getType().equals(Material.BOOK)) {
				continue;
			}
			int occurance = entry.getValue().get("Occurance");
			for (int i = 0; i < occurance; i++) {
				if (!Bookshelf.version.isLegacy()) {
					pick.add(entry.getKey().getKey());
				} else {
					pick.add(entry.getKey().getName());
				}
			}
		}
		if (pick.isEmpty()) {
			return;
		}
		for (int i = pick.size() - 1; i < totalSlots; i++) {
			pick.add(null);
		}
		int additionneeded = (pick.size() * Bookshelf.eTableMulti) - pick.size();
		for (int i = 0; i < additionneeded; i++) {
			pick.add(null);
		}
		Player player = event.getEnchanter();
		EnchantmentOffer[] offers = event.getOffers();
		for (EnchantmentOffer offer : offers) {
			long seed = 0;
			if (!Bookshelf.enchantSeed.containsKey(player)) {		
				Bookshelf.enchantSeed.put(player, System.currentTimeMillis());
			}
			seed = Bookshelf.enchantSeed.get(player);
			Random random = new Random(seed);
			double ran = random.nextDouble();
			Object key = pick.get((int) (ran * pick.size()));
			if (key == null) {
				continue;
			}
			Enchantment ench = null;
			if (!Bookshelf.version.isLegacy()) {
				ench = Enchantment.getByKey((NamespacedKey) key);
			} else {
				ench = Enchantment.getByName((String) key);
			}
			offer.setEnchantment(ench);
			int level = list.get(ench).get("Level");
			if (offer.getEnchantmentLevel() > level) {
				offer.setEnchantmentLevel(level);
			}
		}
		HashMap<ItemStack, EnchantmentOffer[]> offermap = null;
		if (Special.enchantOffers.containsKey(player)) {
			offermap = Special.enchantOffers.get(player);
		} else {
			offermap = new HashMap<ItemStack, EnchantmentOffer[]>();
			Special.enchantOffers.put(player, offermap);
		}
		offermap.put(event.getItem(), offers);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEnchant(EnchantItemEvent event) {
		if (Bookshelf.version.isOld() || Bookshelf.version.equals(MCVersion.V1_9) || Bookshelf.version.equals(MCVersion.V1_9_4) || Bookshelf.version.equals(MCVersion.V1_10)) {
			return;
		}
		if (!Bookshelf.enchantmentTable) {
			return;
		}
		
		if (event.isCancelled()) {
			return;
		}
		
		Player player = event.getEnchanter();
		if (Special.enchantOffers.containsKey(player)) {
			EnchantmentOffer offer = Special.enchantOffers.get(player).get(event.getItem())[event.whichButton()];
			Map<Enchantment, Integer> orginal = event.getEnchantsToAdd();
			List<Enchantment> removelist = new ArrayList<Enchantment>();
			for (Entry<Enchantment, Integer> entry : orginal.entrySet()) {
				if (entry.getKey().conflictsWith(offer.getEnchantment()) || entry.getKey().equals(offer.getEnchantment())) {
					removelist.add(entry.getKey());
				}
			}
			for (Enchantment ench : removelist) {
				orginal.remove(ench);
			}
			orginal.put(offer.getEnchantment(), offer.getEnchantmentLevel());
			Special.enchantOffers.get(player).remove(event.getItem());
		}
		Bookshelf.enchantSeed.remove(player);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCreativePickBlock(InventoryCreativeEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event.getClick().equals(ClickType.CREATIVE)) {
			Player player = (Player) event.getWhoClicked();
			ItemStack item = event.getCursor();
			if (item.getType().equals(Material.BOOKSHELF) && player.getGameMode().equals(GameMode.CREATIVE) && player.isSneaking() && player.hasPermission("bookshelf.copynbt")) {
				Block block = null;
				if (!Bookshelf.version.isLegacy() && !Bookshelf.version.equals(MCVersion.V1_13) && !Bookshelf.version.equals(MCVersion.V1_13_1)) {
					block = player.getTargetBlockExact(10, FluidCollisionMode.NEVER);
				} else {
					block = player.getTargetBlock(MaterialUtils.getNonSolidSet(), 10);
				}
				if (block.getType().equals(Material.BOOKSHELF)) {
					String key = BookshelfUtils.locKey(block.getLocation());
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
					String hash = BookshelfUtils.toBase64(Bookshelf.bookshelfContent.get(key));
					String title = BookshelfManager.getTitle(key);
					if (Bookshelf.version.isLegacy() || Bookshelf.version.equals(MCVersion.V1_13) || Bookshelf.version.equals(MCVersion.V1_13_1)) {
						item = NBTUtils.set(item, hash, "BookshelfContent");
						item = NBTUtils.set(item, title, "BookshelfTitle");
					} else {
						ItemMeta meta = item.getItemMeta();
						PersistentDataContainer pdc = meta.getPersistentDataContainer();
						pdc.set(new NamespacedKey(Bookshelf.plugin, "BookshelfContent"), PersistentDataType.STRING, hash);
						pdc.set(new NamespacedKey(Bookshelf.plugin, "BookshelfTitle"), PersistentDataType.STRING, title);
						item.setItemMeta(meta);
					}
					event.setCursor(item);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDropper(BlockDispenseEvent event) {
		if (!Bookshelf.EnableDropperSupport) {
			return;
		}
		if (event.isCancelled()) {
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
		if (Bookshelf.LWCHook) {
			if (!LWCUtils.checkHopperFlagIn(relative)) {
				event.setCancelled(true);
				return;
			}
		}
		if (Bookshelf.BlockLockerHook) {
			if (!BlockLockerUtils.canRedstone(relative)) {
				event.setCancelled(true);
				return;
			}
		}
		event.setCancelled(true);
		Inventory bookshelf = Bookshelf.bookshelfContent.get(key);
		org.bukkit.block.Dropper d = (org.bukkit.block.Dropper) event.getBlock().getState();
		Inventory dropper = d.getInventory();
		List<ItemStack> newList = new ArrayList<ItemStack>();
		newList.addAll(Arrays.asList(dropper.getContents()));
		newList.add(event.getItem());
		Collections.shuffle(newList);
		for (ItemStack each : newList) {
			if (each == null) {
				continue;
			}
			if (Bookshelf.UseWhitelist) {
				if (!Bookshelf.Whitelist.contains(each.getType().toString().toUpperCase())) {
					continue;
				}
			}
			if (!InventoryUtils.stillHaveSpace(bookshelf, each.getType())) {
				continue;
			}
			ItemStack additem = each.clone();
			additem.setAmount(1);
			bookshelf.addItem(additem);
			boolean removed = false;
			for (int i = 0; i < dropper.getSize(); i = i + 1) {
				ItemStack removeitem = dropper.getItem(i);
            	if (removeitem == null) {
            		continue;
            	}
            	if (removeitem.equals(each)) {
            		removeitem.setAmount(removeitem.getAmount() - 1);
            		dropper.setItem(i, removeitem);
            		removed = true;
            		break;
            	}
			}
			if (!removed) {
				new BukkitRunnable() {
					public void run() {
						for (int i = 0; i < dropper.getSize(); i = i + 1) {
							ItemStack removeitem = dropper.getItem(i);
			            	if (removeitem == null) {
			            		continue;
			            	}
			            	if (removeitem.equals(each)) {
			            		removeitem.setAmount(removeitem.getAmount() - 1);
			            		dropper.setItem(i, removeitem);
			            		break;
			            	}
						}
					}
				}.runTaskLater(Bookshelf.plugin, 1);
			}
			Bookshelf.bookshelfSavePending.add(key);
			if (!Bookshelf.version.isLegacy()) {
				event.getBlock().getWorld().playSound(event.getBlock().getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
			}
			return;
		}
		if (!Bookshelf.version.isLegacy()) {
			event.getBlock().getWorld().playSound(event.getBlock().getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1, 1);
		}
	}
	
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
		for (Block bookshelf : CustomListUtils.reverse(bookshelves)) {
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
		for (Block bookshelf : CustomListUtils.reverse(bookshelves)) {
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
					Bookshelf.bookshelfContent.put(loc, BookshelfUtils.fromBase64(hash, title));
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
					Bookshelf.bookshelfContent.put(loc, BookshelfUtils.fromBase64(hash, title));
				} catch (IOException e) {
					e.printStackTrace();
				}		
				BookshelfManager.setTitle(loc, title);
				BookshelfUtils.saveBookShelf(loc);
				return;
			}
		}
		
		if (Bookshelf.bookshelfContent.containsKey(loc)) {
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
		
		if (!Bookshelf.UseWhitelist) {
			return;
		}
		if (event.getAction().equals(InventoryAction.NOTHING) || event.getAction().equals(InventoryAction.UNKNOWN) || event.getAction().equals(InventoryAction.DROP_ALL_CURSOR) || event.getAction().equals(InventoryAction.DROP_ALL_SLOT) || event.getAction().equals(InventoryAction.DROP_ONE_CURSOR) || event.getAction().equals(InventoryAction.DROP_ONE_SLOT  )) {
        	return;
        }
		
		if (event.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD) || event.getAction().equals(InventoryAction.HOTBAR_SWAP)) {
			if (!Bookshelf.bookshelfContent.containsValue(event.getView().getTopInventory())) {
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
			if (!Bookshelf.bookshelfContent.containsValue(event.getView().getTopInventory())) {
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
		
		for (Entry<String, Inventory> entry : Bookshelf.bookshelfContent.entrySet()) {
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
		
		if (!Bookshelf.version.isOld()) {
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
		if (player.isSneaking()) {
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
		if (Bookshelf.LWCHook) {
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
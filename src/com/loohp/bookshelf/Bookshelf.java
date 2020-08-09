package com.loohp.bookshelf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.loohp.bookshelf.Debug.Debug;
import com.loohp.bookshelf.Hooks.InteractionVisualizerAnimations;
import com.loohp.bookshelf.Listeners.BookshelfEvents;
import com.loohp.bookshelf.Listeners.CreativeEvents;
import com.loohp.bookshelf.Listeners.DispenserEvents;
import com.loohp.bookshelf.Listeners.EnchantingEvents;
import com.loohp.bookshelf.Listeners.PistonEvents;
import com.loohp.bookshelf.Listeners.Hooks.ASkyBlockEvents;
import com.loohp.bookshelf.Listeners.Hooks.BentoBoxEvents;
import com.loohp.bookshelf.Listeners.Hooks.GriefPreventionEvents;
import com.loohp.bookshelf.Listeners.Hooks.LWCEvents;
import com.loohp.bookshelf.Listeners.Hooks.LandEvents;
import com.loohp.bookshelf.Listeners.Hooks.PlotSquared4Events;
import com.loohp.bookshelf.Listeners.Hooks.PlotSquared5Events;
import com.loohp.bookshelf.Listeners.Hooks.RedProtectEvents;
import com.loohp.bookshelf.Listeners.Hooks.ResidenceEvents;
import com.loohp.bookshelf.Listeners.Hooks.SuperiorSkyblock2Events;
import com.loohp.bookshelf.Listeners.Hooks.TownyEvents;
import com.loohp.bookshelf.Listeners.Hooks.WorldGuardEvents;
import com.loohp.bookshelf.Metrics.Charts;
import com.loohp.bookshelf.Metrics.Metrics;
import com.loohp.bookshelf.ObjectHolders.LWCRequestOpenData;
import com.loohp.bookshelf.Updater.Updater;
import com.loohp.bookshelf.Utils.BookshelfUtils;
import com.loohp.bookshelf.Utils.EnchantmentTableUtils;
import com.loohp.bookshelf.Utils.HopperUtils;
import com.loohp.bookshelf.Utils.MCVersion;
import com.loohp.bookshelf.Utils.OpenInvUtils;
import com.loohp.bookshelf.Utils.ParticlesUtils;
import com.loohp.bookshelf.Utils.VanishUtils;
import com.loohp.bookshelf.Utils.Legacy.LegacyConfigConverter;

import net.md_5.bungee.api.ChatColor;

public class Bookshelf extends JavaPlugin {
	
	public static Plugin plugin = null;
	
	public static MCVersion version;
	
	public static boolean VanishHook = false;
	public static boolean CMIHook = false;
	public static boolean EssentialsHook = false;
	public static boolean OpenInvHook = false;
	public static boolean LWCHook = false;
	public static boolean WorldGuardHook = false;
	public static boolean GriefPreventionHook = false;
	public static boolean BlockLockerHook = false;
	public static boolean RedProtectHook = false;
	public static boolean BentoBoxHook = false;
	public static boolean ASkyBlockHook = false;
	public static boolean ResidenceHook = false;
	public static boolean TownyHook = false;
	public static boolean SuperiorSkyblock2Hook = false;
	public static boolean LandHook = false;
	public static boolean PlotSquaredHook = false;
	public static boolean InteractionVisualizerHook = false;
	
	public static boolean EnableHopperSupport = true;
	public static boolean EnableDropperSupport = true;
	public static int HopperTaskID = -1;
	public static int HopperMinecartTaskID = -1;
	public static int HopperTicksPerTransfer = 8;
	public static int HopperAmount = 1;
	
	public static ConcurrentHashMap<String, Inventory> keyToContentMapping = new ConcurrentHashMap<String, Inventory>();
	public static ConcurrentHashMap<Inventory, String> contentToKeyMapping = new ConcurrentHashMap<Inventory, String>();
	
	public static ConcurrentLinkedQueue<String> bookshelfSavePending = new ConcurrentLinkedQueue<String>();
	public static ConcurrentLinkedQueue<Chunk> bookshelfLoadPending = new ConcurrentLinkedQueue<Chunk>();
	public static ConcurrentLinkedQueue<Chunk> bookshelfRemovePending = new ConcurrentLinkedQueue<Chunk>();
	
	public static ConcurrentHashMap<Player, BlockFace> lastBlockFace = new ConcurrentHashMap<Player, BlockFace>();
	
	public static ConcurrentHashMap<Player, LWCRequestOpenData> requestOpen = new ConcurrentHashMap<Player, LWCRequestOpenData>();
	
	public static int BookShelfRows = 2;
	public static boolean UseWhitelist = true;
	public static String Title = "Bookshelf";
	public static Set<String> Whitelist = new HashSet<String>();
	public static boolean particlesEnabled = true;
	
	public static String NoPermissionToReloadMessage = "&cYou do not have permission use this command!";
	public static String NoPermissionToUpdateMessage = "&cYou do not have permission use this command!";
	
	public static Set<Player> lwcCancelOpen = ConcurrentHashMap.newKeySet();
	public static Set<Player> isDonationView = ConcurrentHashMap.newKeySet();
	
	public static Set<String> isEmittingParticle = new HashSet<String>();
	
	public static ConcurrentHashMap<Long, Location> tempRedstone = new ConcurrentHashMap<Long, Location>();
	
	public static ConcurrentHashMap<Player, Long> enchantSeed = new ConcurrentHashMap<Player, Long>();

	private static int spawnchunks = 0;
	private static int done = 0;
	private static String currentWorld = "world";
	
	public static long lastHopperTime = 0;
	public static long lastHoppercartTime = 0;
	
	public static boolean enchantmentTable = true;
	
	public static int eTableMulti = 1;
	
	public static boolean UpdaterEnabled = true;
	public static int UpdaterTaskID = -1;

	@Override
	@SuppressWarnings("deprecation")
	public void onEnable() {	
		plugin = this;
		
		getServer().getPluginManager().registerEvents(new Debug(), this);
		
		int pluginId = 6748;

		Metrics metrics = new Metrics(this, pluginId);
		
		version = MCVersion.fromPackageName(getServer().getClass().getPackage().getName());
	    
	    getServer().getPluginManager().registerEvents(new BookshelfEvents(), this);
	    getServer().getPluginManager().registerEvents(new CreativeEvents(), this);
	    getServer().getPluginManager().registerEvents(new DispenserEvents(), this);
	    getServer().getPluginManager().registerEvents(new EnchantingEvents(), this);
	    getServer().getPluginManager().registerEvents(new PistonEvents(), this);
	    
	    getCommand("bookshelf").setExecutor(new Commands());
		
	    getConfig().options().copyDefaults(true);
	    saveConfig();

	    //v2.0.0 upgrade
	    if (plugin.getConfig().contains("BookShelfData")) {
	    	LegacyConfigConverter.convert();
	    }
	    //------
	    
	    String SuperVanish = "SuperVanish";
	    String PremiumVanish = "PremiumVanish";
	    if (getServer().getPluginManager().getPlugin(SuperVanish) != null || getServer().getPluginManager().getPlugin(PremiumVanish) != null) {
			hookMessage(SuperVanish + "/" + PremiumVanish);
			VanishHook = true;
		}
	    
	    String CMI = "CMI";
		if (getServer().getPluginManager().getPlugin(CMI) != null) {
			hookMessage(CMI);
			CMIHook = true;
		}
		
		String Essentials = "Essentials";
		if (getServer().getPluginManager().getPlugin(Essentials) != null) {
			hookMessage(Essentials);
			EssentialsHook = true;
		}
	    
	    String OpenInv = "OpenInv";
	    if (getServer().getPluginManager().getPlugin(OpenInv) != null) {
			hookMessage(OpenInv);
			OpenInvHook = true;
		}
	    
	    String GriefPrevention = "GriefPrevention";
	    if (getServer().getPluginManager().getPlugin("GriefPrevention") != null) {
	    	hookMessage(GriefPrevention);
	    	getServer().getPluginManager().registerEvents(new GriefPreventionEvents(), this);
			GriefPreventionHook = true;
		}
		
	    String LWC = "LWC";
		if (getServer().getPluginManager().getPlugin(LWC) != null) {
			hookMessage(LWC);
			LWCEvents.hookLWC();
			LWCHook = true;
		}
		
		String BlockLocker = "BlockLocker";
		if (getServer().getPluginManager().getPlugin(BlockLocker) != null) {
			hookMessage(BlockLocker);
			BlockLockerHook = true;
		}
		
		String WorldGuard = "WorldGuard";
		if (getServer().getPluginManager().getPlugin(WorldGuard) != null) {
			hookMessage(WorldGuard);
			getServer().getPluginManager().registerEvents(new WorldGuardEvents(), this);
			WorldGuardHook = true;
		}
		
		String RedProtect = "RedProtect";
		if (getServer().getPluginManager().getPlugin(RedProtect) != null) {
			hookMessage(RedProtect);
			getServer().getPluginManager().registerEvents(new RedProtectEvents(), this);
			RedProtectHook = true;
		}
		
		String BentoBox = "BentoBox";
		if (getServer().getPluginManager().getPlugin(BentoBox) != null) {
			hookMessage(BentoBox);
			getServer().getPluginManager().registerEvents(new BentoBoxEvents(), this);
			BentoBoxHook = true;
		}
		
		String ASkyBlock = "ASkyBlock";
		if (getServer().getPluginManager().getPlugin(ASkyBlock) != null) {
			hookMessage(ASkyBlock);
			getServer().getPluginManager().registerEvents(new ASkyBlockEvents(), this);
			ASkyBlockHook = true;
		}
		
		String Residence = "Residence";	
		if (getServer().getPluginManager().getPlugin(Residence) != null) {
			hookMessage(Residence);
			getServer().getPluginManager().registerEvents(new ResidenceEvents(), this);
			ResidenceHook = true;
		}
		
		String Towny = "Towny";
		if (getServer().getPluginManager().getPlugin(Towny) != null) {
			hookMessage(Towny);
			getServer().getPluginManager().registerEvents(new TownyEvents(), this);
			TownyHook = true;
		}
		
		String SuperiorSkyblock2 = "SuperiorSkyblock2";
		if (getServer().getPluginManager().getPlugin(SuperiorSkyblock2) != null) {
			hookMessage(SuperiorSkyblock2);
			getServer().getPluginManager().registerEvents(new SuperiorSkyblock2Events(), this);
			SuperiorSkyblock2Hook = true;
		}
		
		String Lands = "Lands";
		if (getServer().getPluginManager().getPlugin(Lands) != null) {
			hookMessage(Lands);
			getServer().getPluginManager().registerEvents(new LandEvents(), this);
			LandEvents.setup();
			LandHook = true;
		}
		
		String PlotSquared = "PlotSquared";
		if (getServer().getPluginManager().getPlugin(PlotSquared) != null) {
			String plotSquaredVersion = getServer().getPluginManager().getPlugin(PlotSquared).getDescription().getVersion();
			if (plotSquaredVersion.startsWith("5.")) {
				hookMessage(PlotSquared + " (v5)");
				getServer().getPluginManager().registerEvents(new PlotSquared5Events(), this);
				PlotSquaredHook = true;
			} else if (plotSquaredVersion.startsWith("4.")) {
				hookMessage(PlotSquared + " (v4)");
				getServer().getPluginManager().registerEvents(new PlotSquared4Events(), this);
				PlotSquaredHook = true;
			} else {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Bookshelf] This version of PlotSquared is not supported, only v4 and v5 is supported so far.");
			}
		}
		
		String InteractionVisualizer = "InteractionVisualizer";
		if (getServer().getPluginManager().getPlugin(InteractionVisualizer) != null) {
			hookMessage(InteractionVisualizer);
			getServer().getPluginManager().registerEvents(new InteractionVisualizerAnimations(), this);
			InteractionVisualizerHook = true;
		}

		if (!version.isSupported()) {
	    	getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Bookshelf] This version of minecraft is unsupported!");
	    }
		
		if (plugin.getConfig().contains("Options.EnableHopperDropperSupport")) {
			boolean setting = plugin.getConfig().getBoolean("Options.EnableHopperDropperSupport");
			plugin.getConfig().set("Options.EnableHopperSupport", setting);
			plugin.getConfig().set("Options.EnableDropperSupport", setting);
			plugin.getConfig().set("Options.EnableHopperDropperSupport", null);
			plugin.saveConfig();
		}
	    
	    loadConfig();
	    
	    BookshelfManager.reload();
	    
	    intervalSave();
	    intervalLoad();
	    intervalRemove();
	    particles();
	    
	    loadBookshelf(getServer().getWorlds());	    
	    
	    Charts.loadCharts(metrics);
		
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Bookshelf] BookShelf has been Enabled!");
	}

	@Override
	public void onDisable() {
		getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Saving all pending bookshelves..");
		long start = System.currentTimeMillis();
		bookshelfSavePending.addAll(keyToContentMapping.keySet());
		Set<String> save = new HashSet<String>(bookshelfSavePending);
		for (String key : save) {
			if (keyToContentMapping.containsKey(key)) {
				BookshelfUtils.saveBookShelf(key);
			}
		}
		BookshelfManager.save();
		getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Bookshelves saved! (" + (System.currentTimeMillis() - start) + "ms)");
		getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Bookshelf] BookShelf has been Disabled!");
	}
	
	private static void hookMessage(String name) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Bookshelf] Hooked into " + name + "!");
	}
	
	public static void loadConfig() {	
		BookShelfRows = plugin.getConfig().getInt("Options.BookShelfRows");
		UseWhitelist = plugin.getConfig().getBoolean("Options.UseWhitelist");
		Whitelist = plugin.getConfig().getStringList("Options.Whitelist").stream().collect(Collectors.toSet());
		Title = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Options.Title"));
		NoPermissionToReloadMessage = plugin.getConfig().getString("Options.NoPermissionToReloadMessage");
		NoPermissionToUpdateMessage = plugin.getConfig().getString("Options.NoPermissionToUpdateMessage");
		particlesEnabled = plugin.getConfig().getBoolean("Options.ParticlesWhenOpened");
		EnableHopperSupport = plugin.getConfig().getBoolean("Options.EnableHopperSupport");
		EnableDropperSupport = plugin.getConfig().getBoolean("Options.EnableDropperSupport");
		enchantmentTable = plugin.getConfig().getBoolean("Options.EnableEnchantmentTableBoosting");
		int eTableChance = plugin.getConfig().getInt("Options.EnchantmentTableBoostingMaxPercentage");
		if (eTableChance > 100) {
			eTableChance = 100;
		} else if (eTableChance < 0) {
			eTableChance = 0;
		}
		eTableMulti = (int) Math.pow(((double) eTableChance / 100.0), -1);
		
		lastHopperTime = 0;
		lastHoppercartTime = 0;
		if (HopperTaskID >= 0) {
			Bukkit.getScheduler().cancelTask(HopperTaskID);
		}
		if (HopperMinecartTaskID >= 0) {
			Bukkit.getScheduler().cancelTask(HopperMinecartTaskID);
		}
		if (EnableHopperSupport == true) {
			HopperTicksPerTransfer = Bukkit.spigot().getConfig().getInt("world-settings.default.ticks-per.hopper-transfer");
			HopperAmount = Bukkit.spigot().getConfig().getInt("world-settings.default.hopper-amount");
			HopperUtils.hopperCheck();
			HopperUtils.hopperMinecartCheck();
		}
		
		if (UpdaterTaskID >= 0) {
			Bukkit.getScheduler().cancelTask(UpdaterTaskID);
		}
		UpdaterEnabled = plugin.getConfig().getBoolean("Options.Updater");
		if (UpdaterEnabled == true) {
			Updater.updaterInterval();
		}
	}
	
	public static boolean removeBookshelfFromMapping(String key) {
		Inventory inventory = keyToContentMapping.remove(key);
		if (inventory != null) {
			contentToKeyMapping.remove(inventory);
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean removeBookshelfFromMapping(Inventory inventory) {
		String key = contentToKeyMapping.remove(inventory);
		if (key != null) {
			keyToContentMapping.remove(key);
			return true;
		} else {
			return false;
		}
	}
	
	public static void addBookshelfToMapping(String key, Inventory inventory) {
		keyToContentMapping.put(key, inventory);
		contentToKeyMapping.put(inventory, key);
	}
	
	public static void loadBookshelf(World world) {
		List<World> worlds = new ArrayList<World>(1);
		worlds.add(world);
		loadBookshelf(worlds);
	}
	
	public synchronized static void loadBookshelf(List<World> worlds) {
		long start = System.currentTimeMillis();
		for (World world : worlds) {
			spawnchunks = world.getLoadedChunks().length;
			done = 0;
			currentWorld = world.getName();
			Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Loading bookshelves in spawn chunks in " + world.getName());
			loadBookshelfProgress();
			for (Chunk chunk : world.getLoadedChunks()) {
				for (Block block : BookshelfUtils.getAllBookshelvesInChunk(chunk)) {
					String loc = BookshelfUtils.locKey(block.getLocation());
					if (!keyToContentMapping.containsKey(loc)) {
						if (!BookshelfManager.contains(loc)) {
							String bsTitle = Title;
							addBookshelfToMapping(loc, Bukkit.createInventory(null, (int) (BookShelfRows * 9), bsTitle));
							BookshelfManager.setTitle(loc, bsTitle);
							BookshelfUtils.saveBookShelf(loc);
						} else {
							BookshelfUtils.loadBookShelf(loc);
						}
					}
				}
				done++;
			}
			Bukkit.getConsoleSender().sendMessage("[Bookshelf] Preparing bookshelves in spawn chunks in " + currentWorld + ": 100%");
		}
		BookshelfManager.save();
		BookshelfManager.intervalSaveToFile();
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Bookshelf] Bookshelves loaded in " + worlds.size() + (worlds.size() == 1 ? " world" : " worlds") + "! (" + (System.currentTimeMillis() - start) + "ms)");
	}
	
	public static void loadBookshelfProgress() {
		CompletableFuture.runAsync(()->{
			long start = System.currentTimeMillis();
			String thisWorld = currentWorld;
			long lastDone = 0;
			while (done < spawnchunks && thisWorld == currentWorld) {
				Bukkit.getConsoleSender().sendMessage("[Bookshelf] Preparing bookshelves in spawn chunks in " + currentWorld + ": " + Math.round((double) ((double) done / (double) spawnchunks) * 100) + "%");
				if ((System.currentTimeMillis() - start) > 30000) {
					return;
				}
				if (lastDone != done) {
					start = System.currentTimeMillis();
					lastDone = done;
				}
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException ignore) {}
			}
		});
	}
	
	public void intervalSave() {
		Bukkit.getScheduler().runTaskTimer(this, () -> {
			List<String> removeList = new ArrayList<String>();
			for (String key : bookshelfSavePending) {
				if (keyToContentMapping.containsKey(key)) {
					if (!removeList.contains(key)) {
						BookshelfUtils.saveBookShelf(key);
					}
				}
				removeList.add(key);
			}
			bookshelfSavePending.clear();
		}, 0, 40);
	}
	
	public void intervalLoad() {
		Bukkit.getScheduler().runTaskTimer(this, () -> {
			List<Chunk> remove = new ArrayList<Chunk>();
			int i = 1;
			for (Chunk chunk : bookshelfLoadPending) {
				for (Block block : BookshelfUtils.getAllBookshelvesInChunk(chunk)) {
					String loc = BookshelfUtils.locKey(block.getLocation());
					if (!keyToContentMapping.containsKey(loc)) {
						if (!BookshelfManager.contains(loc)) {
							String bsTitle = Title;
							addBookshelfToMapping(loc , Bukkit.createInventory(null, (int) (BookShelfRows * 9), bsTitle));
							BookshelfManager.setTitle(loc, bsTitle);
							BookshelfUtils.saveBookShelf(loc);
						} else {
							BookshelfUtils.loadBookShelf(loc);
						}
					}
				}
				remove.add(chunk);
				i++;
				if (i > 2) {
					break;
				}
			}
			for (Chunk chunk : remove) {
				bookshelfLoadPending.remove(chunk);
			}
		}, 0, 1);
	}
	
	public void intervalRemove() {
		Bukkit.getScheduler().runTaskTimer(this, () -> {
			int i = 1;
			Iterator<Chunk> itr = bookshelfRemovePending.iterator();
			while (itr.hasNext()) {
				Chunk chunk = itr.next();
				for (Block block : BookshelfUtils.getAllBookshelvesInChunk(chunk)) {
					String loc = BookshelfUtils.locKey(block.getLocation());
					if (keyToContentMapping.containsKey(loc)) {
						BookshelfUtils.saveBookShelf(loc, true);
					}
				}
				itr.remove();
				i++;
				if (i > 2) {
					break;
				}
			}
		}, 0, 1);
	}
	
	public void particles() {
		Bukkit.getScheduler().runTaskTimer(this, () -> {
			if (particlesEnabled == true && !version.isLegacy()) {
				isEmittingParticle.clear();
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (OpenInvUtils.isSlientChest(player)) {
						continue;
					}
					if (VanishUtils.isVanished(player)) {
						continue;
					}
					if (player.getOpenInventory() != null) {
						for (Entry<String, Inventory> entry : keyToContentMapping.entrySet()) {
							if (!isEmittingParticle.contains(entry.getKey())) {
								if (entry.getValue().equals(player.getOpenInventory().getTopInventory())) {
									Location loc = BookshelfUtils.keyLoc(entry.getKey());
									Location loc2 = loc.clone().add(1,1,1);
									DustOptions purple = new DustOptions(Color.fromRGB(153, 51, 255), 1);
									DustOptions yellow = new DustOptions(Color.fromRGB(255, 255, 0), 1);
									for (Location pos : ParticlesUtils.getHollowCube(loc.add(-0.0625, -0.0625, -0.0625), loc2.add(0.0625, 0.0625, 0.0625), 0.1666)) {
										double random = Math.random() * 100;
										if (random > 95) {
											double ranColor = Math.floor(Math.random() * 2) + 1;
											if (ranColor == 1) {
												loc.getWorld().spawnParticle(Particle.REDSTONE, pos, 1, yellow);
											} else if (ranColor == 2) {
												loc.getWorld().spawnParticle(Particle.REDSTONE, pos, 1, purple);
											}
										}
									}
									isEmittingParticle.add(entry.getKey());
								}
							}
						}
						if (enchantmentTable == true) {
							if (player.getOpenInventory().getTopInventory().getType().equals(InventoryType.ENCHANTING)) {
								for (Block block : EnchantmentTableUtils.getBookshelves(player.getOpenInventory().getTopInventory().getLocation().getBlock())) {
									String key = BookshelfUtils.locKey(block.getLocation());
									if (!isEmittingParticle.contains(key)) {
										Location loc = block.getLocation().clone();
										Location loc2 = loc.clone().add(1,1,1);
										DustOptions purple = new DustOptions(Color.fromRGB(204, 0, 204), 1);
										DustOptions blue = new DustOptions(Color.fromRGB(51, 51, 255), 1);
										for (Location pos : ParticlesUtils.getHollowCube(loc.add(-0.0625, -0.0625, -0.0625), loc2.add(0.0625, 0.0625, 0.0625), 0.1666)) {
											double random = Math.random() * 100;
											if (random > 95) {
												double ranColor = Math.floor(Math.random() * 2) + 1;
												if (ranColor == 1) {
													loc.getWorld().spawnParticle(Particle.REDSTONE, pos, 1, blue);
												} else if (ranColor == 2) {
													loc.getWorld().spawnParticle(Particle.REDSTONE, pos, 1, purple);
												}
											}
										}
										isEmittingParticle.add(key);
									}
								}
								String key = BookshelfUtils.locKey(player.getOpenInventory().getTopInventory().getLocation());
								if (!isEmittingParticle.contains(key)) {
									Location pos = player.getOpenInventory().getTopInventory().getLocation().clone().add(0.5, 0.5, 0.5);
									pos.getWorld().spawnParticle(Particle.PORTAL, pos, 75);
									isEmittingParticle.add(key);
								}
							}
						}
					}
				}
			}
		}, 0, 5);
	}
}

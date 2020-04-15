package com.loohp.bookshelf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.loohp.bookshelf.Debug.Debug;
import com.loohp.bookshelf.Listeners.ASEvents;
import com.loohp.bookshelf.Listeners.BBEvents;
import com.loohp.bookshelf.Listeners.Events;
import com.loohp.bookshelf.Listeners.GPEvents;
import com.loohp.bookshelf.Listeners.LWCEvents;
import com.loohp.bookshelf.Listeners.RPEvents;
import com.loohp.bookshelf.Listeners.RSEvents;
import com.loohp.bookshelf.Listeners.WGEvents;
import com.loohp.bookshelf.Metrics.Metrics;
import com.loohp.bookshelf.Updater.Updater;
import com.loohp.bookshelf.Utils.BookshelfUtils;
import com.loohp.bookshelf.Utils.EnchantmentTableUtils;
import com.loohp.bookshelf.Utils.HopperUtils;
import com.loohp.bookshelf.Utils.LegacyConfigConverter;
import com.loohp.bookshelf.Utils.ParticlesUtils;

public class Bookshelf extends JavaPlugin {
	
	public static Plugin plugin = null;
	
	public static String version = "";
	
	public static FileConfiguration config;
	public static File cfile;
	
	public static boolean LWCHook = false;
	public static boolean WGHook = false;
	public static boolean GPHook = false;
	public static boolean BlockLockerHook = false;
	public static boolean RPHook = false;
	public static boolean BBHook = false;
	public static boolean ASHook = false;
	public static boolean RSHook = false;
	
	public static boolean EnableHopperSupport = true;
	public static boolean EnableDropperSupport = true;
	public static int HopperTaskID = -1;
	public static int HopperMinecartTaskID = -1;
	public static long HopperTicksPerTransfer = 8;
	public static long HopperAmount = 1;
	
	public static ConcurrentHashMap<String, Inventory> bookshelfContent = new ConcurrentHashMap<String, Inventory>();
	
	public static ConcurrentLinkedQueue<String> bookshelfSavePending = new ConcurrentLinkedQueue<String>();
	public static ConcurrentLinkedQueue<Chunk> bookshelfLoadPending = new ConcurrentLinkedQueue<Chunk>();
	public static ConcurrentLinkedQueue<Chunk> bookshelfRemovePending = new ConcurrentLinkedQueue<Chunk>();
	
	public static ConcurrentHashMap<Player, BlockFace> lastBlockFace = new ConcurrentHashMap<Player, BlockFace>();
	
	public static ConcurrentHashMap<Player, String> requestOpen = new ConcurrentHashMap<Player, String>();
	
	public static long BookShelfRows = 2;
	public static boolean UseWhitelist = true;
	public static String Title = "Bookshelf";
	public static List<String> Whitelist = new CopyOnWriteArrayList<String>();
	public static boolean particlesEnabled = true;
	
	public static String NoPermissionToReloadMessage = "&cYou do not have permission use this command!";
	public static String NoPermissionToUpdateMessage = "&cYou do not have permission use this command!";
	
	public static List<Player> cancelOpen = new CopyOnWriteArrayList<Player>();
	public static ConcurrentLinkedQueue<Player> isDonationView = new ConcurrentLinkedQueue<Player>();
	
	public static ConcurrentLinkedQueue<String> isEmittingParticle = new ConcurrentLinkedQueue<String>();
	
	public static ConcurrentHashMap<Long, Location> tempRedstone = new ConcurrentHashMap<Long, Location>();
	
	public static ConcurrentHashMap<Player, Long> enchantSeed = new ConcurrentHashMap<Player, Long>();

	private static long spawnchunks = 0;
	private static long done = 0;
	private static String currentWorld = "world";
	
	public static long lastHopperTime = 0;
	public static long lastHoppercartTime = 0;
	
	public static boolean enchantmentTable = true;
	
	public static int eTableMulti = 1;
	
	public static boolean UpdaterEnabled = true;
	public static int UpdaterTaskID = -1;

	@Override
	public void onEnable() {	
		plugin = (Plugin)getServer().getPluginManager().getPlugin("BookShelf");
		
		getServer().getPluginManager().registerEvents(new Debug(), this);
		
		int pluginId = 6748;

		Metrics metrics = new Metrics(this, pluginId);
	    
	    getServer().getPluginManager().registerEvents(new Events(), this);
	    
	    getCommand("bookshelf").setExecutor(new Commands());
		
	    plugin.getConfig().options().copyDefaults(true);
	    plugin.saveConfig();

	    //v2.0.0 upgrade
	    if (plugin.getConfig().contains("BookShelfData")) {
	    	LegacyConfigConverter.convert();
	    }
	    //------
	    
	    if (Bukkit.getServer().getPluginManager().getPlugin("GriefPrevention") != null) {
	    	Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Bookshelf] Hooked into GriefPrevention!");
	    	getServer().getPluginManager().registerEvents(new GPEvents(), this);
			GPHook = true;
		}
		
		if (Bukkit.getServer().getPluginManager().getPlugin("LWC") != null) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Bookshelf] Hooked into LWC!");
			LWCEvents.hookLWC();
			LWCHook = true;
		}
		
		if (Bukkit.getServer().getPluginManager().getPlugin("BlockLocker") != null) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Bookshelf] Hooked into BlockLocker!");
			BlockLockerHook = true;
		}
		
		if (Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Bookshelf] Hooked into WorldGuard!");
			getServer().getPluginManager().registerEvents(new WGEvents(), this);
			WGHook = true;
		}
		
		if (Bukkit.getServer().getPluginManager().getPlugin("RedProtect") != null) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Bookshelf] Hooked into RedProtect!");
			getServer().getPluginManager().registerEvents(new RPEvents(), this);
			RPHook = true;
		}
		
		if (Bukkit.getServer().getPluginManager().getPlugin("BentoBox") != null) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Bookshelf] Hooked into BentoBox!");
			getServer().getPluginManager().registerEvents(new BBEvents(), this);
			BBHook = true;
		}
		
		if (Bukkit.getServer().getPluginManager().getPlugin("ASkyBlock") != null) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Bookshelf] Hooked into ASkyBlock!");
			getServer().getPluginManager().registerEvents(new ASEvents(), this);
			ASHook = true;
		}
		
		if (Bukkit.getServer().getPluginManager().getPlugin("Residence") != null) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Bookshelf] Hooked into Residence!");
			getServer().getPluginManager().registerEvents(new RSEvents(), this);
			RSHook = true;
		}
		
		String packageName = getServer().getClass().getPackage().getName();

        if (packageName.contains("1_15_R1")) {
            version = "1.15";
        } else if (packageName.contains("1_14_R1")) {
            version = "1.14";
        } else if (packageName.contains("1_13_R2")) {
            version = "1.13.1";
        } else if (packageName.contains("1_13_R1")) {
            version = "1.13";
        } else if (packageName.contains("1_12_R1")) {
            version = "legacy1.12";
        } else if (packageName.contains("1_11_R1")) {
            version = "legacy1.11";
        } else if (packageName.contains("1_10_R1")) {
            version = "legacy1.10";
        } else if (packageName.contains("1_9_R2")) {
            version = "legacy1.9.4";
        } else if (packageName.contains("1_9_R1")) {
            version = "legacy1.9";
        } else if (packageName.contains("1_8_R3")) {
            version = "OLDlegacy1.8.4";
        } else if (packageName.contains("1_8_R2")) {
            version = "OLDlegacy1.8.3";
        } else if (packageName.contains("1_8_R1")) {
            version = "OLDlegacy1.8";
        } else {
	    	getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Bookshelf] This version of minecraft is unsupported!");
	    	plugin.getPluginLoader().disablePlugin(this);
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
	    loadBookshelf();
	    
	    metrics.addCustomChart(new Metrics.SingleLineChart("total_bookshelves", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return BookshelfManager.getJsonObject().size();
            }
        }));
	    
	    metrics.addCustomChart(new Metrics.SimplePie("hoppers_enabled", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	        	String string = "Disabled";
	        	if (EnableHopperSupport == true) {
	        		string = "Enabled";
	        	}
	            return string;
	        }
	    }));
	    
	    metrics.addCustomChart(new Metrics.SimplePie("droppers_enabled", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	        	String string = "Disabled";
	        	if (EnableDropperSupport == true) {
	        		string = "Enabled";
	        	}
	            return string;
	        }
	    }));
	    
	    metrics.addCustomChart(new Metrics.SimplePie("enchtable_enabled", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	        	String string = "Disabled";
	        	if (enchantmentTable == true) {
	        		string = "Enabled";
	        	}
	            return string;
	        }
	    }));
	    
	    metrics.addCustomChart(new Metrics.SingleLineChart("average_hopper_process_time", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
            	int num = 2147483647;
            	if (lastHopperTime < 2147483647) {
            		num = (int) lastHopperTime;
            	}
                return num;
            }
        }));
	    
	    metrics.addCustomChart(new Metrics.SingleLineChart("average_hopper_minecart_process_time", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
            	int num = 2147483647;
            	if (lastHoppercartTime < 2147483647) {
            		num = (int) lastHoppercartTime;
            	}
                return num;
            }
        }));
		
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Bookshelf] BookShelf has been Enabled!");
	}

	@Override
	public void onDisable() {
		getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Saving all pending bookshelves..");
		long start = System.currentTimeMillis();
		for (String entry : bookshelfSavePending) {
			if (bookshelfContent.containsKey(entry)) {
				BookshelfUtils.saveBookShelf(entry);
			}
		}
		BookshelfManager.save();
		getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Bookshelves saved! (" + (System.currentTimeMillis() - start) + "ms)");
		getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Bookshelf] BookShelf has been Disabled!");
	}
	
	public static void loadConfig() {	
		BookShelfRows = plugin.getConfig().getLong("Options.BookShelfRows");
		UseWhitelist = plugin.getConfig().getBoolean("Options.UseWhitelist");
		Whitelist = plugin.getConfig().getStringList("Options.Whitelist");
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
			HopperTicksPerTransfer = Bukkit.spigot().getConfig().getLong("world-settings.default.ticks-per.hopper-transfer");
			HopperAmount = Bukkit.spigot().getConfig().getLong("world-settings.default.hopper-amount");
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
	
	public void loadBookshelf() {
		long start = System.currentTimeMillis();
		for (World world : Bukkit.getWorlds()) {
			spawnchunks = world.getLoadedChunks().length;
			done = 0;
			currentWorld = world.getName();
			Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Loading bookshelves in spawn chunks in " + world.getName());
			loadBookshelfProgress();
			for (Chunk chunk: world.getLoadedChunks()) {
				for (Block block : BookshelfUtils.getAllBookshelvesInChunk(chunk)) {
					String loc = BookshelfUtils.locKey(block.getLocation());
					if (!bookshelfContent.containsKey(loc)) {
						if (!BookshelfManager.contains(loc)) {
							String bsTitle = Title;
							bookshelfContent.put(loc , Bukkit.createInventory(null, (int) (BookShelfRows * 9), bsTitle));
							BookshelfManager.setTitle(loc, bsTitle);
							BookshelfUtils.saveBookShelf(loc);
						} else {
							BookshelfUtils.loadBookShelf(loc);
						}
					}
				}
				done = done + 1;
			}
			Bukkit.getConsoleSender().sendMessage("[Bookshelf] Preparing bookshelves in spawn chunks in " + currentWorld + ": 100%");
		}
		BookshelfManager.save();
		BookshelfManager.intervalSaveToFile();
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Bookshelf] Bookshelves loaded! (" + (System.currentTimeMillis() - start) + "ms)");
	}
	
	public void loadBookshelfProgress() {
		CompletableFuture.runAsync(()->{
			String thisWorld = currentWorld;
			while (done < spawnchunks && thisWorld == currentWorld) {
				Bukkit.getConsoleSender().sendMessage("[Bookshelf] Preparing bookshelves in spawn chunks in " + currentWorld + ": " + Math.round((double) ((double) done / (double) spawnchunks) * 100) + "%");
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error");
				}
			}
		});
	}
	
	public void intervalSave() {
		new BukkitRunnable() {
			public void run() {
				List<String> removeList = new ArrayList<String>();
				for (String entry : bookshelfSavePending) {
					if (bookshelfContent.containsKey(entry)) {
						if (!removeList.contains(entry)) {
							BookshelfUtils.saveBookShelf(entry);
						}
					}
					removeList.add(entry);
				}
				bookshelfSavePending.clear();
				removeList.clear();
			}
		}.runTaskTimer(this, 0, 40);
	}
	
	public void intervalLoad() {
		new BukkitRunnable() {
			public void run() {
				List<Chunk> remove = new ArrayList<Chunk>();
				int i = 1;
				for (Chunk chunk : bookshelfLoadPending) {
					for (Block block : BookshelfUtils.getAllBookshelvesInChunk(chunk)) {
						String loc = BookshelfUtils.locKey(block.getLocation());
						if (!bookshelfContent.containsKey(loc)) {
							if (!BookshelfManager.contains(loc)) {
								String bsTitle = Title;
								bookshelfContent.put(loc , Bukkit.createInventory(null, (int) (BookShelfRows * 9), bsTitle));
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
			}
		}.runTaskTimer(this, 0, 1);
	}
	
	public void intervalRemove() {
		new BukkitRunnable() {
			public void run() {
				List<Chunk> remove = new ArrayList<Chunk>();
				int i = 1;
				for (Chunk chunk : bookshelfRemovePending) {
					for (Block block : BookshelfUtils.getAllBookshelvesInChunk(chunk)) {
						String loc = BookshelfUtils.locKey(block.getLocation());
						if (bookshelfContent.containsKey(loc)) {
							BookshelfUtils.saveBookShelf(loc, true);
						}
					}
					remove.add(chunk);
					i++;
					if (i > 2) {
						break;
					}
				}
				for (Chunk chunk : remove) {
					bookshelfRemovePending.remove(chunk);
				}
			}
		}.runTaskTimer(this, 0, 1);
	}
	
	public void particles() {
		new BukkitRunnable() {
			public void run() {
				if (particlesEnabled == true && !version.contains("legacy")) {
					isEmittingParticle.clear();
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (player.getOpenInventory() != null) {
							for (Entry<String, Inventory> entry : bookshelfContent.entrySet()) {
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
			}
		}.runTaskTimerAsynchronously(this, 0, 5);
	}
}

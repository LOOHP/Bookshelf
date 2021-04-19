package com.loohp.bookshelf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loohp.bookshelf.objectholders.BlockPosition;
import com.loohp.bookshelf.objectholders.BookshelfHolder;
import com.loohp.bookshelf.objectholders.ChunkPosition;
import com.loohp.bookshelf.utils.BookshelfUtils;
import com.loohp.bookshelf.utils.EnchantmentTableUtils;
import com.loohp.bookshelf.utils.OpenInvUtils;
import com.loohp.bookshelf.utils.ParticlesUtils;
import com.loohp.bookshelf.utils.VanishUtils;

import net.md_5.bungee.api.ChatColor;

public class BookshelfManager implements Listener, AutoCloseable {

	private static final Map<World, BookshelfManager> BOOKSHELF_MANAGER = Collections.synchronizedMap(new LinkedHashMap<>());
	
	public static BookshelfManager getBookshelfManager(World world) {
		return BOOKSHELF_MANAGER.get(world);
	}
	
	public static List<World> getWorlds() {
		synchronized (BOOKSHELF_MANAGER) {
			return new ArrayList<>(BOOKSHELF_MANAGER.keySet());
		}
	}
	
	public static Iterable<BookshelfHolder> getAllLoadedBookshelves() {
		Iterable<BookshelfHolder> itr = new ArrayList<>();
		for (BookshelfManager manager : BOOKSHELF_MANAGER.values()) {
			itr = Iterables.concat(itr, manager.getLoadedBookshelves());
		}
		return Iterables.unmodifiableIterable(itr);
	}
	
	public synchronized static BookshelfManager loadWorld(Bookshelf plugin, World world) {
		BookshelfManager manager = BOOKSHELF_MANAGER.get(world);
		if (manager != null) {
			return manager;
		}
		manager = new BookshelfManager(plugin, world);
		BOOKSHELF_MANAGER.put(world, manager);
		return manager;
	}
	
	private final Bookshelf plugin;
	private final World world;
	private final File bookshelfFolder;
	private final Map<ChunkPosition, Map<BlockPosition, BookshelfHolder>> loadedBookshelves;	
	private final Set<String> isEmittingParticle = new HashSet<>();
	
	private final Object lock;
	
	private final int autoSaveTask;
	private final int particleTaskId;
	
	private BookshelfManager(Bookshelf plugin, World world) {
		this.lock = new Object();
		this.plugin = plugin;
		this.world = world;
		if (world.getEnvironment().equals(Environment.NETHER)) {
			this.bookshelfFolder = new File(world.getWorldFolder(), "DIM-1/bookshelf");
		} else if (world.getEnvironment().equals(Environment.THE_END)) {
			this.bookshelfFolder = new File(world.getWorldFolder(), "DIM1/bookshelf");
		} else {
			this.bookshelfFolder = new File(world.getWorldFolder(), "bookshelf");
		}
		this.bookshelfFolder.mkdirs();
		this.loadedBookshelves = new ConcurrentHashMap<>();
		
		int spawnchunks = world.getLoadedChunks().length;
		AtomicInteger done = new AtomicInteger(0);
		
		CompletableFuture.runAsync(()->{
			long start = System.currentTimeMillis();
			long lastDone = 0;
			while (done.get() < spawnchunks) {
				Bukkit.getConsoleSender().sendMessage("[Bookshelf] Preparing bookshelves in spawn chunks in " + world.getName() + ": " + Math.round((double) ((double) done.get() / (double) spawnchunks) * 100) + "%");
				if ((System.currentTimeMillis() - start) > 30000) {
					return;
				}
				if (lastDone != done.get()) {
					start = System.currentTimeMillis();
					lastDone = done.get();
				}
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException ignore) {}
			}
		});
		
		for (Chunk chunk : world.getLoadedChunks()) {
			loadChunk(new ChunkPosition(chunk), true);
			done.incrementAndGet();
		}
		
		Bukkit.getConsoleSender().sendMessage("[Bookshelf] Preparing bookshelves in spawn chunks in " + world.getName() + ": 100%");
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
		
		autoSaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			for (ChunkPosition chunk : loadedBookshelves.keySet()) {
				saveChunk(chunk, false);
				if (!plugin.isEnabled()) {
					return;
				}
			}
		}, 6000, 6000).getTaskId();
		
		particleTaskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			if (Bookshelf.particlesEnabled && !Bookshelf.version.isLegacy()) {
				isEmittingParticle.clear();
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (OpenInvUtils.isSlientChest(player)) {
						continue;
					}
					if (VanishUtils.isVanished(player)) {
						continue;
					}
					if (player.getOpenInventory() != null) {
						for (Map<BlockPosition, BookshelfHolder> mapping : loadedBookshelves.values()) {
							for (BookshelfHolder bookshelf : mapping.values()) {
								String key = BookshelfUtils.posKey(bookshelf.getPosition());
								if (!isEmittingParticle.contains(key)) {
									if (bookshelf.getInventory().equals(player.getOpenInventory().getTopInventory())) {
										Location loc = bookshelf.getPosition().getLocation();
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
										isEmittingParticle.add(key);
									}
								}
							}
						}
						if (Bookshelf.enchantmentTable) {
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
		}, 0, 5).getTaskId();
	}
	
	public World getWorld() {
		return world;
	}
	
	public File getBookshelfFolder() {
		return bookshelfFolder;
	}
	
	public int size() {
		return loadedBookshelves.values().stream().mapToInt(each -> each.size()).sum();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChunkLoad(ChunkLoadEvent event) {
		if (!event.getWorld().equals(world)) {
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			loadChunk(new ChunkPosition(event.getChunk()), true);
		});
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChunkUnload(ChunkUnloadEvent event) {
		if (!event.getWorld().equals(world)) {
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			saveChunk(new ChunkPosition(event.getChunk()), true);
		});
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldUnload(WorldUnloadEvent event) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
			close();
		}, 10);
	}
	
	public Iterable<BookshelfHolder> getLoadedBookshelves() {
		Iterable<BookshelfHolder> itr = new ArrayList<>();
		for (Map<BlockPosition, BookshelfHolder> map : loadedBookshelves.values()) {
			itr = Iterables.concat(itr, map.values());
		}
		return Iterables.unmodifiableIterable(itr);
	}
	
	@SuppressWarnings("deprecation")
	public BookshelfHolder getOrCreateBookself(BlockPosition position, String title) {
		if (!position.getWorld().equals(world)) {
			return null;
		}
		Map<BlockPosition, BookshelfHolder> chunkEntry = loadedBookshelves.get(position.getChunkPosition());
		if (chunkEntry == null) {
			chunkEntry = loadChunk(position.getChunkPosition(), false);
		}
		BookshelfHolder bookshelf = chunkEntry.get(position);
		if (bookshelf == null) {
			bookshelf = new BookshelfHolder(position, title, null);
			Inventory inventory = Bukkit.createInventory(bookshelf, Bookshelf.bookShelfRows * 9, title);
			bookshelf.setInventory(inventory);
			chunkEntry.put(position, bookshelf);
		}
		return bookshelf;
	}
	
	@SuppressWarnings("deprecation")
	public void move(BlockPosition position, int x, int y, int z) {
		if (!position.getWorld().equals(world)) {
			throw new IllegalArgumentException("Position not in world");
		}
		Map<BlockPosition, BookshelfHolder> chunkEntry = loadedBookshelves.get(position.getChunkPosition());
		if (chunkEntry == null) {
			chunkEntry = loadChunk(position.getChunkPosition(), false);
		}
		BookshelfHolder bookshelf = chunkEntry.get(position);
		if (bookshelf == null) {
			return;
		}
		chunkEntry.remove(position);
		BlockPosition newPos = new BlockPosition(world, x, y, z);
		bookshelf.setPosition(newPos);
		chunkEntry.put(newPos, bookshelf);
	}
	
	public void remove(BlockPosition position) {
		if (!position.getWorld().equals(world)) {
			throw new IllegalArgumentException("Position not in world");
		}
		Map<BlockPosition, BookshelfHolder> chunkEntry = loadedBookshelves.get(position.getChunkPosition());
		if (chunkEntry == null) {
			chunkEntry = loadChunk(position.getChunkPosition(), false);
		}
		BookshelfHolder bookshelf = chunkEntry.get(position);
		if (bookshelf == null) {
			return;
		}
		chunkEntry.remove(position);
	}
	
	@SuppressWarnings("deprecation")
	private Map<BlockPosition, BookshelfHolder> loadChunk(ChunkPosition chunk, boolean checkPresence) {
		if (!chunk.getWorld().equals(world)) {
			throw new IllegalArgumentException("Position not in world");
		}
		synchronized (lock) {
			Map<BlockPosition, BookshelfHolder> chunkEntry = loadedBookshelves.get(chunk);
			if (chunkEntry != null) {
				return chunkEntry;
			}
			chunkEntry = new ConcurrentHashMap<>();
			loadedBookshelves.put(chunk, chunkEntry);
			File regionFolder = new File(bookshelfFolder, "r." + (chunk.getChunkX() >> 5) + "." + (chunk.getChunkZ() >> 5));
			File chunkFile = new File(regionFolder, "c." + chunk.getChunkX() + "." + chunk.getChunkZ() + ".json");
			if (chunkFile.exists()) {
				try (InputStreamReader reader = new InputStreamReader(new FileInputStream(chunkFile), StandardCharsets.UTF_8)) {
					JSONObject json = (JSONObject) new JSONParser().parse(reader);
					for (Object obj : json.keySet()) {
						String key = obj.toString();
						BlockPosition position = BookshelfUtils.keyPos(world, key);
						if (!checkPresence || position.getBlock().getType().equals(Material.BOOKSHELF)) {
							JSONObject entry = (JSONObject) json.get(key);
							String title = entry.get("Title").toString();
							BookshelfHolder bookshelf = new BookshelfHolder(position, title, null);
							Inventory inventory = BookshelfUtils.fromBase64(entry.get("Inventory").toString(), title, bookshelf);
							bookshelf.setInventory(inventory);
							chunkEntry.put(position, bookshelf);
						}
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			return chunkEntry;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void saveChunk(ChunkPosition chunk, boolean unload) {
		if (!chunk.getWorld().equals(world)) {
			throw new IllegalArgumentException("Position not in world");
		}
		synchronized (lock) {
			Map<BlockPosition, BookshelfHolder> chunkEntry = loadedBookshelves.get(chunk);
			if (chunkEntry == null) {
				return;
			}			
			File regionFolder = new File(bookshelfFolder, "r." + (chunk.getChunkX() >> 5) + "." + (chunk.getChunkZ() >> 5));
			if (!chunkEntry.isEmpty()) {
				regionFolder.mkdirs();
				File chunkFile = new File(regionFolder, "c." + chunk.getChunkX() + "." + chunk.getChunkZ() + ".json");
				try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(chunkFile), StandardCharsets.UTF_8))) {
					JSONObject toSave = new JSONObject();
					for (BookshelfHolder bookshelf : chunkEntry.values()) {
						String key = BookshelfUtils.posKey(bookshelf.getPosition());
						JSONObject entry = new JSONObject();
						entry.put("Title", bookshelf.getTitle());
						entry.put("Inventory", BookshelfUtils.toBase64(bookshelf.getInventory()));
						toSave.put(key, entry);
					}
		        	Gson g = new GsonBuilder().setPrettyPrinting().create();
		            String prettyJsonString = g.toJson(toSave);
		            pw.println(prettyJsonString);
		            pw.flush();
				} catch (Throwable e) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Bookshelf] Unable to save chunk " + chunk.getChunkX() + ", " + chunk.getChunkZ() + " in " + regionFolder.getPath());
					e.printStackTrace();
				}
			} else {
				File chunkFile = new File(regionFolder, "c." + chunk.getChunkX() + "." + chunk.getChunkZ() + ".json");
				if (chunkFile.exists()) {
					chunkFile.delete();
				}
			}
			if (unload) {
				loadedBookshelves.remove(chunk);
			}
		}
	}
	
	@Override
	public void close() {
		Bukkit.getScheduler().cancelTask(autoSaveTask);
		Bukkit.getScheduler().cancelTask(particleTaskId);
		BOOKSHELF_MANAGER.remove(world);
		synchronized (lock) {
			Bukkit.getConsoleSender().sendMessage("[Bookshelf] Saving bookshelves for world " + world.getName());
			for (ChunkPosition chunk : loadedBookshelves.keySet()) {
				saveChunk(chunk, true);
			}
			Bukkit.getConsoleSender().sendMessage("[Bookshelf] (" + world.getName() + "): All bookshelves are saved");
		}
	}
	
}

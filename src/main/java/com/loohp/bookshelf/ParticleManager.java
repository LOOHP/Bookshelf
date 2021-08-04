package com.loohp.bookshelf;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Block;

import com.loohp.bookshelf.utils.EnchantmentTableUtils;
import com.loohp.bookshelf.utils.ParticlesUtils;

public class ParticleManager implements AutoCloseable {
	
	private final Bookshelf plugin;
	private final Set<Block> openedBookshelves;
	private final Map<Block, Set<Block>> enchantingBookshelves;
	private final Random random;
	
	private final int particleTaskId;
	
	public ParticleManager(Bookshelf plugin) {
		this.plugin = plugin;
		this.openedBookshelves = Collections.newSetFromMap(new ConcurrentHashMap<>());
		this.enchantingBookshelves = new ConcurrentHashMap<>();
		this.random = new Random();
		
		if (!Bookshelf.version.isLegacy()) {
			DustOptions purple0 = new DustOptions(Color.fromRGB(153, 51, 255), 1);
			DustOptions yellow = new DustOptions(Color.fromRGB(255, 255, 0), 1);
			
			DustOptions purple1 = new DustOptions(Color.fromRGB(204, 0, 204), 1);
			DustOptions blue = new DustOptions(Color.fromRGB(51, 51, 255), 1);
			
			this.particleTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
				if (Bookshelf.bookshelfParticlesEnabled) {
					for (Block block : openedBookshelves) {
						Location loc = block.getLocation();
						Location loc2 = loc.clone().add(1, 1, 1);
						for (Location pos : ParticlesUtils.getHollowCube(loc.add(-0.0625, -0.0625, -0.0625), loc2.add(0.0625, 0.0625, 0.0625), 0.1666)) {
							double randomNum = random.nextDouble();
							if (randomNum > 0.95) {
								double ranColor = random.nextInt(2) + 1;
								if (ranColor == 1) {
									loc.getWorld().spawnParticle(Particle.REDSTONE, pos, 1, yellow);
								} else if (ranColor == 2) {
									loc.getWorld().spawnParticle(Particle.REDSTONE, pos, 1, purple0);
								}
							}
						}
					}
				}
				if (Bookshelf.enchantingParticlesCount > 0) {
					Set<Block> enchantEmitted = new HashSet<>();
					for (Entry<Block, Set<Block>> entry : enchantingBookshelves.entrySet()) {
						if (!entry.getValue().isEmpty()) {
							Location tablePos = entry.getKey().getLocation().add(0.5, 0.5, 0.5);
							tablePos.getWorld().spawnParticle(Particle.PORTAL, tablePos, Bookshelf.enchantingParticlesCount);
							for (Block block : entry.getValue()) {
								if (!enchantEmitted.contains(block)) {
									Location loc = block.getLocation().clone();
									Location loc2 = loc.clone().add(1,1,1);
									for (Location pos : ParticlesUtils.getHollowCube(loc.add(-0.0625, -0.0625, -0.0625), loc2.add(0.0625, 0.0625, 0.0625), 0.1666)) {
										double randomNum = random.nextDouble();
										if (randomNum > 0.98) {
											double ranColor = random.nextInt(2) + 1;
											if (ranColor == 1) {
												loc.getWorld().spawnParticle(Particle.REDSTONE, pos, 1, blue);
											} else if (ranColor == 2) {
												loc.getWorld().spawnParticle(Particle.REDSTONE, pos, 1, purple1);
											}
										}
									}
									enchantEmitted.add(block);
								}
							}
						}
					}
				}
			}, 0, 5).getTaskId();
		} else {
			this.particleTaskId = -1;
		}
	}
	
	public void openBookshelf(Block bookshelf) {
		openedBookshelves.add(bookshelf);
	}
	
	public void closeBookshelf(Block bookshelf) {
		openedBookshelves.remove(bookshelf);
	}
	
	public void openEnchant(Block enchantingTable) {
		Set<Block> bookshelves = Collections.newSetFromMap(new ConcurrentHashMap<>());
		for (Block b : EnchantmentTableUtils.getBookshelves(enchantingTable)) {
			bookshelves.add(b);
		}
		enchantingBookshelves.put(enchantingTable, bookshelves);
	}
	
	public void removeEnchantBookshelf(Block bookshelf) {
		for (Set<Block> set : enchantingBookshelves.values()) {
			set.remove(bookshelf);
		}
	}
	
	public void closeEnchant(Block enchantingTable) {
		enchantingBookshelves.remove(enchantingTable);
	}

	@Override
	public void close() {
		if (particleTaskId >= 0) {
			Bukkit.getScheduler().cancelTask(particleTaskId);
		}
	}

}

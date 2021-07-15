package com.loohp.bookshelf;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleyaml.configuration.file.FileConfiguration;

import com.loohp.bookshelf.config.Config;
import com.loohp.bookshelf.debug.Debug;
import com.loohp.bookshelf.hooks.InteractionVisualizerAnimations;
import com.loohp.bookshelf.listeners.BookshelfEvents;
import com.loohp.bookshelf.listeners.CreativeEvents;
import com.loohp.bookshelf.listeners.DispenserEvents;
import com.loohp.bookshelf.listeners.EnchantingEvents;
import com.loohp.bookshelf.listeners.PistonEvents;
import com.loohp.bookshelf.listeners.hooks.ASkyBlockEvents;
import com.loohp.bookshelf.listeners.hooks.BentoBoxEvents;
import com.loohp.bookshelf.listeners.hooks.GriefPreventionEvents;
import com.loohp.bookshelf.listeners.hooks.LWCEvents;
import com.loohp.bookshelf.listeners.hooks.LandEvents;
import com.loohp.bookshelf.listeners.hooks.PlotSquared4Events;
import com.loohp.bookshelf.listeners.hooks.PlotSquared5Events;
import com.loohp.bookshelf.listeners.hooks.PlotSquared6Events;
import com.loohp.bookshelf.listeners.hooks.RedProtectEvents;
import com.loohp.bookshelf.listeners.hooks.ResidenceEvents;
import com.loohp.bookshelf.listeners.hooks.SuperiorSkyblock2Events;
import com.loohp.bookshelf.listeners.hooks.TownyEvents;
import com.loohp.bookshelf.listeners.hooks.WorldGuardEvents;
import com.loohp.bookshelf.metrics.Charts;
import com.loohp.bookshelf.metrics.Metrics;
import com.loohp.bookshelf.objectholders.LWCRequestOpenData;
import com.loohp.bookshelf.updater.Updater;
import com.loohp.bookshelf.utils.HopperUtils;
import com.loohp.bookshelf.utils.MCVersion;
import com.loohp.bookshelf.utils.legacy.LegacyConfigConverter;

import net.md_5.bungee.api.ChatColor;

public class Bookshelf extends JavaPlugin {
	
	public static final int BSTATS_PLUGIN_ID = 6748;
	public static final String CONFIG_ID = "config";
	
	public static Bookshelf plugin = null;
	
	public static MCVersion version;
	
	public static boolean vanishHook = false;
	public static boolean cmiHook = false;
	public static boolean essentialsHook = false;
	public static boolean openInvHook = false;
	public static boolean lwcHook = false;
	public static boolean worldGuardHook = false;
	public static boolean griefPreventionHook = false;
	public static boolean blockLockerHook = false;
	public static boolean redProtectHook = false;
	public static boolean bentoBoxHook = false;
	public static boolean aSkyBlockHook = false;
	public static boolean residenceHook = false;
	public static boolean townyHook = false;
	public static boolean superiorSkyblock2Hook = false;
	public static boolean landHook = false;
	public static boolean plotSquaredHook = false;
	public static boolean interactionVisualizerHook = false;
	
	public static boolean enableHopperSupport = true;
	public static boolean enableDropperSupport = true;
	public static int hopperTaskID = -1;
	public static int hopperMinecartTaskID = -1;
	public static int hopperTicksPerTransfer = 8;
	public static int hopperAmount = 1;
	
	public static Map<Player, BlockFace> lastBlockFace = new ConcurrentHashMap<>();
	
	public static Map<Player, LWCRequestOpenData> requestOpen = new ConcurrentHashMap<>();
	
	public static int bookShelfRows = 2;
	public static boolean useWhitelist = true;
	public static String title = "Bookshelf";
	public static Set<String> whitelist = new HashSet<>();
	public static boolean bookshelfParticlesEnabled = true;
	
	public static String noPermissionToReloadMessage = "&cYou do not have permission use this command!";
	public static String noPermissionToUpdateMessage = "&cYou do not have permission use this command!";
	
	public static Set<UUID> lwcCancelOpen = ConcurrentHashMap.newKeySet();
	public static Set<UUID> isDonationView = ConcurrentHashMap.newKeySet();
	
	public static ConcurrentHashMap<Player, Long> enchantSeed = new ConcurrentHashMap<>();
	
	public static long lastHopperTime = 0;
	public static long lastHoppercartTime = 0;
	
	public static boolean enchantmentTable = true;
	public static int eTableMulti = 1;
	public static int enchantingParticlesCount = 75;
	
	public static boolean updaterEnabled = true;
	public static int updaterTaskID = -1;

	@Override
	@SuppressWarnings("deprecation")
	public void onEnable() {	
		plugin = this;
		
		getServer().getPluginManager().registerEvents(new Debug(), this);

		Metrics metrics = new Metrics(this, BSTATS_PLUGIN_ID);
		
		version = MCVersion.fromPackageName(getServer().getClass().getPackage().getName());
		
		//Rename old folder
		File pluginFolder = new File(Bukkit.getWorldContainer(), "plugins");
		if (pluginFolder.exists() && pluginFolder.isDirectory()) {
			for (File file : pluginFolder.listFiles()) {
				if (file.isDirectory() && file.getName().equals("BookShelf")) {
					file.renameTo(new File(pluginFolder, getName()));
				}
			}
		}
	    
	    getServer().getPluginManager().registerEvents(new BookshelfEvents(), this);
	    getServer().getPluginManager().registerEvents(new CreativeEvents(), this);
	    getServer().getPluginManager().registerEvents(new DispenserEvents(), this);
	    getServer().getPluginManager().registerEvents(new EnchantingEvents(), this);
	    getServer().getPluginManager().registerEvents(new PistonEvents(), this);
	    
	    getCommand("bookshelf").setExecutor(new Commands());
		
	    if (!getDataFolder().exists()) {
	    	getDataFolder().mkdirs();
	    }
	    Config.loadConfig(CONFIG_ID, new File(getDataFolder(), "config.yml"), getClass().getClassLoader().getResourceAsStream("config.yml"), getClass().getClassLoader().getResourceAsStream("config.yml"), true);
	    
	    String SuperVanish = "SuperVanish";
	    String PremiumVanish = "PremiumVanish";
	    if (getServer().getPluginManager().getPlugin(SuperVanish) != null || getServer().getPluginManager().getPlugin(PremiumVanish) != null) {
			hookMessage(SuperVanish + "/" + PremiumVanish);
			vanishHook = true;
		}
	    
	    String CMI = "CMI";
		if (getServer().getPluginManager().getPlugin(CMI) != null) {
			hookMessage(CMI);
			cmiHook = true;
		}
		
		String Essentials = "Essentials";
		if (getServer().getPluginManager().getPlugin(Essentials) != null) {
			hookMessage(Essentials);
			essentialsHook = true;
		}
	    
	    String OpenInv = "OpenInv";
	    if (getServer().getPluginManager().getPlugin(OpenInv) != null) {
			hookMessage(OpenInv);
			openInvHook = true;
		}
	    
	    String GriefPrevention = "GriefPrevention";
	    if (getServer().getPluginManager().getPlugin("GriefPrevention") != null) {
	    	hookMessage(GriefPrevention);
	    	getServer().getPluginManager().registerEvents(new GriefPreventionEvents(), this);
			griefPreventionHook = true;
		}
		
	    String LWC = "LWC";
		if (getServer().getPluginManager().getPlugin(LWC) != null) {
			hookMessage(LWC);
			LWCEvents.hookLWC();
			lwcHook = true;
		}
		
		String BlockLocker = "BlockLocker";
		if (getServer().getPluginManager().getPlugin(BlockLocker) != null) {
			hookMessage(BlockLocker);
			blockLockerHook = true;
		}
		
		String WorldGuard = "WorldGuard";
		if (getServer().getPluginManager().getPlugin(WorldGuard) != null) {
			hookMessage(WorldGuard);
			getServer().getPluginManager().registerEvents(new WorldGuardEvents(), this);
			worldGuardHook = true;
		}
		
		String RedProtect = "RedProtect";
		if (getServer().getPluginManager().getPlugin(RedProtect) != null) {
			hookMessage(RedProtect);
			getServer().getPluginManager().registerEvents(new RedProtectEvents(), this);
			redProtectHook = true;
		}
		
		String BentoBox = "BentoBox";
		if (getServer().getPluginManager().getPlugin(BentoBox) != null) {
			hookMessage(BentoBox);
			getServer().getPluginManager().registerEvents(new BentoBoxEvents(), this);
			bentoBoxHook = true;
		}
		
		String ASkyBlock = "ASkyBlock";
		if (getServer().getPluginManager().getPlugin(ASkyBlock) != null) {
			hookMessage(ASkyBlock);
			getServer().getPluginManager().registerEvents(new ASkyBlockEvents(), this);
			aSkyBlockHook = true;
		}
		
		String Residence = "Residence";	
		if (getServer().getPluginManager().getPlugin(Residence) != null) {
			hookMessage(Residence);
			getServer().getPluginManager().registerEvents(new ResidenceEvents(), this);
			residenceHook = true;
		}
		
		String Towny = "Towny";
		if (getServer().getPluginManager().getPlugin(Towny) != null) {
			hookMessage(Towny);
			getServer().getPluginManager().registerEvents(new TownyEvents(), this);
			townyHook = true;
		}
		
		String SuperiorSkyblock2 = "SuperiorSkyblock2";
		if (getServer().getPluginManager().getPlugin(SuperiorSkyblock2) != null) {
			hookMessage(SuperiorSkyblock2);
			getServer().getPluginManager().registerEvents(new SuperiorSkyblock2Events(), this);
			superiorSkyblock2Hook = true;
		}
		
		String Lands = "Lands";
		if (getServer().getPluginManager().getPlugin(Lands) != null) {
			hookMessage(Lands);
			getServer().getPluginManager().registerEvents(new LandEvents(), this);
			LandEvents.setup();
			landHook = true;
		}
		
		String PlotSquared = "PlotSquared";
		if (getServer().getPluginManager().getPlugin(PlotSquared) != null) {
			String plotSquaredVersion = getServer().getPluginManager().getPlugin(PlotSquared).getDescription().getVersion();
			if (plotSquaredVersion.startsWith("6.")) {
				hookMessage(PlotSquared + " (v6)");
				getServer().getPluginManager().registerEvents(new PlotSquared6Events(), this);
				plotSquaredHook = true;
			} else if (plotSquaredVersion.startsWith("5.")) {
				hookMessage(PlotSquared + " (v5)");
				getServer().getPluginManager().registerEvents(new PlotSquared5Events(), this);
				plotSquaredHook = true;
			} else if (plotSquaredVersion.startsWith("4.")) {
				hookMessage(PlotSquared + " (v4)");
				getServer().getPluginManager().registerEvents(new PlotSquared4Events(), this);
				plotSquaredHook = true;
			} else {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Bookshelf] This version of PlotSquared is not supported, only v4 and v5 is supported so far.");
			}
		}
		
		String InteractionVisualizer = "InteractionVisualizer";
		if (getServer().getPluginManager().getPlugin(InteractionVisualizer) != null) {
			hookMessage(InteractionVisualizer);
			InteractionVisualizerAnimations iva = new InteractionVisualizerAnimations();
			iva.register();
			getServer().getPluginManager().registerEvents(iva, this);
			interactionVisualizerHook = true;
		}

		if (!version.isSupported()) {
	    	getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Bookshelf] This version of minecraft is unsupported!");
	    }
		
		if (getConfiguration().contains("Options.EnableHopperDropperSupport")) {
			boolean setting = getConfiguration().getBoolean("Options.EnableHopperDropperSupport");
			getConfiguration().set("Options.EnableHopperSupport", setting);
			getConfiguration().set("Options.EnableDropperSupport", setting);
			getConfiguration().set("Options.EnableHopperDropperSupport", null);
			Config.getConfig(CONFIG_ID).save();
		}
	    
	    loadConfig();
	    
	    File legacyData = new File(Bookshelf.plugin.getDataFolder().getAbsolutePath() + "/bookshelfdata.json");
	    
	    for (World world : getServer().getWorlds()) {
	    	BookshelfManager manager = BookshelfManager.loadWorld(this, world);
	    	if (legacyData.exists()) {
	    		LegacyConfigConverter.mergeLegacy(legacyData, manager);
	    	}
	    }
	    
	    Charts.loadCharts(metrics);
		
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Bookshelf] BookShelf has been Enabled!");
	}

	@Override
	public void onDisable() {
		getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Saving bookshelves..");
		long start = System.currentTimeMillis();
		for (World world : BookshelfManager.getWorlds()) {
			BookshelfManager.getBookshelfManager(world).close();
		}
		getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Bookshelves saved! (" + (System.currentTimeMillis() - start) + "ms)");
		getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Bookshelf] BookShelf has been Disabled!");
	}
	
	private static void hookMessage(String name) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Bookshelf] Hooked into " + name + "!");
	}
	
	public static FileConfiguration getConfiguration() {
		return Config.getConfig(CONFIG_ID).getConfiguration();
	}
	
	public static void loadConfig() {
		Config config = Config.getConfig(CONFIG_ID);
		config.reload();
		
		bookShelfRows = getConfiguration().getInt("Options.BookShelfRows");
		useWhitelist = getConfiguration().getBoolean("Options.UseWhitelist");
		whitelist = getConfiguration().getStringList("Options.Whitelist").stream().collect(Collectors.toSet());
		title = ChatColor.translateAlternateColorCodes('&', getConfiguration().getString("Options.Title"));
		noPermissionToReloadMessage = getConfiguration().getString("Options.NoPermissionToReloadMessage");
		noPermissionToUpdateMessage = getConfiguration().getString("Options.NoPermissionToUpdateMessage");
		bookshelfParticlesEnabled = getConfiguration().getBoolean("Options.ParticlesWhenOpened");
		enableHopperSupport = getConfiguration().getBoolean("Options.EnableHopperSupport");
		enableDropperSupport = getConfiguration().getBoolean("Options.EnableDropperSupport");
		enchantmentTable = getConfiguration().getBoolean("Options.EnableEnchantmentTableBoosting");
		int eTableChance = getConfiguration().getInt("Options.EnchantmentTableBoostingMaxPercentage");
		if (eTableChance > 100) {
			eTableChance = 100;
		} else if (eTableChance < 0) {
			eTableChance = 0;
		}
		eTableMulti = (int) Math.pow(((double) eTableChance / 100.0), -1);
		enchantingParticlesCount = getConfiguration().getInt("Options.EnchantingParticlesCount");
		
		lastHopperTime = 0;
		lastHoppercartTime = 0;
		if (hopperTaskID >= 0) {
			Bukkit.getScheduler().cancelTask(hopperTaskID);
		}
		if (hopperMinecartTaskID >= 0) {
			Bukkit.getScheduler().cancelTask(hopperMinecartTaskID);
		}
		if (enableHopperSupport == true) {
			hopperTicksPerTransfer = Bukkit.spigot().getConfig().getInt("world-settings.default.ticks-per.hopper-transfer");
			hopperAmount = Bukkit.spigot().getConfig().getInt("world-settings.default.hopper-amount");
			HopperUtils.hopperCheck();
			HopperUtils.hopperMinecartCheck();
		}
		
		if (updaterTaskID >= 0) {
			Bukkit.getScheduler().cancelTask(updaterTaskID);
		}
		updaterEnabled = getConfiguration().getBoolean("Options.Updater");
		if (updaterEnabled == true) {
			Bukkit.getPluginManager().registerEvents(new Updater(), Bookshelf.plugin);
		}
	}
}

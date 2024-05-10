/*
 * This file is part of Bookshelf.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.loohp.bookshelf;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.loohp.bookshelf.config.Config;
import com.loohp.bookshelf.debug.Debug;
import com.loohp.bookshelf.hooks.InteractionVisualizerAnimations;
import com.loohp.bookshelf.listeners.BookshelfEvents;
import com.loohp.bookshelf.listeners.CreativeEvents;
import com.loohp.bookshelf.listeners.DispenserEvents;
import com.loohp.bookshelf.listeners.EnchantingEvents;
import com.loohp.bookshelf.listeners.InventoryEvents;
import com.loohp.bookshelf.listeners.PistonEvents;
import com.loohp.bookshelf.listeners.hooks.ASkyBlockEvents;
import com.loohp.bookshelf.listeners.hooks.BentoBoxEvents;
import com.loohp.bookshelf.listeners.hooks.GriefDefenderEvents;
import com.loohp.bookshelf.listeners.hooks.GriefPreventionEvents;
import com.loohp.bookshelf.listeners.hooks.LWCEvents;
import com.loohp.bookshelf.listeners.hooks.LandEvents;
import com.loohp.bookshelf.listeners.hooks.PlotSquared4Events;
import com.loohp.bookshelf.listeners.hooks.PlotSquared5Events;
import com.loohp.bookshelf.listeners.hooks.PlotSquared6Events;
import com.loohp.bookshelf.listeners.hooks.PlotSquared7Events;
import com.loohp.bookshelf.listeners.hooks.RedProtectEvents;
import com.loohp.bookshelf.listeners.hooks.ResidenceEvents;
import com.loohp.bookshelf.listeners.hooks.SuperiorSkyblock2Events;
import com.loohp.bookshelf.listeners.hooks.TownyEvents;
import com.loohp.bookshelf.listeners.hooks.WorldGuardEvents;
import com.loohp.bookshelf.metrics.Charts;
import com.loohp.bookshelf.metrics.Metrics;
import com.loohp.bookshelf.objectholders.BookshelfViewType;
import com.loohp.bookshelf.objectholders.LWCRequestOpenData;
import com.loohp.bookshelf.objectholders.Scheduler;
import com.loohp.bookshelf.updater.Updater;
import com.loohp.bookshelf.utils.ColorUtils;
import com.loohp.bookshelf.utils.HopperUtils;
import com.loohp.bookshelf.utils.MCVersion;
import com.loohp.bookshelf.utils.legacy.LegacyConfigConverter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleyaml.configuration.file.YamlFile;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Bookshelf extends JavaPlugin {

    public static final int BSTATS_PLUGIN_ID = 6748;
    public static final String CONFIG_ID = "config";

    public static Bookshelf plugin = null;

    public static String exactMinecraftVersion;
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
    public static boolean landsHook = false;
    public static boolean plotSquaredHook = false;
    public static boolean griefDefenderHook = false;
    public static boolean interactionVisualizerHook = false;

    public static boolean enableHopperSupport = true;
    public static boolean enableDropperSupport = true;
    public static Scheduler.ScheduledTask hopperTask = null;
    public static Scheduler.ScheduledTask hopperMinecartTask = null;
    public static int hopperTicksPerTransfer = 8;
    public static int hopperAmount = 1;

    public static Map<Player, BlockFace> lastBlockFace = new ConcurrentHashMap<>();

    public static Map<Player, LWCRequestOpenData> requestOpen = new ConcurrentHashMap<>();

    public static int bookShelfRows = 2;
    public static boolean useWhitelist = true;
    public static Set<String> whitelist = new HashSet<>();
    public static boolean bookshelfParticlesEnabled = true;
    public static int bookshelfParticlesFrequency = 5;
    public static Color bookshelfPrimaryColor = ColorUtils.hex2Rgb("#9933FF");
    public static Color bookshelfSecondaryColor = ColorUtils.hex2Rgb("#FFFF00");

    public static String noPermissionToReloadMessage = "&cYou do not have permission use this command!";
    public static String noPermissionToUpdateMessage = "&cYou do not have permission use this command!";

    public static Set<UUID> lwcCancelOpen = ConcurrentHashMap.newKeySet();
    public static Map<UUID, BookshelfViewType> isDonationView = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Player, Long> enchantSeed = new ConcurrentHashMap<>();

    public static long lastHopperTime = 0;
    public static long lastHoppercartTime = 0;

    public static boolean enchantmentTable = true;
    public static int eTableMulti = 1;
    public static int enchantingParticlesCount = 75;
    public static int bookshelfEnchantingParticlesFrequency = 5;
    public static Color boostingPrimaryColor = ColorUtils.hex2Rgb("#CC00CC");
    public static Color boostingSecondaryColor = ColorUtils.hex2Rgb("#3333FF");

    public static List<String> disabledWorlds = new ArrayList<>();

    public static Optional<Component> bookshelfDefaultName = Optional.empty();

    public static boolean updaterEnabled = true;
    public static Scheduler.ScheduledTask updaterTask = null;

    private static void hookMessage(String name) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Bookshelf] Hooked into " + name + "!");
    }

    public static YamlFile getConfiguration() {
        return Config.getConfig(CONFIG_ID).getConfiguration();
    }

    public static boolean isPluginEnabled(String name) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        return plugin != null && plugin.isEnabled();
    }

    public static void loadConfig() {
        Config config = Config.getConfig(CONFIG_ID);
        config.reload();

        bookShelfRows = getConfiguration().getInt("Options.BookShelfRows");
        useWhitelist = getConfiguration().getBoolean("Options.UseWhitelist");
        whitelist = new HashSet<>(getConfiguration().getStringList("Options.Whitelist"));
        noPermissionToReloadMessage = getConfiguration().getString("Options.NoPermissionToReloadMessage");
        noPermissionToUpdateMessage = getConfiguration().getString("Options.NoPermissionToUpdateMessage");
        bookshelfParticlesEnabled = getConfiguration().getBoolean("Options.ParticlesWhenOpened");
        bookshelfParticlesFrequency = getConfiguration().getInt("Options.OpenedParticleColors.Frequency");
        bookshelfPrimaryColor = ColorUtils.hex2Rgb(getConfiguration().getString("Options.OpenedParticleColors.Primary"));
        bookshelfSecondaryColor = ColorUtils.hex2Rgb(getConfiguration().getString("Options.OpenedParticleColors.Secondary"));

        enableHopperSupport = getConfiguration().getBoolean("Options.EnableHopperSupport");
        enableDropperSupport = getConfiguration().getBoolean("Options.EnableDropperSupport");
        enchantmentTable = getConfiguration().getBoolean("Options.EnableEnchantmentTableBoosting");
        int eTableChance = getConfiguration().getInt("Options.EnchantmentTableBoostingMaxPercentage");
        if (eTableChance > 100) {
            eTableChance = 100;
        } else if (eTableChance < 0) {
            eTableChance = 0;
        }
        eTableMulti = eTableChance == 0 ? 0 : (int) Math.pow(((double) eTableChance / 100.0), -1);
        enchantingParticlesCount = getConfiguration().getInt("Options.EnchantingParticlesCount");
        bookshelfEnchantingParticlesFrequency = getConfiguration().getInt("Options.BoostingParticleColors.Frequency");
        boostingPrimaryColor = ColorUtils.hex2Rgb(getConfiguration().getString("Options.BoostingParticleColors.Primary"));
        boostingSecondaryColor = ColorUtils.hex2Rgb(getConfiguration().getString("Options.BoostingParticleColors.Secondary"));

        lastHopperTime = 0;
        lastHoppercartTime = 0;
        if (hopperTask != null) {
            hopperTask.cancel();
        }
        if (hopperMinecartTask != null) {
            hopperMinecartTask.cancel();
        }
        if (enableHopperSupport) {
            hopperTicksPerTransfer = Bukkit.spigot().getConfig().getInt("world-settings.default.ticks-per.hopper-transfer");
            hopperAmount = Bukkit.spigot().getConfig().getInt("world-settings.default.hopper-amount");
            HopperUtils.hopperCheck();
            HopperUtils.hopperMinecartCheck();
        }

        disabledWorlds = getConfiguration().getStringList("Options.DisabledWorlds");

        bookshelfDefaultName = getConfiguration().contains("Options.BookshelfDefaultName") ? Optional.of(LegacyComponentSerializer.legacySection().deserialize(ChatColor.translateAlternateColorCodes('&', getConfiguration().getString("Options.BookshelfDefaultName")))) : Optional.empty();

        if (updaterTask != null) {
            updaterTask.cancel();
        }
        updaterEnabled = getConfiguration().getBoolean("Options.Updater");
        if (updaterEnabled) {
            Bukkit.getPluginManager().registerEvents(new Updater(), Bookshelf.plugin);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onEnable() {
        plugin = this;

        getServer().getPluginManager().registerEvents(new Debug(), this);

        Metrics metrics = new Metrics(this, BSTATS_PLUGIN_ID);

        exactMinecraftVersion = Bukkit.getVersion().substring(Bukkit.getVersion().indexOf("(") + 5, Bukkit.getVersion().indexOf(")"));
        version = MCVersion.resolve();

        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("Bookshelf World Processing Thread #%d").build();
        BookshelfManager.setAsyncExecutor(Executors.newFixedThreadPool(8, factory));

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
        getServer().getPluginManager().registerEvents(new InventoryEvents(), this);

        getCommand("bookshelf").setExecutor(new Commands());

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        try {
            Config.loadConfig(CONFIG_ID, new File(getDataFolder(), "config.yml"), getClass().getClassLoader().getResourceAsStream("config.yml"), getClass().getClassLoader().getResourceAsStream("config.yml"), true);
        } catch (IOException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        String SuperVanish = "SuperVanish";
        String PremiumVanish = "PremiumVanish";
        if (isPluginEnabled(SuperVanish) || isPluginEnabled(PremiumVanish)) {
            hookMessage(SuperVanish + "/" + PremiumVanish);
            vanishHook = true;
        }

        String CMI = "CMI";
        if (isPluginEnabled(CMI)) {
            hookMessage(CMI);
            cmiHook = true;
        }

        String Essentials = "Essentials";
        if (isPluginEnabled(Essentials)) {
            hookMessage(Essentials);
            essentialsHook = true;
        }

        String OpenInv = "OpenInv";
        if (isPluginEnabled(OpenInv)) {
            hookMessage(OpenInv);
            openInvHook = true;
        }

        String GriefPrevention = "GriefPrevention";
        if (isPluginEnabled("GriefPrevention")) {
            hookMessage(GriefPrevention);
            getServer().getPluginManager().registerEvents(new GriefPreventionEvents(), this);
            griefPreventionHook = true;
        }

        String LWC = "LWC";
        if (isPluginEnabled(LWC)) {
            hookMessage(LWC);
            new LWCEvents().registerLWCModule();
            lwcHook = true;
        }

        String BlockLocker = "BlockLocker";
        if (isPluginEnabled(BlockLocker)) {
            hookMessage(BlockLocker);
            blockLockerHook = true;
        }

        String WorldGuard = "WorldGuard";
        if (isPluginEnabled(WorldGuard)) {
            hookMessage(WorldGuard);
            getServer().getPluginManager().registerEvents(new WorldGuardEvents(), this);
            worldGuardHook = true;
        }

        String RedProtect = "RedProtect";
        if (isPluginEnabled(RedProtect)) {
            hookMessage(RedProtect);
            getServer().getPluginManager().registerEvents(new RedProtectEvents(), this);
            redProtectHook = true;
        }

        String BentoBox = "BentoBox";
        if (isPluginEnabled(BentoBox)) {
            hookMessage(BentoBox);
            getServer().getPluginManager().registerEvents(new BentoBoxEvents(), this);
            bentoBoxHook = true;
        }

        String ASkyBlock = "ASkyBlock";
        if (isPluginEnabled(ASkyBlock)) {
            hookMessage(ASkyBlock);
            getServer().getPluginManager().registerEvents(new ASkyBlockEvents(), this);
            aSkyBlockHook = true;
        }

        String Residence = "Residence";
        if (isPluginEnabled(Residence)) {
            hookMessage(Residence);
            getServer().getPluginManager().registerEvents(new ResidenceEvents(), this);
            residenceHook = true;
        }

        String Towny = "Towny";
        if (isPluginEnabled(Towny)) {
            hookMessage(Towny);
            getServer().getPluginManager().registerEvents(new TownyEvents(), this);
            townyHook = true;
        }

        String SuperiorSkyblock2 = "SuperiorSkyblock2";
        if (isPluginEnabled(SuperiorSkyblock2)) {
            hookMessage(SuperiorSkyblock2);
            getServer().getPluginManager().registerEvents(new SuperiorSkyblock2Events(), this);
            superiorSkyblock2Hook = true;
        }

        String Lands = "Lands";
        if (isPluginEnabled(Lands)) {
            hookMessage(Lands);
            getServer().getPluginManager().registerEvents(new LandEvents(), this);
            landsHook = true;
        }

        String PlotSquared = "PlotSquared";
        if (isPluginEnabled(PlotSquared)) {
            String plotSquaredVersion = getServer().getPluginManager().getPlugin(PlotSquared).getDescription().getVersion();
            if (plotSquaredVersion.startsWith("7.")) {
                hookMessage(PlotSquared + " (v7)");
                getServer().getPluginManager().registerEvents(new PlotSquared7Events(), this);
                plotSquaredHook = true;
            } else if (plotSquaredVersion.startsWith("6.")) {
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
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Bookshelf] This version of PlotSquared is not supported, only v4, v5, v6 and v7 are supported so far.");
            }
        }

        String GriefDefender = "GriefDefender";
        if (isPluginEnabled(GriefDefender)) {
            hookMessage(GriefDefender);
            getServer().getPluginManager().registerEvents(new GriefDefenderEvents(), this);
            griefDefenderHook = true;
        }

        String InteractionVisualizer = "InteractionVisualizer";
        if (isPluginEnabled(InteractionVisualizer)) {
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
            if (!disabledWorlds.contains(world.getName())) {
                BookshelfManager manager = BookshelfManager.loadWorld(this, world);
                if (legacyData.exists()) {
                    LegacyConfigConverter.mergeLegacy(legacyData, manager);
                }
            }
        }

        Charts.loadCharts(metrics);

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Bookshelf] BookShelf has been Enabled!");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Saving bookshelves..");
        long start = System.currentTimeMillis();
        for (World world : new ArrayList<>(BookshelfManager.getWorlds())) {
            BookshelfManager.getBookshelfManager(world).close();
        }
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Bookshelves saved! (" + (System.currentTimeMillis() - start) + "ms)");
        ExecutorService asyncExecutor = BookshelfManager.getAsyncExecutor();
        if (asyncExecutor != null) {
            asyncExecutor.shutdown();
        }
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Bookshelf] BookShelf has been Disabled!");
    }

}

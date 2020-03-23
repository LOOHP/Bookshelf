package com.loohp.bookshelf.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loohp.bookshelf.Bookshelf;

import net.md_5.bungee.api.ChatColor;

public class LegacyConfigConverter {
	
	@SuppressWarnings("unchecked")
	public static void convert() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Legacy v1.0.0 data format detected!");
		Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Converting bookshelf data to v2.0.0 JSON format!");
		JSONObject json = new JSONObject();
		Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Loading legacy bookshelf data from config.yml..");
		Map<String, Object> map = Bookshelf.plugin.getConfig().getConfigurationSection("BookShelfData").getValues(false);
		for (Entry<String, Object> entry : map.entrySet()) {
			JSONObject value = new JSONObject();
			if (Bookshelf.plugin.getConfig().getString("BookShelfData." + entry.getKey() + ".Title") != null) {
				value.put("Title", Bookshelf.plugin.getConfig().getString("BookShelfData." + entry.getKey() + ".Title"));
			}
			if (Bookshelf.plugin.getConfig().getString("BookShelfData." + entry.getKey() + ".Inventory") != null) {
				value.put("Inventory", Bookshelf.plugin.getConfig().getString("BookShelfData." + entry.getKey() + ".Inventory"));
			}
			json.put(entry.getKey(), value);
		}
		Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Writing converted data to bookshelfdata.json..");
		try {
        	JSONObject toSave = json;
        
        	TreeMap<String, Object> treeMap = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
        	treeMap.putAll(toSave);
        	
        	Gson g = new GsonBuilder().setPrettyPrinting().create();
            String prettyJsonString = g.toJson(treeMap);
            
            File file = new File(Bookshelf.plugin.getDataFolder().getAbsolutePath() + "/bookshelfdata.json");
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            writer.write(prettyJsonString);
            writer.flush();
            writer.close();
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
		Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Creating backup of legacy bookshelf data..");
		File backupfile = new File(Bookshelf.plugin.getDataFolder().getAbsolutePath() + "/legacybackupdata.yml");
		try {
			InputStream in = new FileInputStream(new File(Bookshelf.plugin.getDataFolder().getAbsolutePath() + "/config.yml"));
	        Files.copy(in, backupfile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Cleaning up config.yml to new formatting..");
		Bookshelf.plugin.getConfig().set("BookShelfData", null);
		Bookshelf.plugin.saveConfig();
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Bookshelf] Upgrade to v2.0.0 done!");
	}
}

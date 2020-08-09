package com.loohp.bookshelf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.md_5.bungee.api.ChatColor;

public class BookshelfManager {

	private static File file;
    private static JSONObject json;
    private static JSONParser parser = new JSONParser();
    private static File BackupFolder = new File(Bookshelf.plugin.getDataFolder().getPath() + "/Backup", "bookshelf");
    
    public static void intervalSaveToFile() {
    	Bukkit.getScheduler().runTaskTimerAsynchronously(Bookshelf.plugin, () -> {
    		BookshelfManager.save();
    	}, 200, 600);
    }

    public synchronized static void reload() {
    	if (!Bookshelf.plugin.getDataFolder().exists()) {
    		Bookshelf.plugin.getDataFolder().mkdir();
		}
		file = new File(Bookshelf.plugin.getDataFolder().getAbsolutePath() + "/bookshelfdata.json");
    	if (!file.exists()) {
			try (PrintWriter pw = new PrintWriter(file, "UTF-8")) {
				pw.print("{");
	    	    pw.print("}");
	    	    pw.flush();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
    	} else {
    		String fileName = new SimpleDateFormat("yyyy'-'MM'-'dd'_'HH'-'mm'-'ss'_'zzz'_bookshelfdata.json'").format(new Date());
    		BackupFolder.mkdirs();
            File outputfile = new File(BackupFolder, fileName);
            try (InputStream in = new FileInputStream(file)) {
                Files.copy(in, outputfile.toPath());
            } catch (IOException e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Bookshelf] Failed to make backup for bookshelfdata.json");
            }
    	}
    	if (BackupFolder.exists()) {
    		for (File file : BackupFolder.listFiles()) {
    			try {
        			String fileName = file.getName();
        			if (fileName.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}_[0-9]{2}-[0-9]{2}-[0-9]{2}_.*_bookshelfdata\\.json$")) {
        				Date timestamp = new SimpleDateFormat("yyyy'-'MM'-'dd'_'HH'-'mm'-'ss'_'zzz'_bookshelfdata.json'").parse(fileName);
        				if ((System.currentTimeMillis() - timestamp.getTime()) > 2592000000L) {
							Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Removing Backup/Backup/" + fileName + " as it is from 30 days ago.");
							file.delete();						
						}
        			}
    			} catch (Exception ignore) {}
    		}
    	}
    	try {
			json = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
    }
    
    @SuppressWarnings("unchecked")
	public synchronized static boolean save() {
        try {
        	JSONObject toSave = json;
        
        	TreeMap<String, Object> treeMap = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
        	treeMap.putAll(toSave);
        	
        	Gson g = new GsonBuilder().setPrettyPrinting().create();
            String prettyJsonString = g.toJson(treeMap);
            
            PrintWriter clear = new PrintWriter(file);
            clear.print("");
            clear.close();
            
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            writer.write(prettyJsonString);
            writer.flush();
            writer.close();

            return true;
        } catch (Exception ex) {
        	ex.printStackTrace();
        	return false;
        }
    }
    
    public static JSONObject getJsonObject() {
    	return json;
    }
   
    public static String getTitle(String key) {   
    	if (!json.containsKey(key)) {
    		return null;
    	}
    	JSONObject value = (JSONObject) json.get(key);
    	if (value.containsKey("Title")) {
    		return (String) value.get("Title");
    	}
    	return null;
    }
    
    public static String getInventoryHash(String key) { 
    	if (!json.containsKey(key)) {
    		return null;
    	}
    	JSONObject value = (JSONObject) json.get(key);
    	if (value.containsKey("Inventory")) {
    		return (String) value.get("Inventory");
    	}
    	return null;
    }
    
    @SuppressWarnings("unchecked")
	public static void setTitle(String key, String title) {
    	if (!json.containsKey(key)) {
    		json.put(key, new JSONObject());
    	}
    	JSONObject value = (JSONObject) json.get(key);
    	value.put("Title", title);
    }
    
    @SuppressWarnings("unchecked")
	public static void setInventoryHash(String key, String hash) {   	
    	if (!json.containsKey(key)) {
    		json.put(key, new JSONObject());
    	}
    	JSONObject value = (JSONObject) json.get(key);
    	value.put("Inventory", hash);
    }
    
    public static void removeShelf(String key) {   	
    	json.remove(key);
    }
    
    public static boolean contains(String key) {   	
    	if (json.containsKey(key)) {
    		return true;
    	}
    	return false;
    }
}

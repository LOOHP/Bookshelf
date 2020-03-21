package com.loohp.bookshelf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BookshelfManager {

	private static File file;
    private static JSONObject json;
    private static JSONParser parser = new JSONParser();
    
    public static void intervalSaveToFile() {
    	new BukkitRunnable() {
    		public void run() {
    			BookshelfManager.save();
    		}
    	}.runTaskTimerAsynchronously(Bookshelf.plugin, 200, 600);
    }

    public static void reload() {
        try {
        	if (!Bookshelf.plugin.getDataFolder().exists()) {
        		Bookshelf.plugin.getDataFolder().mkdir();
    		}
    		file = new File(Bookshelf.plugin.getDataFolder().getAbsolutePath() + "/bookshelfdata.json");
        	if (!file.exists()) {
        	    PrintWriter pw = new PrintWriter(file, "UTF-8");
        	    pw.print("{");
        	    pw.print("}");
        	    pw.flush();
        	    pw.close();
        	}
        	json = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
	public static boolean save() {
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

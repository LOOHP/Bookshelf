package com.loohp.bookshelf.utils.legacy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loohp.bookshelf.BookshelfManager;
import com.loohp.bookshelf.objectholders.BlockPosition;
import com.loohp.bookshelf.objectholders.BookshelfHolder;
import com.loohp.bookshelf.objectholders.ChunkPosition;
import com.loohp.bookshelf.utils.BookshelfUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LegacyConfigConverter {

    @SuppressWarnings("unchecked")
    @Deprecated
    public static void mergeLegacy(File file, BookshelfManager manager) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Legacy v2.0.0 data format detected!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Converting bookshelf data to format!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] Loading legacy bookshelf data..");

        Map<ChunkPosition, Map<BlockPosition, BookshelfHolder>> bookshelves = new HashMap<>();
        World world = manager.getWorld();

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            JSONObject json = (JSONObject) new JSONParser().parse(reader);
            Iterator<Object> itr = json.keySet().iterator();
            while (itr.hasNext()) {
                Object obj = itr.next();
                try {
                    String key = obj.toString();
                    if (!key.startsWith(world.getName())) {
                        continue;
                    }
                    JSONObject entry = (JSONObject) json.get(key);
                    key = key.replace(world.getName() + "_", "");
                    BlockPosition position = BookshelfUtils.keyPos(world, key);
                    Map<BlockPosition, BookshelfHolder> chunkEntry = bookshelves.get(position.getChunkPosition());
                    if (chunkEntry == null) {
                        chunkEntry = new ConcurrentHashMap<>();
                        bookshelves.put(position.getChunkPosition(), chunkEntry);
                    }
                    String title = entry.get("Title").toString();
                    BookshelfHolder bookshelf = new BookshelfHolder(position, title, null);
                    Inventory inventory = BookshelfUtils.fromBase64(entry.get("Inventory").toString(), title, bookshelf);
                    bookshelf.setInventory(inventory);
                    chunkEntry.put(position, bookshelf);
                    itr.remove();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            reader.close();

            if (json.isEmpty()) {
                file.delete();
            } else {
                Gson g = new GsonBuilder().setPrettyPrinting().create();
                String prettyJsonString = g.toJson(json);

                try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                    pw.println(prettyJsonString);
                    pw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (!bookshelves.isEmpty()) {
            try {
                Field field = manager.getClass().getDeclaredField("loadedBookshelves");
                field.setAccessible(true);
                ((Map<ChunkPosition, Map<BlockPosition, BookshelfHolder>>) field.get(manager)).putAll(bookshelves);
                field.setAccessible(false);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Bookshelf] Upgraded to v3.0.0 done!");
    }
}

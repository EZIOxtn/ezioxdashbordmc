package com.ezioxtndashboardmc.dashbordmc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.entity.EntityType;
import org.bukkit.Bukkit;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillManager {

    private final ExamplePlugin plugin;
    private final File file;
    private final Gson gson = new Gson();

    // Map<PlayerUUID, Map<EntityTypeName, kills>>
    private Map<UUID, Map<String, Integer>> killData = new HashMap<>();

    public KillManager(ExamplePlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "kills.json");
    }

    public void loadData() {
        if (!file.exists()) {
            plugin.getLogger().info("No kill data found, creating new file...");
            saveData();
            return;
        }

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<UUID, Map<String, Integer>>>() {}.getType();
            killData = gson.fromJson(reader, type);

            if (killData == null) killData = new HashMap<>();

            plugin.getLogger().info("Kill data loaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        try (Writer writer = new FileWriter(file, false)) {
            gson.toJson(killData, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addKill(UUID playerId, EntityType type) {
        killData.putIfAbsent(playerId, new HashMap<>());
        Map<String, Integer> inner = killData.get(playerId);

        String key = type.name();
        inner.put(key, inner.getOrDefault(key, 0) + 1);
    }

    public Map<String, Integer> getKills(UUID playerId) {
        return killData.getOrDefault(Bukkit.getPlayer(playerId).getName(), new HashMap<>());
    }
}

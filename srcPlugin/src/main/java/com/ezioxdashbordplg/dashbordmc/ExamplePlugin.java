package com.ezioxdashbordmc.dashbordmc;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.OfflinePlayer; 
import org.bukkit.entity.Player; 
import org.bukkit.event.EventHandler; 
import org.bukkit.event.Listener; 
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.command.CommandSender;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

public class ExamplePlugin extends JavaPlugin implements Listener {
    // build cmd , .\gradlew.bat build -x checkstyleMain -x spotbugsMain
    private KillManager killManager;
     private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Override
    public void onEnable() {
        PaperLib.suggestPaper(this);
        saveDefaultConfig();

        killManager = new KillManager(this);
        killManager.loadData();

        getServer().getPluginManager().registerEvents(new KillListener(killManager), this);
        Bukkit.getPluginManager().registerEvents(this, this);
        GetDataCommand getDataCommand = new GetDataCommand(this, killManager);
        getServer().getPluginManager().registerEvents(getDataCommand, this);
        if (getCommand("getdata") != null) {
            getCommand("getdata").setExecutor(getDataCommand);
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(
                this,
                 () -> {
                    
                    
                     if (killManager != null) {
                        killManager.saveData();
                        }
                        getLogger().info("Kill tracking system saved.");
                        saveOnlinePlayers();
                        },
                        
                0L,
                200L
        );
        Bukkit.getScheduler().runTaskTimer(
                this,
                new PlayerDataAutoSave(this, getDataCommand),
                170L,
                200L
        );
        
        
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
      
        if (!player.hasPlayedBefore()) {
            
            getLogger().info("Detected NEW Player: " + player.getName() + ". Running new player command.");
            
          
            handleNewPlayer(player);
        }
    }

private void saveOnlinePlayers() {
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);

        Map<String, Object> onlineData = new HashMap<>();
        for (Player player : players) {
            Map<String, Object> pdata = new HashMap<>();
            pdata.put("uuid", player.getUniqueId().toString());
            pdata.put("name", player.getName());
            onlineData.put(player.getName(), pdata);
        }

        File file = new File(getDataFolder(), "online.json");
        file.getParentFile().mkdirs(); 

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(onlineData, writer);
        } catch (IOException e) {
            getLogger().severe("Failed to save online players: " + e.getMessage());
        }
    }
private void handleNewPlayer(Player player) {

    String name = player.getName();
    CommandSender sender = player.getServer().getConsoleSender();

    
    String[] starterCommands = {
        "give " + name + " cooked_beef 16",
        "give " + name + " stone_axe 1",
        "give " + name + " stone_pickaxe 1",
        "give " + name + " leather_helmet 1",
        "give " + name + " leather_chestplate 1",
        "give " + name + " leather_leggings 1",
        "give " + name + " leather_boots 1"
    };

    // Skin command (Novaskin direct URL)
    String skinCommand = "skin set https://s.namemc.com/i/cc7fe9c6cac4097f.png " ;

    Bukkit.getScheduler().runTask(this, () -> {

        
        for (String cmd : starterCommands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }

        // Apply skin
        Bukkit.dispatchCommand(player, skinCommand);

        // Optional: teleport to spawn for first time
        // Bukkit.dispatchCommand(player, "spawn");
    });
    
    sender.sendMessage("§6Welcome " + name + "! Starter items and skin have been applied.");

}


    @Override
    public void onDisable() {
        if (killManager != null) {
            killManager.saveData();
        }
        getLogger().info("Kill tracking system saved.");
    }

    public KillManager getKillManager() {
        return killManager;
    }
}

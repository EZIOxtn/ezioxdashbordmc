package com.ezioxdashbordmc.dashbordmc;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.OfflinePlayer; // Added for isNewPlayer method signature
import org.bukkit.entity.Player; // Added for onPlayerJoin method signature
import org.bukkit.event.EventHandler; // Added for the event listener
import org.bukkit.event.Listener; // Added to implement Listener
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.command.CommandSender;

public class ExamplePlugin extends JavaPlugin implements Listener {
    // build cmd , .\gradlew.bat build -x checkstyleMain -x spotbugsMain
    private KillManager killManager;

    @Override
    public void onEnable() {
        PaperLib.suggestPaper(this);
        saveDefaultConfig();

        killManager = new KillManager(this);
        killManager.loadData();

        getServer().getPluginManager().registerEvents(new KillListener(killManager), this);
        Bukkit.getPluginManager().registerEvents(this, this);
        GetDataCommand getDataCommand = new GetDataCommand(this, killManager);
        if (getCommand("getdata") != null) {
            getCommand("getdata").setExecutor(getDataCommand);
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(
                this,
                 () -> { if (killManager != null) {
                        killManager.saveData();
                        }
                        getLogger().info("Kill tracking system saved.");
                        },
                0L,
                200L
        );
        Bukkit.getScheduler().runTaskTimerAsynchronously(
                this,
                new PlayerDataAutoSave(this, getDataCommand),
                0L,
                200L
        );
        getLogger().info("Kill tracking system enabled!");
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
      
        if (!player.hasPlayedBefore()) {
            
            getLogger().info("Detected NEW Player: " + player.getName() + ". Running new player command.");
            
            // Execute the command
            handleNewPlayer(player);
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
    String skinCommand = "skin set https://s.namemc.com/i/cc7fe9c6cac4097f.png "  + name ;

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

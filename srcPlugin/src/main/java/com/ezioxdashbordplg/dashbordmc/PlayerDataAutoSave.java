package com.ezioxtndashboardmc.dashbordmc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PlayerDataAutoSave implements Runnable {

    private final ExamplePlugin plugin;
    private final GetDataCommand getDataCommand;

    public PlayerDataAutoSave(ExamplePlugin plugin, GetDataCommand getDataCommand) {
        this.plugin = plugin;
        this.getDataCommand = getDataCommand;
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            try {
                File folder = new File(plugin.getDataFolder(), "playerdata");
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                File file = new File(folder, p.getName() + ".json");
                String json = getDataCommand.generatePlayerJson(p);

                try (FileWriter writer = new FileWriter(file, false)) {
                    writer.write(json);
                }

            } catch (IOException e) {
                plugin.getLogger().severe("Failed to save player data for " + p.getName() + ": " + e.getMessage());
            }
        }
    }
}

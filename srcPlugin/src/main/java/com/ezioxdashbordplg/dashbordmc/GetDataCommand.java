package com.ezioxdashbordmc.dashbordmc;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class GetDataCommand implements CommandExecutor {
    private final ExamplePlugin plugin;
    private final KillManager killManager;
    private final Logger logger;

    public GetDataCommand(ExamplePlugin plugin, KillManager killManager) {
        this.plugin = plugin;
        this.killManager = killManager;
        this.logger = plugin.getLogger();
    }

    public String getPlayerSkinBase64(Player player) {
        return PlaceholderAPI.setPlaceholders(player, "%skinsrestorer_texture_url%");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage: /getdata <player>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage("Player not found or offline: " + args[0]);
            return true;
        }

        String jsonOutput = generatePlayerJson(target);
        saveJsonToFile(jsonOutput, target.getName());
        sender.sendMessage("§6Player data (JSON): " + jsonOutput);
        return true;
    }

    public String generatePlayerJson(Player target) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"player\":\"").append(escape(target.getName())).append("\",");
        json.append("\"uuid\":\"").append(target.getUniqueId()).append("\",");

        // Skin
        json.append("\"skin\":{");
        try {
            PlayerProfile profile = target.getPlayerProfile();
            if (profile != null) {
                ProfileProperty skinProp =
                        profile.getProperties().stream()
                                .filter(p -> "textures".equals(p.getName()))
                                .findFirst()
                                .orElse(null);
                if (skinProp != null) {
                    json.append("\"textures_base64\":\"")
                            .append(escape(getPlayerSkinBase64(target)))
                            .append("\",");
                    json.append("\"signature\":\"")
                            .append(escape(skinProp.getSignature() != null ? skinProp.getSignature() : ""))
                            .append("\"");
                } else {
                    json.append("\"error\":\"No skin data found\"");
                }
            } else {
                json.append("\"error\":\"Could not fetch player profile\"");
            }
        } catch (Throwable ignored) {
            json.append("\"error\":\"Could not fetch skin data\"");
        }
        json.append("},");

        // Health
        json.append("\"health\":{");
        try {
            json.append("\"current\":").append(target.getHealth()).append(",");
            json.append("\"max\":").append(target.getMaxHealth());
        } catch (Throwable ignored) {
            json.append("\"error\":\"Could not fetch health data\"");
        }
        json.append("},");

        // Hunger
        json.append("\"hunger\":{");
        try {
            json.append("\"food_level\":").append(target.getFoodLevel()).append(",");
            json.append("\"saturation\":").append(target.getSaturation());
        } catch (Throwable ignored) {
            json.append("\"error\":\"Could not fetch hunger data\"");
        }
        json.append("},");

        // Experience
        json.append("\"experience\":{");
        try {
            json.append("\"level\":").append(target.getLevel()).append(",");
            json.append("\"exp\":").append(target.getExp()).append(",");
            json.append("\"total_experience\":").append(target.getTotalExperience());
        } catch (Throwable ignored) {
            json.append("\"error\":\"Could not fetch experience data\"");
        }
        json.append("},");

        // Inventory
        json.append("\"inventory\":[");
        ItemStack[] contents = target.getInventory().getContents();
        boolean firstItem = true;
        if (contents != null) {
            for (int i = 0; i < contents.length; i++) {
                ItemStack it = contents[i];
                if (it == null || it.getAmount() <= 0) continue;
                if (!firstItem) json.append(",");
                firstItem = false;
                json.append("{\"slot\":").append(i).append(",");
                json.append("\"type\":\"").append(it.getType().name()).append("\",");
                json.append("\"amount\":").append(it.getAmount());
                ItemMeta meta = it.getItemMeta();
                if (meta != null) {
                    if (meta.hasDisplayName()) {
                        json.append(",\"display_name\":\"").append(escape(meta.getDisplayName())).append("\"");
                    }
                    try {
                        if (meta instanceof org.bukkit.inventory.meta.Damageable) {
                            org.bukkit.inventory.meta.Damageable damageable = (org.bukkit.inventory.meta.Damageable) meta;
                            short maxDurability = it.getType().getMaxDurability();
                            if (maxDurability > 0) {
                                int currentDurability = maxDurability - damageable.getDamage();
                                json.append(",\"durability\":").append(currentDurability);
                                json.append(",\"max_durability\":").append(maxDurability);
                            }
                        }
                    } catch (Throwable ignored) {
                    }
                    if (meta.hasEnchants()) {
                        json.append(",\"enchantments\":[");
                        boolean firstEnchant = true;
                        for (java.util.Map.Entry<org.bukkit.enchantments.Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                            if (!firstEnchant) {
                                json.append(",");
                            }
                            firstEnchant = false;
                            json.append("{\"name\":\"").append(entry.getKey().getKey().getKey()).append("\",");
                            json.append("\"level\":").append(entry.getValue()).append("}");
                        }
                        json.append("]");
                    }
                }
                json.append("}");
            }
        }
        json.append("],");

        // Armor
        json.append("\"armor\":[");
        ItemStack[] armor = target.getInventory().getArmorContents();
        
        firstItem = true;
        if (armor != null) {
            for (int i = 0; i < armor.length; i++) {
                ItemStack it = armor[i];
                if (it == null || it.getAmount() <= 0) continue;
                if (!firstItem) json.append(",");
                firstItem = false;
                json.append("{\"slot\":").append(i).append(",");
                json.append("\"type\":\"").append(it.getType().name()).append("\",");
                json.append("\"amount\":").append(it.getAmount());
                ItemMeta meta = it.getItemMeta();
                if (meta != null) {
                    try {
                        if (meta instanceof org.bukkit.inventory.meta.Damageable) {
                            org.bukkit.inventory.meta.Damageable damageable = (org.bukkit.inventory.meta.Damageable) meta;
                            short maxDurability = it.getType().getMaxDurability();
                            if (maxDurability > 0) {
                                int currentDurability = maxDurability - damageable.getDamage();
                                json.append(",\"durability\":").append(currentDurability);
                                json.append(",\"max_durability\":").append(maxDurability);
                            }
                        }
                    } catch (Throwable ignored) {
                    }
                    if (meta.hasEnchants()) {
                        json.append(",\"enchantments\":[");
                        boolean firstEnchant = true;
                        for (java.util.Map.Entry<org.bukkit.enchantments.Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                            if (!firstEnchant) {
                                json.append(",");
                            }
                            firstEnchant = false;
                            json.append("{\"name\":\"").append(entry.getKey().getKey().getKey()).append("\",");
                            json.append("\"level\":").append(entry.getValue()).append("}");
                        }
                        json.append("]");
                    }
                }
                json.append("}");
            }
        }
        json.append("],");

        // Off-hand
        json.append("\"off_hand\":{");
        try {
            ItemStack offHand = target.getInventory().getItemInOffHand();
            if (offHand != null && offHand.getAmount() > 0) {
                json.append("\"type\":\"").append(offHand.getType().name()).append("\",");
                json.append("\"amount\":").append(offHand.getAmount());
                ItemMeta meta = offHand.getItemMeta();
                if (meta != null) {
                    if (meta.hasDisplayName()) {
                        json.append(",\"display_name\":\"").append(escape(meta.getDisplayName())).append("\"");
                    }
                    try {
                        if (meta instanceof org.bukkit.inventory.meta.Damageable) {
                            org.bukkit.inventory.meta.Damageable damageable = (org.bukkit.inventory.meta.Damageable) meta;
                            short maxDurability = offHand.getType().getMaxDurability();
                            if (maxDurability > 0) {
                                int currentDurability = maxDurability - damageable.getDamage();
                                json.append(",\"durability\":").append(currentDurability);
                                json.append(",\"max_durability\":").append(maxDurability);
                            }
                        }
                    } catch (Throwable ignored) {
                    }
                    if (meta.hasEnchants()) {
                        json.append(",\"enchantments\":[");
                        boolean firstEnchant = true;
                        for (java.util.Map.Entry<org.bukkit.enchantments.Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                            if (!firstEnchant) {
                                json.append(",");
                            }
                            firstEnchant = false;
                            json.append("{\"name\":\"").append(entry.getKey().getKey().getKey()).append("\",");
                            json.append("\"level\":").append(entry.getValue()).append("}");
                        }
                        json.append("]");
                    }
                }
            } else json.append("\"empty\":true");
        } catch (Throwable ignored) {
            json.append("\"error\":\"Could not fetch off-hand item\"");
        }
        json.append("},");

        // Statistics
        json.append("\"statistics\":{");
        try {
            int walk = target.getStatistic(Statistic.WALK_ONE_CM);
            int swim = target.getStatistic(Statistic.SWIM_ONE_CM);
            int fly = target.getStatistic(Statistic.FLY_ONE_CM);
            int fall = target.getStatistic(Statistic.FALL_ONE_CM);
            json.append("\"walk_cm\":").append(walk).append(",");
            json.append("\"swim_cm\":").append(swim).append(",");
            json.append("\"fly_cm\":").append(fly).append(",");
            json.append("\"fall_cm\":").append(fall).append(",");
            json.append("\"total_distance_cm\":").append(walk + swim + fly);
        } catch (Throwable ignored) {
            json.append("\"error\":\"Could not fetch statistics data\"");
        }
        json.append("},");

        // Advancements
        json.append("\"advancements\":{");
        try {
            json.append("\"note\":\"Use /advancement command for detailed achievement info\",");
            json.append("\"total_advancements\":\"Check server logs for full list\"");
        } catch (Throwable ignored) {
            json.append("\"error\":\"Could not fetch advancement data\"");
        }
        json.append("}");

        json.append("}");
        return json.toString();
    }

    private static String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private void saveJsonToFile(String json, String playerName) {
        try {
            Path path = Paths.get(plugin.getDataFolder() + "/playerdata/" + playerName + ".json");
            Files.createDirectories(path.getParent());
            Files.write(path, json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.severe("Failed to save JSON for " + playerName + ": " + e.getMessage());
        }
    }
}

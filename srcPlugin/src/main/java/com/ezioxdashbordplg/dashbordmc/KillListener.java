package com.ezioxtndashboardmc.dashbordmc;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillListener implements Listener {

    private final KillManager killManager;

    public KillListener(KillManager killManager) {
        this.killManager = killManager;
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) return;

        Player killer = event.getEntity().getKiller();
        
        EntityType type = event.getEntityType();

        killManager.addKill(killer.getUniqueId(), type);
    }
}

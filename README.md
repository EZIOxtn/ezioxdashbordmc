# DashboardMC Companion Plugin  
A Paper/Spigot plugin required for the **MC Dashboard** project.

This plugin provides:
- A **kill tracking system** (persistent storage, autosave, API).
- A **first-join detection system** that gives new players a starter kit.
- Automatic **skin assignment** from an external URL (NovaSkin/NameMC).
- A backend data interface used by the **DashboardMC** web dashboard.
- ⚠️ this plugin need "SkinRestorer.jar ", "PlaceholderApi.jar" to work properly 

This plugin is NOT intended to be used alone — it is the backend bridge that the **DashboardMC** panel communicates with.

---

## ✨ Features

### 🔫 Kill Tracking System
- Tracks player kills via a custom `KillManager`.
- Saves kill data periodically (async autosave every 200 ticks).
- `/getdata` command to retrieve kill statistics.
- Safe shutdown saving.

### 👤 New Player Detection
When a brand-new player joins the server:
- Logs detection in console.
- Gives the following starter kit:
  - 16× Cooked Beef  
  - Stone Axe  
  - Stone Pickaxe  
  - Leather Helmet  
  - Leather Chestplate  
  - Leather Leggings  
  - Leather Boots  


- Sends a welcome message through console.

### 🔄 Dashboard Integration
This plugin is designed to integrate with the **DashboardMC** web dashboard (your other repository).  
DashboardMC uses:
- The kill tracking API  
- The autosave system  
- `/getdata` command  
- Player event hooks  

to sync server data with the web panel.

**This plugin is REQUIRED for DashboardMC to function correctly.**

---

## 📦 Installation

1. Download or build the latest version of this plugin (`.jar`).
2. Place it inside your server’s `plugins/` folder.
3. Start the server.
4. The plugin will automatically create configuration files and load kill data.

---

## 🛠️ Building From Source

This plugin uses **Gradle**.  
To build:

```sh
.\gradlew.bat build -x checkstyleMain -x spotbugsMain
```

## 📁 File Structure

  KillManager → Handles tracking, saving, and loading kill data

  KillListener → Listens to kill events

  PlayerDataAutoSave → Periodic autosave (async)

   ExamplePlugin → Main plugin logic (join events, starter items, skin setting)

  

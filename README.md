# DashboardMC Companion Plugin

A **Paper/Spigot Minecraft plugin** required for the **DashboardMC** project.  
This plugin acts as the **backend bridge** between your Minecraft server and the DashboardMC web panel.

> ⚠️ This plugin is **NOT intended to be used alone**.

---

## 🚀 What This Plugin Provides

- 🔫 **Kill tracking system** (persistent storage, autosave, API)
- 👤 **First-join detection** with automatic starter kit
- 🎨 **Automatic skin assignment** from external services (NovaSkin / NameMC)
- 🌐 **Backend data interface** used by the DashboardMC web dashboard

---

## ⚠️ Required Dependencies

This plugin **will NOT work** without the following plugins installed:

- `SkinRestorer.jar`
- `PlaceholderAPI.jar`

Make sure both are placed in your server’s `plugins/` folder before starting the server.

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
---

### 🔄 Dashboard Integration
This plugin is designed specifically to integrate with the **DashboardMC web dashboard**.

The dashboard uses:
- Kill tracking API
- Autosave system
- `/getdata` command
- Player event hooks

📌 **This plugin is REQUIRED for DashboardMC to function correctly.**
[GitHub Repository](https://github.com/EZIOxtn/PaperMC-Dashboard)

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

  

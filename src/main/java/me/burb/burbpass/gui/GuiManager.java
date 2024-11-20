package me.burb.burbpass.gui;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiManager implements Listener {
    private final Plugin plugin;
    private final Map<UUID, SimpleGui> guiMap = new HashMap<>();

    public GuiManager(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void shutdown() {
        for (SimpleGui gui : guiMap.values()) gui.getPlayer().closeInventory();
        guiMap.clear();
        HandlerList.unregisterAll(this);
    }

    public void openGui(SimpleGui gui) {
        /*if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(plugin, () -> openGui(gui));
            return;
        }
        gui.getPlayer().closeInventory();*/
        guiMap.put(gui.getPlayer().getUniqueId(), gui);
        //gui.open();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        SimpleGui gui = guiMap.get(event.getWhoClicked().getUniqueId());
        if (gui == null) return;
        gui.handleClick(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        SimpleGui gui = guiMap.get(event.getPlayer().getUniqueId());
        if (gui == null) return;
        gui.handleClose(event);
        guiMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        SimpleGui gui = guiMap.get(event.getWhoClicked().getUniqueId());
        if (gui == null) return;
        gui.handleDrag(event);
    }
}

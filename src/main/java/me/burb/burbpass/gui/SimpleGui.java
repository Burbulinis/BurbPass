package me.burb.burbpass.gui;

import me.burb.burbpass.gui.util.SlotUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class SimpleGui {
    private final Map<Integer, Consumer<InventoryClickEvent>> handlerMap = new HashMap<>();
    protected final Player player;
    protected final Inventory inventory;

    protected SimpleGui(Player player, Inventory inventory) {
        this.player = player;
        this.inventory = inventory;
    }

    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!inventory.equals(event.getClickedInventory())) return;
        if (!handlerMap.containsKey(event.getSlot())) return;
        handlerMap.get(event.getSlot()).accept(event);
    }

    public void handleClose(InventoryCloseEvent event) {
    }

    public void handleDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    protected void registerHandler(int slot, Consumer<InventoryClickEvent> handler) {
        handlerMap.put(slot, handler);
    }

    protected void clearHandlers() {
        handlerMap.clear();
    }

    public void open() {
        render();
        player.openInventory(inventory);
    }

    public Player getPlayer() {
        return player;
    }

    protected abstract void render();

    protected void fillBorder(ItemStack item) {
        int size = inventory.getSize();
        for (int i = 0; i < size; i++) {
            if (SlotUtil.isBorderSlot(i, size)) {
                inventory.setItem(i, item.clone());
            }
        }
    }

    protected void fillInventory(ItemStack item) {
        int size = inventory.getSize();
        for (int i = 0; i < size; i++) inventory.setItem(i, item);
    }

    protected void fillRow(int row, ItemStack item) {
        int slot = row * 9;
        for (int i = slot; i < slot + 9; i++) inventory.setItem(i, item);
    }
}
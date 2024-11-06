package me.burb.burbpass.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Create custom GUIs
 * <br>Make sure to set the plugin (this is used for event listeners) see {@link #setPlugin(Plugin)}
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class GUI implements InventoryHolder, Listener {

    private static Plugin PLUGIN;

    /**
     * Set the plugin used for listening to events.
     *
     * @param plugin Instance of your plugin
     */
    public static void setPlugin(@NotNull Plugin plugin) {
        PLUGIN = plugin;
    }

    /**
     * Create a chest GUI builder
     *
     * @param rows  Number of rows for the chest (must be between 1 and 6)
     * @param title Title of your GUI
     * @return Instance of GUI builder
     */
    public static GUI chestBuilder(int rows, @Nullable net.kyori.adventure.text.TextComponent title) {
        return new GUI(rows, title);
    }

    /**
     * Create a custom GUI builder using any inventory type
     *
     * @param type  Type of GUI to create
     * @param title Title of your GUI
     * @return Instance of GUI builder
     */
    public static GUI customBuilder(InventoryType type, @Nullable net.kyori.adventure.text.TextComponent title) {
        return new GUI(type, title);
    }

    private final Inventory inventory;
    private final Map<Integer, Consumer<InventoryData>> buttons = new HashMap<>();

    private GUI(int rows, net.kyori.adventure.text.TextComponent title) {
        if (title == null) this.inventory = Bukkit.createInventory(this, rows * 9);
        else this.inventory = Bukkit.createInventory(this, rows * 9, title);
        Bukkit.getPluginManager().registerEvents(this, PLUGIN);
    }

    private GUI(InventoryType type, net.kyori.adventure.text.TextComponent title) {
        if (title == null) this.inventory = Bukkit.createInventory(this, type);
        else this.inventory = Bukkit.createInventory(this, type, title);
        Bukkit.getPluginManager().registerEvents(this, PLUGIN);
    }

    /**
     * Format a slot in the GUI to run a function
     *
     * @param slot      Slot to set
     * @param itemStack ItemStack to appear in slot
     * @param data      Consumer to run when clicked with data from event
     * @return Instance of GUI builder
     */
    public GUI formatSlot(int slot, @NotNull ItemStack itemStack, Consumer<InventoryData> data) {
        if (slot < 0 || slot > this.inventory.getSize())
            throw new IllegalArgumentException("Slot '" + slot + "' outside of GUI");
        this.inventory.setItem(slot, itemStack);
        this.buttons.put(slot, data);
        return this;
    }

    /**
     * Format multiple slots in the GUI to run a function
     *
     * @param slots     Slots to set
     * @param itemStack ItemStack to appear in slot
     * @param data      Consumer to run when clicked with data from event
     * @return Instance of GUI builder
     */
    public GUI formatSlots(int[] slots, @NotNull ItemStack itemStack, Consumer<InventoryData> data) {
        for (int slot : slots) {
            this.inventory.setItem(slot, itemStack);
            this.buttons.put(slot, data);
        }
        return this;
    }

    /**
     * Lock a slot
     *
     * @param slot      Slot to lock
     * @param itemStack ItemStack to appear in slot
     * @return Instance of GUI builder
     */
    public GUI lockSlot(int slot, @NotNull ItemStack itemStack) {
        this.inventory.setItem(slot, itemStack);
        return this;
    }

    /**
     * Fill the GUI with an item to run a function when clicked
     *
     * @param itemStack ItemStack to appear in all slots
     * @param data      Consumer to run when clicked with data from event
     * @return Instance of GUI builder
     */
    public GUI fill(@NotNull ItemStack itemStack, Consumer<InventoryData> data) {
        for (int i = 0; i < this.inventory.getSize(); i++) {
            this.inventory.setItem(i, itemStack);
            this.buttons.put(i, data);
        }
        return this;
    }

    /**
     * Fill the GUI with an item
     *
     * @param itemStack ItemStack to appear in all slots
     * @return Instance of GUI builder
     */
    public GUI fill(@NotNull ItemStack itemStack) {
        for (int i = 0; i < this.inventory.getSize(); i++) {
            this.inventory.setItem(i, itemStack);
        }
        return this;
    }

    /**
     * Get the inventory from this GUI
     *
     * @return Inventory from this GUI
     */
    @NotNull
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Open this GUI to a player
     *
     * @param player Player to open GUI for
     * @return Instance of GUI builder
     */
    public GUI open(Player player) {
        player.openInventory(this.inventory);
        return this;
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getHolder() != this) return;
        event.setCancelled(true);

        int clickedSlot = event.getSlot();
        if (this.buttons.containsKey(clickedSlot)) {
            Consumer<InventoryData> consumer = this.buttons.get(clickedSlot);
            consumer.accept(new InventoryData(event));
        }
    }

    /**
         * Data involved when clicking a GUI slot
         */
        public record InventoryData(InventoryClickEvent event) {

        /**
             * Get the click event
             *
             * @return Click event
             */
            @Override
            @NotNull
            public InventoryClickEvent event() {
                return event;
            }

            /**
             * Get the player who clicked
             *
             * @return Player who clicked
             */
            @NotNull
            public Player getPlayer() {
                return ((Player) event.getWhoClicked());
            }

            /**
             * Get the slot which was clicked
             *
             * @return Slot which was clicked
             */
            public int getSlot() {
                return event.getSlot();
            }

            /**
             * Get the item which was clicked on
             *
             * @return Item which was clicked on
             */
            @Nullable
            public ItemStack getClickedItem() {
                return event.getCurrentItem();
            }

            /**
             * Get the inventory action of the click
             *
             * @return Inventory action of the click
             */
            @NotNull
            public InventoryAction getAction() {
                return event.getAction();
            }

            /**
             * Get the type of slot that was clicked
             *
             * @return Type of slot
             */
            @NotNull
            public InventoryType.SlotType getSlotType() {
                return event.getSlotType();
            }

            public ClickType getClickType() {
                return event.getClick();
            }
        }

}
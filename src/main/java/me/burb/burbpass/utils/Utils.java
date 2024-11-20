package me.burb.burbpass.utils;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import me.burb.burbpass.gui.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ListIterator;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final String PREFIX = "&7[&bBURB&3PASS&7] ";
    private static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f\\d]){6}>");
    public static String format(float f) {
        return String.format("%%,.0f", f);
    }

    @SuppressWarnings("deprecation") // Paper deprecation
    public static String getColString(String string) {
        Matcher matcher = HEX_PATTERN.matcher(string);
        while (matcher.find()) {
            final net.md_5.bungee.api.ChatColor hexColor = net.md_5.bungee.api.ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
            final String before = string.substring(0, matcher.start());
            final String after = string.substring(matcher.end());
            string = before + hexColor + after;
            matcher = HEX_PATTERN.matcher(string);
        }
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', string);
    }

    public static ItemStack getHead(String value, String name) {
        ItemStack item = new ItemBuilder(Material.PLAYER_HEAD). name(Component.text(name)).build();
        NBT.modify(item, nbt -> {
            ReadWriteNBT skullOwnerCompound = nbt.getOrCreateCompound("SkullOwner");

            skullOwnerCompound.setUUID("Id", UUID.randomUUID());
            skullOwnerCompound.getOrCreateCompound("Properties")
                    .getCompoundList("textures")
                    .addCompound()
                    .setString("Value", value);
        });
        return item;
    }

    public static boolean hasEnoughSpace(Player player, ItemStack item) {
        PlayerInventory inv = player.getInventory();
        if (inv.firstEmpty() != -1) return true;

        ListIterator<ItemStack> iterator = inv.iterator(0);
        int amount = 0;
        while (iterator.hasNext()) {
            ItemStack next = iterator.next();
            if (next.isSimilar(item)) {
                amount += (next.getMaxStackSize() - next.getAmount());
            }
        }
        return amount >= item.getAmount();
    }

    public static ItemStack getHeadWithLevel(ItemStack item, int level, boolean editor) {
        if (!editor) return item;
        ItemStack clone = item.clone();
        ItemMeta meta = clone.getItemMeta();
        meta.displayName(Component.text()
                .append(Component.text("Level ", NamedTextColor.GRAY))
                .append(Component.text(level, NamedTextColor.GRAY))
                .build());
        clone.setItemMeta(meta);
        return clone;
    }
}

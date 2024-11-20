package me.burb.burbpass.gui.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

public class ItemBuilder {
    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
        this.meta = this.item.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder name(Component name) {
        meta.displayName(name.decoration(TextDecoration.ITALIC, false));
        return this;
    }

    public ItemBuilder conditionalLore(boolean bool, Component... lores) {
        return bool ? lore(lores) : this;
    }

    public ItemBuilder lore(Component... lores) {
        meta.lore(Stream.of(lores)
                .filter(Objects::nonNull)
                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                .toList());
        return this;
    }

    public ItemBuilder lore(List<Component> lores) {
        meta.lore(lores.stream()
                .filter(Objects::nonNull)
                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                .toList());
        return this;
    }

    public ItemBuilder addLore(Component... lores) {
        List<Component> lore = new ArrayList<>();
        List<Component> oldLore = meta.lore();
        if (oldLore != null) lore.addAll(oldLore);
        lore.addAll(Arrays.stream(lores)
                .filter(Objects::nonNull)
                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                .toList());
        meta.lore(lore);
        return this;
    }

    public ItemBuilder addLore(List<Component> lores) {
        List<Component> lore = new ArrayList<>();
        List<Component> oldLore = meta.lore();
        if (oldLore != null) lore.addAll(oldLore);
        lore.addAll(lores.stream()
                .filter(Objects::nonNull)
                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                .toList());
        meta.lore(lore);
        return this;
    }

    public ItemBuilder glowing() {
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder headSkin(UUID player) {
        if (!(meta instanceof SkullMeta skullMeta)) return this;
        skullMeta.setPlayerProfile(Bukkit.createProfile(player, null));
        return this;
    }

    public ItemBuilder headSkinUrl(String url) {
        if (!(meta instanceof SkullMeta skull)) return this;
        try {
            URL u = new URL(url);
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();
            textures.setSkin(u);
            profile.setTextures(textures);
            skull.setPlayerProfile(profile);
        } catch (Exception ignored) {}
        return this;
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}

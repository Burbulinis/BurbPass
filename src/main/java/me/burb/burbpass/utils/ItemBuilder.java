package me.burb.burbpass.utils;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * ItemBuilder class to quickly/easily create {@link ItemStack ItemStacks} with chained methods
 */
@SuppressWarnings({"deprecation", "unused"})
public class ItemBuilder {

    /**
     * Create a new ItemBuilder
     * <p>Defaults amount = 1</p>
     *
     * @param material Material of Item
     * @return Instance of ItemBuilder
     */
    public static ItemBuilder builder(Material material) {
        return new ItemBuilder(material, 1);
    }

    /**
     * Create a new ItemBuilder
     *
     * @param material Material of Item
     * @param amount   Amount of items in stack
     * @return Instance of ItemBuilder
     */
    public static ItemBuilder builder(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    private ItemBuilder(@NotNull Material material, int amount) {
        Preconditions.checkArgument(material != Material.AIR, "Item can not be air!");
        Preconditions.checkArgument(amount > 0, "Amount can not be less than 1");
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    /**
     * Set the amount of the item
     *
     * @param amount Amount of the item
     * @return Instance of ItemBuilder
     */
    public ItemBuilder amount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    /**
     * Set the name of the item
     *
     * @param name Name of the item
     * @return Instance of ItemBuilder
     */
    public ItemBuilder name(String name) {
        this.itemMeta.setDisplayName(Utils.getColString(name));
        return this;
    }

    /**
     * Add an enchantment to the item
     *
     * @param enchantment Enchantment to add
     * @param level       Level of enchantment to add
     * @return Instance of ItemBuilder
     */
    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        this.itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    /**
     * Set the lore of the item
     *
     * @param lores Lore to set
     * @return Instance of ItemBuilder
     */
    public ItemBuilder lore(String... lores) {
        List<String> lore = new ArrayList<>();
        for (String s : lores) {
            lore.add(Utils.getColString(s));
        }
        this.itemMeta.setLore(lore);
        return this;
    }

    /**
     * Add a line of lore to the item
     *
     * @param lore Lore to add
     * @return Instance of ItemBuilder
     */
    public ItemBuilder addLore(String lore) {
        List<String> loreList = this.itemMeta.getLore();
        if (loreList == null) loreList = new ArrayList<>();
        loreList.add(Utils.getColString(lore));
        this.itemMeta.setLore(loreList);
        return this;
    }

    /**
     * Make the item unbreakable
     *
     * @return Instance of ItemBuilder
     */
    public ItemBuilder unbreakable() {
        this.itemMeta.setUnbreakable(true);
        return this;
    }

    /**
     * Set the custom model data of the item
     *
     * @param model Custom model data of the item
     * @return Instance of ItemBuilder
     */
    public ItemBuilder customModelData(int model) {
        this.itemMeta.setCustomModelData(model);
        return this;
    }

    /**
     * Add an attribute modifier to the item
     *
     * @param attribute Attribute to add
     * @param modifier  Attribute modifier to add
     * @return Instance of ItemBuilder
     */
    public ItemBuilder attribute(Attribute attribute, AttributeModifier modifier) {
        this.itemMeta.addAttributeModifier(attribute, modifier);
        return this;
    }

    /**
     * Get the ItemStack from this builder
     *
     * @return ItemStack from builder
     */
    public ItemStack item() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }

}

package me.burb.burbpass.api.battlepass;

import me.burb.burbpass.api.battlepass.data.BattlePassData;
import me.burb.burbpass.utils.GUI;
import me.burb.burbpass.utils.ItemBuilder;
import me.burb.burbpass.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BattlePass {

    private BattlePassData battlePassData;

    public BattlePass(BattlePassData battlePassData) {
        this.battlePassData = battlePassData;
    }

    public void open(int page, boolean editor) {
        if (battlePassData.getPlayer() == null) { return; }

        Player player = battlePassData.getPlayer();
        int level = battlePassData.getLevel();
        float progress = battlePassData.getProgress();

        GUI gui = GUI.chestBuilder(6, Component.text("            Battle Pass", NamedTextColor.YELLOW, TextDecoration.BOLD));
        ItemStack glass = ItemBuilder.builder(Material.BLACK_STAINED_GLASS_PANE).name(" ").item();
        gui.fill(glass);

        ItemStack goBack = Utils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2EyYzEyY2IyMjkxODM4NGUwYTgxYzgyYTFlZDk5YWViZGNlOTRiMmVjMjc1NDgwMDk3MjMxOWI1NzkwMGFmYiJ9fX0=", "&b <- ᴘᴀɢᴇ " + (page - 1));
        ItemStack goForward = Utils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjkxYWM0MzJhYTQwZDdlN2E2ODdhYTg1MDQxZGU2MzY3MTJkNGYwMjI2MzJkZDUzNTZjODgwNTIxYWYyNzIzYSJ9fX0=", "&bᴘᴀɢᴇ " + (page+1) + " ->");

        int end, pos;
        int[] slots;
        if (page == 1 || page == 2) {
            end = 21 * page;
            pos = end - 20;
            slots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        } else {
            end = 50;
            pos = 43;
            slots = new int[]{10,11,12,13,14,15,16,22};
            gui.formatSlot(46, goBack, data -> open(2, editor));
        }

        gui.formatSlots(slots, Utils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjcwNWZkOTRhMGM0MzE5MjdmYjRlNjM5YjBmY2ZiNDk3MTdlNDEyMjg1YTAyYjQzOWUwMTEyZGEyMmIyZTJlYyJ9fX0=", " "), data -> {});
        gui.formatSlot(49, Utils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==", "&cᴇxɪᴛ"), data -> player.closeInventory());

        if (page == 1) gui.formatSlot(52, goForward, data -> open(2, editor));
        else if (page == 2) {
            gui.formatSlot(46, goBack, data -> open(1, editor));
            gui.formatSlot(49, goForward, data -> open(3, editor));
        }

        HashMap<Integer, ItemStack> rewards = BattlePassData.getRewards();
        HashMap<Integer, ItemStack> clone = new HashMap<>();
        for (int i = 0; i < 50; i++) {
            if (!rewards.containsKey(i)) {
                clone.put(i, ItemBuilder.builder(Material.FIRE).item());
                continue;
            }
            clone.put(i, rewards.get(i).clone());
        }

        for (int i = pos; i <= end; i++) {
            ItemStack reward = clone.get(i);
            if (reward.getType().equals(Material.FIRE)) continue;

            if (level >= (i+1)) {

            }

        }

        gui.open(player);
    }

    public ItemStack getFormatted(int level, boolean editor) {
        if (!BattlePassData.getRewards().containsKey(level-1)) return null;
        ItemStack clone = BattlePassData.getRewards().get(level-1).clone();

        Component name = clone.displayName()
                .append(Component.text(" [", NamedTextColor.GOLD))
                .append(Component.text("ʟᴠʟ ", NamedTextColor.YELLOW))
                .append(Component.text(battlePassData.getLevel(), NamedTextColor.YELLOW))
                .append(Component.text(']', NamedTextColor.GOLD));

        Component progress = Component.text()
                .appendNewline()
                .append(Component.text("ᴘʀᴏɢʀᴇss: ", NamedTextColor.YELLOW))
                .append(Component.text(battlePassData.getPercent(), NamedTextColor.GOLD))
                .append(Component.text('%', NamedTextColor.GOLD))
                .build();

        Component claim;
        if (battlePassData.getRewardsClaimed().contains(level)) claim = Component.text("ᴄʟᴀɪᴍᴇᴅ", NamedTextColor.GRAY);
        else if (battlePassData.canClaim(level)) claim = Component.text("ɴᴏᴛ ᴄʟᴀɪᴍᴇᴅ", NamedTextColor.GREEN);
        else claim = Component.text("ᴄᴀɴɴᴏᴛ ᴄʟᴀɪᴍ", NamedTextColor.RED);

        ItemMeta meta = clone.getItemMeta();
        meta.displayName(name);
        List<Component> lore = meta.lore();
        if (lore == null) lore = new ArrayList<>();
        lore.add(progress);
        lore.add(claim);
        clone.setItemMeta(meta);
        return clone;
    }

    public BattlePassData getBattlePassData() {
        return battlePassData;
    }
}

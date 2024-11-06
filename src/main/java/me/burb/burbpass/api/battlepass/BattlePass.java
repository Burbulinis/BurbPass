package me.burb.burbpass.api.battlepass;

import me.burb.burbpass.BurbPass;
import me.burb.burbpass.api.battlepass.data.BattlePassData;
import me.burb.burbpass.utils.GUI;
import me.burb.burbpass.utils.ItemBuilder;
import me.burb.burbpass.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BattlePass {

    public static void open(int page, BattlePassData battlePassData, boolean editor) {
        if (Bukkit.getPlayer(battlePassData.getUUID()) == null) { return; }

        Player player = Bukkit.getPlayer(battlePassData.getUUID());
        int level = battlePassData.getLevel();

        GUI gui = GUI.chestBuilder(6, Component.text("         Battle Pass", NamedTextColor.YELLOW, TextDecoration.BOLD));
        ItemStack glass = ItemBuilder.builder(Material.BLACK_STAINED_GLASS_PANE).name(" ").item();
        gui.fill(glass);

        ItemStack goBack = Utils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2EyYzEyY2IyMjkxODM4NGUwYTgxYzgyYTFlZDk5YWViZGNlOTRiMmVjMjc1NDgwMDk3MjMxOWI1NzkwMGFmYiJ9fX0=", "&b <- ᴘᴀɢᴇ " + (page - 1));
        ItemStack goForward = Utils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjkxYWM0MzJhYTQwZDdlN2E2ODdhYTg1MDQxZGU2MzY3MTJkNGYwMjI2MzJkZDUzNTZjODgwNTIxYWYyNzIzYSJ9fX0=", "&bᴘᴀɢᴇ " + (page+1) + " ->");

        int end, start;
        int[] slots;
        if (page == 1 || page == 2) {
            end = 21 * page;
            start = end - 20;
            slots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        } else {
            end = 50;
            start = 43;
            slots = new int[]{10,11,12,13,14,15,16,22};
            gui.formatSlot(46, goBack, data -> open(2, battlePassData, editor));
        }
        ItemStack emptyHead = Utils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjcwNWZkOTRhMGM0MzE5MjdmYjRlNjM5YjBmY2ZiNDk3MTdlNDEyMjg1YTAyYjQzOWUwMTEyZGEyMmIyZTJlYyJ9fX0=", " ");
        gui.formatSlot(49, Utils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==", "&cᴇxɪᴛ"), data -> player.closeInventory());

        if (page == 1) gui.formatSlot(52, goForward, data -> open(2, battlePassData, editor));
        else if (page == 2) {
            gui.formatSlot(46, goBack, data -> open(1, battlePassData, editor));
            gui.formatSlot(52, goForward, data -> open(3, battlePassData, editor));
        }


        int pos = 0;
        for (int i = start; i <= end; i++) {
            int otherI = i;
            ItemStack reward = BattlePassData.getReward(i);
            if (reward == null) {

                gui.formatSlot(slots[pos], Utils.getHeadWithLevel(emptyHead, i, editor), data -> {
                    if (editor) {
                        if (!player.getItemOnCursor().isEmpty()) {
                            BattlePassData.setReward(otherI, player.getItemOnCursor());
                            open(page, battlePassData, true);
                        }
                    }
                });
                pos++;
                continue;
            }

            if (level >= i || (i - level) == 1 || editor) {
                ItemStack item = getFormatted(i, battlePassData, editor);
                gui.formatSlot(slots[pos], item, data -> {
                    if (editor) {
                        if (data.getClickType().equals(ClickType.SHIFT_RIGHT)) {
                            BattlePassData.removeReward(otherI);
                        }
                    } else {
                        if (!battlePassData.canClaim(otherI)) return;
                        if (!Utils.hasEnoughSpace(player, reward)) return;

                        player.getInventory().addItem(reward);
                        battlePassData.addClaimedReward(otherI);
                    }
                    open(page, battlePassData, editor);
                });
            } else gui.formatSlot(slots[pos], emptyHead, data -> {});
            pos++;
        }

        gui.open(player);
    }

    public static ItemStack getFormatted(int level, BattlePassData battlePassData, boolean editor) {
        if (BattlePassData.getReward(level) == null) return null;
        ItemStack clone = BattlePassData.getReward(level).clone();
        System.out.println(level);
        System.out.println(BattlePassData.getReward(level));
        System.out.println(clone);

        Component name = clone.displayName()
                .append(Component.text(" [", NamedTextColor.GOLD))
                .append(Component.text("ʟᴠʟ ", NamedTextColor.YELLOW))
                .append(Component.text(level, NamedTextColor.YELLOW))
                .append(Component.text(']', NamedTextColor.GOLD));

        Component progress = Component.text()
                .append(Component.text("ᴘʀᴏɢʀᴇss: ", NamedTextColor.YELLOW))
                .append(Component.text(battlePassData.getPercent(), NamedTextColor.GOLD))
                .append(Component.text('%', NamedTextColor.GOLD))
                .build();

        Component claim;
        if (battlePassData.isClaimed(level)) claim = Component.text("ᴄʟᴀɪᴍᴇᴅ", NamedTextColor.GRAY);
        else if (battlePassData.canClaim(level)) claim = Component.text("ɴᴏᴛ ᴄʟᴀɪᴍᴇᴅ", NamedTextColor.GREEN);
        else claim = Component.text("ᴄᴀɴɴᴏᴛ ᴄʟᴀɪᴍ", NamedTextColor.RED);

        if (editor) {
            progress = Component.text("ʏᴏᴜ ᴀʀᴇ ᴇᴅɪᴛɪɴɢ ᴛʜᴇ ʙᴀᴛᴛʟᴇᴘᴀss", NamedTextColor.GRAY);
            claim = Component.text()
                    .append(Component.text("sʜɪғᴛ ", NamedTextColor.RED))
                    .append(Component.text("+ ", NamedTextColor.GRAY))
                    .append(Component.text("ʀɪɢʜᴛ ᴄʟɪᴄᴋ ", NamedTextColor.RED))
                    .append(Component.text("ᴛᴏ ʀᴇᴍᴏᴠᴇ", NamedTextColor.GRAY))
                    .build();
        }

        ItemMeta meta = clone.getItemMeta();
        meta.displayName(name);
        List<Component> lore = meta.lore();
        if (lore == null) lore = new ArrayList<>();
        lore.add(progress);
        lore.add(claim);
        clone.lore(lore);
        clone.setItemMeta(meta);
        return clone;
    }
}

package me.burb.burbpass.battlepass;

import me.burb.burbpass.BurbPass;
import me.burb.burbpass.battlepass.data.BattlePassData;
import me.burb.burbpass.gui.PaginatedGui;
import me.burb.burbpass.gui.util.ItemBuilder;
import me.burb.burbpass.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BattlePass {

    private final BattlePassData data;

    public BattlePass(BattlePassData data) {
        this.data = data;
    }

    public BattlePassData getData() { return data; }

    public void open(int page, boolean editor) {
        if (Bukkit.getPlayer(data.getUUID()) == null) return;
        if (page <= 0 || page > 3) return;

        Player player = Bukkit.getPlayer(data.getUUID());
        GUI gui = new GUI(player, Bukkit.createInventory(player, 54, Component.text("         Battle Pass", NamedTextColor.YELLOW, TextDecoration.BOLD)));
        gui.setMaxPage(3);
        gui.setEditor(editor);
        gui.setPage(page);
        gui.open();
    }

    public class GUI extends PaginatedGui {

        private boolean editor;

        public GUI(Player player, Inventory inventory) {
            super(player, inventory);
        }

        public void setPage(int page) {
            super.setPage(page);
        }

        public void setEditor(boolean editor) {
            this.editor = editor;
        }

        @Override
        protected void render() {
            int page = getPage();

            fillInventory(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(Component.empty()).build());

            ItemStack goBack = Utils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2EyYzEyY2IyMjkxODM4NGUwYTgxYzgyYTFlZDk5YWViZGNlOTRiMmVjMjc1NDgwMDk3MjMxOWI1NzkwMGFmYiJ9fX0=", Component.text("<- ᴘʀᴇᴠɪᴏᴜs ᴘᴀɢᴇ", NamedTextColor.AQUA));
            ItemStack goForward = Utils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjkxYWM0MzJhYTQwZDdlN2E2ODdhYTg1MDQxZGU2MzY3MTJkNGYwMjI2MzJkZDUzNTZjODgwNTIxYWYyNzIzYSJ9fX0=", Component.text("ɴᴇxᴛ ᴘᴀɢᴇ ->", NamedTextColor.AQUA));

            BattlePassData.Reward rewardInstance = data.getRewardInstance();

            inventory.setItem(46, goBack);
            registerPreviousPage(46);
            inventory.setItem(52, goForward);
            registerNextPage(52);

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
            }

            ItemStack emptyHead = Utils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjcwNWZkOTRhMGM0MzE5MjdmYjRlNjM5YjBmY2ZiNDk3MTdlNDEyMjg1YTAyYjQzOWUwMTEyZGEyMmIyZTJlYyJ9fX0=", Component.empty());
            inventory.setItem(49, Utils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==", Component.text("ᴇxɪᴛ", NamedTextColor.RED)));
            registerHandler(49, e -> e.getWhoClicked().closeInventory());


            int level = getData().getLevel();
            int pos = 0;

            for (int i = start; i <= end; i++) {
                int otherI = i;
                ItemStack reward = rewardInstance.getLevelReward(i);

                if (reward == null) {

                    inventory.setItem(slots[pos], Utils.getHeadWithLevel(emptyHead, i, editor));
                    registerHandler(slots[pos], e -> {
                        if (!editor) return;
                        if (player.getItemOnCursor().isEmpty()) return;

                        BattlePassData.Reward.setReward(otherI, player.getItemOnCursor());
                        open();
                    });
                    pos++;
                    continue;
                }

                if (level >= i || (i - level) == 1 || editor) {
                    ItemStack formattedReward = rewardInstance.getLevelFormattedReward(level, editor);
                    inventory.setItem(slots[pos], formattedReward);

                    registerHandler(slots[pos], e -> {
                        if (editor) {
                            if (e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                                BattlePassData.Reward.removeReward(otherI);
                                open();
                            }
                        } else {
                            if (!rewardInstance.canClaim(otherI)) return;
                            if (!Utils.hasEnoughSpace(player, reward)) return;

                            inventory.addItem(reward);
                            rewardInstance.addClaimedReward();
                        }
                    });
                } else inventory.setItem(slots[pos], emptyHead);
                pos++;
            }
        }
    }
}

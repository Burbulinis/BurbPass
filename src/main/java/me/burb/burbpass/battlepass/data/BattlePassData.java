package me.burb.burbpass.battlepass.data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class BattlePassData {

    private static final HashMap<Integer, ItemStack> REWARDS = new HashMap<>();
    private static final HashMap<UUID, BattlePassData> DATA = new HashMap<>();

    public static BattlePassData getOrCreateData(UUID uuid) {
        return DATA.computeIfAbsent(uuid, k -> {
            BattlePassData data = new BattlePassData(k, 0, 0, null);
            DATA.put(k, data);
            return data;
        });
    }

    public static HashMap<UUID, BattlePassData> getAllData() {
        return DATA;
    }

    public void setLevel(short level) {
        this.level = level;
        progress = (level^2)*64;
    }

    private final UUID uuid;

    private int level;
    private float progress;
    private final List<Integer> rewardsClaimed;

    private BattlePassData(UUID uuid, int level, float progress, List<Integer> rewardsClaimed)  {
        this.uuid = uuid;
        this.level = level;
        this.progress = progress;
        this.rewardsClaimed = rewardsClaimed;
    }

    public UUID getUUID() {
        return uuid;
    }

    public int getLevel() {
        return level;
    }

    public float getProgress() {
        return progress;
    }

    public float getPercent() {
        float progress = (this.level^2)*64;
        float xp = (((this.level+1)^2)*64) - progress;
        float x = this.progress - progress;

        if (level == 0) x = this.progress;

        float percent = (x/xp)*100;
        if (percent > 100) percent = 100;

        return percent;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        level = (int) Math.floor(Math.sqrt((double) progress / 64));
    }

    public void reset() {
        level = 0;
        progress = 0f;
        rewardsClaimed.clear();
    }

    public void delete() {
        reset();
        DATA.remove(getUUID());
    }

    public Reward getRewardInstance() {
        return new Reward();
    }

    public class Reward {

        public static void setReward(int key, ItemStack item) {
            REWARDS.put(key-1, item.clone());
        }

        public static ItemStack getReward(int key) {
            return REWARDS.get(key-1);
        }

        public static HashMap<Integer, ItemStack> getRewards() {
            return REWARDS;
        }

        public static void removeReward(int key) {
            REWARDS.remove(key-1);
        }

        public void removeClaimedReward(int... rewardsClaimed) {
            for (int i : rewardsClaimed) getClaimedRewards().remove(i-1);
        }

        public void addClaimedReward(int... rewardsClaimed) {
            for (int i : rewardsClaimed) getClaimedRewards().add(i-1);
        }

        public boolean canClaim(int level) {
            if (getLevel() >= level) {
                isClaimed(level);
            }
            return false;
        }

        public boolean isClaimed(int level) {
            if (rewardsClaimed == null) return false;
            return rewardsClaimed.contains(level-1);
        }

        public List<Integer> getClaimedRewards() {
            return rewardsClaimed;
        }

        public ItemStack getLevelReward(int level) {
            return getReward(level-1);
        }

        public ItemStack getLevelFormattedReward(int level, boolean editor) {
            return getFormatted(level-1, editor);
        }

        private ItemStack getFormatted(int level, boolean editor) {
            if (getReward(level) == null) return null;
            ItemStack clone = getReward(level).clone();
            System.out.println(level);
            System.out.println(getReward(level));
            System.out.println(clone);

            Component name = clone.displayName()
                    .append(Component.text(" [", NamedTextColor.GOLD))
                    .append(Component.text("ʟᴠʟ ", NamedTextColor.YELLOW))
                    .append(Component.text(level, NamedTextColor.YELLOW))
                    .append(Component.text(']', NamedTextColor.GOLD));

            Component progress = Component.text()
                    .append(Component.text("ᴘʀᴏɢʀᴇss: ", NamedTextColor.YELLOW))
                    .append(Component.text(getPercent(), NamedTextColor.GOLD))
                    .append(Component.text('%', NamedTextColor.GOLD))
                    .build();

            Component claim;
            if (isClaimed(level)) claim = Component.text("ᴄʟᴀɪᴍᴇᴅ", NamedTextColor.GRAY);
            else if (canClaim(level)) claim = Component.text("ɴᴏᴛ ᴄʟᴀɪᴍᴇᴅ", NamedTextColor.GREEN);
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
}

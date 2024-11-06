package me.burb.burbpass.api.battlepass.data;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BattlePassData {

    private static final HashMap<Integer, ItemStack> REWARDS = new HashMap<>();
    private static final HashMap<UUID, BattlePassData> DATA = new HashMap<>();

    private final UUID uuid;

    private int level;
    private float progress;
    private final List<Integer> rewardsClaimed;

    private BattlePassData(UUID uuid, int level, float progress, List<Integer> rewardsClaimed) {
        this.uuid = uuid;
        this.level = level;
        this.progress = progress;
        this.rewardsClaimed = rewardsClaimed;
        DATA.put(uuid, this);
    }

    public static BattlePassData getDataOrDefault(UUID uuid) {
        if (!DATA.containsKey(uuid)) return new BattlePassData(uuid, 0, 0, null);
        return DATA.get(uuid);
    }

    public static HashMap<UUID, BattlePassData> getAllData() {
        return DATA;
    }

    public static void setReward(int key, ItemStack item) {
        REWARDS.put(key, item);
    }

    @Nullable
    public static ItemStack getReward(int key) {
        return REWARDS.get(key);
    }

    public static HashMap<Integer, ItemStack> getRewards() {
        return REWARDS;
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
    public boolean isClaimed(int level) {
        if (rewardsClaimed == null) return false;
        return rewardsClaimed.contains(level);
    }

    public List<Integer> getClaimedRewards() {
        return rewardsClaimed;
    }

    public static void removeReward(int key) {
        REWARDS.remove(key);
    }

    public void setLevel(short level) {
        this.level = level;
        progress = (level^2)*64;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        level = (int) Math.floor(Math.sqrt((double) progress / 64));
    }

    public void addClaimedReward(int... rewardsClaimed) {
        for (int i : rewardsClaimed) this.rewardsClaimed.add(i-1);
    }

    public void removeClaimedReward(int... rewardsClaimed) {
        for (int i : rewardsClaimed) this.rewardsClaimed.remove(i-1);
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

    public boolean canClaim(int level) {
        return (this.level >= level && !isClaimed(level));
    }
}

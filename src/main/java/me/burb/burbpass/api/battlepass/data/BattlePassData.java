package me.burb.burbpass.api.battlepass.data;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BattlePassData {

    private static HashMap<Integer, ItemStack> rewards = new HashMap<>();

    private final UUID uuid;

    private int level;
    private float progress;
    private List<Integer> rewardsClaimed;

    public BattlePassData(UUID uuid, int level, float progress, List<Integer> rewardsClaimed) {
        this.uuid = uuid;
        this.level = level;
        this.progress = progress;
        this.rewardsClaimed = rewardsClaimed;
    }

    @Nullable
    public static ItemStack getReward(int key) {
        return rewards.get(key);
    }

    public static HashMap<Integer, ItemStack> getRewards() {
        return rewards;
    }

    public Player getPlayer() {
        return Bukkit.getOfflinePlayer(uuid).getPlayer();
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

    public List<Integer> getRewardsClaimed() {
        return rewardsClaimed;
    }

    public void setReward(int key, ItemStack item) {
        rewards.put(key, item);
    }

    public void setLevel(short level) {
        this.level = level;
        progress = (level^2)*64;
    }

    public void setProgress(float progress) {
        this.progress = progress; // lvl 5 = 1600
        level = (int) Math.floor(Math.sqrt((double) progress / 64));
    }

    public void setRewardsClaimed(boolean claimed, int... rewardsClaimed) {
        for (int i : rewardsClaimed) {
            if (this.rewardsClaimed.get(i) != null) { return; }

            if (claimed) { this.rewardsClaimed.add(i); }
            else { this.rewardsClaimed.remove(i); }
        }
    }

    public boolean canClaim(int level) {
        return (this.level >= level && !rewardsClaimed.contains(level));
    }
}

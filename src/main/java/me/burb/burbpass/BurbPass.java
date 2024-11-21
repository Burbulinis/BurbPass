package me.burb.burbpass;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import me.burb.burbpass.battlepass.BattlePass;
import me.burb.burbpass.battlepass.data.BattlePassData;
import me.burb.burbpass.gui.GuiManager;
import me.burb.burbpass.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.List;

public class BurbPass extends JavaPlugin {

    private static GuiManager GUI_MANAGER;
    private static BurbPass INSTANCE;

    public static BurbPass getInstance() { return INSTANCE; }

    public static GuiManager getGuiManager() { return GUI_MANAGER; }

    @Override
    public void onEnable() {
        INSTANCE = this;

        GUI_MANAGER = new GuiManager(this);

        registerCommands();
    }

    private void registerCommands() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
        CommandAPI.onEnable();

        List<Argument<?>> arguments = new ArrayList<>();
        List<Argument<?>> playerArg = new ArrayList<>();
        arguments.add(new StringArgument("value").replaceSuggestions(ArgumentSuggestions.strings(info -> {
            Player player = (Player) info.sender();
            if (player.hasPermission("battlepass.edit")) return new String[]{"info", "edit", "reset", "wipe"};
            return new String[]{"info"};
        })));
        playerArg.add(new StringArgument("player").replaceSuggestions(ArgumentSuggestions.strings(info -> {
            if (info.previousArgs().get("info") == null && info.previousArgs().get("reset") == null)
                return new String[0];
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList().toArray(new String[0]);
        })));

        new CommandAPICommand("battlepass")
                .withAliases("bp")
                .withOptionalArguments(arguments)
                .withOptionalArguments(playerArg)
                .executesPlayer((player, args) -> {
                    Object arg = args.get("value");
                    BattlePassData data = BattlePassData.getOrCreateData(player.getUniqueId());
                    final BattlePass battlePass = new BattlePass(data);

                    if (arg == null) {
                        battlePass.open(1, false);
                        return;
                    }

                    Player playerToReset = null;

                    Object target = args.get("player");
                    if (target != null) {
                        playerToReset = Bukkit.getPlayer(args.toString());
                        if (playerToReset == null) return;
                        data = BattlePassData.getOrCreateData(playerToReset.getUniqueId());
                    }

                    switch (arg.toString()) {
                        case "info" -> {
                            final int level = data.getLevel();
                            final float progress = data.getProgress();
                            final float progressPercentage = (progress / ((50^2)*64))*100;
                            final float percent = data.getPercent();

                            if (playerToReset != null) player.sendMessage(Component.text("BATTLEPASS INFO OF " + playerToReset.getName() + ':', NamedTextColor.GREEN, TextDecoration.BOLD));
                            else player.sendMessage(Component.text("YOUR BATTLEPASS INFO:", NamedTextColor.GREEN, TextDecoration.BOLD));

                            player.sendMessage(Component.text()
                                    .append(Component.text("  Level:", NamedTextColor.YELLOW))
                                    .append(Component.text(" Level " + level, NamedTextColor.GREEN))
                                    .append(Component.text('!', NamedTextColor.YELLOW))
                                    .appendNewline()
                                    .append(Component.text("  Mining XP: ", NamedTextColor.YELLOW))
                                    .append(Component.text(Utils.format(progress) + " XP", NamedTextColor.GREEN))
                                    .build());

                            if (level < 50) {
                                player.sendMessage(" ");
                                if (level < 49) player.sendMessage(Component.text()
                                        .append(Component.text("  " + percent, NamedTextColor.GREEN))
                                        .append(Component.text(" into reaching the next Level! (", NamedTextColor.YELLOW))
                                        .append(Component.text("Level " + level+1, NamedTextColor.GREEN))
                                        .append(Component.text(')', NamedTextColor.YELLOW))
                                        .build());
                                player.sendMessage(Component.text()
                                        .append(Component.text("  " + progressPercentage, NamedTextColor.GREEN))
                                        .append(Component.text(" into reaching ", NamedTextColor.YELLOW))
                                        .append(Component.text("Level 50", NamedTextColor.GREEN))
                                        .append(Component.text('!', NamedTextColor.YELLOW))
                                        .build());
                            }
                        }

                        case "edit" -> {
                            if (!player.hasPermission("battlepass.edit")) return;
                            battlePass.open(1, true);
                        }

                        case "reset" -> {
                            if (!player.hasPermission("battlepass.edit")) return;

                            data.reset();
                            player.sendMessage(Component.text()
                                    .append(Component.text("Successfully reset the data of ", NamedTextColor.GREEN))
                                    .append(Component.text(playerToReset == null ? player.getName() : playerToReset.getName(), NamedTextColor.GREEN))
                                    .build());
                        }
                        case "wipe" -> {
                            if (!player.hasPermission("battlepass.edit")) return;

                            List<BattlePassData> dataList = BattlePassData.getAllData().values().stream().toList();
                            BattlePassData.getAllData().clear();
                            BattlePassData.Reward.getRewards().clear();
                            for (BattlePassData battlePassData : dataList) {
                                BattlePassData.Reward reward = battlePassData.getRewardInstance();
                                if (reward.getClaimedRewards() == null) continue;
                                reward.getClaimedRewards().clear();
                            }
                            player.sendMessage(Component.text("Successfully wiped all data", NamedTextColor.GREEN));
                        }
                    }
                })
                .register();
    }
}

package me.burb.burbpass;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.burb.burbpass.battlepass.BattlePass;
import me.burb.burbpass.battlepass.data.BattlePassData;
import me.burb.burbpass.gui.GuiManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BurbPass extends JavaPlugin {

    private static GuiManager GUI_MANAGER;
    private static BurbPass INSTANCE;

    public static BurbPass getInstance() { return INSTANCE; }

    public static GuiManager getGuiManager() { return GUI_MANAGER; }

    @Override
    public void onEnable() {
        INSTANCE = this;

        GUI_MANAGER = new GuiManager(this);

        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
        CommandAPI.onEnable();

        List<Argument<?>> arguments = new ArrayList<>();
        List<Argument<?>> playerArg = new ArrayList<>();
        arguments.add(new StringArgument("value").replaceSuggestions(ArgumentSuggestions.strings(info -> {
            Player player = (Player) info.sender();
            if (player.hasPermission("battlepass.edit")) return new String[]{"info", "edit", "reset", "wipe"};
            return new String[]{"info"};
        })));
        playerArg.add(new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(info -> {
            if (info.previousArgs().getOptional(Arrays.toString(new String[]{"info", "reset"})).isEmpty()) return new String[0];
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList().toArray(new String[0]);
        })));

        new CommandAPICommand("battlepass")
                .withAliases("bp")
                .withOptionalArguments(arguments)
                .withOptionalArguments(new PlayerArgument("player"))
                .executesPlayer((player, args) -> {
                    Object arg = args.get("value");
                    BattlePassData data = BattlePassData.getOrCreateData(player.getUniqueId());
                    BattlePass battlePass = new BattlePass(data);
                    if (arg == null)
                        battlePass.open(1, false);
                    else if (arg.equals("edit")) {
                        if (!player.hasPermission("battlepass.edit")) return;
                        battlePass.open(1, true);
                    }
                    else if (arg.equals("reset")) {
                        if (!player.hasPermission("battlepass.edit")) return;
                        Player player1 = null;
                        Object target = args.get("player");
                        if (target == null) data.reset();
                        else {
                            player1 = (Player) target;
                            BattlePassData.getOrCreateData(player1.getUniqueId()).reset();
                        }

                        player.sendMessage(Component.text()
                                .append(Component.text("Successfully reset the data of ", NamedTextColor.GREEN))
                                .append(Component.text(player1 == null ? player.getName() : player1.getName(), NamedTextColor.GREEN))
                                .build());
                    }
                    else if (arg.equals("wipe")) {
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
                })
                .register();
    }
}
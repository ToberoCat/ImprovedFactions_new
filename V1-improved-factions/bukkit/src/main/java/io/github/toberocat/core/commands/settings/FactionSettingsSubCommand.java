package io.github.toberocat.core.commands.settings;

import io.github.toberocat.MainIF;
import io.github.toberocat.core.factions.local.managers.FactionPerm;
import io.github.toberocat.core.gui.faction.FactionSettingsGui;
import io.github.toberocat.core.utility.command.SubCommand;
import io.github.toberocat.core.utility.command.SubCommandSettings;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class FactionSettingsSubCommand extends SubCommand {
    public FactionSettingsSubCommand() {
        super("settings", "settings", "", false);
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings()
                .setNeedsFaction(SubCommandSettings.NYI.Yes)
                .setFactionPermission(FactionPerm.FACTION_SETTING_PERM);
    }

    @Override
    protected void commandExecute(Player player, String[] args) {
        new BukkitRunnable() {
            @Override
            public void run() {
                new FactionSettingsGui(player);
            }
        }.runTask(MainIF.getIF());
    }

    @Override
    protected List<String> commandTab(Player player, String[] args) {
        return null;
    }
}

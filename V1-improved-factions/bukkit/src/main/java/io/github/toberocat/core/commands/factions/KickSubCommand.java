package io.github.toberocat.core.commands.factions;

import io.github.toberocat.core.factions.Faction;
import io.github.toberocat.core.factions.FactionManager;
import io.github.toberocat.core.utility.command.SubCommand;
import io.github.toberocat.core.utility.command.SubCommandSettings;
import io.github.toberocat.core.utility.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class KickSubCommand extends SubCommand {
    public KickSubCommand() {
        super("kick", "command.kick.description", false);
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings().setArgLength(1).setNeedsFaction(SubCommandSettings.NYI.Yes);
    }

    @Override
    protected void commandExecute(Player player, String[] args) {
        Faction faction = FactionManager.getPlayerFaction(player);
        OfflinePlayer kick = Bukkit.getOfflinePlayer(args[0]);

        if (kick == null) {
            sendCommandExecuteError(CommandExecuteError.PlayerNotFound, player);
            return;
        }

        faction.kick(kick);
        Language.sendRawMessage("You kicked &e" + kick.getName(), player);
    }

    @Override
    protected List<String> commandTab(Player player, String[] args) {
        Faction faction = FactionManager.getPlayerFaction(player);
        if (faction == null) return null;

        return faction.getFactionMemberManager().getMembers().stream().map(x -> Bukkit.getOfflinePlayer(x).getName()).toList();
    }
}

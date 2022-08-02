package io.github.toberocat.core.utility.action.provided;

import io.github.toberocat.core.factions.Faction;
import io.github.toberocat.core.factions.FactionManager;
import io.github.toberocat.core.utility.action.Action;
import io.github.toberocat.core.utility.language.Language;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BroadcastAllyAction extends Action {
    @Override
    public @NotNull String label() {
        return "message-factions-broadcast-ally";
    }

    @Override
    public void run(@NotNull Player player, @NotNull String provided) {
        Faction faction = FactionManager.getPlayerFaction(player);
        if (faction == null) return;

        faction.getMessageModule().broadcastAlly(Language.format(provided));
    }
}

package io.github.toberocat.core.commands.zones;

import io.github.toberocat.MainIF;
import io.github.toberocat.core.utility.Result;
import io.github.toberocat.core.utility.claim.ClaimManager;
import io.github.toberocat.core.utility.command.AutoSubCommand;
import io.github.toberocat.core.utility.command.SubCommand;
import io.github.toberocat.core.utility.command.SubCommandSettings;
import io.github.toberocat.core.utility.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class SafeZoneSubCommand extends AutoSubCommand {
    public SafeZoneSubCommand() {
        super("safezone", "zones.safezone", "command.zones.safezone.description", false);
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings().setUseWhenFrozen(true);
    }

    @Override
    public String getEnabledKey() {
        return "command.zones.safezone.auto-enabled";
    }

    @Override
    public String getDisabledKey() {
        return "command.zones.safezone.auto-disabled";
    }

    @Override
    public void onSingle(Player player) {
        Result result = MainIF.getIF().getClaimManager().protectChunk(ClaimManager.SAFEZONE_REGISTRY, player.getLocation().getChunk());

        if (result.isSuccess())
            Language.sendMessage("command.zones.safezone.claim", player);
        else Language.sendRawMessage(result.getPlayerMessage(), player);

    }
}

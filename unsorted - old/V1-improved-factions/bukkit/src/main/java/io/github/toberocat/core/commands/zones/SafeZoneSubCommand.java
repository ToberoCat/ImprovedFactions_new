package io.github.toberocat.core.commands.zones;

import io.github.toberocat.core.utility.claim.ClaimManager;
import io.github.toberocat.core.utility.command.SubCommandSettings;
import io.github.toberocat.core.utility.command.auto.AutoSubCommand;
import io.github.toberocat.core.utility.exceptions.chunks.ChunkAlreadyClaimedException;
import io.github.toberocat.core.utility.language.Language;
import io.github.toberocat.core.utility.language.Parser;
import org.bukkit.entity.Player;

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
        try {
            ClaimManager.protectChunk(ClaimManager.SAFEZONE_REGISTRY, player.getLocation().getChunk());
            Language.sendMessage("command.zones.safezone.claim", player);
        } catch (ChunkAlreadyClaimedException e) {
            Parser.run("command.zone.safezone.already-claimed")
                    .parse("{registry}", e.getRegistry())
                    .send(player);
        }
    }
}

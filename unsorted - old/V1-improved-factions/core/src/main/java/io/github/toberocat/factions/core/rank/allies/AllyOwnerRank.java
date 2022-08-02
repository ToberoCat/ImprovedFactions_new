package io.github.toberocat.factions.core.rank.allies;

import io.github.toberocat.core.factions.rank.Rank;
import io.github.toberocat.core.utility.Utility;
import io.github.toberocat.core.utility.language.Language;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static io.github.toberocat.core.utility.config.ConfigManager.getValue;

public class AllyOwnerRank extends Rank {

    public static final String registry = "AllyOwner";

    public AllyOwnerRank(int priority) {
        super(getValue("faction.ranks.ally-owner", "Ally owner"), registry, priority, false);
    }

    @Override
    public String description(Player player) {
        return null;
    }

    @Override
    public ItemStack getItem(Player player) {
        return Utility.getSkull("https://textures.minecraft.net/texture/134b0ab8933d5366d7c4c1dc0b0ccc97dcd1d20a4baaa8b04598a4bfaf26b59", 1,
                Language.getMessage("rank.ally-owner.title", player),
                Language.getLore("rank.ally-owner.lore", player));
    }
}

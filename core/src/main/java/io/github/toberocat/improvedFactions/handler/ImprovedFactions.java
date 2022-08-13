package io.github.toberocat.improvedFactions.handler;

import io.github.toberocat.improvedFactions.exceptions.NoImplementationProvidedException;
import io.github.toberocat.improvedFactions.player.FactionPlayer;
import io.github.toberocat.improvedFactions.player.OfflineFactionPlayer;
import io.github.toberocat.improvedFactions.registry.ImplementationHolder;
import io.github.toberocat.improvedFactions.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface ImprovedFactions {

    static @NotNull ImprovedFactions api() {
        ImprovedFactions implementation = ImplementationHolder.improvedFactions;
        if (implementation == null) throw new NoImplementationProvidedException("improved faction");
        return implementation;
    }

    /* Player */
    @Nullable FactionPlayer<?> getPlayer(@NotNull UUID id);

    @Nullable FactionPlayer<?> getPlayer(@NotNull String name);

    @Nullable OfflineFactionPlayer<?> getOfflinePlayer(@NotNull UUID id);

    @Nullable OfflineFactionPlayer<?> getOfflinePlayer(@NotNull String name);

    /* World */

    @Nullable World getWorld(@NotNull String name);

    @NotNull List<World> getAllWorlds();

    /* File */

    @NotNull File getDataFolder();

}

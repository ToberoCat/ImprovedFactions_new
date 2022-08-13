package io.github.toberocat.improvedFactions.faction.components;

import io.github.toberocat.improvedFactions.claims.ClaimHandler;
import io.github.toberocat.improvedFactions.claims.component.Claim;
import io.github.toberocat.improvedFactions.faction.Faction;
import io.github.toberocat.improvedFactions.handler.ImprovedFactions;
import io.github.toberocat.improvedFactions.utils.CUtils;
import io.github.toberocat.improvedFactions.world.Chunk;
import io.github.toberocat.improvedFactions.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.Stream;

public final class FactionClaims<F extends Faction<F>> {
    private LinkedHashMap<String, Stream<FactionClaim<F>>> claims;
    private final F faction;

    public FactionClaims(F faction) {
        this.faction = faction;
        this.claims = null;
    }

    public static <F extends Faction<F>> FactionClaims<F> createClaims(@NotNull F faction) {
        return new FactionClaims<>(faction);
    }

    public void forceCalculate() {
        claims = new LinkedHashMap<>();
        ClaimHandler.api().forEach((name, c) -> {
            World world = ImprovedFactions.api().getWorld(name);
            if (world == null || CUtils.isWorldAllowed(world)) return;

            claims.put(name, c.getAllClaims()
                            .filter(Objects::nonNull)
                    .filter(x -> x.registry().equals(faction.getRegistry()))
                    .map(x -> FactionClaim.fromClaim(faction, x)));
        });
    }

    /**
     * This value is only temporary and is not getting updated once the object got created
     *
     * @return The total claims this faction has
     */
    public long getTotal() {
        return ClaimHandler.api()
                .registryClaims(faction.getRegistry())
                .count();
    }

    public void claim(@NotNull Chunk chunk){
        claim(chunk.getWorld().getWorldName(), chunk.getX(), chunk.getZ());
    }

    public void claim(@NotNull String worldName, int x, int z) {
        World world = ImprovedFactions.api().getWorld(worldName);
        if (world == null) return;

        Chunk chunk = world.getChunkAt(x, z);
        try {
            ClaimHandler.api().protectChunk(faction.getRegistry(), chunk);
        } catch (ChunkAlreadyClaimedException e) {
            overclaim(chunk, faction, e.getRegistry());
        }
    }

    private void overclaim(@NotNull Chunk chunk, @NotNull Faction<?> faction, @NotNull String claim)
            throws ChunkAlreadyClaimedException, FactionNotInStorage {
        if (faction.getRegistry().equals(claim)) throw new ChunkAlreadyClaimedException(claim);
        if (ClaimManager.isManageableZone(claim)) throw new ChunkAlreadyClaimedException(claim);
        if (checkPower(faction, FactionHandler.getFaction(claim))) return;

        ClaimManager.removeProtection(chunk);
    }

    private boolean checkPower(@NotNull Faction<?> sender, @NotNull Faction<?> target) {
        return sender.getActivePower().compareTo(target.getActiveMaxPower()) > 0;
    }

    public void unclaimAll() {
        claims.forEach((worldName, chunks) -> chunks.forEach(FactionClaim::unclaim));
        claims.clear();
    }

    public LinkedHashMap<String, Stream<FactionClaim<F>>> claims() {
        if (claims == null) forceCalculate();
        return claims;
    }

    public F faction() {
        return faction;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FactionClaims<?>) obj;
        return Objects.equals(this.claims, that.claims) &&
                Objects.equals(this.faction, that.faction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(claims, faction);
    }

    @Override
    public String toString() {
        return "FactionClaims[" +
                "claims=" + claims + ", " +
                "faction=" + faction + ']';
    }


    public record FactionClaim<F extends Faction<F>>(Faction<F> faction, String world, int x, int z) {

        public static <F extends Faction<F>> FactionClaim<F> fromClaim(@NotNull F faction,
                                                                       @NotNull Claim claim) {
            return new FactionClaim<>(faction, claim.world(), claim.x(), claim.z());
        }

        public void unclaim() {
            World world = ImprovedFactions.api().getWorld(world());
            if (world == null) return;

            ClaimHandler.removeProtection(world.getChunkAt(x, z));
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, z);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FactionClaim<?> that = (FactionClaim<?>) o;
            return x == that.x && z == that.z;
        }
    }
}

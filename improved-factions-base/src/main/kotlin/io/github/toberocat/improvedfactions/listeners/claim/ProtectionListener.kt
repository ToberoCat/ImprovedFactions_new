package io.github.toberocat.improvedfactions.listeners.claim

import io.github.toberocat.improvedfactions.ImprovedFactionsPlugin
import io.github.toberocat.improvedfactions.claims.clustering.Position
import io.github.toberocat.improvedfactions.claims.getFactionClaim
import io.github.toberocat.improvedfactions.translation.sendLocalized
import io.github.toberocat.improvedfactions.user.factionUser
import io.github.toberocat.improvedfactions.user.noFactionId
import org.bukkit.Chunk
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Listener
import org.jetbrains.exposed.sql.transactions.transaction

abstract class ProtectionListener(protected val zoneType: String) : Listener {
    private val claimClusters = ImprovedFactionsPlugin.instance.claimChunkClusters
    abstract fun namespace(): String

    protected fun protectChunk(event: Cancellable, entity: Entity?, player: Player) =
        protectChunk(event, entity?.location?.chunk, player)


    protected fun protectChunk(event: Cancellable, block: Block?, player: Player) =
        protectChunk(event, block?.chunk, player)

    private fun protectChunk(event: Cancellable, chunk: Chunk?, player: Player) = transaction {
        val claim = chunk?.getFactionClaim()
        if (claim?.zoneType != zoneType || (claim.zone()?.protectAlways == false && claim.factionId == noFactionId))
            return@transaction

        val claimedFaction = claim.factionId
        val playerFaction = player.factionUser().factionId
        if (claimedFaction == playerFaction)
            return@transaction

        if (claimClusters.getCluster(Position(chunk.x, chunk.z, claimedFaction))?.isUnprotected(chunk.x, chunk.z) == true)
            return@transaction

        event.isCancelled = true
        player.sendLocalized("base.claim.protected")
    }
}
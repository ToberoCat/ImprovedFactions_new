package io.github.toberocat.improvedfactions.claims

import io.github.toberocat.improvedfactions.exceptions.CantClaimThisChunkException
import io.github.toberocat.improvedfactions.exceptions.FactionDoesntHaveThisClaimException
import io.github.toberocat.toberocore.command.exceptions.CommandException
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.block.Block
import org.jetbrains.exposed.sql.and

fun Block.getFactionClaim(): FactionClaim? = chunk.getFactionClaim()
fun Location.getFactionClaim(): FactionClaim? = chunk.getFactionClaim()

fun Chunk.getFactionClaim(): FactionClaim? =
    FactionClaim.find {
        FactionClaims.chunkX eq x and
                (FactionClaims.chunkZ eq z) and
                (FactionClaims.world eq world.name)
    }
        .firstOrNull()

fun squareClaimAction(
    centerChunk: Chunk,
    squareRadius: Int,
    action: (chunk: Chunk) -> Unit,
    handleError: (e: CommandException) -> Unit
): ClaimStatistics {
    var successfulClaims = 0
    var totalClaims = 0
    val world = centerChunk.world
    val centerX = centerChunk.x
    val centerZ = centerChunk.z
    for (x in -squareRadius..squareRadius) {
        for (z in -squareRadius..squareRadius) {
            val chunk = world.getChunkAt(centerX + x, centerZ + z)
            try {
                totalClaims++
                action(chunk)
                successfulClaims++
            } catch (e: CommandException) {
                handleError(e)
            }
        }
    }
    return ClaimStatistics(totalClaims, successfulClaims)
}
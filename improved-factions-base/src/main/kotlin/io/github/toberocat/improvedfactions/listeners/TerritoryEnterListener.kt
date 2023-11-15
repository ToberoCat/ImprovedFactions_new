package io.github.toberocat.improvedfactions.listeners

import io.github.toberocat.improvedfactions.claims.FactionClaim
import io.github.toberocat.improvedfactions.claims.getFactionClaim
import io.github.toberocat.improvedfactions.translation.getLocalized
import io.github.toberocat.improvedfactions.user.noFactionId
import io.github.toberocat.improvedfactions.utils.toAudience
import io.github.toberocat.improvedfactions.zone.ZoneHandler
import net.kyori.adventure.title.TitlePart
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.jetbrains.exposed.sql.transactions.transaction

class TerritoryEnterListener : Listener {
    @EventHandler
    fun playerMove(event: PlayerMoveEvent) {
        val to = event.to?.chunk
        val from = event.from.chunk
        if (to == event.from.chunk) return
        transaction {
            val toClaim = to?.getFactionClaim()
            val fromClaim = from.getFactionClaim()
            if (hideTerritoryAnnouncement(toClaim, fromClaim)) return@transaction

            val toFaction = toClaim?.faction()
            val toZone = toClaim?.zone()
            val key = if (toFaction != null) "base.claim-faction-territory" else (toZone?.noFactionTitle
                ?: "base.zone.wilderness")
            val component = event.player.getLocalized(
                key, mapOf(
                    "faction" to (toFaction?.name ?: "Wilderness")
                )
            )
            event.player.toAudience().sendTitlePart(TitlePart.TITLE, component)
        }
    }

    private fun hideTerritoryAnnouncement(toClaim: FactionClaim?, fromClaim: FactionClaim?): Boolean {
        val toZone = toClaim?.zone()
        return when (toZone?.announceTitle) {
            false -> true
            else -> {
                val toFaction = toClaim?.faction()
                val fromFaction = fromClaim?.factionId ?: noFactionId

                val fromZoneType = fromClaim?.zoneType
                (toFaction?.id?.value ?: noFactionId) == fromFaction && (toZone?.type
                    ?: ZoneHandler.FACTION_ZONE_TYPE) == (fromZoneType ?: ZoneHandler.FACTION_ZONE_TYPE)
            }
        }
    }
}
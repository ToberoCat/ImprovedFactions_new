package io.github.toberocat.improvedfactions.functions

import io.github.toberocat.guiengine.context.GuiContext
import io.github.toberocat.guiengine.function.GuiComputeFunction
import io.github.toberocat.improvedfactions.user.factionUser
import org.jetbrains.exposed.sql.transactions.transaction


class FactionPermissionFunction : GuiComputeFunction {
    private val prefix = "has:faction.permission."

    override fun checkForFunction(value: String): Boolean = value.startsWith(prefix)
    override fun compute(context: GuiContext, value: String): String {
        val viewer = context.viewer() ?: return "$prefix$value"
        return transaction {
            viewer.factionUser()
                .hasPermission(value.replace(prefix, "")).toString()
        }
    }
}
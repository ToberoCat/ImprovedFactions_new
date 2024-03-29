package io.github.toberocat.improvedfactions.commands

import io.github.toberocat.improvedfactions.ImprovedFactionsPlugin
import io.github.toberocat.improvedfactions.commands.admin.ReloadCommand
import io.github.toberocat.improvedfactions.commands.admin.ZoneCommandRoute
import io.github.toberocat.improvedfactions.commands.claim.ClaimCommand
import io.github.toberocat.improvedfactions.commands.claim.UnclaimCommand
import io.github.toberocat.improvedfactions.commands.invite.InviteAcceptCommand
import io.github.toberocat.improvedfactions.commands.invite.InviteCommand
import io.github.toberocat.improvedfactions.commands.invite.InviteDiscardCommand
import io.github.toberocat.improvedfactions.commands.invite.ListInvitesCommand
import io.github.toberocat.improvedfactions.commands.manage.CreateCommand
import io.github.toberocat.improvedfactions.commands.manage.DeleteCommand
import io.github.toberocat.improvedfactions.commands.manage.IconCommand
import io.github.toberocat.improvedfactions.commands.manage.RenameCommand
import io.github.toberocat.improvedfactions.commands.member.*
import io.github.toberocat.improvedfactions.commands.rank.RankCommandRoute
import io.github.toberocat.improvedfactions.translation.sendLocalized
import io.github.toberocat.toberocore.command.CommandExecutor
import org.bukkit.entity.Player

/**
 * Created: 04.08.2023
 * @author Tobias Madlberger (Tobias)
 */
class FactionCommandExecutor(plugin: ImprovedFactionsPlugin) {
    init {
        val executor = CommandExecutor.createExecutor("factions")
        executor.setSendException { sender, exception ->
            if (sender !is Player)
                sender.sendMessage(exception.message)
            else
                exception.message?.let { sender.sendLocalized(it, exception.placeholders) }
        }
        executor.addChild(InfoCommand(plugin))

        executor.addChild(CreateCommand(plugin))
        executor.addChild(DeleteCommand(plugin))
        executor.addChild(IconCommand(plugin))
        executor.addChild(RenameCommand(plugin))
        executor.addChild(RankCommandRoute(plugin))

        executor.addChild(JoinCommand(plugin))
        executor.addChild(LeaveCommand(plugin))
        executor.addChild(KickCommand(plugin))
        executor.addChild(BanCommand(plugin))
        executor.addChild(UnBanCommand(plugin))
        executor.addChild(MembersCommand(plugin))
        executor.addChild(TransferOwnershipCommand(plugin))

        executor.addChild(ClaimCommand(plugin))
        executor.addChild(UnclaimCommand(plugin))

        executor.addChild(InviteCommand(plugin))
        executor.addChild(ListInvitesCommand(plugin))
        executor.addChild(InviteAcceptCommand(plugin))
        executor.addChild(InviteDiscardCommand(plugin))

        executor.addChild(ReloadCommand(plugin))
        executor.addChild(ZoneCommandRoute(plugin))

        plugin.addModuleCommands(executor)

        executor.addChild(HelpCommand(plugin, executor))
    }
}
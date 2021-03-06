package io.github.toberocat.core.utility.messages;

import io.github.toberocat.core.utility.data.access.AbstractAccess;
import io.github.toberocat.core.utility.data.database.DatabaseAccess;
import io.github.toberocat.core.utility.data.database.sql.MySqlDatabase;
import io.github.toberocat.core.utility.data.database.sql.builder.Select;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

// ToDo: Make message system useful
public class MessageSystem implements Listener {

    public MessageSystem() {
    }

    public static void dispose() {

    }

    public static void sendMessage(@NotNull OfflinePlayer player,
                                   @NotNull String message) {
        Player on = player.getPlayer();
        if (on != null) on.sendMessage(message);
        else writeToStorage(player.getUniqueId(), message);

    }

    private static void writeToStorage(@NotNull UUID id, @NotNull String message) {
        if (AbstractAccess.isAccess(DatabaseAccess.class)) {
            MySqlDatabase database = DatabaseAccess.accessPipeline(DatabaseAccess.class)
                    .database();

            database.evalTry("INSERT INTO messages VALUE (%s, %s)", id, message)
                    .get(PreparedStatement::executeUpdate);

        }

    }

    private static boolean playerInStorageDb(@NotNull MySqlDatabase database,
                                             @NotNull UUID id) {
        return database.rowSelect(new Select()
                        .setTable("messages")
                        .setColumns("content")
                        .setFilter("player = %s", id))
                .getRows().size() > 0;
    }

    private static Stream<String> messagesInStorageDb(@NotNull MySqlDatabase database,
                                                      @NotNull UUID id) {
        return database.rowSelect(new Select()
                        .setTable("messages")
                        .setColumns("content")
                        .setFilter("player = %s", id))
                .getRows()
                .stream()
                .map(x -> x.get("content").toString());
    }

    @EventHandler(priority = EventPriority.LOW)
    private void join(PlayerJoinEvent event) {

    }

    @EventHandler(priority = EventPriority.LOW)
    private void leave(PlayerQuitEvent event) {

    }
}

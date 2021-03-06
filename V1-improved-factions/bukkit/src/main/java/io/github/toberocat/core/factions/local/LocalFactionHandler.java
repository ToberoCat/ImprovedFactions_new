package io.github.toberocat.core.factions.local;

import io.github.toberocat.MainIF;
import io.github.toberocat.core.factions.Faction;
import io.github.toberocat.core.factions.components.rank.Rank;
import io.github.toberocat.core.factions.handler.FactionHandler;
import io.github.toberocat.core.factions.handler.FactionHandlerInterface;
import io.github.toberocat.core.utility.async.AsyncTask;
import io.github.toberocat.core.utility.config.DataManager;
import io.github.toberocat.core.utility.data.Table;
import io.github.toberocat.core.utility.data.access.FileAccess;
import io.github.toberocat.core.utility.events.faction.FactionLoadEvent;
import io.github.toberocat.core.utility.exceptions.faction.FactionNotInStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

public class LocalFactionHandler implements FactionHandlerInterface<LocalFaction> {
    private final Map<String, LocalFaction> factions;
    private final FileAccess access;

    public LocalFactionHandler() {
        this.factions = new HashMap<>();
        access = FileAccess.accessPipeline(FileAccess.class);
    }

    @Override
    public @NotNull LocalFaction create(@NotNull String display, @NotNull Player owner) {
        LocalFaction faction = new LocalFaction(display, owner);
        factions.put(faction.getRegistry(), faction);

        return faction;
    }

    @Override
    public @NotNull LocalFaction load(@NotNull String registry) throws FactionNotInStorage {
        if (!access.has(Table.FACTIONS, registry)) throw
                new FactionNotInStorage(registry, FactionNotInStorage.StorageType.LOCAL_FILE);

        return access.read(Table.FACTIONS, registry);
    }

    @Override
    public boolean isLoaded(@NotNull String registry) {
        return factions.containsKey(registry);
    }

    @Override
    public boolean exists(@NotNull String registry) {
        return access.has(Table.FACTIONS, registry);
    }

    @Override
    public @NotNull Map<String, LocalFaction> getLoadedFactions() {
        return factions;
    }

    @Override
    public void deleteCache(@NotNull String registry) {
        factions.remove(registry);
    }

    @Override
    public @NotNull Rank getSavedRank(@NotNull OfflinePlayer player) {
        return null;
    }

    public static void migrateBeta() {
        DataManager factions = new DataManager(MainIF.getIF(), "Data/factions.yml");

        if (factions.getConfig().getConfigurationSection("f") == null) return;
        for (String key : factions.getConfig().getConfigurationSection("f").getKeys(false)) {
            String displayName = ChatColor.translateAlternateColorCodes('&', factions.getConfig().getString("f." + key + ".displayName"));

            String owner = factions.getConfig().getString("f." + key + ".owner");
            if (owner == null) {
                MainIF.logMessage(Level.WARNING, "Couldn't read owner from " + key + ". Faction couldn't get loaded, due too this error. Please fix it int he original file to be able to load it into this version");
                continue;
            }

            UUID ownerUUID = UUID.fromString(owner);

            LocalFaction faction = new LocalFaction(displayName, key, ownerUUID, LocalFaction.OpenType.INVITE_ONLY);

            // Adding players
            List<String> raw = factions.getConfig().getStringList("f." + key + ".members");
            for (String rawMember : raw) {
                Map.Entry<UUID, String> member = getFromFactionMember(rawMember);
                if (Bukkit.getOfflinePlayer(member.getKey()).isOnline()) {
                    faction.getFactionMemberManager().join(Bukkit.getPlayer(member.getKey()));
                } else {
                    // ToDo: Add a way to set persistent data for offline players
                    faction.getFactionMemberManager().getMembers().add(member.getKey());
                    faction.getFactionPerm().getMemberRanks().put(member.getKey(), member.getValue().replace("}", ""));
                    faction.getPowerManager()
                            .increaseMax(MainIF.config().getInt("power.maxPowerPerPlayer"));
                }
            }


            List<String> rawBanned = factions.getConfig().getStringList("f." + key + ".banned");
            ArrayList<UUID> banned = new ArrayList<>();

            for (String rawBan : rawBanned) {
                try {
                    rawBan = rawBan.replace("]", "").replace("[", "");
                    banned.add(UUID.fromString(rawBan.trim()));
                } catch (IllegalArgumentException exception) {
                    MainIF.logMessage(Level.WARNING, "&cCouldn't load banned for " + key + ". ");
                }
            }

            faction.getRelationManager().getAllies().addAll(factions.getConfig().getStringList("f." + key + ".allies"));
            faction.getRelationManager().getEnemies().addAll(factions.getConfig().getStringList("f." + key + ".enemies"));

            if (factions.getConfig().contains("f." + key + ".permanent"))
                faction.setPermanent(factions.getConfig().getBoolean("f." + key + ".permanent"));
            if (factions.getConfig().contains("f." + key + ".frozen"))
                faction.setFrozen(factions.getConfig().getBoolean("f." + key + ".frozen"));


            faction.getPowerManager().setCurrentPower(factions.getConfig().getInt("f." + key + ".power"));
            faction.setClaimedChunks(factions.getConfig().getInt("f." + key + ".claimedChunks"));
            faction.getFactionMemberManager().setBanned(banned);
            faction.setMotd(factions.getConfig().getString("f." + key + ".motd"));
            faction.setDescription(Collections.singletonList(factions.getConfig().getString("f." + key + ".description")).toArray(String[]::new));

            faction.setRegistryName(key);
            faction.setDisplayName(displayName);

            AsyncTask.runLaterSync(0, () -> {
                Bukkit.getPluginManager().callEvent(new FactionLoadEvent(faction));
                FileAccess.write("Factions", key, faction);
                LocalFaction.getLoadedFactions().put(key, faction);
            });
        }

        MainIF.logMessage(Level.INFO, "You can now safely delete the file factions.yml from the Data folder, if no warnings popped up before");
    }

    private static Map.Entry<UUID, String> getFromFactionMember(String str) {
        UUID uuid = null;
        String rank = null;
        String[] parms = str.split("[,=]");
        for (int i = 0; i < parms.length; i++) {
            String parm = parms[i];
            if (parm.contains("uuid")) {
                uuid = UUID.fromString(parms[i + 1]);
            }

            if (parm.contains("rank")) {
                rank = parms[i + 1];
            }
        }

        UUID finalUuid = uuid;
        String finalRank = rank;

        assert finalUuid != null;
        assert finalRank != null;

        return Map.entry(finalUuid, finalRank);
    }
}

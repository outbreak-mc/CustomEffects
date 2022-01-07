package space.outbreak.customeffects.db;

import org.bukkit.Bukkit;
import space.outbreak.customeffects.CustomEffectEntry;
import space.outbreak.customeffects.CustomEffectsAPIPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class SQLDB implements Database {
    protected Connection connection;

    protected final CustomEffectsAPIPlugin plugin;

    public SQLDB(CustomEffectsAPIPlugin plugin) {
        this.plugin = plugin;
    }

    private Connection getConnection() {
        return connection;
    }

    private PreparedStatement prepareStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    @Override
    public abstract void init();

    @Override
    public void deinit() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public void addEffect(CustomEffectEntry entry) {
        async(() -> {
            String sql = "INSERT INTO CustomEffects" +
                    "(uuid, owner, name, durationMillis, amplifier, startTimeMillis, data," +
                    " server, world, keepAfterDeath, milkPersistent, timeless) " +
                    "VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            try {
                PreparedStatement st = prepareStatement(sql);
                st.setString(1, entry.getUniqueId().toString());
                st.setString(2, entry.getOwnerUUID().toString());
                st.setString(3, entry.getEffectName());
                st.setLong(4, entry.getDurationMillis());
                st.setInt(5, entry.getAmplifier());
                st.setLong(6, entry.getStartTimeMillis());
                st.setString(7, entry.getConfig().getDataString());
                st.setString(8, entry.getServer());
                st.setString(9, entry.getWorld());
                st.setBoolean(10, entry.isKeepAfterDeath());
                st.setBoolean(11, entry.isMilkPersistent());
                st.setBoolean(12, entry.isTimeless());
                st.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void updateEffectEntry(CustomEffectEntry entry) {
        async(() -> {
            String sql = "UPDATE CustomEffects" +
                    "SET " +
                    "uuid = ?," +
                    "owner = ?," +
                    "name = ?," +
                    "durationMillis = ?," +
                    "amplifier = ?," +
                    "startTimeMillis = ?," +
                    "data = ?," +
                    "server = ?," +
                    "world = ?," +
                    "keepAfterDeath = ? " +
                    "milkPersistent = ? " +
                    "timeless = ? " +
                    "WHERE uuid = ?;";
            try {
                PreparedStatement st = prepareStatement(sql);
                st.setString(1, entry.getUniqueId().toString());
                st.setString(11, entry.getUniqueId().toString());
                st.setString(2, entry.getOwnerUUID().toString());
                st.setString(3, entry.getEffectName());
                st.setLong(4, entry.getDurationMillis());
                st.setInt(5, entry.getAmplifier());
                st.setLong(6, entry.getStartTimeMillis());
                st.setString(7, entry.getConfig().getDataString());
                st.setString(8, entry.getServer());
                st.setString(9, entry.getWorld());
                st.setBoolean(10, entry.isKeepAfterDeath());
                st.setBoolean(11, entry.isMilkPersistent());
                st.setBoolean(12, entry.isTimeless());
                st.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void dropEffect(UUID entryUUID) {
        async(() -> {
            String sql = "DELETE FROM CustomEffects WHERE uuid=?;";
            try {
                PreparedStatement st = prepareStatement(sql);
                st.setString(1, entryUUID.toString());
                st.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void getActualPlayerEffects(UUID owner, long currentTime, String world, String server,
                                       Consumer<HashMap<UUID, CustomEffectEntry>> callback) {
        async(() -> {
            String sql = "SELECT * FROM CustomEffects " +
                    "WHERE owner=? " +
                    "AND ((startTimeMillis+durationMillis) > ? AND timeless != 1)" +
                    "AND (world = '*' OR ?) " +
                    "AND (server = '*' OR ?);";
            try {
                HashMap<UUID, CustomEffectEntry> r = new HashMap<>();
                PreparedStatement st = prepareStatement(sql);
                st.setString(1, owner.toString());
                st.setLong(2, currentTime);
                st.setString(3, world);
                st.setString(4, server);
                ResultSet result = st.executeQuery();
                while (result.next()) {
                    CustomEffectEntry e = resultset2model(result);
                    r.put(e.getUniqueId(), e);
                }
                callback.accept(r);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void getAllPlayerEffects(UUID owner, Consumer<List<CustomEffectEntry>> callback) {
        async(() -> {
            String sql = "SELECT * FROM CustomEffects WHERE owner=?;";
            try {
                List<CustomEffectEntry> r = new ArrayList<>();
                PreparedStatement st = prepareStatement(sql);
                st.setString(1, owner.toString());
                ResultSet result = st.executeQuery();
                while (result.next())
                    r.add(resultset2model(result));
                callback.accept(r);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void dropOutdated() {
        async(() -> {
            String sql = "DELETE FROM CustomEffects WHERE (startTimeMillis+durationMillis) <= ? AND timeless != 1;";
            try {
                PreparedStatement st = prepareStatement(sql);
                st.setLong(1, System.currentTimeMillis());
                st.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CustomEffectsAPIPlugin getPlugin() {
        return plugin;
    }
}

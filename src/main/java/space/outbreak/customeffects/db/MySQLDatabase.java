package space.outbreak.customeffects.db;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import space.outbreak.customeffects.CustomEffectEntry;
import space.outbreak.customeffects.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class MySQLDatabase implements Database {
    private Connection connection;

    private final String host;
    private final int port;
    private final String password;
    private final String username;
    private final String database;
    private final boolean useSSL;

    private final JavaPlugin plugin;

    public MySQLDatabase(String host, int port, String username, String password, String database, boolean useSSL, JavaPlugin plugin) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.username = username;
        this.database = database;
        this.plugin = plugin;
        this.useSSL = useSSL;
    }

    private Connection getConnection() {
        return connection;
    }

    private PreparedStatement prepareStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    @Override
    public void init() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database+"?useSSL="+useSSL, username, password);

            Statement st = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
                    " server, world, keepAfterDeath) " +
                    "VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            try {
                PreparedStatement st = prepareStatement(sql);
                st.setString(1, entry.getUniqueId().toString());
                st.setString(2, entry.getOwner().getUniqueId().toString());
                st.setString(3, entry.getEffect().getName());
                st.setLong(4, entry.getDurationMillis());
                st.setInt(5, entry.getAmplifier());
                st.setLong(6, entry.getStartTimeMillis());
                st.setString(7, entry.getDataLine());
                st.setString(8, entry.getServer());
                st.setString(9, entry.getWorld());
                st.setBoolean(10, entry.isKeepAfterDeath());
                st.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void dropEffect(CustomEffectEntry entry) {
        async(() -> {
            String sql = "DELETE FROM CustomEffects WHERE uuid=?;";
            try {
                PreparedStatement st = prepareStatement(sql);
                st.setString(1, entry.getUniqueId().toString());
                st.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void clearEffects(UUID owner) {
        async(() -> {
            String sql = "DELETE FROM CustomEffects WHERE owner=?;";
            try {
                PreparedStatement st = prepareStatement(sql);
                st.setString(1, owner.toString());
                st.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void getPlayerEffects(UUID owner, Consumer<List<CustomEffectsDBModel>> callback) {
        async(() -> {
            String sql = "SELECT * FROM CustomEffects WHERE owner=? AND (startTimeMillis+durationMillis) <= ?";
            try {
                List<CustomEffectsDBModel> r = new ArrayList<>();
                PreparedStatement st = prepareStatement(sql);
                st.setString(1, owner.toString());
                st.setLong(2, System.currentTimeMillis());
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
            String sql = "DELETE FROM CustomEffects WHERE (startTimeMillis+durationMillis) <= ?;";
            try {
                PreparedStatement st = prepareStatement(sql);
                st.setLong(1, System.currentTimeMillis());
                st.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
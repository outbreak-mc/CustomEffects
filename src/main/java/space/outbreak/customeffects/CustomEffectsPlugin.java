package space.outbreak.customeffects;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import space.outbreak.customeffects.db.SQLiteDatabase;
import space.outbreak.customeffects.util.config.ConfigAdapter;
import space.outbreak.customeffects.util.config.ConfigMap;
import space.outbreak.customeffects.db.MySQLDatabase;

public final class CustomEffectsPlugin extends JavaPlugin implements Listener {
    private CustomEffectsAPI api;
    private Database db;
    private int dropOutdatedIf5 = 0;

    @Override
    public void onEnable() {
        reload();

        api = new CustomEffectsAPI(this);
        getServer().getPluginManager().registerEvents(this, this);
        api.registerEffect(new SimplePotionEffect());
        getCommand("customeffects").setExecutor(new CustomEffectsCommand(this));
    }

    public void reload() {
        saveDefaultConfig();
        reloadConfig();

        if (db != null)
            db.deinit();

        ConfigAdapter config = new ConfigAdapter(this.getConfig());

        if (config.getBoolean(ConfigMap.USE_MYSQL)) {
            db = new MySQLDatabase(
                    config.getString(ConfigMap.MYSQL_HOST),
                    config.getInt(ConfigMap.MYSQL_PORT),
                    config.getString(ConfigMap.MYSQL_USERNAME),
                    config.getString(ConfigMap.MYSQL_PASSWORD),
                    config.getString(ConfigMap.MYSQL_DATABASE),
                    config.getBoolean(ConfigMap.MYSQL_USE_SSL),
                    this
            );
        } else {
            db = new SQLiteDatabase(this);
        }
        db.init();
        db.dropOutdated();
    }

    Database getDatabase() {
        return db;
    }

    public CustomEffectsAPI getAPI() {
        return api;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        api.loadPlayer(event.getPlayer());
        dropOutdatedIf5++;
        if (dropOutdatedIf5 == 5) {
            getDatabase().dropOutdated();
            dropOutdatedIf5 = 0;
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        api.unloadPlayer(event.getPlayer());
    }
}

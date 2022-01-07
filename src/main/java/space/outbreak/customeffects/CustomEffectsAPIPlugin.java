package space.outbreak.customeffects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import space.outbreak.customeffects.db.Database;
import space.outbreak.customeffects.db.MySQLDatabase;
import space.outbreak.customeffects.db.SQLiteDatabase;
import space.outbreak.customeffects.effects.SimplePotionEffect;
import space.outbreak.customeffects.util.ConfigAdapter;
import space.outbreak.customeffects.util.ConfigMap;

import java.util.concurrent.Callable;


public final class CustomEffectsAPIPlugin extends JavaPlugin implements Listener {
    private Database db;
    private static CustomEffectsAPI api;
    private ConfigAdapter config;

    @Override
    public void onEnable() {
        reload();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("customeffects").setExecutor(new CustomEffectsCommand(this));

        api = new CustomEffectsAPI(this);

        api.registerCustomEffectHandler(new SimplePotionEffect());
    }

    public void reload() {
        saveDefaultConfig();
        reloadConfig();

        if (db != null)
            db.deinit();

        config = new ConfigAdapter(this.getConfig());

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

    public static CustomEffectsAPI getApi() {
        return api;
    }

    Database getDatabase() {
        return db;
    }

    /**
     * Получает из базы данных все актуальные эффекты и накладывает их, а
     * неактуальные снимает.
     *
     * causedByDeath и causedByMilk используются, чтобы переналожить или снять эффект в зависимости от его
     * параметров, если игрок умер или попил молока, в результате чего мог потерять некоторые эффекты.
     *
     * @param player - игрок
     * @param causedByDeath - перезагрузка связана со смертью
     * @param causedByMilk - перезагрузка из-за того, что игрок попил молока
     */
    private void loadPlayerEffects(Player player, boolean causedByDeath, boolean causedByMilk) {
        getDatabase().getActualPlayerEffects(player.getUniqueId(), System.currentTimeMillis(), player.getWorld().getName(), getConfig().getString(ConfigMap.SERVER_NAME.str()), entries -> {
            // Запрос возвращает только актуальные для этого мира/сервера/времени эффекты. Снимаем остальные.
            for (CustomEffectEntry entry : api.getPlayersEffects(player)) {
                if (!entries.containsKey(entry.getUniqueId()))
                    api._removeEffect(player, entry, false);
            }
            for (CustomEffectEntry entry : entries.values()) {
                Callable<Boolean> whatToDo = () -> {api._applyEffect(player, entry, false); return null; };
                // Переприменение при подозрениях на случайный сброс смертью или молоком,
                // снятие эффекта, если защиты от сброса и не предусмотрено,
                // или пропуск, если эффект уже есть, и делать ничего не надо.
                // Иначе - просто применение эффекта.
                if ((causedByMilk && entry.isMilkPersistent()) || (causedByDeath && entry.isKeepAfterDeath())) {
                    whatToDo = () -> {api._removeEffect(player, entry, false); api._applyEffect(player, entry, false); return null;};
                } else if (causedByDeath || causedByMilk) {
                    whatToDo = () -> {api._removeEffect(player, entry, true); return null; };
                } else if (api.getPlayersEffects(player).contains(entry)) {
                    continue;
                }
                Bukkit.getScheduler().callSyncMethod(this, whatToDo);
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        loadPlayerEffects(event.getPlayer(), false, false);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (CustomEffectEntry entry : api.getPlayersEffects(event.getPlayer()))
            api._removeEffect(event.getPlayer(), entry, false);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        loadPlayerEffects(event.getPlayer(), false, false);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        loadPlayerEffects(event.getEntity(), true, false);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.MILK_BUCKET) {
            loadPlayerEffects(event.getPlayer(), false, true);
        }
    }
}

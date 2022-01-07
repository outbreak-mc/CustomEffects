package space.outbreak.customeffects.db;


import space.outbreak.customeffects.CustomEffectConfig;
import space.outbreak.customeffects.CustomEffectEntry;
import space.outbreak.customeffects.CustomEffectsAPIPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface Database {
    void init();
    void deinit();

    void addEffect(CustomEffectEntry effect);
    void dropEffect(UUID entryUUID);
    void updateEffectEntry(CustomEffectEntry entry);
    void getActualPlayerEffects(UUID owner, long currentTime, String world, String server, Consumer<HashMap<UUID, CustomEffectEntry>> callback);
    void getAllPlayerEffects(UUID owner, Consumer<List<CustomEffectEntry>> callback);
    void dropOutdated();
    CustomEffectsAPIPlugin getPlugin();

    default CustomEffectEntry resultset2model(ResultSet result) throws SQLException {
        return new CustomEffectEntry(
                result.getString("name"),
                UUID.fromString(result.getString("uuid")),
                UUID.fromString(result.getString("owner")),
                result.getString("world"),
                result.getString("server"),
                result.getBoolean("keepAfterDeath"),
                result.getLong("durationMillis"),
                result.getInt("amplifier"),
                result.getLong("startTimeMillis"),
                new CustomEffectConfig(result.getString("data")),
                result.getBoolean("milkPersistent"),
                result.getBoolean("timeless"),
                getPlugin()
        );
    }
}

package space.outbreak.customeffects;


import space.outbreak.customeffects.db.CustomEffectsDBModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface Database {
    void init();
    void deinit();

    void addEffect(CustomEffectEntry effect);
    void dropEffect(CustomEffectEntry effect);
    void clearEffects(UUID owner);
    void getPlayerEffects(UUID uuid, Consumer<List<CustomEffectsDBModel>> callback);
    void dropOutdated();

    default CustomEffectsDBModel resultset2model(ResultSet result) throws SQLException {
        return new CustomEffectsDBModel(
                UUID.fromString(result.getString("uuid")),
                UUID.fromString(result.getString("owner")),
                result.getString("name"),
                result.getString("server"),
                result.getString("world"),
                result.getString("data").split(":"),
                result.getLong("durationMillis"),
                result.getLong("startTimeMillis"),
                result.getBoolean("keepAfterDeath"),
                result.getInt("amplifier")
        );
    }
}

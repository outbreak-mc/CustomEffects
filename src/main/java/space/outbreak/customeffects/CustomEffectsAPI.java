package space.outbreak.customeffects;

import org.bukkit.entity.Player;
import space.outbreak.customeffects.db.CustomEffectsDBModel;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public final class CustomEffectsAPI {
    private final CustomEffectsPlugin plugin;
    private static final HashMap<String, CustomEffect> effectNamesMap = new HashMap<>();
    private static final HashMap<UUID, List<CustomEffectEntry>> playerEffects = new HashMap<>();

    static CustomEffect getEffect(String name) {
        return effectNamesMap.get(name);
    }

    public void registerEffect(CustomEffect effectType) {
        effectNamesMap.put(effectType.getName(), effectType);
    }

    public static List<String> getRegisteredEffects() {
        return new ArrayList<>(effectNamesMap.keySet());
    }

    void loadPlayer(Player player) {
        playerEffects.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());
        plugin.getDatabase().getPlayerEffects(player.getUniqueId(), effectsData -> {
            for (CustomEffectsDBModel entryData : effectsData) {
                CustomEffect effect = getEffect(entryData.name);
                CustomEffectEntry entry = new CustomEffectEntry(
                        effect, entryData.uuid, player, entryData.world, entryData.server,
                        entryData.keepAfterDeath, entryData.durationMillis, entryData.amplifier,
                        entryData.startTimeMillis, entryData.args
                );
                playerEffects.get(player.getUniqueId()).add(entry);
                entry.apply();
            }
        });
    }

    void unloadPlayer(Player player) {
        List<CustomEffectEntry> effectEntries = playerEffects.get(player.getUniqueId());
        if (effectEntries == null)
            return;
        for (CustomEffectEntry entry : effectEntries)
            entry.unapply();
        playerEffects.remove(player.getUniqueId());
    }

    public CustomEffectsAPI(CustomEffectsPlugin plugin) {
        this.plugin = plugin;
    }

    /** Применяет новый эффект на игрока и сохраняет данные в базу данных. */
    public CustomEffectEntry applyEffect(Player target, String server, String world, boolean keepAfterDeath,
                                    String name, long durationMillis, int amplifier, String[] args) {
        CustomEffectEntry entry = new CustomEffectEntry(
                effectNamesMap.get(name), UUID.randomUUID(), target, world, server, keepAfterDeath, durationMillis,
                amplifier, System.currentTimeMillis(), args
        );
        entry.apply();
        plugin.getDatabase().addEffect(entry);
        return entry;
    }

    /** Снимает с игрока эффект, удаляя его из базы данных */
    public void removeEffect(CustomEffectEntry entry) {
        entry.unapply();
        plugin.getDatabase().dropEffect(entry);
    }

    /** Снимает с игрока все эффекты, удаляя их из базы данных */
    public void clearEffects(UUID uuid) {
        for (CustomEffectEntry entry : playerEffects.get(uuid)) {
            entry.unapply();
        }
        plugin.getDatabase().clearEffects(uuid);
    }
}

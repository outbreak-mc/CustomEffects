package space.outbreak.customeffects;

import org.bukkit.entity.Player;
import space.outbreak.customeffects.util.ConfigMap;

import java.util.Date;
import java.util.UUID;

/** Представляет собой эффект, наложенный на игрока. */
public final class CustomEffectEntry {
    private final UUID uuid;
    private final UUID owner;
    private final String world;
    private final String server;
    private final boolean keepAfterDeath;
    private long durationMillis;
    private int amplifier;
    private final long startTimeMillis;
    private final CustomEffectConfig config;
    private final String effectName;
    private final CustomEffectsAPIPlugin plugin;
    private final boolean milkPersistent;
    private final boolean timeless;

    boolean isChanged = false;

    public CustomEffectEntry(String effectName, UUID uuid, UUID owner, String world, String server, boolean keepAfterDeath,
                             long durationMillis, int amplifier, long startTimeMillis, CustomEffectConfig config,
                             boolean milkPersistent, boolean timeless, CustomEffectsAPIPlugin plugin) {
        this.effectName = effectName;
        this.uuid = uuid;
        this.owner = owner;
        this.world = world;
        this.server = server;
        this.keepAfterDeath = keepAfterDeath;
        this.durationMillis = durationMillis;
        this.amplifier = amplifier;
        this.startTimeMillis = startTimeMillis;
        this.config = config;
        this.plugin = plugin;
        this.milkPersistent = milkPersistent;
        this.timeless = timeless;
    }

    public boolean isTimeless() {
        return timeless;
    }

    public void setEndTime(Date date) {
        this.durationMillis = date.getTime()-System.currentTimeMillis();
        isChanged = true;
    }

    public void setDurationMillis(long durationMillis) {
        this.durationMillis = durationMillis;
        isChanged = true;
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
        isChanged = true;
    }

    /** Название эффекта */
    public String getEffectName() {
        return effectName;
    }

    /** Сохраняется ли эффект после смерти игрока */
    public boolean isKeepAfterDeath() {
        return keepAfterDeath;
    }

    /** Сохраняется ли эффект, если игрок выпил молоко */
    public boolean isMilkPersistent() {
        return milkPersistent;
    }

    /** Уровень эффекта */
    public int getAmplifier() {
        return amplifier;
    }

    /** Миллисекунды до окончания действия */
    public long getDurationMillis() {
        return durationMillis;
    }

    /** Миллисекунды до окончания действия */
    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    /** UUID игрока, на которого наложен эффект */
    public UUID getOwnerUUID() {
        return owner;
    }

    /** Параметры эффекта (свои для каждого объекта) */
    public CustomEffectConfig getConfig() {
        return config;
    }

    /** Название сервера, на котором действует эффект.
     * `*` - все сервера. */
    public String getServer() {
        return server;
    }

    /** Название мира, в котором действует эффект.
     * `*` - все миры. */
    public String getWorld() {
        return world;
    }

    /** Возвращает уникальный UUID, который присваивается каждому накладываемому эффекту */
    public UUID getUniqueId() {
        return uuid;
    }

    public long getCurrentMillis() {
        return System.currentTimeMillis() - startTimeMillis;
    }

    public long getRemainingMillis() {
        return durationMillis - getCurrentMillis();
    }

    /** Возвращает оставшееся время в тиках */
    public int getRemainingTicks() {
        return (int)(getRemainingMillis() / 50L);
    }

    public double getRemainingSeconds() { return ((double)getRemainingMillis()/1000.0d); };

    public boolean fitsWorldAndCurrentServer(Player player) {
        return (getWorld().equals("*") || player.getWorld().getName().equalsIgnoreCase(getWorld()))
                && (getServer().equals("*") || getServer().equalsIgnoreCase(plugin.getConfig().getString(ConfigMap.SERVER_NAME.str())));
    }

    /** Проверяет, закончилось ли время у эффекта */
    public boolean isExpired() {
        return (!timeless && getRemainingMillis() <= 0) || durationMillis <= 0;
    }

    /** Сохраняет текущий конфиг эффекта в базу данных */
    public void saveConfig() {
        plugin.getDatabase().updateEffectEntry(this);
    }
}

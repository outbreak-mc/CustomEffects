package space.outbreak.customeffects;

import org.bukkit.entity.Player;
import space.outbreak.customeffects.errors.EffectDataParsingError;
import space.outbreak.customeffects.errors.InvalidEffectData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Представляет собой эффект, наложенный на игрока.
 * Хранит все данные об эффекте и сам объект эффекта.
 * */
public class CustomEffectEntry {
    private UUID uuid;
    private Player owner;
    String world;
    String server;
    boolean keepAfterDeath;
    long durationMillis;
    int amplifier;
    long startTimeMillis;
    String[] args;
    private CustomEffect effect;

    public CustomEffectEntry(CustomEffect effect, UUID uuid, Player owner, String world, String server, boolean keepAfterDeath,
                             long durationMillis, int amplifier, long startTimeMillis, String[] args) {
        this.effect = effect;
        this.uuid = uuid;
        this.owner = owner;
        this.world = world;
        this.server = server;
        this.keepAfterDeath = keepAfterDeath;
        this.durationMillis = durationMillis;
        this.amplifier = amplifier;
        this.startTimeMillis = startTimeMillis;
        this.args = args;
    }

    public CustomEffect getEffect() {
        return effect;
    }

    public boolean isKeepAfterDeath() {
        return keepAfterDeath;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public Player getOwner() {
        return owner;
    }

    public String[] getArgs() {
        return args;
    }

    public String getDataLine() {
        return String.join(":", args);
    }

    public String getServer() {
        return server;
    }

    public String getWorld() {
        return world;
    }

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

    /** Применяет эффект */
    void apply() {
        this.effect.apply(this);
    }

    /** Снимает применённый эффект */
    void unapply() {
        this.effect.unapply(this);
    }
}

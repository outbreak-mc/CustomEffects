package space.outbreak.customeffects.db;

import java.util.UUID;

public class CustomEffectsDBModel {
    public UUID uuid;
    public UUID owner;
    public String name;
    public String server;
    public String world;
    public String[] args;
    public long durationMillis;
    public long startTimeMillis;
    public boolean keepAfterDeath;
    public int amplifier;

    public CustomEffectsDBModel(UUID uuid, UUID owner, String name, String server, String world, String[] args,
                                long durationMillis, long startTimeMillis, boolean keepAfterDeath, int amplifier) {
        this.uuid = uuid;
        this.owner = owner;
        this.name = name;
        this.world = world;
        this.server = server;
        this.args = args;
        this.durationMillis = durationMillis;
        this.startTimeMillis = startTimeMillis;
        this.keepAfterDeath = keepAfterDeath;
        this.amplifier = amplifier;
    }
}

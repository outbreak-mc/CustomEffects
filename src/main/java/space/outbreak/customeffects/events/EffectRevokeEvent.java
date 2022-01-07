package space.outbreak.customeffects.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import space.outbreak.customeffects.CustomEffectEntry;

/** Срабатывает, когда с игрока снимается эффект */
public class EffectRevokeEvent extends Event {
    CustomEffectEntry effectEntry;
    boolean pause;
    Player player;

    private static final HandlerList handlers = new HandlerList();

    public EffectRevokeEvent(CustomEffectEntry entry, Player player, boolean pause) {
        this.effectEntry = entry;
        this.player = player;
        this.pause = pause;
    }

    public Player getPlayer() {
        return player;
    }

    /** Является ли это полным снятием эффекта, или же просто паузой перед выходом игрока */
    public boolean isPause() {
        return pause;
    }

    public CustomEffectEntry getEffectEntry() {
        return effectEntry;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}


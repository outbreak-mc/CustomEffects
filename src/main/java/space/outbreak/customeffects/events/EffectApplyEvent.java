package space.outbreak.customeffects.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import space.outbreak.customeffects.CustomEffectEntry;

/** Срабатывает, когда на игрока накладывается эффект */
public class EffectApplyEvent extends Event {
    CustomEffectEntry effectEntry;
    Player player;

    private static final HandlerList handlers = new HandlerList();

    public EffectApplyEvent(CustomEffectEntry entry, Player player) {
        this.effectEntry = entry;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public CustomEffectEntry getEffectEntry() {
        return effectEntry;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}


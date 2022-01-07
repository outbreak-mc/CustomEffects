package space.outbreak.customeffects;

import org.bukkit.entity.Player;

public interface CustomEffectHandler {
    String getName();

    long getReactivatePeriod();

    void activate(CustomEffectEntry entry, Player player);
    void initialize(CustomEffectEntry entry, Player player, boolean isNew);
    void revoke(CustomEffectEntry entry, Player player, boolean isPause);
}

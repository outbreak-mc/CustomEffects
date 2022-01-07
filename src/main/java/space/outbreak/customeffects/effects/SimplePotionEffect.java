package space.outbreak.customeffects.effects;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import space.outbreak.customeffects.CustomEffectConfig;
import space.outbreak.customeffects.CustomEffectEntry;
import space.outbreak.customeffects.CustomEffectHandler;
import space.outbreak.customeffects.util.IConfigKeyMap;

public class SimplePotionEffect implements CustomEffectHandler {
    public enum ConfigMap implements IConfigKeyMap {
        EFFECT_NAME("effect-name"),
        AMBIENT("ambient"),
        PARTICLES("particles"),
        ICON("icon");

        private final String k;

        ConfigMap(String k) {
            this.k = k;
        }

        @Override
        public String str() {
            return k;
        }
    }

    @Override
    public String getName() {
        return "SIMPLE_POTION_EFFECT";
    }

    @Override
    public long getReactivatePeriod() {
        return 150;
    }

    @Override
    public void activate(CustomEffectEntry entry, Player player) {
        System.out.println("Reactivation!");
        CustomEffectConfig config = entry.getConfig();
        String effect_name = config.getString(ConfigMap.EFFECT_NAME, "speed");
        PotionEffectType effectType = PotionEffectType.getByName(effect_name);

        player.removePotionEffect(effectType);
        player.addPotionEffect(
                new PotionEffect(effectType,
                        310,
                        entry.getAmplifier(),
                        config.getBoolean(ConfigMap.AMBIENT, false),
                        config.getBoolean(ConfigMap.PARTICLES, false),
                        config.getBoolean(ConfigMap.ICON, true))
        );
    }

    @Override
    public void initialize(CustomEffectEntry entry, Player player, boolean isNew) {}

    @Override
    public void revoke(CustomEffectEntry entry, Player player, boolean isPause) {
        String effect_name = entry.getConfig().getString(ConfigMap.EFFECT_NAME, "speed");
        player.removePotionEffect(PotionEffectType.getByName(effect_name));
    }
}

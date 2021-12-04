package space.outbreak.customeffects;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import space.outbreak.customeffects.errors.EffectDataParsingError;

public class SimplePotionEffect implements CustomEffect {
    @Override
    public String getName() {
        return "SIMPLE_POTION_EFFECT";
    }

    @Override
    public void apply(CustomEffectEntry entry) {
        String[] args = entry.getArgs();
        String effect_name = "";
        boolean ambient = false;
        boolean particles = true;
        boolean icon = true;

        try {
            effect_name = args[0];
            if (args.length > 1)
                ambient = Integer.parseInt(args[1]) != 0;
            if (args.length > 2)
                particles = Integer.parseInt(args[2]) != 0;
            if (args.length > 3)
                icon = Integer.parseInt(args[3]) != 0;
        } catch (NumberFormatException | IndexOutOfBoundsException e){
            throw new EffectDataParsingError("Wrong effect data: '" + entry.getDataLine() + "' for effect '"+effect_name+"'!");
        }

        entry.getOwner().addPotionEffect(new PotionEffect(PotionEffectType.getByName(effect_name),
                        entry.getRemainingTicks(), entry.getAmplifier(),
                        ambient, particles, icon));
    }

    @Override
    public void unapply(CustomEffectEntry entry) {
        String effect_name = entry.getArgs()[0];
        entry.getOwner().removePotionEffect(PotionEffectType.getByName(effect_name));
    }
}

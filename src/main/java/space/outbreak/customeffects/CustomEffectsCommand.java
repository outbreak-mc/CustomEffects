package space.outbreak.customeffects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import space.outbreak.customeffects.util.CommandArgsParser;

import java.util.ArrayList;
import java.util.Arrays;

public class CustomEffectsCommand extends CommandArgsParser {
    private final CustomEffectsPlugin plugin;

    public CustomEffectsCommand(CustomEffectsPlugin plugin) {
        this.plugin = plugin;

        addPattern("outbreak.customeffects.admin",
                "give <username> <effect> <?seconds> <?amplifier> <?keep> <?server> <?world>"
        )
                .addAction(useCase -> {
                        CustomEffectsAPI api = plugin.getAPI();

                        Player target = Bukkit.getPlayer(useCase.getArgVal("<username>"));
                        String[] effect_data = useCase.getArgVal("<effect>").split(":");
                        String effect_name = effect_data[0];
                        String[] args = Arrays.copyOfRange(effect_data, 1, effect_data.length);
                        Integer seconds = Integer.parseInt(useCase.getArgVal("<?seconds>"));
                        Integer amplifier = Integer.parseInt(useCase.getArgVal("<?amplifier>"));
                        boolean keep = useCase.getArgVal("<?keep>").equals("1");
                        String server = useCase.getArgVal("<?server>");
                        String world = useCase.getArgVal("<?world>");

                        api.applyEffect(target, server, world, keep, effect_name, seconds*1000,
                                        amplifier, args);
                    }
                )
                .addOptionsProvider("<effect>", (sender) -> CustomEffectsAPI.getRegisteredEffects())
        ;
    }
}

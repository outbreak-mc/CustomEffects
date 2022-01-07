package space.outbreak.customeffects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import space.outbreak.customeffects.events.EffectApplyEvent;
import space.outbreak.customeffects.events.EffectRevokeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class CustomEffectsAPI {
    private CustomEffectsAPIPlugin plugin;
    private static final HashMap<Player, HashMap<UUID, CustomEffectEntry>> playerEffects = new HashMap<>();
    private static final HashMap<String, CustomEffectHandler> handlers = new HashMap<>();
    private static HashMap<CustomEffectEntry, BukkitTask> tasks = new HashMap<>();

    public CustomEffectsAPI(CustomEffectsAPIPlugin plugin) {
        this.plugin = plugin;
    }

    public List<CustomEffectEntry> getPlayersEffects(Player player) {
        if (playerEffects.get(player) != null)
            return new ArrayList<>(playerEffects.get(player).values());
        return new ArrayList<>();
    }

    /**
     * Применяет новый эффект на игрока и сохраняет его в базу данных.
     *
     * @param player - на кого накладывать эффект
     * @param server - название сервера, на котором действует эффект. * - для всех серверов.
     * @param world - название мира, в котором действует эффект. * - для всех миров
     * @param keepAfterDeath - true = переприменять эффект после смерти воизбежании потерь. false - удалить эффект после смерти.
     * @param name - название эффекта
     * @param durationMillis - длительность в миллисекундах
     * @param amplifier - уровень
     * @param milkPersistent - true = переприменять эффект, если игрок попил молока. false - снять эффект, если игрок попил молока.
     * @param timeless - для эффекта не будет измеряться время. Он будет продолжаться бесконечно, пока его
     *                 внутренняя логика не установит 0 в качестве значения длительности (setDurationMillis)
     * @param config - конфиг эффекта. Ожидается, что эффекты знают свои стандартные значения, так что
     *               можно передавать просто new CustomEffectConfig()
     * @return Объект CustomEffectEntry наложенного на игрока эффекта.
     */
    public CustomEffectEntry applyNewEffect(Player player, String server, String world, boolean keepAfterDeath,
                                            String name, long durationMillis, int amplifier,
                                            boolean milkPersistent, boolean timeless, CustomEffectConfig config) {
        CustomEffectEntry entry = new CustomEffectEntry(name, UUID.randomUUID(), player.getUniqueId(), world, server,
                keepAfterDeath, durationMillis, amplifier, System.currentTimeMillis(), config, milkPersistent,
                timeless, plugin
        );
        _applyEffect(player, entry, true);
        return entry;
    }

    /**
     * Используется после вызовов эвентов. Обновляет данные в базе или, если
     * нужно, снимает эффект, если объект был изменён.
     * */
    void _saveEntryIfChanged(Player player, CustomEffectEntry entry) {
        if (entry.isChanged || entry.getConfig().isChanged) {
            if (entry.isExpired()) {
                _removeEffect(player, entry, true);
                return;
            }
            plugin.getDatabase().updateEffectEntry(entry);
            entry.isChanged = false;
            entry.getConfig().isChanged = false;
        }
    }

    void _callApplyEvent(Player player, CustomEffectEntry entry) {
        Bukkit.getPluginManager().callEvent(new EffectApplyEvent(entry, player));
        _saveEntryIfChanged(player, entry);
    }

    void _callRevokeEvent(Player player, CustomEffectEntry entry, boolean pause) {
        Bukkit.getPluginManager().callEvent(new EffectRevokeEvent(entry, player, pause));
        _saveEntryIfChanged(player, entry);
    }

    /**
     * Вся необходимая логика для применения эффекта - сохранение в БД, вызов эвентов, сохранение
     * результатов изменений после эвентов и т.д.
     *
     * @param player - игрок, на которого накладывается эффект
     * @param entry - объект эффекта
     * @param allNew - является ли эффект полностью новым, или мы возобновляем действие существующего.
     *               Если эффект новый, он будет сохранён в базу данных, и `initialize()` будет вызван.
     */
    void _applyEffect(Player player, CustomEffectEntry entry, boolean allNew) {
        playerEffects.computeIfAbsent(player, k -> new HashMap<>());
        playerEffects.get(player).put(entry.getUniqueId(), entry);

        plugin.getLogger().info(player.getName() + " получает эффект " + entry.getEffectName() + " (" + entry.getRemainingMillis() + " мс).");

        if (allNew)
             plugin.getDatabase().addEffect(entry);

        // Ставим таймер окончания эффекта
        if (!entry.isTimeless())
            Bukkit.getScheduler().runTaskLater(plugin, () -> _removeEffect(player, entry, true), entry.getRemainingTicks());

        CustomEffectHandler handler = handlers.get(entry.getEffectName());

        handler.initialize(entry, player, allNew);
        _saveEntryIfChanged(player, entry);

        if (handler.getReactivatePeriod() <= 0) {
            handler.activate(entry, player);
            _saveEntryIfChanged(player, entry);
        } else {
            tasks.put(entry, Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (!playerEffects.containsKey(player) || !playerEffects.get(player).containsKey(entry.getUniqueId())) {
                    tasks.get(entry).cancel();
                    return;
                }
                handler.activate(entry, player);
                _saveEntryIfChanged(player, entry);
            }, 0L, handler.getReactivatePeriod()));
        }

        _callApplyEvent(player, entry);
    }

    void _removeEffect(Player target, CustomEffectEntry entry, boolean removeFromDb) {
        playerEffects.get(target).remove(entry.getUniqueId());
        if (removeFromDb)
            plugin.getDatabase().dropEffect(entry.getUniqueId());
        _callRevokeEvent(target, entry, removeFromDb);
        handlers.get(entry.getEffectName()).revoke(entry, target, !removeFromDb);
        if (tasks.containsKey(entry)) {
            tasks.get(entry).cancel();
            tasks.remove(entry);
        }
        target.sendMessage("Снимаем с вас эффект " + entry.getEffectName());
    }

    /** Снимает с игрока эффект, удаляя его из базы данных */
    public void removeEffect(CustomEffectEntry entry, Player player) {
        _removeEffect(player, entry, true);
    }

    /** Снимает с игрока все эффекты, удаляя их из базы данных */
    public void clearEffects(Player player) {
        for (CustomEffectEntry entry : getPlayersEffects(player))
            _removeEffect(player, entry, true);
    }

    /**
     * Регистрирует кастомный эффект.
     *
     * Повторная регистрация одинакового эффекта приведёт к RuntimeException.
     * */
    public void registerCustomEffectHandler(CustomEffectHandler handler) {
        if (handlers.get(handler.getName()) != null)
            throw new RuntimeException("Handler already exists");
        handlers.put(handler.getName(), handler);
    }
}

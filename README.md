# CustomEffectsAPI

Плагин предоставляет API для удобной реализации кастомных эффектов с собственной логикой.

### Проверка установленного CustomEffectsAPI
```
        Plugin customEffectsAPI = getServer().getPluginManager().getPlugin("CustomEffectsAPI");
        if (customEffectsAPI == null) {
            getLogger().severe("CustomEffectsAPI is not installed!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
```

### Использование API
```
    
    CustomEffectsAPI api = CustomEffectsAPIPlugin.getApi();

    // Получить список действующих эффектов игрока
    api.getPlayersEffects()
    
    // Наложить новый эффект:
    api.applyNewEffect()
    
    // Снять существующий эффект:
    api.removeEffect()
    
    // Снять все эффекты с игрока:
    api.clearEffects()
    
    // Зарегистрировать свой эффект (подробнее ниже)
    api.registerCustomEffectHandler()
```

### Создание эффекта

```java
// Для создания эффекта нужно создать класс, реализующих интерфейс CustomEffectHandler.
// Этот класс выступает лишь приёмником эвентов, экземпляр создаётся только один.
public class SimplePotionEffect implements CustomEffectHandler {
    // Избавление от магических строк - карта конфига
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

    // Имя эффекта. Не должно повторяться для разных эффектов.
    @Override
    public String getName() {
        return "SIMPLE_POTION_EFFECT";
    }
    
    // Период для таймера в тиках. Если больше 0, то метод activate будет вызываться 
    // таймером снова и снова, пока эффект не закончится.
    @Override
    public long getReactivatePeriod() {
        return 200;
    }
    
    // То, что происходит при активации.
    @Override
    public void activate(CustomEffectEntry entry, Player player) {
        // Здесь entry - объект с данными эффекта, такими, как длительность, 
        // уровень, название эффекта, сервер, мир и т.д.
        // Если данные объекта entry были изменены в этом методе, или в initialize, 
        // они автоматически сохранятся в базу данных.
        
        // player - игрок, на которого наложен эффект.
        
        // Эффекты имеют конфиг, похожий на тот, что имеют плагины, но с
        // поддержкой получения значений по ключам IConfigKeyMap.
        // Сохранение конфига также автоматическое.
        CustomEffectConfig config = entry.getConfig();

        // Если нужно резко завершить эффект, следует просто установить ему время на 0
        // entry.setDurationMillis(0);

        // В данном примере на игрока накладывается стандартный эффект. Время стандартных эффектов не идёт, пока игрок
        // не в сети, и они могут оставаться, когда действие эффекта должно уже закончиться.
        // Плагин вызывает метод деактивации при окончании эффекта только когда игрок онлайн или перед выходом,
        // так что нужно, чтобы эффекты не задерживались. Выдаём стандартный эффект на 16 секунд каждые 10 секунд.
        // (дополнительные 6 секунд нужны, чтобы избежать анимации моргания)

        String effect_name = config.getString(ConfigMap.EFFECT_NAME, "speed");
        PotionEffectType effectType = PotionEffectType.getByName(effect_name);

        player.removePotionEffect(effectType);
        player.addPotionEffect(
                new PotionEffect(effectType,
                        320,
                        entry.getAmplifier(),
                        config.getBoolean(ConfigMap.AMBIENT, false),
                        config.getBoolean(ConfigMap.PARTICLES, false),
                        config.getBoolean(ConfigMap.ICON, true))
        );
    }

    // Вызывается однократно при наложении или переналожении эффекта. Далее
    // срабатывает activate, и, если задано время, повторяется таймером.
    @Override
    public void initialize(CustomEffectEntry entry, Player player, boolean isNew) {}

    // Вызывается, когда заканчивается действие эффекта, игрок выходит с сервера или
    // переходит в мир, где этот эффект не действует.
    // isPause будет false только если это окончательное завершение действия эффекта.
    @Override
    public void revoke(CustomEffectEntry entry, Player player, boolean isPause) {
        // Снимаем с игрока эффект
        String effect_name = entry.getConfig().getString(ConfigMap.EFFECT_NAME, "speed");
        player.removePotionEffect(PotionEffectType.getByName(effect_name));
    }
}
```

#### Созданный эффект необходимо 
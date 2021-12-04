package space.outbreak.customeffects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public enum EffectType {
    POTION_EFFECT(SimplePotionEffect.class);

    private final Class<? extends CustomEffect> effectClass;

    EffectType(Class<? extends CustomEffect> effectClass) {
        this.effectClass = effectClass;
    }

    public CustomEffect init(String[] params) {
        try {
            Constructor<?> ctor = effectClass.getConstructor(ArrayList.class);
            return (CustomEffect) ctor.newInstance(new ArrayList<>(List.of(params)));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Неправильно создан класс эффекта "+this);
    }
}

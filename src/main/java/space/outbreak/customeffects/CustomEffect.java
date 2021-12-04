package space.outbreak.customeffects;

import org.bukkit.entity.Player;
import space.outbreak.customeffects.errors.EffectDataParsingError;
import space.outbreak.customeffects.errors.InvalidEffectData;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public interface CustomEffect {

    String getName();
    void apply(CustomEffectEntry entry);
    void unapply(CustomEffectEntry entry);



//    /** Позволяет создать полностью новый объект эффекта, который потом нужно будет сохранить в базу данных */
//    public  static CustomEffect createNew(Class<? extends CustomEffect> clazz, String server, String world,
//                                       Boolean keepAfterDeath, String name, Long durationMillis,
//                                       Integer amplifier, Long startTimeMillis, String data) {
//        try {
//            Constructor<?> constructor = clazz.getConstructor(
//                    UUID.class,
//                    server.getClass(),
//                    world.getClass(),
//                    keepAfterDeath.getClass(),
//                    name.getClass(),
//                    durationMillis.getClass(),
//                    amplifier.getClass(),
//                    startTimeMillis.getClass(),
//                    data.getClass()
//            );
//            return (CustomEffect) constructor.newInstance(
//                    UUID.randomUUID(), server, world, keepAfterDeath, name, durationMillis, amplifier, startTimeMillis, data
//            );
//        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /** Позволяет получить объект, если эффект до этого уже существовал и известен его UUID */
//    public static CustomEffect get(Class<? extends CustomEffect> clazz, UUID uuid, String server, String world,
//                                       Boolean keepAfterDeath, String name, Long durationMillis,
//                                       Integer amplifier, Long startTimeMillis, String data) {
//        try {
//            Constructor<?> constructor = clazz.getConstructor(
//                    uuid.getClass(),
//                    server.getClass(),
//                    world.getClass(),
//                    keepAfterDeath.getClass(),
//                    name.getClass(),
//                    durationMillis.getClass(),
//                    amplifier.getClass(),
//                    startTimeMillis.getClass(),
//                    data.getClass()
//            );
//            return (CustomEffect) constructor.newInstance(
//                    uuid, server, world, keepAfterDeath, name, durationMillis, amplifier, startTimeMillis, data
//            );
//        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public int getRemainingTicks() {
//        return (int)(getRemainingMillis() / 50L);
//    }
//
//    public String getData() {return data;};
//
//    abstract void apply(Player player);
//
//    abstract void unapply();
//
//    public long getStartTimeMillis() {
//        return startTimeMillis;
//    }
//
//    public long getCurrentMillis() {
//        return System.currentTimeMillis() - startTimeMillis;
//    }
//
//    public long getRemainingMillis() {
//        return durationMillis - getCurrentMillis();
//    }
//
//    public boolean isExpired() {
//        return getRemainingMillis() <= 0;
//    }
//
//    public int getAmplifier() {
//        return amplifier;
//    }
//
//    public long getDurationMillis() {
//        return durationMillis;
//    }
//
//    public String getName() { return name; }
}

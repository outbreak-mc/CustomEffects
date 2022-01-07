package space.outbreak.customeffects.util;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IConfig {
    String getString(String key);
    int getInt(String key);
    long getLong(String key);
    List<String> getStringList(String key);
    boolean getBoolean(String key);
    ItemStack getItemStack(String key);

    String getString(String key, String f);
    int getInt(String key, int f);
    long getLong(String key, long f);
    boolean getBoolean(String key, boolean f);
    ItemStack getItemStack(String key, ItemStack f);

    void set(String key, Object value);

    void save();


    default String getString(IConfigKeyMap key) {
        return getString(key.str());
    }
    default int getInt(IConfigKeyMap key) {
        return getInt(key.str());
    }
    default long getLong(IConfigKeyMap key) {
        return getLong(key.str());
    }
    default List<String> getStringList(IConfigKeyMap key) {
        return getStringList(key.str());
    }
    default boolean getBoolean(IConfigKeyMap key) { return getBoolean(key.str()); }
    default ItemStack getItemStack(IConfigKeyMap key) { return getItemStack(key.str()); }

    default String getString(IConfigKeyMap key, String f) {
        return getString(key.str(), f);
    }
    default int getInt(IConfigKeyMap key, int f) {
        return getInt(key.str(), f);
    }
    default long getLong(IConfigKeyMap key, long f) {
        return getLong(key.str(), f);
    }
    default boolean getBoolean(IConfigKeyMap key, boolean f) { return getBoolean(key.str(), f); }
    default ItemStack getItemStack(IConfigKeyMap key, ItemStack f) { return getItemStack(key.str(), f); }

    default void set(IConfigKeyMap key, Object value) {
        set(key.str(), value);
    }
}

package space.outbreak.customeffects.util.config;

import java.util.List;

public interface IConfig {
    String getString(String key);
    int getInt(String key);
    long getLong(String key);
    List<String> getStringList(String key);
    boolean getBoolean(String key);

    void set(String key, Object value);

    void save();

    default String getString(ConfigKeyMap key) {
        return getString(key.str());
    }
    default int getInt(ConfigKeyMap key) {
        return getInt(key.str());
    }
    default long getLong(ConfigKeyMap key) {
        return getLong(key.str());
    }
    default List<String> getStringList(ConfigKeyMap key) {
        return getStringList(key.str());
    }
    default boolean getBoolean(ConfigKeyMap key) {
        return getBoolean(key.str());
    }
    default void set(ConfigKeyMap key, Object value) {
        set(key.str(), value);
    }
}

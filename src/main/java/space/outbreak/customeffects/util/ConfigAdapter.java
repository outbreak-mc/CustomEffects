package space.outbreak.customeffects.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ConfigAdapter implements IConfig {
    private final FileConfiguration config;
    public ConfigAdapter(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public String getString(String key) {
        return config.getString(key);
    }

    @Override
    public int getInt(String key) {
        return config.getInt(key);
    }

    @Override
    public long getLong(String key) {
        return config.getLong(key);
    }

    @Override
    public List<String> getStringList(String key) {
        return config.getStringList(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return config.getBoolean(key);
    }

    @Override
    public void set(String key, Object value) {
        config.set(key, value);
    }

    @Override
    public ItemStack getItemStack(String key) {
        return config.getItemStack(key);
    }

    @Override
    public String getString(String key, String f) {
        return config.getString(key, f);
    }

    @Override
    public int getInt(String key, int f) {
        return config.getInt(key, f);
    }

    @Override
    public long getLong(String key, long f) {
        return config.getLong(key, f);
    }

    @Override
    public boolean getBoolean(String key, boolean f) {
        return config.getBoolean(key, f);
    }

    @Override
    public ItemStack getItemStack(String key, ItemStack f) {
        return config.getItemStack(key, f);
    }

    @Override
    public void save() {}
}

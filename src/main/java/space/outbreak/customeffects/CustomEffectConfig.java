package space.outbreak.customeffects;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import space.outbreak.customeffects.util.IConfig;

import java.util.List;

public class CustomEffectConfig implements IConfig {
    private FileConfiguration config;
    private String data;

    boolean isChanged = false;

    public CustomEffectConfig(String data) {
        this.data = data;
        reload();
    }

    public void reload() {
        config = new YamlConfiguration();
        try {
            config.loadFromString(data);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
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
    public void set(String key, Object value) {
        config.set(key, value);
        isChanged = true;
    }

    @Override
    public void save() {
        data = config.saveToString();
    }

    public String getDataString() {
        return data;
    }
}

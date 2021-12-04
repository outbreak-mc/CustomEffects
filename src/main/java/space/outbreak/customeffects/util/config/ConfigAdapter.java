package space.outbreak.customeffects.util.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigAdapter implements IConfig {
    private FileConfiguration config;
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
    public void save() {}
}

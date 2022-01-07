package space.outbreak.customeffects.db;

import space.outbreak.customeffects.CustomEffectsAPIPlugin;

import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabase extends SQLDB {
    public SQLiteDatabase(CustomEffectsAPIPlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        try {
            String path = Paths.get(plugin.getDataFolder().toString(), "database.db").toString();
            connection = DriverManager.getConnection("jdbc:sqlite:"+path);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

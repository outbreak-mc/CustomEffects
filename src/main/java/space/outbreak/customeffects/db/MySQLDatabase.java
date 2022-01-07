package space.outbreak.customeffects.db;

import space.outbreak.customeffects.CustomEffectsAPIPlugin;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDatabase extends SQLDB {
    private final String host;
    private final int port;
    private final String password;
    private final String username;
    private final String database;
    private final boolean useSSL;

    public MySQLDatabase(String host, int port, String username, String password, String database, boolean useSSL, CustomEffectsAPIPlugin plugin) {
        super(plugin);
        this.host = host;
        this.port = port;
        this.password = password;
        this.username = username;
        this.database = database;
        this.useSSL = useSSL;
    }

    @Override
    public void init() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database+"?useSSL="+useSSL, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
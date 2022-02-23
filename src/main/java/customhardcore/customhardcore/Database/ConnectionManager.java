package customhardcore.customhardcore.Database;

import customhardcore.customhardcore.CustomHardcore;
import customhardcore.customhardcore.Enums.SQLiteDataTypes;
import customhardcore.customhardcore.Generic.DatabaseObject;
import customhardcore.customhardcore.Helpers.Logger;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionManager {

    private static Connection connection = null;
    private static final CustomHardcore instance = CustomHardcore.getInstance();

    public static void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:hc.db");
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            Logger.error(ConnectionManager.class, "Fatal error, failed to connect to the database");
            instance.getPluginLoader().disablePlugin(instance);
        }
    }

    public static boolean isNotConnected() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            Logger.error(ConnectionManager.class, "Error when checking if the database is connected");
            return false;
        }
    }

    public static <T extends DatabaseObject> void updateSchema(@Nonnull Class<T> clazz, T instance) {
        if (isNotConnected())
            connect();

        if (!tableExists(clazz.getSimpleName())) {
            if (!createTable(clazz.getSimpleName())) {
                Logger.error(ConnectionManager.class, "Failed to create table: " + clazz.getSimpleName());
                return;
            }
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase("id"))
                continue;

            if (instance.getIgnoredFields() != null && instance.getIgnoredFields().contains(field))
                continue;

            String sqlType = objectToSQLType(field);
            if (sqlType == null) {
                Logger.error(ConnectionManager.class, "Failed to get field: " + field.getName());
                continue;
            }

            try {
                addColumn(clazz.getSimpleName(), field.getName(), sqlType);
            } catch (SQLException e) {
                Logger.error(ConnectionManager.class, "updateSchema", e);
            }
        }
    }

    private static String objectToSQLType(Field field) {
        return SQLiteDataTypes.getSqlLiteDataTypeFromField(field);
    }

    private static boolean tableExists(String name) {
        try {
            String sql = String.format("SELECT id FROM %s LIMIT 1", name);
            Logger.info(sql);
            ResultSet result = get(sql);
            return result.next();
        } catch (SQLException e) {
            Logger.error(ConnectionManager.class, "Failed to check if table exists for " + name);
            return false;
        }
    }

    private static boolean createTable(String name) {
        try {
            String sql = String.format("CREATE TABLE %s (" +
                    "id INT PRIMARY KEY NOT NULL" +
                    ")", name);
            Logger.info(sql);
            run(sql);
            return true;
        } catch (SQLException e) {
            Logger.error(ConnectionManager.class, "Failed to create table " + name, e);
            return false;
        }
    }

    private static void addColumn(String tableName, String columnName, String type) throws SQLException {
        try {
            if (columnExists(tableName, columnName))
                return;

            if (isNotConnected())
                connect();
            connection.createStatement().execute(String.format("ALTER TABLE %s " +
                    "ADD COLUMN %s %s", tableName, columnName, type));
            connection.commit();
        } finally {
            connection.close();
        }
    }

    private static boolean columnExists(String tableName, String columnName) {
        try {
            if (isNotConnected())
                connect();
            connection.createStatement().executeQuery(String.format("SELECT %s FROM %s LIMIT 1", columnName, tableName));
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static void run(String sql) throws SQLException {
        if (isNotConnected())
            connect();
        try {
            connection.createStatement().execute(sql);
            connection.commit();
        } finally {
            connection.close();
        }
    }

    private static ResultSet get(String sql) throws SQLException {
        try {
            return connection.createStatement().executeQuery(sql);
        } finally {
            connection.close();
        }
    }

}

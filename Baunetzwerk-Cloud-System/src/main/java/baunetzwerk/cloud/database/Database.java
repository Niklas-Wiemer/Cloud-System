package baunetzwerk.cloud.database;

import org.mariadb.jdbc.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class Database {

    private static DatabaseConfig databaseConfig;

    private final String database;
    private Connection connection;

    public Database(String database) {
        this.database = databaseConfig.getDatabase() + database;
    }

    // Create Table

    public void createTable(String sql) {
        // Execute statement
        checkConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable(String tableName, String[] columns) {
        // Build SQL statement
        StringBuilder content = new StringBuilder();

        for (int i = 0; i < columns.length; i++) {
            content.append(columns[i]);
            if (i + 1 < columns.length) {
                content.append(", ");
            }
        }

        // execute statement
        checkConnection();
        try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (" + content + ")")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get

    public DataSet get(SQLSelectBuilder builder) throws SQLException {
        checkConnection();
        PreparedStatement statement = connection.prepareStatement(builder.asSQL());

        // Replace ? to value | SQLInjection prevention
        int index = 1;
        if (builder.getWhereResults() != null) {
            formatObjects(builder.getWhereResults());
            for (Object r : builder.getWhereResults()) {
                statement.setObject(index++, r);
            }
        }
        return new DataSet(statement.executeQuery());
    }

    public DataSet get(String sql, Object... replacement) throws SQLException {
        checkConnection();
        PreparedStatement statement = connection.prepareStatement(sql);

        // Replace ? to value | SQLInjection prevention
        formatObjects(replacement);
        int index = 1;
        for (Object r : replacement) {
            statement.setObject(index++, r);
        }
        return new DataSet(statement.executeQuery());
    }

    // Insert

    public void insert(String table, String[] columns, Object... values) {
        // Build SQL statement
        StringBuilder builder = new StringBuilder("INSERT INTO ");
        builder.append(table);

        builder.append("(");
        for (int i = 0; i < columns.length; i++) {
            builder.append(columns[i]);
            if (i + 1 < columns.length) {
                builder.append(", ");
            }
        }
        builder.append(")");

        builder.append(" VALUES(");

        for (int i = 0; i < values.length; i++) {
            builder.append("?");
            if (i + 1 < values.length) {
                builder.append(", ");
            }
        }
        builder.append(")");

        // format values
        formatObjects(values);

        // execute statement
        checkConnection();
        try (PreparedStatement statement = connection.prepareStatement(builder.toString())) {
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(String table, Object... values) {
        // Build SQL statement
        StringBuilder builder = new StringBuilder("INSERT INTO ");
        builder.append(table).append(" VALUES(");

        for (int i = 0; i < values.length; i++) {
            builder.append("?");
            if (i + 1 < values.length) {
                builder.append(", ");
            }
        }
        builder.append(")");

        // format values
        formatObjects(values);

        // execute statement
        checkConnection();
        try (PreparedStatement statement = connection.prepareStatement(builder.toString())) {
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update

    public void update(String table, String column, Object newValue, String where, Object result) {
        update(table, new String[]{column}, new Object[]{newValue}, new String[]{where}, new Object[]{result});
    }

    public void update(String table, String column, Object newValue, String[] where, Object[] result) {
        update(table, new String[]{column}, new Object[]{newValue}, where, result);
    }

    public void update(String table, String[] columns, Object[] newValues, String where, Object result) {
        update(table, columns, newValues, new String[]{where}, new Object[]{result});
    }

    public void update(String table, String[] columns, Object[] newValues, String[] where, Object[] result) {
        // Build SQL statement
        StringBuilder builder = new StringBuilder("UPDATE ");
        builder.append(table).append(" SET ");

        for (int i = 0; i < columns.length; i++) {
            builder.append(columns[i]).append(" = ?");
            if (i + 1 < columns.length) {
                builder.append(", ");
            }
        }

        builder.append(" WHERE ");
        for (int i = 0; i < where.length; i++) {
            builder.append(where[i]).append(" = ?");
            if (i + 1 < where.length) {
                builder.append(" AND ");
            }
        }

        // format newValues and result
        formatObjects(newValues);
        formatObjects(result);

        // execute statement
        checkConnection();
        try (PreparedStatement statement = connection.prepareStatement(builder.toString())) {
            int index = 1;
            for (Object newValue : newValues) {
                statement.setObject(index++, newValue);
            }
            for (Object o : result) {
                statement.setObject(index++, o);
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String sql, Object... replacement) {
        checkConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            formatObjects(replacement);
            int index = 1;
            for (Object r : replacement) {
                statement.setObject(index++, r);
            }

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    // Delete
    public void delete(String table, String where, Object result) {
        delete(table, new String[]{where}, new Object[]{result});
    }

    public void delete(String table, String[] where, Object[] results) {
        // Build SQL statement
        StringBuilder builder = new StringBuilder("DELETE FROM ");
        builder.append(table);

        if (where != null) {
            builder.append(" WHERE ");
            for (int i = 0; i < where.length; i++) {
                builder.append(where[i]).append(" = ?");
                if (i + 1 < where.length) {
                    builder.append(" AND ");
                }
            }
        }

        // format results
        formatObjects(results);

        // execute statement
        checkConnection();
        try (PreparedStatement statement = connection.prepareStatement(builder.toString())) {
            for (int i = 0; i < results.length; i++) {
                statement.setObject(i + 1, results[i]);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Utils

    private void formatObjects(Object[] objects) {
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof UUID uuid) {
                objects[i] = uuid.toString();
            } else if (objects[i] instanceof Enum<?> e) {
                objects[i] = e.name();
            }
        }
    }

    // Connection

    public boolean checkConnection() {
        try {
            if (connection == null || !connection.isValid(10) || connection.isClosed()) {
                return connect();
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    private boolean connect() {
        try {
            DriverManager.registerDriver(new Driver());
            connection = DriverManager.getConnection("jdbc:mariadb://" + databaseConfig.getHost() + ":" + databaseConfig.getPort() + "/" + database + "?useSLL=false", databaseConfig.getUsername(), databaseConfig.getPassword());
        } catch (SQLException e) {
            try {
                connection = DriverManager.getConnection("jdbc:mariadb://" + databaseConfig.getHost() + ":" + databaseConfig.getPort() + "/" + database + "?useSLL=false", databaseConfig.getUsername(), databaseConfig.getPassword());
            } catch (SQLException e1) {
                return false;
            }
        }
        return true;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                connection = null;
            } finally {
                connection = null;
            }
        }
    }

    public static void setDatabaseConfig(DatabaseConfig databaseConfig) {
        Database.databaseConfig = databaseConfig;
    }

    public static DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }
}

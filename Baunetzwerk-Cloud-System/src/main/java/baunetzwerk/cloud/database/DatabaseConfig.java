package baunetzwerk.cloud.database;

public class DatabaseConfig {

    private final String host;
    private final String username;
    private final String password;
    private final int port;
    private final String database;

    public DatabaseConfig(String host, String username, String password, int port, String database) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
        this.database = database;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }
}

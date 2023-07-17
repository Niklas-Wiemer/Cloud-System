package baunetzwerk.cloud.service;

import baunetzwerk.cloud.database.DataSet;
import baunetzwerk.cloud.database.Database;
import baunetzwerk.cloud.database.SQLSelectBuilder;
import baunetzwerk.cloud.server.Server;
import baunetzwerk.cloud.server.ServerType;
import baunetzwerk.cloud.template.ServerTemplate;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ServerService {

    private static ServerService instance;
    private final ServerStarterService starterService;
    private final Database database;
    private final List<Server> serverList = new LinkedList<>();

    public ServerService(ServerTemplateService templateService, ServerStarterService starterService) {
        instance = this;
        this.starterService = starterService;

        database = new Database("cloud");

        database.createTable("CREATE TABLE IF NOT EXISTS server (id varchar(36) PRIMARY KEY, name varchar(64) NOT NULL, type varchar(16) NOT NULL, minRAM INT NOT NULL, maxRAM INT NOT NULL)");
        database.createTable("CREATE TABLE IF NOT EXISTS templateConnection (serverID varchar(36), templateID varchar(36), " +
                "FOREIGN KEY (serverID) REFERENCES server(id) ON DELETE CASCADE," +
                "FOREIGN KEY (templateID) REFERENCES templates(id) ON DELETE CASCADE)");

        Set<ServerTemplate> templates;

        SQLSelectBuilder sqlSelectBuilder = new SQLSelectBuilder("server");
        try (DataSet set = database.get(sqlSelectBuilder)) {
            while (set.next()) {
                templates = new HashSet<>();

                sqlSelectBuilder = new SQLSelectBuilder("templateConnection");
                DataSet templateSet = database.get(sqlSelectBuilder);
                while (templateSet.next()) {
                    templates.add(templateService.getServerTemplate(templateSet.getUUID("templateID")));
                }

                serverList.add(new Server(
                        set.getUUID("id"),
                        set.getString("name"),
                        set.getEnum("type", ServerType.class),
                        set.getInt("minRAM"),
                        set.getInt("maxRAM"),
                        templates
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<?> createServer(String name, ServerType serverType, int minRam, int maxRam) {
        return new CompletableFuture<>().completeAsync(() -> {
            Server server = new Server(UUID.randomUUID(), name, serverType, minRam, maxRam, new HashSet<>());
            serverList.add(server);

            database.insert("server", server.getId(), name, serverType, minRam, maxRam);

            starterService.startServer(server);

            return null;
        });
    }

    public CompletableFuture<?> deleteServer(String name) {
        return new CompletableFuture<>().completeAsync(() -> {
            Server server = getServer(name);
            serverList.remove(server);

            if (starterService.isOnline(server)) {
                starterService.stopServer(server);
            }

            database.delete("server", "id", server.getId());

            return null;
        });
    }

    public CompletableFuture<?> addServerTemplate(Server server, ServerTemplate serverTemplate) {
        return new CompletableFuture<>().completeAsync(() -> {
            server.addServerTemplate(serverTemplate);

            database.insert("templateConnection", server.getId(), serverTemplate.getId());

            return null;
        });
    }

    public CompletableFuture<?> removeServerTemplate(Server server, ServerTemplate serverTemplate) {
        return new CompletableFuture<>().completeAsync(() -> {
            server.removeServerTemplate(serverTemplate);

            database.delete("templateConnection", new String[]{"serverID", "templateID"}, new Object[]{server.getId(), serverTemplate.getId()});

            return null;
        });
    }

    public Server getServer(String name) {
        return serverList.stream().filter(server -> server.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Server getServer(UUID id) {
        return serverList.stream().filter(server -> server.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Server> getServer() {
        return serverList;
    }

    public static ServerService getInstance() {
        return instance;
    }
}

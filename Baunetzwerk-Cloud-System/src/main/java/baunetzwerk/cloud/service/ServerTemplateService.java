package baunetzwerk.cloud.service;

import baunetzwerk.cloud.database.DataSet;
import baunetzwerk.cloud.database.Database;
import baunetzwerk.cloud.database.SQLSelectBuilder;
import baunetzwerk.cloud.server.Server;
import baunetzwerk.cloud.template.ServerTemplate;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ServerTemplateService {

    private static ServerTemplateService instance;

    private final Database database;
    private final List<ServerTemplate> serverTemplateList = new LinkedList<>();

    public ServerTemplateService() {
        instance = this;

        database = new Database("cloud");

        database.createTable("CREATE TABLE IF NOT EXISTS templates (id varchar(36) PRIMARY KEY, name varchar(64) NOT NULL)");

        SQLSelectBuilder sqlSelectBuilder = new SQLSelectBuilder("templates");
        try (DataSet set = database.get(sqlSelectBuilder)) {
            while (set.next()) {
                serverTemplateList.add(
                        new ServerTemplate(set.getUUID("id"),
                                set.getString("name"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<?> createServerTemplate(String name) {
        return new CompletableFuture<>().completeAsync(() -> {
            ServerTemplate serverTemplate = new ServerTemplate(UUID.randomUUID(), name);
            serverTemplateList.add(serverTemplate);

            database.insert("templates", serverTemplate.getId(), name);

            return null;
        });
    }

    public CompletableFuture<?> deleteServerTemplate(String name) {
        return new CompletableFuture<>().completeAsync(() -> {
            ServerTemplate serverTemplate = getServerTemplate(name);
            serverTemplateList.remove(serverTemplate);

            for (Server server : ServerService.getInstance().getServer()) {
                server.removeServerTemplate(serverTemplate);
            }

            database.delete("templates", "id", serverTemplate.getId());

            return null;
        });
    }

    public ServerTemplate getServerTemplate(String name) {
        return serverTemplateList.stream().filter(serverTemplate -> serverTemplate.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public ServerTemplate getServerTemplate(UUID id) {
        return serverTemplateList.stream().filter(serverTemplate -> serverTemplate.getId().equals(id)).findFirst().orElse(null);
    }

    public List<ServerTemplate> getServerTemplateList() {
        return serverTemplateList;
    }

    public static ServerTemplateService getInstance() {
        return instance;
    }
}

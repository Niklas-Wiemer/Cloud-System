package baunetzwerk.cloud.server;

import baunetzwerk.cloud.service.FileService;
import baunetzwerk.cloud.template.ServerTemplate;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class Server {

    private final UUID id;
    private final String name;
    private final ServerType serverType;
    private final int minRam;
    private final int maxRam;
    private final Set<ServerTemplate> serverTemplateList;

    public Server(UUID id, String name, ServerType serverType, int minRam, int maxRam, Set<ServerTemplate> serverTemplateList) {
        this.id = id;
        this.name = name;
        this.serverType = serverType;
        this.minRam = minRam;
        this.maxRam = maxRam;
        this.serverTemplateList = serverTemplateList;
    }

    public void copyTemplates() throws IOException {
        for (ServerTemplate serverTemplate : serverTemplateList) {
            serverTemplate.copyToServer(this);
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public int getMinRam() {
        return minRam;
    }

    public int getMaxRam() {
        return maxRam;
    }

    public void addServerTemplate(ServerTemplate serverTemplate) {
        serverTemplateList.add(serverTemplate);
    }

    public void removeServerTemplate(ServerTemplate serverTemplate) {
        serverTemplateList.remove(serverTemplate);
    }

    public boolean containsServerTemplate(ServerTemplate serverTemplate) {
        return serverTemplateList.contains(serverTemplate);
    }

    public File getPath() {
        return new File(FileService.serverPath + getName());
    }
}

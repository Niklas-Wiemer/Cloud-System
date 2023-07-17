package baunetzwerk.cloud.console.commands;

import baunetzwerk.cloud.server.Server;
import baunetzwerk.cloud.server.ServerType;
import baunetzwerk.cloud.service.ServerService;
import baunetzwerk.cloud.service.ServerTemplateService;
import baunetzwerk.cloud.template.ServerTemplate;
import baunetzwerk.cloud.utils.TableBuilder;

import java.util.LinkedList;
import java.util.List;

public class ServerCommand extends ACommand {

    private final ServerService serverService;
    private final ServerTemplateService templateService;

    public ServerCommand(String command, ServerService serverService, ServerTemplateService templateService) {
        super(command);
        this.serverService = serverService;
        this.templateService = templateService;
    }

    @Override
    public void execute(String[] args) {
        switch (args.length) {
            case 1 -> {
                if (args[0].equalsIgnoreCase("list")) {
                    list();
                } else {
                    sendUsage();
                }
            }
            case 2 -> {
                if (args[0].equalsIgnoreCase("delete")) {
                    delete(args[1]);
                } else {
                    sendUsage();
                }
            }
            case 3 -> {
                switch (args[0].toLowerCase()) {
                    case "addtemplate" -> addTemplate(args[1], args[2]);
                    case "removetemplate" -> removeTemplate(args[1], args[2]);
                    default -> sendUsage();
                }
            }
            case 5 -> {
                if (args[0].equalsIgnoreCase("create")) {
                    create(args[1], args[2], args[3], args[4]);
                } else {
                    sendUsage();
                }
            }
            default -> sendUsage();
        }
    }

    private void sendUsage() {
        logger.info("server create <Name> <Type> <minRam> <maxRam>");
        logger.info("server delete <Name>");
        logger.info("server addTemplate <Server> <Template>");
        logger.info("server removeTemplate <Server> <Template>");
        logger.info("server list");
    }

    private void create(String name, String sType, String sMinRam, String sMaxRam) {
        if (serverService.getServer(name) != null) {
            logger.info("Dieser Server existiert bereits.");
            return;
        }
        ServerType serverType;
        try {
            serverType = ServerType.valueOf(sType.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.info("Dieser ServerTyp existiert nicht.");
            return;
        }
        int minRam;
        int maxRam;

        try {
            minRam = Integer.parseInt(sMinRam);
            maxRam = Integer.parseInt(sMaxRam);
        } catch (NumberFormatException e) {
            logger.info("Ungültiges RAM Format.");
            return;
        }
        if (minRam < 0 || minRam % 512 != 0 || maxRam < 0 || maxRam % 512 != 0) {
            logger.info("Der RAM-Wert muss größer als 0 sein und durch 512 teilbar sein.");
            return;
        }
        serverService.createServer(name, serverType, minRam, maxRam).thenAcceptAsync(v -> logger.info("Der Server " + name + " wurde erstellt."));
    }

    private void delete(String name) {
        if (serverService.getServer(name) == null) {
            logger.info("Dieser Server existiert nicht.");
            return;
        }
        serverService.deleteServer(name).thenAcceptAsync(v -> logger.info("Der Server " + name + " wurde gelöscht."));
    }

    private void addTemplate(String sServer, String sTemplate) {
        Server server = serverService.getServer(sServer);
        if (server == null) {
            logger.info("Dieser Server existiert nicht.");
            return;
        }
        ServerTemplate serverTemplate = templateService.getServerTemplate(sTemplate);
        if (serverTemplate == null) {
            logger.info("Dieses Template existiert nicht.");
            return;
        }
        if (server.containsServerTemplate(serverTemplate)) {
            logger.info("Der Server besitzt dieses Template bereits.");
            return;
        }
        serverService.addServerTemplate(server, serverTemplate).thenAcceptAsync(v -> logger.info("Das Template " + serverTemplate.getName() + " wurde zum Server " + server.getName() + " hinzugefügt."));
    }

    private void removeTemplate(String sServer, String sTemplate) {
        Server server = serverService.getServer(sServer);
        if (server == null) {
            logger.info("Dieser Server existiert nicht.");
            return;
        }
        ServerTemplate serverTemplate = templateService.getServerTemplate(sTemplate);
        if (serverTemplate == null) {
            logger.info("Dieses Template existiert nicht.");
            return;
        }
        if (!server.containsServerTemplate(serverTemplate)) {
            logger.info("Der Server besitzt dieses Template nicht.");
            return;
        }
        serverService.removeServerTemplate(server, serverTemplate).thenAcceptAsync(v -> logger.info("Das Template " + serverTemplate.getName() + " wurde vom Server " + server.getName() + " entfernt."));
    }

    private void list() {
        if (serverService.getServer().isEmpty()) {
            logger.info("Derzeit existiert kein Server.");
            return;
        }

        String[] header = {"Server", "Typ", "minRAM", "maxRAM"};
        String[][] content = new String[serverService.getServer().size()][4];

        for (int i = 0; i < serverService.getServer().size(); i++) {
            content[i][0] = serverService.getServer().get(i).getName();
            content[i][1] = serverService.getServer().get(i).getServerType().name();
            content[i][2] = String.valueOf(serverService.getServer().get(i).getMinRam());
            content[i][3] = String.valueOf(serverService.getServer().get(i).getMaxRam());
        }

        String table = TableBuilder.createTable(header, content);
        logger.info(table);
    }

    @Override
    protected List<SubCommand> getSubCommands() {
        List<SubCommand> subCommandList = new LinkedList<>();

        subCommandList.add(new SubCommand("create", "server create <Name> <Type> <minRam> <maxRam>", "Erstellt einen neuen Server"));
        subCommandList.add(new SubCommand("delete", "server delete <Name>", "Löscht den Server"));
        subCommandList.add(new SubCommand("list", "server list", "Listet alle Server auf"));
        subCommandList.add(new SubCommand("addTemplate", "server addTemplate <Server> <Template>", "Fügt ein Template zu dem Server hinzu"));
        subCommandList.add(new SubCommand("removeTemplate", "server removeTemplate <Server> <Template>", "Entfernt ein Template vom Server"));

        return subCommandList;
    }
}

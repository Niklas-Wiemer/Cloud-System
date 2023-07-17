package baunetzwerk.cloud.console.commands;

import baunetzwerk.cloud.service.ServerTemplateService;
import baunetzwerk.cloud.utils.TableBuilder;

import java.util.LinkedList;
import java.util.List;

public class TemplateCommand extends ACommand {

    private final ServerTemplateService templateService;

    public TemplateCommand(String command, ServerTemplateService templateService) {
        super(command);
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
                switch (args[0].toLowerCase()) {
                    case "create" -> create(args[1]);
                    case "delete" -> delete(args[1]);
                    default -> sendUsage();
                }
            }
            default -> sendUsage();
        }
    }

    private void sendUsage() {
        logger.info("template create <Name>");
        logger.info("template delete <Name>");
        logger.info("template list");
    }

    private void create(String name) {
        if (templateService.getServerTemplate(name) != null) {
            logger.info("Dieses Template existiert bereits");
            return;
        }
        templateService.createServerTemplate(name).thenAcceptAsync(v -> logger.info("Das Template " + name + " wurde erstellt."));
    }

    private void delete(String name) {
        if (templateService.getServerTemplate(name) == null) {
            logger.info("Dieses Template existiert nicht");
            return;
        }
        templateService.deleteServerTemplate(name).thenAcceptAsync(v -> logger.info("Das Template " + name + " wurde gelöscht."));
    }

    private void list() {
        if (templateService.getServerTemplateList().isEmpty()) {
            logger.info("Derzeit existiert kein Template.");
            return;
        }
        String[] header = {"Template"};
        String[][] content = new String[templateService.getServerTemplateList().size()][1];

        for (int i = 0; i < templateService.getServerTemplateList().size(); i++) {
            content[i][0] = templateService.getServerTemplateList().get(i).getName();
        }

        String table = TableBuilder.createTable(header, content);
        logger.info(table);
    }

    @Override
    protected List<SubCommand> getSubCommands() {
        List<SubCommand> subCommandList = new LinkedList<>();

        subCommandList.add(new SubCommand("create", "template create <Name>", "Erstellt ein neues Template"));
        subCommandList.add(new SubCommand("delete", "template delete <Name>", "Löscht das Template"));
        subCommandList.add(new SubCommand("list", "template list", "Listet alle Templates auf"));

        return subCommandList;
    }
}

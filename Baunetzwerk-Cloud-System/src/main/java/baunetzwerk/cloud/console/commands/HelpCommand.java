package baunetzwerk.cloud.console.commands;

import java.util.LinkedList;
import java.util.List;

public class HelpCommand extends ACommand {

    public HelpCommand(String command) {
        super(command);
    }

    @Override
    public void execute(String[] args) {
        logger.info("*** Hilfe ***");
        logger.info("server create <Name> <Type> <minRam> <maxRam>");
        logger.info("server delete <Name>");
        logger.info("server addTemplate <Server> <Template>");
        logger.info("server removeTemplate <Server> <Template>");
        logger.info("server list");
        logger.info("template create <Name>");
        logger.info("template delete <Name>");
        logger.info("template list");
    }

    @Override
    protected List<SubCommand> getSubCommands() {
        return new LinkedList<>();
    }

}

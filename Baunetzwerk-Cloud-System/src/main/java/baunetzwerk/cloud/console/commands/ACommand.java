package baunetzwerk.cloud.console.commands;

import baunetzwerk.cloud.logger.LoggerBuilder;
import org.jline.console.CmdDesc;
import org.jline.utils.AttributedString;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public abstract class ACommand {

    protected static final Logger logger = LoggerBuilder.LoggerBuilder("Command");
    private final String command;

    public ACommand(String command) {
        this.command = command;
    }

    protected abstract List<SubCommand> getSubCommands();

    public abstract void execute(String[] args);

    public CmdDesc getCmdDesc() {
        List<AttributedString> mainDesc = new LinkedList<>();
        Map<String, List<AttributedString>> widgetOpts = new HashMap<>();

        for (SubCommand subCommand : getSubCommands()) {
            mainDesc.add(new AttributedString(subCommand.getUsage()));
            widgetOpts.put(subCommand.getSubCommand(), List.of(new AttributedString(subCommand.getDescription())));
        }

        return new CmdDesc(mainDesc, new LinkedList<>(), widgetOpts);
    }

    public String getCommand() {
        return command;
    }
}

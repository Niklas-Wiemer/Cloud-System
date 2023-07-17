package baunetzwerk.cloud.console.commands;

public class SubCommand {

    private final String subCommand;
    private final String usage;
    private final String description;

    public SubCommand(String subCommand, String usage, String description) {
        this.subCommand = subCommand;
        this.usage = usage;
        this.description = description;
    }

    public String getSubCommand() {
        return subCommand;
    }

    public String getUsage() {
        return usage;
    }

    public String getDescription() {
        return description;
    }
}

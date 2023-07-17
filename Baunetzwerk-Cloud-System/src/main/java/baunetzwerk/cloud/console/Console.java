package baunetzwerk.cloud.console;

import baunetzwerk.cloud.console.commands.ACommand;
import baunetzwerk.cloud.console.commands.HelpCommand;
import baunetzwerk.cloud.console.commands.ServerCommand;
import baunetzwerk.cloud.console.commands.TemplateCommand;
import baunetzwerk.cloud.logger.LoggerBuilder;
import baunetzwerk.cloud.runnable.ServerStarter;
import baunetzwerk.cloud.service.ServerService;
import baunetzwerk.cloud.service.ServerTemplateService;
import org.jline.console.CmdDesc;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.terminal.TerminalBuilder;
import org.jline.widget.TailTipWidgets;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Logger;

public class Console {
    /*
        Commands:
        - server create <Name> <Type> <minRam> <maxRam>
        - server delete <Name>
        - server list
        - server addTemplate <Server> <Template>
        - server removeTemplate <Server> <Template>

        - template create <Name>
        - template delete <Name>
        - template list

        - version
        - clear

     */

    private LineReader reader;
    private final ServerStarter serverStarter;
    private final List<ACommand> commandList;
    private Thread thread;

    public Console(ServerService serverService, ServerTemplateService templateService, ServerStarter serverStarter) {
        this.serverStarter = serverStarter;
        Logger logger = LoggerBuilder.LoggerBuilder("Console");
        logger.info("\n" +
                "  ______  __        ______    __    __   _______     ____    ____  __      \n" +
                " /      ||  |      /  __  \\  |  |  |  | |       \\    \\   \\  /   / /_ |     \n" +
                "|  ,----'|  |     |  |  |  | |  |  |  | |  .--.  |    \\   \\/   /   | |     \n" +
                "|  |     |  |     |  |  |  | |  |  |  | |  |  |  |     \\      /    | |     \n" +
                "|  `----.|  `----.|  `--'  | |  `--'  | |  '--'  |      \\    / __  | |  __ \n" +
                " \\______||_______| \\______/   \\______/  |_______/        \\__/ (__) |_| (__)\n" +
                "                                                                           ");

        commandList = new LinkedList<>();
        commandList.add(new ServerCommand("server", serverService, templateService));
        commandList.add(new TemplateCommand("template", templateService));
        commandList.add(new HelpCommand("help"));

        TerminalBuilder terminalBuilder = TerminalBuilder.builder();
        terminalBuilder.encoding(Charset.defaultCharset());
        terminalBuilder.system(true);

        try {
            this.reader = LineReaderBuilder.builder()
                    .terminal(terminalBuilder.build())
                    .completer(new AggregateCompleter())
                    .parser(new DefaultParser())
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, CmdDesc> tailTips = new HashMap<>();

        for (ACommand command : commandList) {
            tailTips.put(command.getCommand(), command.getCmdDesc());
        }

        TailTipWidgets tailTipWidgets = new TailTipWidgets(
                reader,
                tailTips,
                5,
                TailTipWidgets.TipType.COMBINED
        );

        tailTipWidgets.enable();
    }

    public void start() {
        thread = new Thread(() -> {
            String input;
            String[] commandInput;
            String[] args;
            do {
                input = reader.readLine("> ").trim();
                commandInput = input.split(" ");

                if (commandInput.length >= 1) {
                    for (ACommand command : commandList) {
                        if (command.getCommand().equalsIgnoreCase(commandInput[0])) {
                            if (commandInput.length > 1) {
                                args = Arrays.copyOfRange(commandInput, 1, commandInput.length);
                            } else {
                                args = new String[0];
                            }
                            command.execute(args);
                            break;
                        }
                    }
                }

            } while (!input.equalsIgnoreCase("exit") && !thread.isInterrupted());
            thread.interrupt();
            serverStarter.stop();
        });
        thread.start();
    }


}

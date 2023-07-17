package baunetzwerk.cloud.installer;


import baunetzwerk.cloud.database.DatabaseConfig;
import baunetzwerk.cloud.logger.LoggerBuilder;
import baunetzwerk.cloud.service.FileService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class CloudInstaller {

    private final Logger logger;
    private int index;
    private final Map<String, Object> values = new HashMap<>();

    public CloudInstaller() {
        this.logger = LoggerBuilder.LoggerBuilder("Installation");
        this.index = 0;

        values.put("database.host", "localhost");
        values.put("database.username", "root");
        values.put("database.password", "");
        values.put("database.port", 3306);
        values.put("database.database", "cloud_system");
    }

    public void startInstallation() {
        if (new File(FileService.configPath + "config.json").exists()) return;

        logger.info("Install Cloud");
        logger.info("Schritte:");
        logger.info("1. Datenbank Verbindung");
        logger.info("- Host");
        logger.info("- Username");
        logger.info("- Password");
        logger.info("- Port");
        logger.info("- Database");

        TerminalBuilder terminalBuilder = TerminalBuilder.builder();
        terminalBuilder.encoding(Charset.defaultCharset().name());
        terminalBuilder.system(true);

        try {
            Terminal terminal = terminalBuilder.build();
            DefaultParser parser = new DefaultParser();

            LineReader reader = LineReaderBuilder
                    .builder()
                    .terminal(terminal)
                    .parser(parser)
                    .build();

            String line;

            while (index < values.size()) {
                switch (index) {
                    case 0 -> {
                        logger.info("Datenbank - Host (Default: " + values.get("database.host") + ")");
                        line = reader.readLine("> ");
                        values.put("database.host", line);
                    }
                    case 1 -> {
                        logger.info("Datenbank - Username (Default: " + values.get("database.username") + ")");
                        line = reader.readLine("> ");
                        values.put("database.username", line);
                    }
                    case 2 -> {
                        logger.info("Datenbank - Password (Default: -)");
                        line = reader.readLine("> ");
                        values.put("database.password", line);
                    }
                    case 3 -> {
                        logger.info("Datenbank - Port (Default: " + values.get("database.port") + ")");
                        line = reader.readLine("> ");
                        values.put("database.port", Integer.parseInt(line));
                    }
                    case 4 -> {
                        logger.info("Datenbank - Database (Default: " + values.get("database.database") + "");
                        line = reader.readLine("> ");
                        values.put("database.database", line);
                    }
                }
                index++;
            }
            terminal.close();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(FileService.configPath + "/config.json")) {
                gson.toJson(new DatabaseConfig(
                        (String) values.get("database.host"),
                        (String) values.get("database.username"),
                        (String) values.get("database.password"),
                        (Integer) values.get("database.port"),
                        (String) values.get("database.database")
                ), writer);
            } catch (IOException e) {
                e.printStackTrace();
            }

            logger.info("Cloud installiert.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
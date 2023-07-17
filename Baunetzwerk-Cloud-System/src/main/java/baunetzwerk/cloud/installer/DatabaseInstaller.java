package baunetzwerk.cloud.installer;

import baunetzwerk.cloud.database.Database;
import baunetzwerk.cloud.logger.LoggerBuilder;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public class DatabaseInstaller {

    private final Logger logger;

    public DatabaseInstaller() {
        this.logger = LoggerBuilder.LoggerBuilder("Database Installer");
    }

    public boolean startInstallation() {
        Database database = new Database("cloud");

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

            String line = "";

            while (!database.checkConnection() && !line.equalsIgnoreCase("n")) {
                logger.info("Es konnte keine Verbindung zur Datenbank hergestellt werden. Bitte überprüfe ob die Datenbank \"" + Database.getDatabaseConfig().getDatabase() + "cloud\" existiert.");
                logger.info("Bitte gib \"y\" ein um es neu zu versuchen. \"n\" um den Vorgang abzubrechen.");
                line = reader.readLine("> ");
            }
            terminal.close();

            database.closeConnection();
            if (line.equalsIgnoreCase("n")) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

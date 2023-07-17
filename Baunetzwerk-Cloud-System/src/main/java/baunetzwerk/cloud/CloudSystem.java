package baunetzwerk.cloud;

import baunetzwerk.cloud.console.Console;
import baunetzwerk.cloud.database.Database;
import baunetzwerk.cloud.database.DatabaseConfig;
import baunetzwerk.cloud.installer.CloudInstaller;
import baunetzwerk.cloud.installer.DatabaseInstaller;
import baunetzwerk.cloud.logger.LoggerBuilder;
import baunetzwerk.cloud.runnable.ServerStarter;
import baunetzwerk.cloud.service.FileService;
import baunetzwerk.cloud.service.ServerService;
import baunetzwerk.cloud.service.ServerStarterService;
import baunetzwerk.cloud.service.ServerTemplateService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

public class CloudSystem {

    public static void main(String[] args) {
        Logger logger = LoggerBuilder.LoggerBuilder("Cloud");

        // create all paths
        FileService.loadPaths();

        // Install cloud
        new CloudInstaller().startInstallation();

        // load database
        File file = new File(FileService.configPath + "config.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileReader reader = new FileReader(file)) {
            DatabaseConfig databaseConfig = gson.fromJson(reader, DatabaseConfig.class);
            Database.setDatabaseConfig(databaseConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean success = new DatabaseInstaller().startInstallation();
        if (!success) {
            logger.severe("Es konnte keine Verbindung zur Datenbank hergestellt werden. Die Cloud wird beendet.");
            return;
        }

        // init service
        ServerTemplateService templateService = new ServerTemplateService();
        ServerStarterService starterService = new ServerStarterService();
        ServerService serverService = new ServerService(templateService, starterService);

        // start server
        ServerStarter serverStarter = new ServerStarter(serverService, starterService);
        serverStarter.start();

        // start console
        Console console = new Console(serverService, templateService, serverStarter);
        console.start();

    }

}

package baunetzwerk.cloud.service;

import baunetzwerk.cloud.logger.LoggerBuilder;
import baunetzwerk.cloud.server.Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class ServerStarterService {

    private static ServerStarterService instance;
    private final Logger logger;

    public ServerStarterService() {
        instance = this;
        this.logger = LoggerBuilder.LoggerBuilder("StarterService");
    }

    public void startServer(Server server) {
        if (!server.getPath().exists()) {
            logger.warning("Could not start server " + server.getName() + ". Path not exist.");
            return;
        }
        try {
            server.copyTemplates();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("starting server " + server.getName() + "...");
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command("/bin/sh", "-c", "screen -mdS " + server.getName() + " java -Xmn" + server.getMinRam() + "M -Xmx" + server.getMaxRam() + "M -jar " + server.getServerType().getJarName());
            builder.directory(new File(FileService.serverPath + server.getName()));
            builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer(Server server) {
        logger.info("stopping server " + server.getName() + "...");

        ProcessBuilder builder = new ProcessBuilder();
        builder.command("/bin/sh",
                "-c",
                "screen -XS " + server.getName() + " kill"); // ToDo execute command in screen
        try {
            builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isOnline(Server server) {
        try {
            Process process = new ProcessBuilder(
                    "/bin/sh", "-c",
                    "screen -ls"
            ).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(server.getName())) return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ServerStarterService getInstance() {
        return instance;
    }
}

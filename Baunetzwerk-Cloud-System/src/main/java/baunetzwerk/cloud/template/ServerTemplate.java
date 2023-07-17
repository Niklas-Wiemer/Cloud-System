package baunetzwerk.cloud.template;

import baunetzwerk.cloud.logger.LoggerBuilder;
import baunetzwerk.cloud.server.Server;
import baunetzwerk.cloud.service.FileService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.UUID;
import java.util.logging.Logger;

public class ServerTemplate {

    private final static Logger logger = LoggerBuilder.LoggerBuilder("ServerTemplate");
    ;
    private final UUID id;
    private final String name;

    public ServerTemplate(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public void copyToServer(Server server) throws IOException {
        File path = getPath();
        if (!path.exists()) {
            logger.warning("Could not copy ServerTemplate " + getName() + " to " + server.getName() + ". ServerTemplate does not exist.");
            return;
        }
        File serverFile = server.getPath();

        for (String files : path.list()) {
            copyDirectoryCompatibityMode(new File(path, files), new File(serverFile, files));
        }
    }

    private void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdir();
        }
        for (String f : sourceDirectory.list()) {
            copyDirectoryCompatibityMode(new File(sourceDirectory, f), new File(destinationDirectory, f));
        }
    }

    private void copyDirectoryCompatibityMode(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            copyDirectory(source, destination);
        } else {
            copyFile(source, destination);
        }
    }

    private void copyFile(File sourceFile, File destinationFile) throws IOException {
        try (InputStream in = Files.newInputStream(sourceFile.toPath());
             OutputStream out = Files.newOutputStream(destinationFile.toPath())) {

            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        }
    }

    // getter

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public File getPath() {
        return new File(FileService.templatePath + getName());
    }
}

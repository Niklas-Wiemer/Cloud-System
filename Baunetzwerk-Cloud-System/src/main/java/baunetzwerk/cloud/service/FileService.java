package baunetzwerk.cloud.service;

import java.io.File;

public class FileService {

    public final static String cloudPath = "cloud" + File.separator;
    public final static String serverPath = cloudPath + "server" + File.separator;
    public final static String configPath = cloudPath + "config" + File.separator;
    public final static String templatePath = cloudPath + "template" + File.separator;

    public static void loadPaths() {
        new File(serverPath).mkdirs();
        new File(configPath).mkdirs();
        new File(templatePath).mkdirs();
    }

}

package baunetzwerk.cloud.server;

public enum ServerType {

    PROXY("waterfall.jar"),
    PAPER("paper.jar");

    private final String jarName;

    ServerType(String jarName) {
        this.jarName = jarName;
    }

    public String getJarName() {
        return jarName;
    }
}

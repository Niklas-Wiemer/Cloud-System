package baunetzwerk.cloud.runnable;

import baunetzwerk.cloud.server.Server;
import baunetzwerk.cloud.service.ServerService;
import baunetzwerk.cloud.service.ServerStarterService;

public class ServerStarter {
    private final static int updateTime = 5;

    private final ServerService serverService;
    private final ServerStarterService serverStarterService;
    private Thread thread;

    public ServerStarter(ServerService serverService, ServerStarterService serverStarterService) {
        this.serverService = serverService;
        this.serverStarterService = serverStarterService;
    }

    public void start() {
        thread = new Thread(() -> {
            while (!thread.isInterrupted()) {
                for (Server server : serverService.getServer()) {
                    if (!serverStarterService.isOnline(server)) {
                        serverStarterService.startServer(server);
                    }
                }
                try {
                    Thread.sleep(1000 * updateTime);
                } catch (InterruptedException e) {
                    thread.interrupt();
                }
            }
        });
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

}

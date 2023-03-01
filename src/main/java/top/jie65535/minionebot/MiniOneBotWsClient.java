package top.jie65535.minionebot;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

public class MiniOneBotWsClient {

    private final String wsUrl;
    private final Logger logger;

    private final Timer wsConnectDaemon;

    public MiniOneBotWsClient(@NotNull String wsUrl, @NotNull Logger logger) {
        this.wsUrl = wsUrl;
        this.logger = logger;

        wsConnectDaemon = new Timer("WsClientDaemon", true);
        wsConnectDaemon.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.debug("Timer...");
            }
        }, 60_000);
    }

}

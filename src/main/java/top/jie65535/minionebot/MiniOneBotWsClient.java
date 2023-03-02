package top.jie65535.minionebot;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MiniOneBotWsClient extends WebSocketClient implements WsStream {
    private final Logger logger;

    private MiniOneBotWsClient(URI serverUri, Map<String, String> headers, Logger logger) {
        super(serverUri, headers);

        this.logger = logger;
    }

    public static MiniOneBotWsClient create(URI serverUri, String token, Logger logger) {
        var headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + token);
        var client = new MiniOneBotWsClient(serverUri, headers, logger);
        var wsClientDaemon = new Timer("WsClientDaemon", true);
        wsClientDaemon.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!client.isOpen()) {
                    logger.debug("Try connect...");
                    client.connect();
                }
            }
        }, 5_000);
        return client;
    }

    private WsMessageHandler callback;

    @Override
    public void subscribe(WsMessageHandler callback) {
        this.callback = callback;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("onOpen: statusMessage={}", handshakedata.getHttpStatusMessage());
    }

    @Override
    public void onMessage(String message) {
        logger.info("onMessage: {}", message);
        callback.onMessage(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("onClose: code={} reason={} isRemote={}", code, reason, remote);
    }

    @Override
    public void onError(Exception ex) {
        logger.error("onError:", ex);
    }

    @Override
    public void send(String message) {
        if (isOpen()) {
            super.send(message);
        }
    }
}

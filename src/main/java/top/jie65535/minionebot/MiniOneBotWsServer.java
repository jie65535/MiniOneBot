package top.jie65535.minionebot;

import io.javalin.Javalin;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.CloseStatus;
import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MiniOneBotWsServer implements WsStream, Closeable {

    private final String token;
    private final Logger logger;
    private final Map<WsContext, String> connections = new ConcurrentHashMap<>();

    public MiniOneBotWsServer(Javalin javalin, String path, String token, Logger logger) {
        this.token = token;
        this.logger = logger;
        javalin.ws(path, ws -> {
            ws.onConnect(this::onConnect);
            ws.onClose(this::onClose);
            ws.onError(this::onError);
            ws.onMessage(this::onMessage);
        });

        logger.info("WebSocket server started at {}", path);
    }

    public void onConnect(WsConnectContext ctx) {
        logger.debug("onConnect: address={} headers={}", ctx.session.getRemoteAddress(), ctx.headerMap());
        var author = ctx.header("Authorization");
        // Check access token.
        if (author == null) {
            ctx.session.close(new CloseStatus(401, "Unauthorized"));
        } else if (!author.equals("Bearer " + token)) {
            ctx.session.close(new CloseStatus(403, "Unauthorized"));
        } else {
            var selfId = ctx.header("X-Self-ID");
            if (selfId != null && !selfId.isEmpty()) {
                logger.info("Bot [{}] WebSocket connected", selfId);
            } else {
                logger.info("[{}] WebSocket connected", ctx.session.getRemoteAddress());
            }
            connections.put(ctx, selfId);
        }
    }

    public void onClose(WsCloseContext ctx) {
        logger.debug("onClose: address={} status={} reason={}", ctx.session.getRemoteAddress(), ctx.status(), ctx.reason());
        var selfId = connections.remove(ctx);
        if (selfId != null && !selfId.isEmpty()) {
            logger.info("Bot [{}] WebSocket disconnected", selfId);
        } else {
            logger.info("[{}] WebSocket disconnected", ctx.session.getRemoteAddress());
        }
    }

    public void onError(WsErrorContext ctx) {
        logger.warn("onError: address={}", ctx.session.getRemoteAddress(), ctx.error());
        var selfId = connections.remove(ctx);
        if (selfId != null && !selfId.isEmpty()) {
            logger.info("Bot [{}] WebSocket disconnected", selfId);
        } else {
            logger.info("[{}] WebSocket disconnected", ctx.session.getRemoteAddress());
        }
    }

    public void onMessage(WsMessageContext ctx) {
        logger.debug("onMessage: {}", ctx.message());

        callback.onMessage(ctx.message());
    }

    private WsMessageHandler callback;

    @Override
    public void subscribe(WsMessageHandler callback) {
        this.callback = callback;
    }

    @Override
    public void send(String message) {
        if (connections.isEmpty()) return;
        for (var ctx : connections.keySet()) {
            if (ctx.session.isOpen()) {
                ctx.send(message);
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (connections.isEmpty()) return;
        for (var ctx : connections.keySet()) {
            if (ctx.session.isOpen()) {
                ctx.session.close(1001, "Service stopped");
            }
        }
        connections.clear();
    }
}

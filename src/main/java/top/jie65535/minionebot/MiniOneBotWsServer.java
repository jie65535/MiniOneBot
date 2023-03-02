package top.jie65535.minionebot;

import io.javalin.Javalin;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.CloseStatus;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MiniOneBotWsServer implements WsStream {

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
//        var map = JsonUtils.decode(ctx.message(), Map.class);
//        // 消息事件上报
//        if (Objects.equals(map.get("post_type"), "message")) {
//            // 群消息上报 https://docs.go-cqhttp.org/event/#%E7%BE%A4%E6%B6%88%E6%81%AF
//            if (Objects.equals(map.get("message_type"), "group")
//                    && Objects.equals(map.get("sub_type"), "normal")) {
//                // 检查群号
//                var groupId = (Long)map.get("group_id");
//                if (Objects.equals(config.groupId, groupId)) {
////                    var message = (List<Map<?, ?>>)map.get("message");
//                    var rawMessage = map.get("raw_message").toString();
//
//                    // 发送者信息 https://docs.go-cqhttp.org/reference/data_struct.html#post-message-messagesender
//                    var sender = (Map<?, ?>) map.get("sender");
//                    var senderId = sender.get("user_id").toString();
//                    var senderNickname = sender.get("nickname").toString();
//                    var senderCard = sender.get("card").toString();
//                    chatSystem.broadcastChatMessage(config.groupToGameFormat
//                            .replace("{card}", senderCard)
//                            .replace("{id}", senderId)
//                            .replace("{nickname}", senderNickname)
//                            .replace("{message}", rawMessage));
//                }
//            }
//        }
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
}

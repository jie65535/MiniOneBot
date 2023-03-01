package top.jie65535.minionebot;

import io.javalin.Javalin;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MiniOneBotWsServer {

    private final Logger logger;
    private final Map<WsContext, String> connections = new ConcurrentHashMap<>();
    public MiniOneBotWsServer(@NotNull Javalin javalin, @NotNull String path, @NotNull Logger logger) {
        this.logger = logger;
        javalin.ws(path, ws -> {
            ws.onConnect(this::onConnect);
            ws.onClose(this::onClose);
            ws.onError(this::onError);
            ws.onMessage(this::onMessage);
        });

        logger.info("WebSocket server started at {}", path);
    }

    public void onConnect(@NotNull WsConnectContext ctx) {
        logger.debug("onConnect: address={} headers={}", ctx.session.getRemoteAddress(), ctx.headerMap());
        var selfId = ctx.header("X-Self-ID");
        if (selfId != null && !selfId.isEmpty()) {
            logger.info("Bot [{}] WebSocket connected", selfId);
        } else {
            logger.info("[{}] WebSocket connected", ctx.session.getRemoteAddress());
        }
        connections.put(ctx, selfId);
    }

    public void onClose(@NotNull WsCloseContext ctx) {
        logger.debug("onClose: address={} status={} reason={}", ctx.session.getRemoteAddress(), ctx.status(), ctx.reason());
        var selfId = connections.remove(ctx);
        if (selfId != null && !selfId.isEmpty()) {
            logger.info("Bot [{}] WebSocket disconnected", selfId);
        } else {
            logger.info("[{}] WebSocket disconnected", ctx.session.getRemoteAddress());
        }
    }

    public void onError(@NotNull WsErrorContext ctx) {
        logger.warn("onError: address={}", ctx.session.getRemoteAddress(), ctx.error());
        var selfId = connections.remove(ctx);
        if (selfId != null && !selfId.isEmpty()) {
            logger.info("Bot [{}] WebSocket disconnected", selfId);
        } else {
            logger.info("[{}] WebSocket disconnected", ctx.session.getRemoteAddress());
        }
    }

    public void onMessage(@NotNull WsMessageContext ctx) {
        logger.debug("onMessage: {}", ctx.message());

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
}

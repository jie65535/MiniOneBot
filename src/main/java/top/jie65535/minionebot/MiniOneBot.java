package top.jie65535.minionebot;

import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.jie65535.minionebot.events.GroupMessageHandler;
import top.jie65535.minionebot.events.GuildChannelMessageHandler;

import java.net.URI;

public class MiniOneBot implements WsStream.WsMessageHandler {
    private final Logger logger = LoggerFactory.getLogger("Console");
    private final Javalin javalin;
    private final String token;
    private MiniOneBotWsServer server;
    private MiniOneBotWsClient client;

    public MiniOneBot(Javalin javalin, String token) {
        this.javalin = javalin;
        this.token = token;
        logger.info("Test");
    }

    // region WebSocket

    public void startWsServer(String path) {
        if (server == null) {
            logger.info("Start MiniOneBot WebSocket Server");
            server = new MiniOneBotWsServer(javalin, path, token, logger);
            server.subscribe(this);
        }
    }

    public void startWsClient(URI serverUri) {
        if (client == null) {
            logger.info("Start MiniOneBot WebSocket Client");
            client = MiniOneBotWsClient.create(serverUri, token, logger);
            client.subscribe(this);
        }
    }

    private void sendMessageToAll(String message) {
        server.send(message);
        client.send(message);
    }

    @Override
    public void onMessage(String message) {
        // TODO
    }

    // endregion

    // region Message API

    public void sendMessage(String sender, String message) {
        // TODO
    }

    GroupMessageHandler groupMessageHandler;
    public void subscribeGroupMessageEvent(GroupMessageHandler handler) {
        groupMessageHandler = handler;
    }

    GuildChannelMessageHandler guildChannelMessageHandler;
    public void subscribeGuildChannelMessageEvent(GuildChannelMessageHandler handler) {
        guildChannelMessageHandler = handler;
    }

    // endregion

    // region Utils

    private static String escape(String msg) {
        return msg.replace("&", "&amp;")
                .replace("[", "&#91;")
                .replace("]", "&#93;")
                .replace(",", "&#44;");
    }

    private static String unescape(String msg) {
        return msg.replace("&amp;", "&")
                .replace("&#91;", "[")
                .replace("&#93;", "]")
                .replace("&#44;", ",");
    }

    // endregion
}

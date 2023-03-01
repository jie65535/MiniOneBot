package top.jie65535.minionebot;

import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.jie65535.minionebot.events.GroupMessageHandler;
import top.jie65535.minionebot.events.GuildChannelMessageHandler;

public class MiniOneBot {
    private final Logger logger = LoggerFactory.getLogger("Console");
    private final Javalin javalin;
    private MiniOneBotWsServer server;
    private MiniOneBotWsClient client;
    public MiniOneBot(Javalin javalin) {
        this.javalin = javalin;
        logger.info("Test");
    }

    public void startWsServer(String path) {
        if (server == null) {
            server = new MiniOneBotWsServer(javalin, path, logger);
        }
    }

    public void startWsClient(String wsUrl) {
        if (client == null) {
            client = new MiniOneBotWsClient(wsUrl, logger);
        }
    }

    private static String escape(String msg){
        return msg.replace("&", "&amp;")
                .replace("[", "&#91;")
                .replace("]", "&#93;")
                .replace(",", "&#44;");
    }

    private static String unescape(String msg){
        return msg.replace("&amp;", "&")
                .replace("&#91;", "[")
                .replace("&#93;", "]")
                .replace("&#44;", ",");
    }

    public void sendMessage(String sender, String message) {
        
    }

    public void subscribeGroupMessageEvent(GroupMessageHandler handler) {

    }

    public void subscribeGuildChannelMessageEvent(GuildChannelMessageHandler handler) {

    }
}

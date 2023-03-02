package top.jie65535.minionebot;

import io.javalin.Javalin;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//        System.out.println("Hello world!");
//
//        // {"group_id":685816675,"raw_message":"Go","sender":{"area":"","role":"member","level":"108","user_id":840465812,"sex":"unknown","nickname":"筱傑","title":"加油啊5号马","age":22,"card":""},"sub_type":"normal","user_id":840465812,"self_id":1379892815,"message_id":-518394285,"message_type":"group","post_type":"message","time":1677306537,"message":"Go","font":0}
//        var gson = new Gson();
//        var json = "{\"type\":\"message\",\"data\":{\"type\":\"text\",\"data\":\"Hello world!\"}}";
//        var obj = gson.fromJson(json, JsonObject.class);
//        System.out.println(obj.get("type").getAsString());
//        var data = obj.get("data").getAsJsonObject();
//        System.out.println(data.get("type").getAsString());
//        System.out.println(data.get("data").getAsString());
//
//        var map = gson.fromJson(json, Map.class);
//        System.out.println(map);
//        System.out.println(Objects.equals(map.get("type"), "message"));
//        var dataMap = (Map<?,?>) map.get("data");
//        System.out.println(Objects.equals(dataMap.get("type"), "text"));
//        System.out.println((String)dataMap.get("data"));


        Javalin app = Javalin.create().start(7070);
        var bot = new MiniOneBot(app, "HelloOneBot");
        bot.startWsServer("/openchat");
        try {
            bot.startWsClient(new URI("ws://localhost:8080"));
        } catch (URISyntaxException ignored) {
            System.out.println("Uri Syntax error!");
        }
        bot.subscribeGroupMessageEvent(event -> System.out.println(event.message));
        bot.subscribeGuildChannelMessageEvent(event -> System.out.println(event.message));

        var scanner = new Scanner(System.in);
        while (true) {
            var input = scanner.nextLine();
            if (input.isEmpty() || input.equals("exit") || input.equals("quit")) {
                break;
            }

            bot.sendMessage("Console", input);
        }
    }
}
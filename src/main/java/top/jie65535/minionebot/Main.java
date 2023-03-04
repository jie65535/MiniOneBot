package top.jie65535.minionebot;

import io.javalin.Javalin;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7070);
        var bot = new MiniOneBot(app, "HelloOneBot");
        bot.startWsServer("/openchat");
        try {
            bot.startWsClient(new URI("ws://localhost:8080"));
        } catch (URISyntaxException ignored) {
            System.out.println("Uri Syntax error!");
        }
        bot.subscribeGroupMessageEvent(event -> System.out.printf("[QQ]<%s> %s%n", event.senderCardOrNickname(), event.message()));
        bot.subscribeGuildChannelMessageEvent(event -> System.out.printf("[Cl]<%s> %s%n", event.senderName(), event.message()));

        var scanner = new Scanner(System.in);
        while (true) {
            var input = scanner.nextLine();
            if (input.isEmpty() || input.equals("stop") || input.equals("exit") || input.equals("quit")) {
                break;
            }

            bot.sendGroupMessage(0, "[MOB] Console: " + input);
        }
        System.out.println("Ending...");
        bot.stop();
        app.stop();
        System.out.println("Ended...");
    }
}
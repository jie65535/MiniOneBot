package top.jie65535.minionebot;

public interface WsStream {
    void subscribe(WsMessageHandler callback);

    void send(String message);

    interface WsMessageHandler {
        void onMessage(String message);
    }
}

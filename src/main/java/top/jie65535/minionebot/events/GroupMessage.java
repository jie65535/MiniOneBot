package top.jie65535.minionebot.events;

public record GroupMessage(
        long groupId,
        String message,
        long senderId,
        String senderCardOrNickname,
        String senderLevel,
        String senderRole,
        String senderTitle
) {
}

package top.jie65535.minionebot.events;

public record GuildChannelMessage(
        String guildId,
        String channelId,
        String message,
        String senderId,
        String senderName
) {
}

package com.rugzar.discordbot.events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageCache extends ListenerAdapter {

    private static final Map<String, CachedMessage> CACHE = new ConcurrentHashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (!event.isFromGuild()) return;
        if (event.getAuthor().isBot()) return;

        cacheMessage(
                event.getMessageId(),
                event.getAuthor().getAsMention(),
                event.getMessage().getContentDisplay()
        );
    }

    public static void cacheMessage(
            String messageId,
            String author,
            String content
    ) {
        CACHE.put(messageId, new CachedMessage(author, content));

        if (CACHE.size() > 5000) {
            CACHE.keySet()
                    .stream()
                    .limit(1000)
                    .forEach(CACHE::remove);
        }
    }

    public static String getCachedContent(String messageId) {
        CachedMessage message = CACHE.get(messageId);
        return message != null ? message.content : null;
    }

    private static class CachedMessage {
        final String author;
        final String content;

        CachedMessage(String author, String content) {
            this.author = author;
            this.content = content;
        }
    }
}

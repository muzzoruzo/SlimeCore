package com.rugzar.discordbot.events;

import com.rugzar.discordbot.logging.LogManager;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageDeleteLog extends ListenerAdapter {

    private static final Map<String, CachedMessage> CACHE = new ConcurrentHashMap<>();

    public static void cacheMessage(
            String messageId,
            String author,
            String content
    ) {
        CACHE.put(
                messageId,
                new CachedMessage(author, content)
        );

        // Belleğin gereksiz büyümesini önle
        if (CACHE.size() > 5000) {
            CACHE.keySet().stream()
                    .limit(1000)
                    .forEach(CACHE::remove);
        }
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {

        if (event.getGuild() == null) return;

        CachedMessage message = CACHE.remove(event.getMessageId());

        String author = message != null
                ? message.author
                : "Bilinmiyor";

        String content = message != null && !message.content.isBlank()
                ? message.content
                : "Mesaj içeriği alınamadı.";

        String channelMention = "<#" + event.getChannel().getId() + ">";

        LogManager.send(
                event.getJDA(),
                event.getGuild().getId(),
                "🗑️ Mesaj Silindi",
                "👤 **Kullanıcı:** " + author
                        + "\n📍 **Kanal:** " + channelMention
                        + "\n💬 **Mesaj:** " + content
        );
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

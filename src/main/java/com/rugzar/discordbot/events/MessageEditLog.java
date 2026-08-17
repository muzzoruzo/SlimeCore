package com.rugzar.discordbot.events;

import com.rugzar.discordbot.logging.LogManager;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageEditLog extends ListenerAdapter {

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {

        if (!event.isFromGuild()) return;
        if (event.getAuthor().isBot()) return;

        String oldContent = MessageCache.getCachedContent(event.getMessageId());

        String newContent = event.getMessage().getContentDisplay();

        if (oldContent == null) {
            oldContent = "Eski mesaj içeriği alınamadı.";
        }

        if (oldContent.equals(newContent)) return;

        LogManager.send(
                event.getJDA(),
                event.getGuild().getId(),
                "✏️ Mesaj Düzenlendi",
                "👤 **Kullanıcı:** " + event.getAuthor().getAsMention()
                        + "\n📍 **Kanal:** <#" + event.getChannel().getId() + ">"
                        + "\n📝 **Eski mesaj:** " + oldContent
                        + "\n📝 **Yeni mesaj:** " + newContent
        );

        MessageCache.cacheMessage(
                event.getMessageId(),
                event.getAuthor().getAsMention(),
                newContent
        );
    }
}

package com.rugzar.discordbot.events;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TicketTranscript {

    public static String create(TextChannel channel, List<Message> messages) {
        StringBuilder transcript = new StringBuilder();

        transcript.append("========================================\n");
        transcript.append("DISCORD TICKET TRANSCRIPT\n");
        transcript.append("Kanal: ").append(channel.getName()).append("\n");
        transcript.append("========================================\n\n");

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        for (Message message : messages) {
            transcript.append("[")
                    .append(message.getTimeCreated()
                            .format(formatter))
                    .append("] ");

            transcript.append(message.getAuthor().getName())
                    .append(": ");

            String content = message.getContentDisplay();

            if (content == null || content.isBlank()) {
                content = "[Mesaj içeriği yok / ek dosya veya embed]";
            }

            transcript.append(content)
                    .append("\n");
        }

        return transcript.toString();
    }
}

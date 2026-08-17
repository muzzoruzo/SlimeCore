package com.rugzar.discordbot.logging;

import com.rugzar.discordbot.database.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LogManager {

    public static void send(
            JDA jda,
            String guildId,
            String title,
            String description
    ) {

        String sql = "SELECT log_channel_id FROM guild_settings WHERE guild_id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, guildId);

            try (ResultSet result = statement.executeQuery()) {

                if (!result.next()) return;

                String channelId = result.getString("log_channel_id");

                if (channelId == null || channelId.isBlank()) return;

                TextChannel channel = jda.getTextChannelById(channelId);

                if (channel == null) return;

                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle(title)
                        .setDescription(description)
                        .setTimestamp(java.time.Instant.now());

                channel.sendMessageEmbeds(embed.build()).queue();
            }

        } catch (Exception e) {
            System.err.println("Log gönderilemedi!");
            e.printStackTrace();
        }
    }
}

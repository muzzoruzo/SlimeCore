package com.rugzar.discordbot.events;

import com.rugzar.discordbot.database.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;

public class GoodbyeEvent extends ListenerAdapter {

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {

        String sql = "SELECT goodbye_channel_id FROM guild_settings WHERE guild_id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, event.getGuild().getId());

            try (ResultSet result = statement.executeQuery()) {

                if (!result.next()) return;

                String channelId = result.getString("goodbye_channel_id");

                if (channelId == null || channelId.isBlank()) return;

                var channel = event.getJDA().getTextChannelById(channelId);

                if (channel == null) return;

                var user = event.getUser();
                var guild = event.getGuild();

                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("👋 Güle Güle!")
                        .setDescription(
                                "**" + user.getName() +
                                "** sunucudan ayrıldı.\n\n" +
                                "Seni özleyeceğiz! 👋"
                        )
                        .addField(
                                "👤 Kullanıcı",
                                user.getName(),
                                true
                        )
                        .addField(
                                "👥 Kalan Üye",
                                String.valueOf(guild.getMemberCount()),
                                true
                        )
                        .addField(
                                "🏠 Sunucu",
                                guild.getName(),
                                true
                        )
                        .setThumbnail(user.getEffectiveAvatarUrl())
                        .setTimestamp(Instant.now());

                channel.sendMessageEmbeds(embed.build()).queue();
            }

        } catch (Exception e) {
            System.err.println("Güle güle mesajı gönderilemedi!");
            e.printStackTrace();
        }
    }
}

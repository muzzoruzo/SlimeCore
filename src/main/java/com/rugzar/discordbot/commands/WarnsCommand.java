package com.rugzar.discordbot.commands;

import com.rugzar.discordbot.database.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WarnsCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equals("warns")) return;

        if (!event.isFromGuild()) {
            event.reply("❌ Bu komut sadece sunucularda kullanılabilir.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (!event.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("❌ Bu komutu kullanmak için moderasyon yetkisine sahip olmalısın.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        Member target = event.getOption("kullanici").getAsMember();

        if (target == null) {
            event.reply("❌ Geçerli bir kullanıcı seçmelisin.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String sql = """
                SELECT reason, moderator_id
                FROM warnings
                WHERE guild_id = ? AND user_id = ?
                ORDER BY id DESC
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, event.getGuild().getId());
            statement.setString(2, target.getId());

            try (ResultSet result = statement.executeQuery()) {

                StringBuilder text = new StringBuilder();
                int count = 0;

                while (result.next()) {
                    count++;

                    String reason = result.getString("reason");
                    String moderatorId = result.getString("moderator_id");

                    text.append("**")
                            .append(count)
                            .append(".** ")
                            .append(reason)
                            .append("\n")
                            .append("👮 Yetkili: <@")
                            .append(moderatorId)
                            .append(">\n\n");
                }

                if (count == 0) {
                    event.reply("✅ Bu kullanıcının hiç uyarısı bulunmuyor.")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("⚠️ Uyarı Geçmişi")
                        .setDescription("👤 " + target.getAsMention())
                        .addField("🔢 Toplam Uyarı", String.valueOf(count), false)
                        .addField("📋 Uyarılar", text.toString(), false);

                event.replyEmbeds(embed.build()).queue();
            }

        } catch (Exception e) {
            e.printStackTrace();

            event.reply("❌ Uyarılar alınırken veritabanı hatası oluştu.")
                    .setEphemeral(true)
                    .queue();
        }
    }
}

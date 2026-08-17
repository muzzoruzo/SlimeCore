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

public class WarnCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equals("warn")) return;

        if (!event.isFromGuild()) {
            event.reply("❌ Bu komut sadece sunucularda kullanılabilir.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (!event.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("❌ Bu komutu kullanmak için **Üyeleri Zaman Aşımına Uğrat** yetkisine sahip olmalısın.")
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

        if (target.equals(event.getMember())) {
            event.reply("❌ Kendine uyarı veremezsin.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String reason = event.getOption("sebep").getAsString();

        String sql = """
                INSERT INTO warnings
                (guild_id, user_id, moderator_id, reason, created_at)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, event.getGuild().getId());
            statement.setString(2, target.getId());
            statement.setString(3, event.getMember().getId());
            statement.setString(4, reason);
            statement.setLong(5, System.currentTimeMillis());

            statement.executeUpdate();

            int count = getWarningCount(
                    connection,
                    event.getGuild().getId(),
                    target.getId()
            );

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️ Kullanıcı Uyarıldı")
                    .addField("👤 Kullanıcı", target.getAsMention(), true)
                    .addField("🛡️ Yetkili", event.getMember().getAsMention(), true)
                    .addField("🔢 Uyarı Sayısı", String.valueOf(count), true)
                    .addField("📝 Sebep", reason, false);

            event.replyEmbeds(embed.build()).queue();

        } catch (Exception e) {
            e.printStackTrace();

            event.reply("❌ Uyarı veritabanına kaydedilirken bir hata oluştu.")
                    .setEphemeral(true)
                    .queue();
        }
    }

    private int getWarningCount(
            Connection connection,
            String guildId,
            String userId
    ) throws Exception {

        String sql = """
                SELECT COUNT(*) FROM warnings
                WHERE guild_id = ? AND user_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, guildId);
            statement.setString(2, userId);

            try (ResultSet result = statement.executeQuery()) {
                return result.getInt(1);
            }
        }
    }
}

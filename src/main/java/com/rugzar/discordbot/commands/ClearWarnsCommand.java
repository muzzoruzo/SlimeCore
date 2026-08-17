package com.rugzar.discordbot.commands;

import com.rugzar.discordbot.database.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ClearWarnsCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equals("clearwarns")) return;

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
                DELETE FROM warnings
                WHERE guild_id = ? AND user_id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, event.getGuild().getId());
            statement.setString(2, target.getId());

            int deleted = statement.executeUpdate();

            if (deleted == 0) {
                event.reply("ℹ️ Bu kullanıcının silinecek bir uyarısı bulunmuyor.")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("🗑️ Uyarılar Temizlendi")
                    .addField("👤 Kullanıcı", target.getAsMention(), true)
                    .addField("🔢 Silinen Uyarı", String.valueOf(deleted), true)
                    .addField("🛡️ Yetkili", event.getMember().getAsMention(), true);

            event.replyEmbeds(embed.build()).queue();

        } catch (Exception e) {
            e.printStackTrace();

            event.reply("❌ Uyarılar silinirken veritabanı hatası oluştu.")
                    .setEphemeral(true)
                    .queue();
        }
    }
}

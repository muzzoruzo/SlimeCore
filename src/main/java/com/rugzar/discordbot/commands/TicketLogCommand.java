package com.rugzar.discordbot.commands;

import com.rugzar.discordbot.database.Database;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TicketLogCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equals("ticketlog")) return;

        if (!event.isFromGuild()) {
            event.reply("❌ Bu komut sadece sunucularda kullanılabilir.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            event.reply("❌ Bu komutu kullanmak için **Sunucuyu Yönet** yetkisine sahip olmalısın.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (event.getOption("kanal") == null) {
            event.reply("❌ Bir log kanalı seçmelisin.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        TextChannel channel =
                event.getOption("kanal").getAsChannel().asTextChannel();

        String sql = """
                INSERT INTO guild_settings (
                    guild_id,
                    ticket_log_channel_id
                )
                VALUES (?, ?)
                ON CONFLICT(guild_id)
                DO UPDATE SET
                    ticket_log_channel_id =
                        excluded.ticket_log_channel_id
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, event.getGuild().getId());
            statement.setString(2, channel.getId());

            statement.executeUpdate();

            event.reply(
                    "✅ Ticket transcript kanalı ayarlandı: "
                            + channel.getAsMention()
            ).setEphemeral(true).queue();

        } catch (Exception e) {
            e.printStackTrace();

            event.reply(
                    "❌ Ticket log kanalı ayarlanırken bir hata oluştu."
            ).setEphemeral(true).queue();
        }
    }
}

package com.rugzar.discordbot.commands;

import com.rugzar.discordbot.database.Database;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TicketRoleCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equals("ticketrol")) return;

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

        String islem = event.getOption("islem").getAsString();
        Role role = event.getOption("rol").getAsRole();

        try (Connection connection = Database.getConnection()) {

            if (islem.equals("ekle")) {

                String sql = """
                        INSERT OR IGNORE INTO ticket_support_roles
                        (guild_id, role_id)
                        VALUES (?, ?)
                        """;

                try (PreparedStatement statement = connection.prepareStatement(sql)) {

                    statement.setString(1, event.getGuild().getId());
                    statement.setString(2, role.getId());

                    int affected = statement.executeUpdate();

                    if (affected == 0) {
                        event.reply("ℹ️ " + role.getAsMention()
                                + " zaten destek rollerinde.")
                                .setEphemeral(true)
                                .queue();
                    } else {
                        event.reply("✅ " + role.getAsMention()
                                + " ticket destek rollerine eklendi.")
                                .queue();
                    }
                }

            } else if (islem.equals("kaldir")) {

                String sql = """
                        DELETE FROM ticket_support_roles
                        WHERE guild_id = ? AND role_id = ?
                        """;

                try (PreparedStatement statement = connection.prepareStatement(sql)) {

                    statement.setString(1, event.getGuild().getId());
                    statement.setString(2, role.getId());

                    int affected = statement.executeUpdate();

                    if (affected == 0) {
                        event.reply("ℹ️ " + role.getAsMention()
                                + " zaten destek rollerinde değil.")
                                .setEphemeral(true)
                                .queue();
                    } else {
                        event.reply("✅ " + role.getAsMention()
                                + " ticket destek rollerinden kaldırıldı.")
                                .queue();
                    }
                }

            } else {

                event.reply("❌ İşlem olarak **ekle** veya **kaldir** seçmelisin.")
                        .setEphemeral(true)
                        .queue();
            }

        } catch (Exception e) {

            e.printStackTrace();

            event.reply("❌ Destek rolü kaydedilirken bir hata oluştu.")
                    .setEphemeral(true)
                    .queue();
        }
    }
}

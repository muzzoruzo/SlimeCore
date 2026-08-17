package com.rugzar.discordbot.commands;

import com.rugzar.discordbot.database.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TicketSetupCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equals("ticketkur")) return;

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

        var kategoriChannel = event.getOption("kategori").getAsChannel();
        var panelChannel = event.getOption("kanal").getAsChannel();
        Role supportRole = event.getOption("destek_rolu").getAsRole();

        if (kategoriChannel.getType() != ChannelType.CATEGORY) {
            event.reply("❌ **Kategori** kısmında bir kategori seçmelisin.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (panelChannel.getType() != ChannelType.TEXT) {
            event.reply("❌ **Kanal** kısmında bir yazı kanalı seçmelisin.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        Category category = kategoriChannel.asCategory();
        TextChannel channel = panelChannel.asTextChannel();

        String sql = """
                INSERT INTO guild_settings (
                    guild_id,
                    ticket_category_id,
                    ticket_panel_channel_id,
                    ticket_support_role_id
                )
                VALUES (?, ?, ?, ?)
                ON CONFLICT(guild_id)
                DO UPDATE SET
                    ticket_category_id = excluded.ticket_category_id,
                    ticket_panel_channel_id = excluded.ticket_panel_channel_id,
                    ticket_support_role_id = excluded.ticket_support_role_id
                """;

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, event.getGuild().getId());
            statement.setString(2, category.getId());
            statement.setString(3, channel.getId());
            statement.setString(4, supportRole.getId());

            statement.executeUpdate();

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("🎫 Destek Sistemi")
                    .setDescription(
                            "Yardıma ihtiyacın varsa aşağıdaki butona basarak "
                                    + "bir destek talebi oluşturabilirsin.\n\n"
                                    + "🎫 **Ticket Aç** butonuna bas."
                    );

            channel.sendMessageEmbeds(embed.build())
                    .setComponents(
                            ActionRow.of(
                                    Button.primary(
                                            "ticket:open",
                                            "🎫 Ticket Aç"
                                    )
                            )
                    )
                    .queue();

            event.reply(
                    "✅ Ticket sistemi başarıyla kuruldu!\n"
                            + "📁 Kategori: " + category.getAsMention() + "\n"
                            + "📢 Panel: " + channel.getAsMention() + "\n"
                            + "🛡️ Destek rolü: " + supportRole.getAsMention()
            ).setEphemeral(true).queue();

        } catch (Exception e) {

            e.printStackTrace();

            event.reply("❌ Ticket sistemi kurulurken bir hata oluştu.")
                    .setEphemeral(true)
                    .queue();
        }
    }
}

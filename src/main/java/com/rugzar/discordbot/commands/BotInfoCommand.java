package com.rugzar.discordbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotInfoCommand extends ListenerAdapter {

    private final long startTime;

    public BotInfoCommand(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equals("botinfo")) return;

        long uptime = System.currentTimeMillis() - startTime;

        long seconds = uptime / 1000;
        long days = seconds / 86400;
        seconds %= 86400;

        long hours = seconds / 3600;
        seconds %= 3600;

        long minutes = seconds / 60;
        seconds %= 60;

        String uptimeText = String.format(
                "%d gün, %d saat, %d dakika, %d saniye",
                days,
                hours,
                minutes,
                seconds
        );

        int guildCount =
                event.getJDA().getGuilds().size();

        int userCount =
                event.getJDA()
                        .getGuilds()
                        .stream()
                        .mapToInt(guild -> guild.getMemberCount())
                        .sum();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("🟢 SlimeCore")
                .setDescription(
                        "🚀 **SlimeCore**, sunucunuzu yönetmek ve "
                        + "sunucunuzu yönetmenize yardımcı olan Discord botudur."
                )

                .addField(
                        "📦 Sürüm",
                        "**v1.5.0**",
                        true
                )

                .addField(
                        "🌐 Sunucular",
                        String.valueOf(guildCount),
                        true
                )

                .addField(
                        "👥 Kullanıcılar",
                        String.valueOf(userCount),
                        true
                )

                .addField(
                        "⏱️ Çalışma Süresi",
                        uptimeText,
                        false
                )

                .addField(
                        "☕ Java",
                        "Java 21",
                        true
                )

                .addField(
                        "⚙️ Altyapı",
                        "JDA 6.5.0",
                        true
                )

                .addField(
                        "🎫 Sistemler",
                        "Ticket • Moderasyon • Log • Karşılama",
                        false
                )

                .setFooter(
                        "SlimeCore • v1.5.0"
                );

        event.replyEmbeds(embed.build()).queue();
    }
}

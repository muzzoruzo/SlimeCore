package com.rugzar.discordbot.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PingCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("ping")) return;

        event.deferReply().queue(hook -> {
            JDA jda = event.getJDA();

            long gatewayPing = jda.getGatewayPing();

            String durum;

            if (gatewayPing < 80) {
                durum = "🟢 Çok iyi";
            } else if (gatewayPing < 150) {
                durum = "🟡 İyi";
            } else if (gatewayPing < 250) {
                durum = "🟠 Orta";
            } else {
                durum = "🔴 Yüksek";
            }

            hook.editOriginal(
                    "🏓 **Pong!**\n\n" +
                    "📡 Gateway: **" + gatewayPing + " ms**\n" +
                    "⚡ Durum: **" + durum + "**"
            ).queue();
        });
    }
}

package com.rugzar.discordbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class HelpCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equals("help")) return;

        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("🤖 Bot Yardım Menüsü");
        embed.setDescription(
                "**SlimeCore • Sürüm 1.5.0**\n\n" +

                "Botun tüm aktif komutları aşağıdadır.\n\n" +

                "🏓 **Genel**\n" +
                "`/help` — Yardım menüsünü gösterir.\n" +
                "`/ping` — Botun gecikmesini gösterir.\n" +
                "`/botinfo` — Bot hakkında bilgi verir.\n\n" +

                "🛡️ **Moderasyon**\n" +
                "`/kick` — Bir kullanıcıyı sunucudan atar.\n" +
                "`/ban` — Bir kullanıcıyı sunucudan yasaklar.\n" +
                "`/mute` — Bir kullanıcıyı belirlenen süre boyunca susturur.\n" +
                "`/unmute` — Kullanıcının susturmasını kaldırır.\n" +
                "`/warn` — Bir kullanıcıya uyarı verir.\n" +
                "`/warns` — Kullanıcının uyarı geçmişini gösterir.\n" +
                "`/clearwarns` — Kullanıcının tüm uyarılarını temizler.\n" +
                "`/logayarla` — Moderasyon log kanalını ayarlar.\n\n" +

                "🎫 **Ticket Sistemi**\n" +
                "`/ticketkur` — Ticket sistemini kurar.\n" +
                "`/ticketlog` — Ticket transcript log kanalını ayarlar.\n" +
                "`/ticketrol` — Ticket destek rollerini yönetir.\n\n" +

                "🎟️ **Ticket Özellikleri**\n" +
                "📢 Yetkili Çağır — Destek rollerini ticket kanalına çağırır.\n" +
                "🔒 Ticket Kapat — Ticketı kapatır.\n" +
                "⭐ Değerlendirme — 1–5 yıldız ile destek puanlanabilir.\n" +
                "💬 Yorum Yaz — Ticket hakkında yorum bırakılabilir.\n" +
                "📨 Formu Gönder — Değerlendirmeyi gönderir.\n" +
                "📄 Transcript — Ticket geçmişini log kanalına kaydeder.\n\n" +

                "👋 **Sunucu**\n" +
                "`/hosgeldinayarla` — Hoş geldin kanalını ayarlar.\n" +
                "`/guleguleayarla` — Güle güle kanalını ayarlar."
        );

        embed.setFooter("SlimeCore • v1.5.0 • Yardım Sistemi");

        event.replyEmbeds(embed.build()).queue();
    }
}

package com.rugzar.discordbot.commands;

import com.rugzar.discordbot.logging.LogManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Duration;

public class MuteCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equals("mute")) return;

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
            event.reply("❌ Kendini susturamazsın.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (!event.getGuild().getSelfMember().canInteract(target)) {
            event.reply("❌ Bu kullanıcıya zaman aşımı uygulayamıyorum. Rolü botun rolünden yüksek veya eşit.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String sureText = event.getOption("sure").getAsString().toLowerCase().trim();

        Duration duration;

        try {
            duration = parseDuration(sureText);
        } catch (IllegalArgumentException e) {
            event.reply("❌ Geçersiz süre! Örnekler: `2m`, `30m`, `2h`, `1d`")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (duration.isZero() || duration.isNegative()) {
            event.reply("❌ Süre 0'dan büyük olmalı.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (duration.compareTo(Duration.ofDays(28)) > 0) {
            event.reply("❌ Timeout süresi en fazla **28 gün** olabilir.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String reason = event.getOption("sebep") != null
                ? event.getOption("sebep").getAsString()
                : "Sebep belirtilmedi.";

        target.timeoutFor(duration)
                .reason(reason)
                .queue(
                        success -> {

                            String formattedDuration = formatDuration(duration);

                            EmbedBuilder embed = new EmbedBuilder()
                                    .setTitle("🔇 Kullanıcı Susturuldu")
                                    .addField("👤 Kullanıcı", target.getAsMention(), true)
                                    .addField("🛡️ Yetkili", event.getMember().getAsMention(), true)
                                    .addField("⏱️ Süre", formattedDuration, true)
                                    .addField("📝 Sebep", reason, false);

                            event.replyEmbeds(embed.build()).queue();

                            LogManager.send(
                                    event.getJDA(),
                                    event.getGuild().getId(),
                                    "🔇 Kullanıcı Susturuldu",
                                    "👤 **Kullanıcı:** " + target.getAsMention()
                                            + "\n🛡️ **Yetkili:** " + event.getMember().getAsMention()
                                            + "\n⏱️ **Süre:** " + formattedDuration
                                            + "\n📝 **Sebep:** " + reason
                            );
                        },
                        error -> event.reply(
                                        "❌ Kullanıcı susturulurken bir hata oluştu."
                                )
                                .setEphemeral(true)
                                .queue()
                );
    }

    private static Duration parseDuration(String input) {

        if (input.length() < 2) {
            throw new IllegalArgumentException();
        }

        String numberPart = input.substring(0, input.length() - 1);
        char unit = input.charAt(input.length() - 1);

        long number;

        try {
            number = Long.parseLong(numberPart);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }

        if (number <= 0) {
            throw new IllegalArgumentException();
        }

        return switch (unit) {
            case 'm' -> Duration.ofMinutes(number);
            case 'h' -> Duration.ofHours(number);
            case 'd' -> Duration.ofDays(number);
            default -> throw new IllegalArgumentException();
        };
    }

    private static String formatDuration(Duration duration) {

        long totalMinutes = duration.toMinutes();

        long days = totalMinutes / (60 * 24);
        long hours = (totalMinutes % (60 * 24)) / 60;
        long minutes = totalMinutes % 60;

        StringBuilder result = new StringBuilder();

        if (days > 0) {
            result.append(days).append(" gün");
        }

        if (hours > 0) {
            if (result.length() > 0) result.append(" ");
            result.append(hours).append(" saat");
        }

        if (minutes > 0) {
            if (result.length() > 0) result.append(" ");
            result.append(minutes).append(" dakika");
        }

        return result.toString();
    }
}

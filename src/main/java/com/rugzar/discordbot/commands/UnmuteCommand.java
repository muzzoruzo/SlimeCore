package com.rugzar.discordbot.commands;

import com.rugzar.discordbot.logging.LogManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UnmuteCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equals("unmute")) return;

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

        if (!event.getGuild().getSelfMember().canInteract(target)) {
            event.reply("❌ Bu kullanıcının timeout'unu kaldıramıyorum. Rolü botun rolünden yüksek veya eşit.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        target.removeTimeout()
                .reason("Timeout kaldırıldı: " + event.getMember().getUser().getName())
                .queue(
                        success -> {
                            EmbedBuilder embed = new EmbedBuilder()
                                    .setTitle("🔊 Kullanıcının Susturması Kaldırıldı")
                                    .addField("👤 Kullanıcı", target.getAsMention(), true)
                                    .addField("🛡️ Yetkili", event.getMember().getAsMention(), true);

                            event.replyEmbeds(embed.build()).queue();

                            LogManager.send(
                                    event.getJDA(),
                                    event.getGuild().getId(),
                                    "🔊 Susturma Kaldırıldı",
                                    "👤 **Kullanıcı:** " + target.getAsMention()
                                            + "\n🛡️ **Yetkili:** " + event.getMember().getAsMention()
                            );
                        },
                        error -> event.reply("❌ Kullanıcının timeout'u kaldırılırken bir hata oluştu.")
                                .setEphemeral(true)
                                .queue()
                );
    }
}

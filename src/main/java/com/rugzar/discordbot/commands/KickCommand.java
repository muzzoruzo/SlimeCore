package com.rugzar.discordbot.commands;

import com.rugzar.discordbot.logging.LogManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class KickCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equals("kick")) {
            return;
        }

        if (!event.isFromGuild()) {
            event.reply("❌ Bu komut sadece sunucularda kullanılabilir.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (!event.getMember().hasPermission(Permission.KICK_MEMBERS)) {
            event.reply("❌ Bu komutu kullanmak için **Üyeleri At** yetkisine sahip olmalısın.")
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
            event.reply("❌ Kendini atamazsın.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (!event.getGuild().getSelfMember().canInteract(target)) {
            event.reply("❌ Bu kullanıcıyı atamıyorum. Kullanıcının rolü botun rolünden yüksek veya eşit.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String reason = event.getOption("sebep") != null
                ? event.getOption("sebep").getAsString()
                : "Sebep belirtilmedi.";

        target.kick()
                .reason(reason)
                .queue(
                        success -> {

                            EmbedBuilder embed = new EmbedBuilder()
                                    .setTitle("👢 Kullanıcı Atıldı")
                                    .addField("👤 Kullanıcı", target.getAsMention(), true)
                                    .addField("🛡️ Yetkili", event.getMember().getAsMention(), true)
                                    .addField("📝 Sebep", reason, false);

                            event.replyEmbeds(embed.build()).queue();

                            LogManager.send(
                                    event.getJDA(),
                                    event.getGuild().getId(),
                                    "👢 Kullanıcı Atıldı",
                                    "👤 **Kullanıcı:** " + target.getAsMention()
                                            + "\n🛡️ **Yetkili:** " + event.getMember().getAsMention()
                                            + "\n📝 **Sebep:** " + reason
                            );
                        },
                        error -> {
                            event.reply("❌ Kullanıcı atılırken bir hata oluştu.")
                                    .setEphemeral(true)
                                    .queue();
                        }
                );
    }
}

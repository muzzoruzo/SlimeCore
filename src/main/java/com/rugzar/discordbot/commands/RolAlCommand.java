package com.rugzar.discordbot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RolAlCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equals("rolal")) {
            return;
        }

        if (!event.isFromGuild()) {
            event.reply("❌ Bu komut sadece sunucularda kullanılabilir.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        Member executor = event.getMember();

        if (executor == null || !executor.hasPermission(Permission.MANAGE_ROLES)) {
            event.reply("❌ Bu komutu kullanmak için **Rolleri Yönet** yetkisine sahip olmalısın.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        Member target = event.getOption("kullanici").getAsMember();
        Role role = event.getOption("rol").getAsRole();

        if (target == null || role == null) {
            event.reply("❌ Geçerli bir kullanıcı ve rol seçmelisin.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (role.isPublicRole()) {
            event.reply("❌ `@everyone` rolü alınamaz.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (!event.getGuild().getSelfMember().canInteract(role)) {
            event.reply("❌ Bu role dokunamıyorum. Rol, botun en yüksek rolünden yüksek veya eşit.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (!target.getRoles().contains(role)) {
            event.reply("❌ Bu kullanıcıda bu rol zaten yok.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        event.getGuild().removeRoleFromMember(target, role).queue(
                success -> event.reply(
                        "✅ **" + role.getName() + "** rolü " + target.getAsMention() + " kullanıcısından alındı."
                ).queue(),
                error -> event.reply("❌ Rol alınırken bir hata oluştu.")
                        .setEphemeral(true)
                        .queue()
        );
    }
}

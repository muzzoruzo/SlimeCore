package com.rugzar.discordbot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RolVerCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equals("rolver")) {
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
            event.reply("❌ `@everyone` rolü verilemez.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (!event.getGuild().getSelfMember().canInteract(role)) {
            event.reply("❌ Bu rolü veremiyorum. Rol, botun en yüksek rolünden yüksek veya eşit.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (target.getRoles().contains(role)) {
            event.reply("❌ Bu kullanıcıda bu rol zaten var.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        event.getGuild().addRoleToMember(target, role).queue(
                success -> event.reply(
                        "✅ **" + role.getName() + "** rolü " + target.getAsMention() + " kullanıcısına verildi."
                ).queue(),
                error -> event.reply("❌ Rol verilirken bir hata oluştu.")
                        .setEphemeral(true)
                        .queue()
        );
    }
}

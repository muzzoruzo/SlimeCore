package com.rugzar.discordbot;
import com.rugzar.discordbot.database.Database;

import com.rugzar.discordbot.commands.PingCommand;
import com.rugzar.discordbot.commands.HelpCommand;
import com.rugzar.discordbot.commands.BotInfoCommand;
import com.rugzar.discordbot.commands.KickCommand;
import com.rugzar.discordbot.commands.BanCommand;
import com.rugzar.discordbot.commands.MuteCommand;
import com.rugzar.discordbot.commands.UnmuteCommand;
import com.rugzar.discordbot.commands.WarnCommand;
import com.rugzar.discordbot.commands.WarnsCommand;
import com.rugzar.discordbot.commands.ClearWarnsCommand;
import com.rugzar.discordbot.commands.LogSetupCommand;
import com.rugzar.discordbot.commands.TicketSetupCommand;
import com.rugzar.discordbot.commands.TicketLogCommand;
import com.rugzar.discordbot.commands.TicketRoleCommand;
import com.rugzar.discordbot.commands.WelcomeSetupCommand;
import com.rugzar.discordbot.commands.GoodbyeSetupCommand;
import com.rugzar.discordbot.commands.RolVerCommand;
import com.rugzar.discordbot.commands.RolAlCommand;
import com.rugzar.discordbot.events.MessageDeleteLog;
import com.rugzar.discordbot.events.MessageCache;
import com.rugzar.discordbot.events.MessageEditLog;
import com.rugzar.discordbot.events.TicketButtonEvent;
import com.rugzar.discordbot.events.StatusRotator;
import com.rugzar.discordbot.events.TicketRatingEvent;
import com.rugzar.discordbot.events.WelcomeEvent;
import com.rugzar.discordbot.events.GoodbyeEvent;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class Main {

    public static void main(String[] args) throws Exception {
        Database.init();

        String token = System.getenv("DISCORD_TOKEN");

        if (token == null || token.isBlank()) {
            System.err.println("DISCORD_TOKEN bulunamadi!");
            System.exit(1);
        }

        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.MESSAGE_CONTENT
                )
                .addEventListeners(
                        new PingCommand(),
                        new HelpCommand(),
                        new BotInfoCommand(System.currentTimeMillis()),
                        new KickCommand(),
                        new BanCommand(),
                        new MuteCommand(),
                        new UnmuteCommand(),
                        new WarnCommand(),
                        new WarnsCommand(),
                        new ClearWarnsCommand(),
                        new LogSetupCommand(),
                        new TicketSetupCommand(),
                        new TicketLogCommand(),
                        new TicketRoleCommand(),
                        new TicketButtonEvent(),
                        new TicketRatingEvent(),
                        new WelcomeSetupCommand(),
                        new GoodbyeSetupCommand(),
                        new RolVerCommand(),
                        new RolAlCommand(),
                        new MessageCache(),
                        new MessageEditLog(),
                        new MessageDeleteLog(),
                        new WelcomeEvent(),
                        new GoodbyeEvent()
                )
                .build();

        StatusRotator statusRotator = new StatusRotator(jda);
        statusRotator.start();

        jda.awaitReady();
        jda.upsertCommand("hosgeldinayarla", "Hoş geldin mesajının gönderileceği kanalı ayarlar.").addOption(OptionType.CHANNEL, "kanal", "Hoş geldin kanalı", true).queue();
        jda.upsertCommand("guleguleayarla", "Güle güle mesajının gönderileceği kanalı ayarlar.").addOption(OptionType.CHANNEL, "kanal", "Güle güle kanalı", true).queue();
        jda.upsertCommand("mute", "Bir kullanıcıyı belirlenen süre boyunca susturur.").addOption(OptionType.USER, "kullanici", "Susturulacak kullanıcı", true).addOption(OptionType.STRING, "sure", "Süre: 2m, 1h, 1d gibi", true).addOption(OptionType.STRING, "sebep", "Susturma sebebi", false).queue();
        jda.upsertCommand("unmute", "Bir kullanıcının susturmasını kaldırır.").addOption(OptionType.USER, "kullanici", "Susturması kaldırılacak kullanıcı", true).queue();
        jda.upsertCommand("warn", "Bir kullanıcıya uyarı verir.").addOption(OptionType.USER, "kullanici", "Uyarılacak kullanıcı", true).addOption(OptionType.STRING, "sebep", "Uyarı sebebi", true).queue();
        jda.upsertCommand("warns", "Bir kullanıcının uyarı geçmişini gösterir.").addOption(OptionType.USER, "kullanici", "Uyarıları gösterilecek kullanıcı", true).queue();
        jda.upsertCommand("clearwarns", "Bir kullanıcının tüm uyarılarını siler.").addOption(OptionType.USER, "kullanici", "Uyarıları temizlenecek kullanıcı", true).queue();
        jda.upsertCommand("rolver", "Bir kullanıcıya rol verir.").addOption(OptionType.USER, "kullanici", "Rol verilecek kullanıcı", true).addOption(OptionType.ROLE, "rol", "Verilecek rol", true).queue();
        jda.upsertCommand("rolal", "Bir kullanıcıdan rol alır.").addOption(OptionType.USER, "kullanici", "Rolü alınacak kullanıcı", true).addOption(OptionType.ROLE, "rol", "Alınacak rol", true).queue();
        jda.upsertCommand("logayarla", "Moderasyon log kanalını ayarlar.").addOption(OptionType.CHANNEL, "kanal", "Logların gönderileceği kanal", true).queue();

        // /help
        jda.upsertCommand(
                "help",
                "Botun yardım menüsünü gösterir."
        ).queue();
        // /ping
        jda.upsertCommand(
                "ping",
                "Botun gecikmesini kontrol eder."
        ).queue();

        // /botinfo
        jda.upsertCommand(
                "botinfo",
                "Bot hakkında bilgi verir."
        ).queue();

        // /kick
        jda.upsertCommand(
                "kick",
                "Bir kullanıcıyı sunucudan atar."
        )
        .addOption(
                OptionType.USER,
                "kullanici",
                "Atılacak kullanıcı",
                true
        )
        .addOption(
                OptionType.STRING,
                "sebep",
                "Atılma sebebi",
                false
        )
        .queue();

        jda.upsertCommand("ban", "Bir kullanıcıyı sunucudan yasaklar.").addOption(OptionType.USER, "kullanici", "Yasaklanacak kullanıcı", true).addOption(OptionType.STRING, "sebep", "Yasaklama sebebi", false).queue();
        jda.upsertCommand("ticketkur", "Ticket sistemini kurar.").addOption(OptionType.CHANNEL, "kategori", "Ticketların açılacağı kategori", true).addOption(OptionType.CHANNEL, "kanal", "Ticket panelinin gönderileceği kanal", true).addOption(OptionType.ROLE, "destek_rolu", "Ticketları görebilecek destek rolü", true).queue();
        jda.upsertCommand("ticketlog", "Ticket transcriptlerinin gönderileceği kanalı ayarlar.").addOption(OptionType.CHANNEL, "kanal", "Transcript kanalı", true).queue();
        jda.upsertCommand("ticketrol", "Ticket destek rollerini yönetir.").addOption(OptionType.STRING, "islem", "ekle veya kaldir", true).addOption(OptionType.ROLE, "rol", "Destek rolü", true).queue();
        System.out.println("================================");
        System.out.println("SlimeCore ONLINE!");
        System.out.println("/ping kaydedildi!");
        System.out.println("/botinfo kaydedildi!");
        System.out.println("/kick kaydedildi!");
        System.out.println("================================");
    }
}

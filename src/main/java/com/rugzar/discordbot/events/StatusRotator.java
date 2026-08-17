package com.rugzar.discordbot.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatusRotator {

    private final JDA jda;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    private final Activity[] activities = {
            Activity.listening("Profesyonel Ticket Sistemi"),
            Activity.playing("Slime Rancher Oynuyor"),
            Activity.watching("SlimeCore Destek"),
            Activity.streaming(
                    "Bir Sürü Yeni Şeyler",
                    "https://www.twitch.tv/slimecore"
            )
    };

    private final OnlineStatus[] statuses = {
            OnlineStatus.DO_NOT_DISTURB,
            OnlineStatus.IDLE,
            OnlineStatus.ONLINE,
            OnlineStatus.DO_NOT_DISTURB
    };

    private int index = 0;

    public StatusRotator(JDA jda) {
        this.jda = jda;
    }

    public void start() {
        updatePresence();

        scheduler.scheduleAtFixedRate(() -> {
            index++;

            if (index >= activities.length) {
                index = 0;
            }

            updatePresence();

        }, 45, 45, TimeUnit.SECONDS);
    }

    private void updatePresence() {
        jda.getPresence().setPresence(
                statuses[index],
                activities[index]
        );
    }

    public void stop() {
        scheduler.shutdown();
    }
}

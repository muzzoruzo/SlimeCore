package com.rugzar.discordbot.events;

import com.rugzar.discordbot.database.Database;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.modals.Modal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TicketRatingEvent extends ListenerAdapter {

    private static final Map<String, Integer> ratings =
            new ConcurrentHashMap<>();

    private static final Map<String, String> comments =
            new ConcurrentHashMap<>();

    private String key(ButtonInteractionEvent event) {
        return event.getChannel().getId()
                + ":"
                + event.getUser().getId();
    }

    private String key(ModalInteractionEvent event) {
        return event.getChannel().getId()
                + ":"
                + event.getUser().getId();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        if (!event.isFromGuild()) return;

        String id = event.getComponentId();

        // ==============================
        // YORUM YAZ
        // ==============================

        if (id.equals("ticket:comment")) {

            TextInput input = TextInput.create(
                    "ticket_comment",
                    TextInputStyle.PARAGRAPH
            )
                    .setPlaceholder(
                            "Belirtmek istediğiniz yorumu yazın..."
                    )
                    .setRequired(false)
                    .setMaxLength(1000)
                    .build();

            Modal modal = Modal.create(
                    "ticket:comment_modal",
                    "Ticket Değerlendirmesi"
            )
                    .addComponents(
                            Label.of(
                                    "Belirtmek istediğiniz sorun",
                                    input
                            )
                    )
                    .build();

            event.replyModal(modal).queue();
            return;
        }

        // ==============================
        // FORMU GÖNDER
        // ==============================

        if (id.equals("ticket:submit")) {

            String mapKey = key(event);

            Integer rating = ratings.get(mapKey);
            String comment = comments.getOrDefault(mapKey, "");

            if (rating == null) {
                event.reply(
                        "❌ Önce 1 ile 5 arasında bir puan seçmelisin."
                )
                        .setEphemeral(true)
                        .queue();
                return;
            }

            try (
                    Connection connection = Database.getConnection();
                    PreparedStatement statement =
                            connection.prepareStatement("""
                                INSERT INTO ticket_ratings
                                (
                                    guild_id,
                                    ticket_channel_id,
                                    user_id,
                                    rating,
                                    comment,
                                    created_at
                                )
                                VALUES (?, ?, ?, ?, ?, ?)
                                """)
            ) {

                statement.setString(
                        1,
                        event.getGuild().getId()
                );

                statement.setString(
                        2,
                        event.getChannel().getId()
                );

                statement.setString(
                        3,
                        event.getUser().getId()
                );

                statement.setInt(
                        4,
                        rating
                );

                statement.setString(
                        5,
                        comment
                );

                statement.setLong(
                        6,
                        System.currentTimeMillis()
                );

                statement.executeUpdate();

                ratings.remove(mapKey);
                comments.remove(mapKey);

                event.reply(
                        "✅ **Form başarıyla gönderildi!**\n\n"
                                + "⭐ Puan: **"
                                + rating
                                + "/5**\n"
                                + "💬 Yorum: "
                                + (comment.isBlank()
                                ? "Yok"
                                : comment)
                )
                        .setEphemeral(true)
                        .queue();

            } catch (Exception e) {

                e.printStackTrace();

                event.reply(
                        "❌ Form gönderilirken veritabanı hatası oluştu."
                )
                        .setEphemeral(true)
                        .queue();
            }

            return;
        }

        // ==============================
        // PUAN SEÇ
        // ==============================

        if (!id.startsWith("ticket:rating:")) return;

        String ratingText =
                id.substring("ticket:rating:".length());

        int rating;

        try {
            rating = Integer.parseInt(ratingText);
        } catch (NumberFormatException e) {

            event.reply(
                    "❌ Geçersiz puan."
            )
                    .setEphemeral(true)
                    .queue();

            return;
        }

        if (rating < 1 || rating > 5) {

            event.reply(
                    "❌ Puan 1 ile 5 arasında olmalı."
            )
                    .setEphemeral(true)
                    .queue();

            return;
        }

        // ==============================
        // TICKET SAHİBİ KONTROLÜ
        // ==============================

        String topic =
                event.getChannel()
                        .asTextChannel()
                        .getTopic();

        if (topic == null ||
                !topic.startsWith("ticket-owner:")) {

            event.reply(
                    "❌ Bu ticketın sahibi belirlenemedi."
            )
                    .setEphemeral(true)
                    .queue();

            return;
        }

        String ownerId =
                topic.substring(
                        "ticket-owner:".length()
                );

        int newline =
                ownerId.indexOf('\n');

        if (newline != -1) {
            ownerId =
                    ownerId.substring(
                            0,
                            newline
                    );
        }

        ownerId = ownerId.trim();

        if (!event.getUser().getId().equals(ownerId)) {

            event.reply(
                    "❌ Bu değerlendirmeyi sadece ticket sahibi yapabilir."
            )
                    .setEphemeral(true)
                    .queue();

            return;
        }

        // ==============================
        // PUANI KAYDET
        // ==============================

        ratings.put(
                key(event),
                rating
        );

        event.reply(
                "⭐ Puanın **"
                        + rating
                        + "/5** olarak seçildi."
        )
                .setEphemeral(true)
                .queue();

        // ==============================
        // BUTONLARI GÜNCELLE
        // ==============================

        event.getMessage()
                .editMessageComponents(

                        ActionRow.of(
                                Button.primary(
                                        "ticket:rating:1",
                                        "1 ⭐"
                                ),
                                Button.primary(
                                        "ticket:rating:2",
                                        "2 ⭐"
                                ),
                                Button.primary(
                                        "ticket:rating:3",
                                        "3 ⭐"
                                ),
                                Button.primary(
                                        "ticket:rating:4",
                                        "4 ⭐"
                                ),
                                Button.primary(
                                        "ticket:rating:5",
                                        "5 ⭐"
                                )
                        ),

                        ActionRow.of(
                                Button.secondary(
                                        "ticket:comment",
                                        "💬 Yorum Yaz"
                                ),
                                Button.success(
                                        "ticket:submit",
                                        "📨 Formu Gönder"
                                ),
                                Button.danger(
                                        "ticket:delete_final",
                                        "🗑️ Ticketı Sil"
                                )
                        )
                )
                .queue();
    }

    // ==============================
    // YORUM MODALI
    // ==============================

    @Override
    public void onModalInteraction(
            ModalInteractionEvent event
    ) {

        if (!event.isFromGuild()) return;

        if (!event.getModalId().equals(
                "ticket:comment_modal"
        )) {
            return;
        }

        String comment = "";

        if (event.getValue("ticket_comment") != null) {

            comment =
                    event.getValue("ticket_comment")
                            .getAsString();
        }

        comments.put(
                key(event),
                comment
        );

        event.reply(
                comment.isBlank()
                        ? "💬 Yorum boş bırakıldı."
                        : "💬 Yorum kaydedildi."
        )
                .setEphemeral(true)
                .queue();
    }
}

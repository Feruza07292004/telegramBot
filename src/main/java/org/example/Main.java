package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Main extends TelegramLongPollingBot {

    private final String ADMIN_ID = "5333472541";

    private String lastText = "";
    private String lastFileId = "";
    private String lastType = "";

    private String addLink(String text) {
        if (text == null) text = "";
        return text + "\n\n🔗 https://t.me/donotkillmyvibeeee";
    }

    @Override
    public String getBotUsername() {
        return "fatest88bot";
    }

    @Override
    public String getBotToken() {
        return "8612643074:AAFlNANRo-uFthmelyAQ4C3GuNXBgXJid7Q"; //
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new Main());
        System.out.println("Bot ishga tushdi...");
    }

    private InlineKeyboardMarkup getButtons() {
        InlineKeyboardButton yes = new InlineKeyboardButton();
        yes.setText("✅ Ha");
        yes.setCallbackData("YES");

        InlineKeyboardButton no = new InlineKeyboardButton();
        no.setText("❌ Yo‘q");
        no.setCallbackData("NO");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(yes);
        row.add(no);

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        return markup;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {

            if (update.hasChannelPost()) {

                var post = update.getChannelPost();

                if (post.hasText()) {
                    lastText = post.getText();
                    lastType = "text";
                }

                else if (post.hasPhoto()) {
                    lastFileId = post.getPhoto()
                            .get(post.getPhoto().size() - 1)
                            .getFileId();
                    lastText = post.getCaption() != null ? post.getCaption() : "";
                    lastType = "photo";
                }

                else if (post.hasVideo()) {
                    lastFileId = post.getVideo().getFileId();
                    lastText = post.getCaption() != null ? post.getCaption() : "";
                    lastType = "video";
                }

                System.out.println("SAQLANDI: " + lastType);

           
                SendMessage msg = new SendMessage();
                msg.setChatId(ADMIN_ID);
                msg.setText("Yangi post keldi. Chop etilsinmi?");
                msg.setReplyMarkup(getButtons());

                execute(msg);
            }

            if (update.hasCallbackQuery()) {

                String data = update.getCallbackQuery().getData();
                long chatId = update.getCallbackQuery().getMessage().getChatId();

                SendMessage response = new SendMessage();
                response.setChatId(String.valueOf(chatId));

                if (data.equals("YES")) {

                    if (lastType.equals("text")) {
                        SendMessage post = new SendMessage();
                        post.setChatId("@donotkillmyvibeeee");
                        post.setText(addLink(lastText));
                        execute(post);
                    }

                    else if (lastType.equals("photo")) {
                        SendPhoto photo = new SendPhoto();
                        photo.setChatId("@donotkillmyvibeeee");
                        photo.setPhoto(new InputFile(lastFileId));
                        photo.setCaption(addLink(lastText));
                        execute(photo);
                    }

                    else if (lastType.equals("video")) {
                        SendVideo video = new SendVideo();
                        video.setChatId("@donotkillmyvibeeee");
                        video.setVideo(new InputFile(lastFileId));
                        video.setCaption(addLink(lastText));
                        execute(video);
                    }

                    response.setText("✅ Post chiqarildi!");

                } else {
                    response.setText("❌ Bekor qilindi!");
                }

                execute(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

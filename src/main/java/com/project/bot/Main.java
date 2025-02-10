package com.project.bot;

import com.project.bot.botQuestions.QuestionsList;

import org.telegram.telegrambots.meta.TelegramBotsApi;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        TelegramBotsApi telegramBotsApi = null;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        try {
            telegramBotsApi.registerBot(new QuestionsList());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
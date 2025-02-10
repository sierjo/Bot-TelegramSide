package com.project.bot.botQuestions;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QuestionsList extends TelegramLongPollingBot {

    private final Map<String, UserState> userStates = new HashMap<>();

    //    Храним состояния пользователя
    private static class UserState {
        private int state;
        private String firstMessage;


        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getFirstMessage() {
            return firstMessage;
        }

        public void setFirstMessage(String firstMessage) {
            this.firstMessage = firstMessage;
        }

    }

    @Override
    public String getBotUsername() {
        String naimBot = null;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("D:\\! ERASMUS YCZOBA\\SOFTWARE_DELOVERY\\Tg_bot\\Api_Key.txt"))) {
            naimBot = bufferedReader.readLine(); // Читаем первую строку файла
        } catch (IOException e) {
            e.printStackTrace(); // Логирует ошибку при чтении файла
        }
        return naimBot;
    }

    @Override

    public String getBotToken() {
        String token = null;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("D:\\! ERASMUS YCZOBA\\SOFTWARE_DELOVERY\\Tg_bot\\Api_Key.txt"))) {
            bufferedReader.readLine();
            token = bufferedReader.readLine(); // Читает вторую строку файла
        } catch (IOException e) {
            e.printStackTrace();
        }
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText();


            //  Получаем или создаем состояние пользователя
            UserState userState = userStates.getOrDefault(chatId, new UserState());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);

            // Обработка команды /restart
            if (text.equals("/restart")) {
                userStates.remove(chatId); // Сбрасываем состояние пользователя
                userStates.put(chatId, userState); // Сохранение состояния
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                return; // Завершаем обработку, чтобы не продолжать с состояниями
            }
            System.out.println("Состояние " + userState.getState());
            switch (userState.getState()) {
                case 0: // первое сообщение
                    if (text.equals("/start")) {
                        userState.setState(1);
                        userStates.put(chatId, userState);
                        // Строим маршрут?
                    } else {
                        sendMessage.setText("Enter the command: /start");
                    }
                    break;
                case 1:
                    sendMessage.setText("Enter the text_1!");
                    userState.setState(2);
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    sendMessage.setText("Enter the command: /restart");
                    userStates.remove(chatId);
                    break;
            }
        }
    }
}

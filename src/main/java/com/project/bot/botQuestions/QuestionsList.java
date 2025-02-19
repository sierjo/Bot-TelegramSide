package com.project.bot.botQuestions;

import com.project.bot.botMapPointList.AllMapPoint;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionsList extends TelegramLongPollingBot {

    private final Map<String, UserState> userStates = new HashMap<>();

    private final AllMapPoint allMapPoint = new AllMapPoint();

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
            String pointText = update.getMessage().getText();


            //  Получаем или создаем состояние пользователя
            UserState userState = userStates.getOrDefault(chatId, new UserState());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);

            // Обработка команды /restart
            if (text.equals("/restart")) {
                System.out.println(allMapPoint.allMapPoint());
                userStates.remove(chatId); // Сбрасываем состояние пользователя
                setPoints(chatId);
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
                        setPoints(chatId);
                        userState.setState(1);
                        userStates.put(chatId, userState);
                        // Строим маршрут?
                    } else {
                        sendMessage.setText("Enter the command: /start");
                    }
                    break;
                case 1:
                    allMapPoint.addMapPoint(pointText);
                    Continue_Indicate_Points(chatId);
                    System.out.println("Continue state " + userState.getState());
                    // ПРОСТО ПРОВЕРКА
                    System.out.println("CASE 1");
                    System.out.println("getNumberMapPoint " + allMapPoint.getNumberMapPoint());
                    System.out.println("getPointCounter" + allMapPoint.getPointCounter());
                    break;
                default:
                    sendMessage.setText("Enter the command: /restart");
                    userStates.remove(chatId);
                    break;
            }
        }
        if (update.hasCallbackQuery()) {

            String callbackData = update.getCallbackQuery().getData();

            long messageId = update.getCallbackQuery().getMessage().getMessageId();     /* получение Id текста сообщения для
                                                                                           изменения сообщения без пересылки */
            long chatId = update.getCallbackQuery().getMessage().getChatId();           /* получение Id чата, что-бы бот знал
                                                                                           в какой чат отсылать сообщения*/
            UserState userState = userStates.getOrDefault(chatId, new UserState());

            EditMessageText editMessageText = new EditMessageText();

            if (callbackData.equals("Agree_To_Set_Point")) {
                System.out.println(messageId);
                allMapPoint.incrementCounter();
                editMessageText.setChatId(chatId);
                editMessageText.setMessageId(Math.toIntExact(messageId)); // Указываем ID сообщения, чтобы его отредактировать
//                editMessageText.setText("Укажите адрес точки № " + allQuestion.getPointCounter());
                editMessageText.setText("Specify the address of point № " + allMapPoint.getPointCounter());
                try {
                    execute(editMessageText);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (callbackData.equals("No_Set_Point")) {
                editMessageText.setChatId(chatId);
                editMessageText.setMessageId(Math.toIntExact(messageId)); // Указываем ID сообщения, чтобы его отредактировать
                editMessageText.setText("End");
                try {
                    execute(editMessageText);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (callbackData.equals("Agree")) {
                System.out.println("Your points: " + "\n" + allMapPoint.allMapPoint());
            } else if (callbackData.equals("No_Agree")) {
                allMapPoint.point_is_added();
                allMapPoint.incrementCounter();
                editMessageText.setChatId(chatId);
                editMessageText.setMessageId(Math.toIntExact(messageId)); // Указываем ID сообщения, чтобы его отредактировать
//                editMessageText.setText("Укажите адрес точки № " + allQuestion.getPointCounter());
                editMessageText.setText("Specify the address of point № " + allMapPoint.getPointCounter());
                try {
                    execute(editMessageText);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setPoints(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
//        message.setText("Строим маршрут?");
        message.setText("Are we planning a route?");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();
        yesButton.setText("Yes");
        yesButton.setCallbackData("Agree_To_Set_Point");       // Модификатор позволяющий боту понять какая кнопка была нажата
        var noButton = new InlineKeyboardButton();
        noButton.setText("No");
        noButton.setCallbackData("No_Set_Point");       // Модификатор позволяющий боту понять какая кнопка была нажата

        rowInline.add(yesButton);
        rowInline.add(noButton);

        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);

        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void Continue_Indicate_Points(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
//        message.setText("Все точки указаны?");
        message.setText("Are all the points listed?");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();
        yesButton.setText("Yes");
        yesButton.setCallbackData("Agree");       // Модификатор позволяющий боту понять какая кнопка была нажата
        var noButton = new InlineKeyboardButton();
        noButton.setText("No");
        noButton.setCallbackData("No_Agree");       // Модификатор позволяющий боту понять какая кнопка была нажата

        rowInline.add(yesButton);
        rowInline.add(noButton);

        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);

        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

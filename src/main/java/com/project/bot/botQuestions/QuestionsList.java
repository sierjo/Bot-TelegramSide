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
            System.out.println("It's states " + userState.getState());     // Проверка
//            System.out.println("It's message " + text);                   // Проверка
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);

            // Обработка команды /restart
            if (text.equals("/restart")) {
                allMapPoint.clearMapPoint();
                allMapPoint.setPointCounter(0);
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
                        // Строим маршрут?
                        setPoints(chatId);
                        userState.setState(1);
                        System.out.println("CASE 0");
                    } else {
                        sendMessage.setText("Enter the command: /start");
                    }
                    break;
                case 1: // Все точки указаны?
//                    System.out.println("Sige map point " + allQuestion.allMapPoint().size());
                    allMapPoint.addMapPoint(pointText);
                    Continue_Indicate_Points(chatId);
                    System.out.println("Continue state " + userState.getState());
                    // ПРОСТО ПРОВЕРКА
                    System.out.println("CASE 1");
                    System.out.println("getNumberMapPoint " + allMapPoint.getNumberMapPoint());
                    System.out.println("getPointCounter" + allMapPoint.getPointCounter());
                    break;
                case 2:
                    if (userState.getFirstMessage() != " ") {
                        text = userState.getFirstMessage();
                        userState.setState(3);
                        System.out.println("ISPOKNILSIA IF Case 2");
                    }
                    System.out.println("Содержимое ответа пользователя на CASE 2" + text);
                    System.out.println("CASE 2");
                    SendMessage sendidMessage = new SendMessage();
                    sendidMessage.setChatId(chatId);
                    String saveUserMessage = update.getMessage().getText();
                    // Извлекаем номер точки из состояния
                    // Обновляем точку в allQuestion
                    allMapPoint.updateMapPoint(allMapPoint.getIndexMapPoint() - 1, saveUserMessage); // Нумерация точек начинается с 1, поэтому вычитаем 1
                    // Подтверждаем изменение
                    System.out.println("Zamena ticki " + saveUserMessage);
                    sendidMessage.setText("Точка " + allMapPoint.getIndexMapPoint() + " успешно обновлена на: " + saveUserMessage);
                    // Сбрасываем состояние
                    try {
                        execute(sendidMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    checkingAllPoints(Long.parseLong(chatId));
                    userStates.remove(chatId); // Сбрасываем сост пользователя
                    userState.setState(1);
                    userStates.put(chatId, userState); // Сохранение состояния
                    break;
                case 3:
                    checkingAllPoints(Long.parseLong(chatId));
                    userStates.remove(chatId); // Сбрасываем сост пользователя
                    userState.setState(1);
                    userStates.put(chatId, userState); // Сохранение состояния
                    System.out.println("CASE 3");
                    break;
                default:
                    sendMessage.setText("Enter the command: /restart");
                    userStates.remove(chatId);
                    break;
            }

//            Сохраняем состояние пользователя
            userStates.put(chatId, userState);

            try {
                this.execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
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
            EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();

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
                checkingAllPoints(chatId);

                editMessageText.setChatId(chatId);
                editMessageText.setMessageId(Math.toIntExact(messageId));
//                editMessageText.setText("Точки указаны верно?");
                editMessageText.setText("Are the points indicated correctly?");
                try {
                    execute(editMessageText);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

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
            } else if (callbackData.equals("Agree_Сhecking")) {
                messageId--;
                editMessageText.setChatId(chatId);
                editMessageText.setMessageId(Math.toIntExact(messageId)); // Указываем ID сообщения, чтобы его отредактировать
//                editMessageText.setText("Ваши точки:" + "\n");
                editMessageText.setText("Your points:" + "\n");
                try {
                    execute(editMessageText);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                messageId++;
                editMarkup.setChatId(chatId);
                editMarkup.setMessageId(Math.toIntExact(messageId));
                editMarkup.setReplyMarkup(null); // Убираем кнопки полностью
                try {
                    execute(editMarkup);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (callbackData.equals("No_Agree_Сhecking")) {
                editPoint(chatId);
            } else if (callbackData.startsWith("Point_")) {

                allMapPoint.setIndexMapPoint(Integer.parseInt(callbackData.split("_")[1]));

                // Логируем выбранную точку
                System.out.println("Пользователь выбрал точку: " + allMapPoint.getIndexMapPoint());

                // Запрашиваем новый адрес для этой точки
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Вы выбрали Точку " + allMapPoint.getIndexMapPoint() + ". Введите новый адрес:");

                userState.setState(2);
                userStates.put(String.valueOf(chatId), userState); // Сохранение состояния
                System.out.println("It's states 1 posle KNOPKI TOCHKA " + userState.getState());
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            if (userState.getState() == 3) {


                messageId--;
                editMessageText.setChatId(chatId);
                editMessageText.setMessageId(Math.toIntExact(messageId)); // Указываем ID сообщения, чтобы его отредактировать
                editMessageText.setText("Are the points indicated correctly?");
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

    private void checkingAllPoints(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("\n" + allMapPoint.allMapPoint());
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();
        yesButton.setText("Yes");
        yesButton.setCallbackData("Agree_Сhecking");       // Модификатор позволяющий боту понять какая кнопка была нажата
        var noButton = new InlineKeyboardButton();
        noButton.setText("No");
        noButton.setCallbackData("No_Agree_Сhecking");       // Модификатор позволяющий боту понять какая кнопка была нажата

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

    private void editPoint(long chatId) {
        ;
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Какую точку нужно отредактировать?");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        System.out.println("getPointCounter " + allMapPoint.getPointCounter());
        System.out.println("getAllMapPoint " + allMapPoint.getNumberMapPoint());
        for (int i = 1; i <= allMapPoint.getPointCounter(); i++) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            InlineKeyboardButton editPointButton = new InlineKeyboardButton();
            editPointButton.setText("Point " + i);
            editPointButton.setCallbackData("Point_" + i);
            rowInline.add(editPointButton);
            rowsInline.add(rowInline);
        }

        markupInline.setKeyboard(rowsInline);

        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
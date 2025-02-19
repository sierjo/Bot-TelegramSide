package com.project.bot.botMapPointList;

import java.util.ArrayList;
import java.util.List;

public class AllMapPoint {
    private List<String> mapPoint;

    // Создаём пустой список
    public AllMapPoint() {
        mapPoint = new ArrayList<>();
    }

    public String allMapPoint() {
//        numberMapPoint = 0;
        StringBuilder result = new StringBuilder();
        for (String point : mapPoint) {
            result.append(point).append("\n"); // Добавляем элемент и символ с новой строки
        }
        return result.toString();
    }

    public void addMapPoint(String point) {
        mapPoint.add(point);
    }

}

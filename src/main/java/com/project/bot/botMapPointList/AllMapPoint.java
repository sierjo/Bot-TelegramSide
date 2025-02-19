package com.project.bot.botMapPointList;

import java.util.ArrayList;
import java.util.List;

public class AllMapPoint {
    private List<String> mapPoint;
    private int pointCounter = 0;

    public int getPointCounter() {
        return pointCounter;
    }
    private int numberMapPoint = 0;

    public int getNumberMapPoint() {
        return numberMapPoint;
    }
    private int indexMapPoint = 0;

    public void setPointCounter(int pointCounter) {
        this.pointCounter = pointCounter;
    }

    public int getIndexMapPoint() {
        return indexMapPoint;
    }

    public void setIndexMapPoint(int indexMapPoint) {
        this.indexMapPoint = indexMapPoint;
    }


    // Создаём пустой список
    public AllMapPoint() {
        mapPoint = new ArrayList<>();
    }

    public List<String> getMapPoint() {
        return mapPoint;
    }
    // Получаем все точки маршрута

    public String allMapPoint() {
//        numberMapPoint = 0;
        StringBuilder result = new StringBuilder();
        for (String point : mapPoint) {
            result.append(point).append("\n"); // Добавляем элемент и символ новой строки
        }
        return result.toString();
    }

// Добавляем точку в список

    public void addMapPoint(String point) {
        mapPoint.add(point);
    }

    public void clearMapPoint() {
        mapPoint.clear();
    }

    public void incrementCounter() {
        pointCounter++;
    }

    public void point_is_added() {
        if (mapPoint.size() >= 0) {
            numberMapPoint++;
        }
    }

    public void updateMapPoint(int index, String newAddress) {
        if (index >= 0 && index < mapPoint.size()) {
            mapPoint.set(index, newAddress);
        }
    }
}

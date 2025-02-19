FROM openjdk:17-jdk-slim-buster
WORKDIR /app
COPY /target/Bot-Telegram_Side-1.0-SNAPSHOT.jar /app/tgbot.jar
COPY /Api_Key.txt /app/Api_Key.txt
ENTRYPOINT ["java", "-jar", "tgbot.jar"]
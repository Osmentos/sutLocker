# SutLocker

Менеджер паролей на Java (курсовая работа, тема 9). Регистрация и вход, хранение
логинов и паролей в SQLite, выбор способа хранения пароля: открытый вид, Base64,
MD5, шифр Фейстеля, MD5 с солью.

## Требования
- JDK 17

## Сборка и запуск
gradlew build
java -jar build/libs/sutlocker.jar

Тесты: `gradlew test`. Документация: `gradlew javadoc` (результат в `docs/javadoc`).

## Где хранятся данные
Файл базы `sutlocker.db` и лог `sutlocker.log` создаются рядом с jar-файлом
(при запуске из IDE в папке проекта).

## Стек
Java 17, Gradle, JavaFX, SQLite (sqlite-jdbc), log4j2, JUnit 5.

## Документация
HTML-документация Javadoc: папка `docs/javadoc` (откройте `index.html`).


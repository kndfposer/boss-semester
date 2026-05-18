# Веб-сервис «Босс семестра»

Курсовой проект на Java/Spring Boot для IntelliJ IDEA: backend + frontend + PostgreSQL + Swagger + интеграция с GigaChat.

## Что реализовано

- Регистрация и вход пользователей.
- Создание запроса генерации финального босса по списку предметов, сложности, эмоциональному фону и стилю.
- Один запрос = одно изображение.
- Статусы: `PENDING`, `RUNNING`, `COMPLETED`, `ERROR`.
- История собственных запросов.
- Избранное.
- Создание нового запроса на основе предыдущего.
- Сохранение входных параметров, статуса, промпта, логов внешнего API и данных файла изображения в PostgreSQL.
- Swagger UI: `http://localhost:8080/swagger-ui.html`.
- Frontend: `http://localhost:8080/`.
- Дополнительные функции:
  - ограничение количества генераций в день;
  - коллекция боссов по семестрам;
  - публичная витрина с модерацией.

## Использованные паттерны проектирования

- **Builder + Director**: `PromptDirector`, `SemesterBossPromptBuilder` — пошаговая сборка промпта.
- **Strategy**: разные стили визуализации без большого `if` в одном методе.
- **Chain of Responsibility**: цепочка валидации запроса.
- **Adapter**: `GigaChatImageGeneratorAdapter` адаптирует внешний API GigaChat к внутреннему интерфейсу `ImageGenerator`.
- **Proxy**: `RateLimitedImageGeneratorProxy` добавляет ограничение обращений до вызова реального генератора.
- **Facade**: `BossSemesterFacade` объединяет сценарии для контроллеров.
- **State**: классы статусов запроса.
- **Observer**: логирование событий генерации через подписчика.
- **Command**: команда запуска генерации.
- **Memento**: сохранение параметров старого запроса при создании нового на его основе.

## Настройка PostgreSQL через pgAdmin

1. Откройте pgAdmin.
2. Подключитесь к серверу PostgreSQL. Обычно:
   - user: `postgres`
   - password: `1234`
3. Создайте базу данных:
   - правой кнопкой по `Databases` → `Create` → `Database...`
   - Database: `boss_semester`
   - Owner: `postgres`
4. Проверьте параметры в `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/boss_semester
spring.datasource.username=postgres
spring.datasource.password=1234
spring.jpa.hibernate.ddl-auto=update
```

5. При первом запуске Hibernate сам создаст таблицы.

## Настройка GigaChat

В коде нельзя хранить ключ доступа открытым текстом. Поэтому ключ задаётся через переменную окружения.

### В IntelliJ IDEA

1. Откройте проект как Maven project.
2. `Run` → `Edit Configurations...`.
3. Выберите конфигурацию Spring Boot.
4. В `Environment variables` добавьте:

```text
GIGACHAT_AUTH_KEY=ваш_authorization_key_из_личного_кабинета
GIGACHAT_SCOPE=GIGACHAT_API_PERS
GIGACHAT_DEMO_MODE=false
```

Если ключа пока нет, оставьте:

```text
GIGACHAT_DEMO_MODE=true
```

Тогда приложение будет создавать локальную SVG-заглушку, чтобы можно было проверить базу, фронтенд, историю, избранное, коллекции и витрину без внешнего API.

### Как работает интеграция

1. Backend получает access token через OAuth endpoint GigaChat.
2. Затем отправляет запрос в `/chat/completions` с `function_call: auto`.
3. GigaChat возвращает идентификатор файла изображения.
4. Backend скачивает файл через `/files/{file_id}/content`.
5. Изображение сохраняется в папку `generated-images`, а метаданные сохраняются в PostgreSQL.

## Запуск

```bash
mvn spring-boot:run
```

Откройте:

```text
http://localhost:8080/
```

Администратор для модерации создаётся автоматически:

```text
login: admin
password: admin123
```

## Проверка в Swagger

- Регистрация: `POST /api/auth/register`
- Вход: `POST /api/auth/login`
- Создание босса: `POST /api/boss`
- История: `GET /api/boss`
- Добавить в избранное: `POST /api/boss/{id}/favorite?value=true`
- Создать на основе прошлого: `POST /api/boss/{id}/clone`
- Коллекции: `/api/collections`
- Публичная витрина: `/api/showcase`
- Модерация: `/api/showcase/moderation`

Для защищённых методов нужно передать header:

```text
Authorization: Bearer <token>
```

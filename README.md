

# StoryVision Client

Мобильное Android‑приложение для управления проектами, персонажами, событиями и связями между ними.  
Стек: **Kotlin + Jetpack Compose + Material 3 + MVVM + Retrofit**.

## Быстрый старт

### Требования

- Android Studio **Hedgehog / Iguana+**
- JDK **17** (встроенный в Android Studio)
- Android SDK **24+**
- Запущенный backend‑сервер (по умолчанию ожидается на `http://10.0.2.2:8081/`)

### Установка и запуск

1. **Клонировать репозиторий**
   ```bash
   git clone https://github.com/username/storyvision-client.git
   cd storyvision-client
   ```

2. **Открыть проект в Android Studio**  
   `File → Open… → выбрать корень репозитория`

3. **Проверить адрес backend‑сервера**  
   В файле, где объявлён `NetworkModule`, при необходимости изменить:
   ```kotlin
   private const val BASE_URL = "http://10.0.2.2:8081/"
   ```

4. **Синхронизировать зависимости**  
   Android Studio обычно делает это автоматически (`Sync Project with Gradle Files`).

5. **Собрать и запустить приложение**
   - Выбрать эмулятор или физическое устройство (API 24+).
   - Нажать **Run ▶**.

6. **Авторизация**
   - Зарегистрировать нового пользователя или войти с существующими данными.
   - После логина откроется список проектов, затем экран конкретного проекта (Библиотека / Заметки / Импорт).

## Основные возможности

- Авторизация и регистрация с валидацией полей и обработкой ошибок сервера.
- Список проектов, создание новых проектов.
- Для каждого проекта:
  - **Библиотека**: персонажи, события, типы связей.
  - Просмотр и создание сущностей, атрибуты (кастомные поля).
  - Просмотр связей персонажа.
  - Поиск и сортировка по имени.
  - **Импорт** данных из `.json` файла (multipart/form-data).
- Экран аккаунта с возможностью выхода (очистка токенов).
- Поддержка **светлой/тёмной темы** с переключателем в TopBar и сохранением выбора.

## Структура проекта

Структура может немного отличаться в последних правках:

```text
app/
 ├─ src/main/java/…/storyvision_client/
 │   ├─ data/
 │   │   ├─ auth/           # AuthRepository, AuthApi, модели запросов/ответов, TokenStorage
 │   │   ├─ entities/       # EntitiesRepository, EntitiesApi, DTO персонажей/событий/связей
 │   │   ├─ importdata/     # ImportRepository, ImportApi, модели ответа импорта
 │   │   └─ network/        # NetworkModule (Retrofit, OkHttp, BASE_URL)
 │   │
 │   ├─ ui/
 │   │   ├─ auth/           # LoginScreen, RegisterScreen, AuthViewModel
 │   │   ├─ projectlist/    # ProjectListScreen, ProjectListViewModel
 │   │   ├─ project/        # ProjectScreen (табы: Library / Notes / Import)
 │   │   ├─ library/        # LibraryPanel, EntityContent, CharactersList, EventsList, RelationsList,
 │   │   │                  # диалоги создания сущностей и атрибутов, CharacterDetailsDialog
 │   │   ├─ import/         # ImportPanel, ImportViewModel
 │   │   ├─ account/        # AccountScreen, AccountViewModel
 │   │   └─ components/     # Общие компоненты: ConnectionBox, EmptyState и т.п.
 │   │
 │   ├─ model/              # Общие DTO, если вынесены отдельно
 │   ├─ navigation/         # NavDisplay, описания маршрутов (LoginRoute, ProjectRoute и др.)
 │   ├─ theme/              # Material 3 тема, цветовые схемы, типографика, шейпы
 │   └─ MainActivity.kt     # Точка входа: создание репозиториев, ViewModelFactory, навигация, хранение темы
 │
 └─ src/main/res/
     ├─ layout/             # Обычно пусто/минимально, т.к. UI на Compose
     ├─ drawable/           # Логотипы, фоновые картинки (login/register/account/import)
     ├─ values/             # colors.xml, strings.xml, fonts (если нужно)
     └─ mipmap/             # Иконка приложения
```

### Архитектура

- **UI (Compose)**  
  `*Screen` / `*Panel` composable-функции отображают состояние и вызывают методы ViewModel.

- **ViewModel (MVVM)**  
  - Держит `UiState` через `StateFlow`.
  - Запускает корутины для запросов в репозитории.
  - Обновляет состояние, обрабатывает ошибки и события (успех/ошибка/Unauthorized).

- **Repository слой**  
  - Инкапсулирует работу с Retrofit‑API.
  - Добавляет Bearer‑токен через `AuthRepository.authorizedCall`.
  - Парсит ответы, мапит их в `AuthResult<…>`.

- **Network / Auth**  
  - `NetworkModule` создаёт `Retrofit`/`OkHttpClient` с логированием.
  - `AuthRepository` хранит/обновляет токены, реализует `logout()` с очисткой токенов.

***

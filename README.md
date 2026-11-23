# CryptoSCAM - біржа криптовалют

Android-застосунок, який показує ціни **топ-30 криптовалют у доларах США**, зберігає їх локально в Room і періодично оновлює у фоні через WorkManager. Після першого успішного завантаження список доступний без інтернету, зокрема після перезапуску.

**Демонстраційне відео:** (https://drive.google.com/file/d/14ULu1qN38XRNWIpT33V0pIi3Qi5JjJ2o/view?usp=sharing)

## Команда

| Учасник | GitHub | Зона відповідальності                                                                                                                                                                                                                                       |  
| --- | --- | --- |  
| Аввакумов Володимир | [@bobser223](https://github.com/bobser223) | Data-шар і фонова синхронізація: Retrofit + OkHttp, DTO, Room (Entity, DAO, база), маппери, `CryptoRepositoryImpl`, use case'и, `SyncWorker`, `SyncScheduler`, `AppContainer`; обробка кнопки «Назад»; зрозумілі тексти помилок, порожній стан, скам-діалог |  
| Дзюменко Артем | [@Shwei1](https://github.com/Shwei1) | Presentation-шар: `CryptoListViewModel`, `SyncStatus`, Compose-екран і компоненти, форматування цін, шрифти й тема; екран «немає даних»; додаткові фічі - пошук, деталі валюти, обране                                                                      |  

Обидва учасники переглядали код одне одного через pull request.

## Можливості

- Список топ-30 криптовалют (`LazyColumn`): ранг, логотип, символ, ціна в USD.
- Ручне оновлення кнопкою в TopAppBar (одноразове завдання WorkManager); також запускається автоматично при відкритті екрана.
- Періодичне фонове оновлення **раз на 60 хвилин**.
- Офлайн-перегляд збережених даних; для некешованих зображень - заглушка.
- Стан оновлення в TopAppBar: очікування мережі, виконання, час останнього успішного оновлення або помилка зі зрозумілим текстом (немає з'єднання / невірний API-ключ / некоректна відповідь).
- До першого успішного завантаження показується стан «Ще немає збережених даних».
- Відсутня ціна показується як «Немає даних», малі ціни не округлюються до `0.00`.
- Збереження стану й прокрутки при повороті екрана, підтримка світлої/темної теми.

**Додатково **

- Пошук за назвою або символом серед локально збережених валют.
- Деталі валюти (розгортання рядка): капіталізація, історичний максимум ціни (ATH) і його дата, час оновлення.
- Обране (зірочка) зі збереженням у DataStore - вибір не губиться після синхронізації; фільтр «лише обрані».
- Кнопка/жест «Назад» послідовно згортає розгорнуті картки, скидає пошук і фільтр, прокручує список угору, і лише потім закриває застосунок.
- Пасхалка: якщо догортати список до кінця, один раз з'являється скам - вікно Безкоштовний біткоїн!, яке після натискання розкриває жарт і нагадує про цифрову гігієну. Введені дані картки нікуди не зберігаються й не надсилаються.

## Архітектура

Один Gradle-модуль, три шари у вигляді пакетів. Залежності передаються вручну через `AppContainer`, який створюється в `CryptoApp`.

```text  
com.example.cryptoscam  
├── data  
│   ├── remote        CoinGeckoApi (Retrofit), dto/CoinDto  
│   ├── local         CryptoExchangeEntity, CryptoExchangeDao, CryptoExchangeDatabase, StarredCoinsStore  
│   ├── mapper        CoinDto → Entity → CryptoAsset  
│   └── repository    CryptoRepositoryImpl  
├── domain  
│   ├── model         CryptoAsset, SyncResult, SyncStatus  
│   ├── repository    CryptoRepository (інтерфейс)  
│   └── usecase       ObserveCryptoAssets, ObserveLastUpdated, SyncCryptoAssets, ToggleStar  
├── presentation  
│   ├── viewmodel     CryptoListViewModel  
│   ├── compose       екранні компоненти  
│   └── format        форматування цін і капіталізації  
├── worker            SyncWorker, SyncScheduler  
├── AppContainer.kt   ручний DI  
└── CryptoApp.kt      Application: створює контейнер і реєструє періодичну синхронізацію  
```  

### Потік даних

```text  
WorkManager → SyncWorker → SyncCryptoAssetsUseCase → CryptoRepository → CoinGecko API → Room  
Room → Flow → CryptoRepository → use case → CryptoListViewModel → StateFlow → Compose  
```  

- **Room - єдине джерело даних для UI.** Відповідь API ніколи не потрапляє у ViewModel напряму: вона лише перезаписує базу, а екран оновлюється через `Flow`.
- DTO (`CoinDto`), Entity (`CryptoExchangeEntity`) і доменна модель (`CryptoAsset`) - окремі типи.
- Обране зберігається окремо від Room (DataStore) і поєднується зі списком у репозиторії через `combine`, тому синхронізація його не стирає.

### Збереження та синхронізація

- Первинний ключ - стабільний `id` активу з API, тому повторні оновлення не створюють дублікатів.
- Ранг береться з порядку відповіді API; DAO повертає `Flow` з `ORDER BY rank`.
- Нові дані записуються після успішного запиту в одній транзакції (`@Transaction replaceAll`: видалення + вставка). Валюти, що випали з топ-30, зникають. При помилці мережі, помилковому HTTP-коді або порожній відповіді база не змінюється.
- Час оновлення (`updatedAt`) записується разом із новими даними; на екрані показується `MAX(updatedAt)`. Порожній стан теж визначається за ним: якщо `MAX(updatedAt)` дорівнює `null`, у базі ще немає даних.

### WorkManager

| Параметр | Значення                                                                                                                  |  
| --- | --- |  
| Worker | `SyncWorker : CoroutineWorker` - чекає завершення запиту й запису в базу                                                  |  
| Обмеження | `NetworkType.CONNECTED` для обох типів задач                                                                              |  
| Періодична задача | `enqueueUniquePeriodicWork("crypto_periodic_sync", KEEP)`, інтервал 60 хв                                                 |  
| Ручне оновлення | `enqueueUniqueWork("crypto_manual_sync", KEEP)` - повторне натискання не дублює незавершену задачу                        |  
| Мережа / 5xx / 429 | `Result.retry()` зі стандартним експоненційним backoff WorkManager (від 30 с), максимум 3 спроби; далі `Result.failure()` |  
| 401 / 403 (ключ) | `Result.failure()` одразу, без повторів; користувач бачить «невірний API-ключ або немає доступу»                          |  
| Некоректна відповідь | `Result.failure()`, попередні дані залишаються                                                                            |  

Worker не має посилань на Activity чи ViewModel і бере залежності з `AppContainer`, тому працює і при закритому застосунку. Стан ручної задачі ViewModel отримує з `WorkManager.getWorkInfosForUniqueWorkFlow()` і перетворює на `SyncStatus`; код помилки з `outputData` Worker перетворюється на текст для користувача. Статус у TopAppBar відображає лише ручну задачу.

## Джерело даних

Використовується **CoinGecko API** (Demo-план).

```text  
GET https://api.coingecko.com/api/v3/coins/markets  
    ?vs_currency=usd  
    &order=market_cap_desc  
    &per_page=30  
    &page=1  
```  

Запит повертає топ-30 криптовалют за спаданням ринкової капіталізації з ціною в USD. Використовувані поля: `id`, `symbol`, `name`, `image` (логотип), `current_price`, `market_cap`, `ath`, `ath_date`.

Зображення завантажуються через **Coil 3** (`AsyncImage`) за полем `image`, із заглушкою на час завантаження й у разі помилки.

## Налаштування API-ключа

1. Зареєструйтеся на [coingecko.com](https://www.coingecko.com/en/api) і створіть Demo API key.
2. Додайте ключ у файл `local.properties` у корені проєкту (файл виключений з Git):

   ```properties  
   COINGECKO_API_KEY=  
   ```  
3. Ключ потрапляє в `BuildConfig.COINGECKO_API_KEY` і додається до кожного запиту заголовком `x-cg-demo-api-key` через OkHttp `Interceptor`.

> Не комітьте справжній ключ і не показуйте його в логах чи на відео.

## Запуск

1. Клонуйте репозиторій:
   ```bash  
   git clone https://github.com/bobser223/humsterCombat.git  
   ```
2. Відкрийте проєкт в Android Studio.
3. Додайте API-ключ у `local.properties` (див. вище).
4. Дочекайтеся Gradle Sync і запустіть конфігурацію `app` на емуляторі або пристрої (minSdk 24).

Для перевірки використовуйте **App Inspection → Database Inspector** (таблиця `crypto_assets`) і **Background Task Inspector** (задачі `crypto_periodic_sync` та `crypto_manual_sync`).

## Технології

Kotlin, Jetpack Compose (Material 3), ViewModel + StateFlow, Kotlin Coroutines / Flow, Retrofit 3 + Gson, OkHttp, Room, WorkManager, DataStore Preferences, Coil 3.

## Історія розробки

Розробка велася у feature-гілках, які зливалися в `main` через pull request.

| Гілка                     | PR  | Автор               | Що зроблено                                                                                                                                          |     |
| ------------------------- | --- | ------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------- | --- |
| `main` (початкові коміти) | -   | @bobser223          | Room, Retrofit, маппери, репозиторій, use case, `SyncWorker`, `SyncScheduler`, `AppContainer`                                                        |     |
| `main` (початкові коміти) | -   | @Shwei1             | ViewModel, Compose-екран, `SyncStatus`, форматування цін, шрифти                                                                                     |     |
| `feature/search-bar`      | #1  | @Shwei1             | Пошук за назвою та символом через запит до Room                                                                                                      |     |
| `feature/coin-info`       | #2  | @Shwei1             | Деталі валюти: капіталізація, ATH, дата ATH                                                                                                          |     |
| `feature/stars`           | #3  | @Shwei1             | Обране: DataStore, `ToggleStarUseCase`, фільтр «лише обрані»                                                                                         |     |
| `fix/ath-price-format`    | #4  | @bobser223          | Коректне форматування малих значень ATH (не `$0.00`)                                                                                                 |     |
| `backHander`              | #5  | @bobser223          | Обробка «Назад»: згортання карток, скидання фільтрів, прокрутка вгору; стан розгорнутих карток перенесено у ViewModel                                |     |
| `scam`                    | #6  | @Shwei1, @bobser223 | Екран «Ще немає збережених даних» (@Shwei1); компонент скам-діалогу `FreeBitcoinDialog` (@bobser223)                                                 |     |
| `main`                    | -   | @bobser223          | Зрозумілі тексти помилок синхронізації; порожній стан за наявністю даних у Room, а не за статусом WorkManager; підключення скам-діалогу до прокрутки |     |
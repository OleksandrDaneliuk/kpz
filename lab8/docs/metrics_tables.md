# Метричний аналіз: таблиці

## 1. Low Coupling (CBO)

| Клас | Типи, на які є посилання (до) | CBO до | Типи, на які є посилання (після) | CBO після |
|---|---|---|---|---|
| `HotelManager` (застарілий моноліт) | Guest, Room, Booking | 3* | видалено | – |
| `BookingApplicationController` | BookingRepository, NotificationService, Guest, Room, Booking | **5** | BookingFactory, BookingRepository, NotificationService, Booking | **4** |
| `Booking` | Guest, Room, SeasonalPricingStrategy, Season, TouristTaxCalculator, StandardTouristTaxCalculator | **6** | Guest, Room, SeasonalPricingStrategy, TouristTaxCalculator | **4** |
| `ConsoleBookingUi` | BookingApplicationController, ConsoleCommandParser | 2 | без змін | 2 |
| `BookingFactory` (новий) | – | – | Guest, Room, Booking, Season, TouristTaxCalculator | 5** |
| `Main` (composition root) | 7 типів | 7 | 9 типів | 9** |

\* На рівні типів CBO класу `HotelManager` дорівнює 3, але це приховує його реальне зчеплення. Поведінку файлового репозиторію та SMS-сервісу він вбудовує як жорстко закодовані виклики `println`. Якщо врахувати їх як логічні залежності, скоригований CBO становить 5.

\** `BookingFactory` і `Main` свідомо є місцями, де концентрується зчеплення, пов'язане зі створенням об'єктів. Вони лежать поза ціллю ≤4, яка стосується бізнес-класів і класів оркестрації.

## 2. High Cohesion (LCOM4)

| Клас | LCOM4 до | LCOM4 після | Примітка |
|---|---|---|---|
| `Booking` | **2** | **1** | `status` тепер використовується в `generateConfirmationText` |
| `BookingApplicationController` | 1 | 1 | один метод, що використовує всі поля |
| `ConsoleBookingUi` | 1 | 1 | `start()` використовує обидва поля |
| `BookingFactory` (новий) | – | 1 | `create()` використовує обидва поля |
| `HotelManager` | 1 (виродженний випадок) | – | один метод, жодних полів. LCOM4 не бачить чотирьох змішаних відповідальностей (UI, домен, збереження, SMS) |
| `Guest`, `Room` | 4, 3 | 4, 3 | об'єкти-значення лише з аксесорами, відомий артефакт LCOM4. Не рефакторилися, тому поза межами завдання |

## 3. Підсумкова порівняльна таблиця

| Метрика | Клас | До | Після | Ціль | Досягнуто |
|---|---|---|---|---|---|
| CBO | `Booking` | 6 | 4 | ≤ 4 | Так |
| CBO | `BookingApplicationController` | 5 | 4 | ≤ 4 | Так |
| CBO | `ConsoleBookingUi` | 2 | 2 | ≤ 4 | Так |
| CBO | `HotelManager` (скоригований) | 5 | видалено | ≤ 4 | Так |
| CBO | `BookingFactory` / `Main` | – / 7 | 5 / 9 | виняток | Рішення щодо меж |
| LCOM4 | `Booking` | 2 | 1 | = 1 | Так |
| LCOM4 | `BookingApplicationController` | 1 | 1 | = 1 | Так |
| LCOM4 | `ConsoleBookingUi` | 1 | 1 | = 1 | Так |
| LCOM4 | `BookingFactory` | – | 1 | = 1 | Так |

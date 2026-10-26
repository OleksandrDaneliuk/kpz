void main() {
    demoTask1_Singleton();
    IO.println("\n-----------------------------------------------------------------------\n");

}

public static void demoTask1_Singleton() {
    IO.println(">>> ЗАВДАННЯ 1: Моделювання єдиного реєстру ліжкового фонду (Singleton)\n");

    Logger.log("Приймальне Відділення", "Запит на отримання екземпляра реєстру...");
    BedRegistry registry1 = BedRegistry.getInstance();
    registry1.addBed(new Bed("101", "Терапія"));
    registry1.addBed(new Bed("202", "Хірургія"));

    Logger.log("Кардіологія", "Запит на отримання екземпляра реєстру...");
    BedRegistry registry2 = BedRegistry.getInstance();

    Logger.log("Приймальне Відділення", "Поселення пацієнта Коваленко І. В. у палату 101");
    registry1.getBed("101").occupy("Коваленко І. В.");

    Logger.log("Кардіологія", "Перевірка ліжка 101 через інше посилання системи:");
    Logger.log("Кардіологія", "Стан ліжка 101: " + registry2.getBed("101"));

    Logger.log("Main", "Перевірка посилань (registry1 == registry2): " + (registry1 == registry2));
}
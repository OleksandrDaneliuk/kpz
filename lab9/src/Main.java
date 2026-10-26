import java.util.List;

public class Main {
    void main() {
        demoTask1_Singleton();
        System.out.println("\n-----------------------------------------------------------------------\n");

        demoTask2_Prototype();
        System.out.println("\n-----------------------------------------------------------------------\n");

    }

    public static void demoTask1_Singleton() {
        System.out.println(">>> ЗАВДАННЯ 1: Моделювання єдиного реєстру ліжкового фонду (Singleton)\n");

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

    public static void demoTask2_Prototype() {
        System.out.println(">>> ЗАВДАННЯ 2: Клонування планів обстеження й лікування (Prototype)\n");

        Logger.log("Система", "Ініціалізація базового прототипу кардіологічного плану...");
        TreatmentPlan cardiologyBasePrototype = new TreatmentPlan(
                "Кардіологічний базовий",
                List.of("Загальний аналіз крові", "ЕКГ", "Біохімія крові"),
                List.of("ЕхоКГ", "Моніторинг АТ"),
                "Дієта №10 (низькосольова)",
                "Палатний"
        );

        System.out.println();

        Logger.log("Лікар-Кардіолог", "Клонування базового плану для пацієнта Петренко А. М.");
        TreatmentPlan planPatient1 = cardiologyBasePrototype.clone();
        planPatient1.setPatientName("Петренко А. М.");
        planPatient1.addTest("Аналіз на тропоніни");
        planPatient1.printInfo();

        System.out.println();

        Logger.log("Лікар-Кардіолог", "Клонування базового плану для пацієнта Сидоренко О. В.");
        TreatmentPlan planPatient2 = cardiologyBasePrototype.clone();
        planPatient2.setPatientName("Сидоренко О. В.");
        planPatient2.setDiet("Дієта №9 (гіпоглікемічна)");
        planPatient2.printInfo();

        System.out.println();

        Logger.log("Система", "Перевірка стану вихідного базового прототипу:");
        cardiologyBasePrototype.printInfo();
    }
}
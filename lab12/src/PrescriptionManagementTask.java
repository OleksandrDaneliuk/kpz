import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PrescriptionManagementTask {

    private static void log(String event) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.printf("[%s] [Flyweight ] %s%n", time, event);
    }

    // Внутрішній стан (Intrinsic State) — Легковаговик
    public static class MedicineType {
        private final String name;
        private final String activeIngredient;
        private final String form;
        private final String instruction;

        public MedicineType(String name, String activeIngredient, String form, String instruction) {
            this.name = name;
            this.activeIngredient = activeIngredient;
            this.form = form;
            this.instruction = instruction;
            log("Створено об'єкт препарату в пам'яті: " + name);
        }

        public String getName() {
            return name;
        }

        public String getDetails() {
            return String.format("%s (%s, %s)", name, activeIngredient, form);
        }
    }

    // Потокобезпечна фабрика легковаговиків
    public static class MedicineFactory {
        private static final Map<String, MedicineType> medicinesPool = new ConcurrentHashMap<>();

        public static MedicineType getMedicine(String name, String activeIngredient, String form, String instruction) {
            return medicinesPool.computeIfAbsent(name,
                    k -> new MedicineType(name, activeIngredient, form, instruction));
        }

        public static int getCreatedMedicinesCount() {
            return medicinesPool.size();
        }
    }

    // Зовнішній стан (Extrinsic State / Context) — Призначення
    public static class Prescription {
        private final String patientName;
        private final String dosage;
        private final String time;
        private final MedicineType medicine;

        public Prescription(String patientName, String dosage, String time, MedicineType medicine) {
            this.patientName = patientName;
            this.dosage = dosage;
            this.time = time;
            this.medicine = medicine;
        }

        public MedicineType getMedicine() {
            return medicine;
        }

        public void printInfo() {
            System.out.printf("  Призначення: Пацієнт %s | Доза: %s | Час: %s | Препарат: %s [Ref ID: %d]%n",
                    patientName, dosage, time, medicine.getDetails(), System.identityHashCode(medicine));
        }
    }

    public static void run() {
        String[][] catalog = {
                {"Парацетамол", "Paracetamol", "Таблетки", "Приймати після їди при температурі"},
                {"Ібупрофен", "Ibuprofen", "Капсули", "Протизапальний засіб, 1 таб 3р/день"},
                {"Амоксицилін", "Amoxicillin", "Капсули", "Антибіотик, суворо за розкладом"},
                {"Аспірин", "Acetylsalicylic acid", "Таблетки", "Розрідження крові"},
                {"Омепразол", "Omeprazole", "Капсули", "Приймати вранці до їди"},
                {"Лорноксикам", "Lornoxicam", "Розчин", "Внутрішньом'язово при болю"},
                {"Дексаметазон", "Dexamethasone", "Ампули", "Глюкокортикостероїд"},
                {"Ацетилцестеїн", "Acetylcysteine", "Порошок", "Розчинити у воді при кашлі"}
        };

        List<Prescription> prescriptions = new ArrayList<>();
        Random random = new Random(42);

        // Формування 1000 призначень для 300 пацієнтів
        for (int i = 1; i <= 1000; i++) {
            String patient = "Пацієнт_№" + (random.nextInt(300) + 1);
            String[] medInfo = catalog[random.nextInt(8)];

            MedicineType medType = MedicineFactory.getMedicine(medInfo[0], medInfo[1], medInfo[2], medInfo[3]);
            String dosage = (random.nextInt(3) + 1) + " таб/день";
            String time = (8 + random.nextInt(12)) + ":00";

            prescriptions.add(new Prescription(patient, dosage, time, medType));
        }

        log("Генерацію 1000 призначень завершено.");
        log("================ СТАТИСТИКА ОБ'ЄКТІВ ================");
        log("Кількість створених об'єктів призначень (Prescription): " + prescriptions.size());
        log("Кількість фактично створених об'єктів препаратів (MedicineType): " + MedicineFactory.getCreatedMedicinesCount());

        log("================ ПЕРЕВІРКА ПОСИЛАНЬ В ПАМ'ЯТІ ================");
        Prescription p1 = null;
        Prescription p2 = null;

        for (Prescription p : prescriptions) {
            if (p.getMedicine().getName().equals("Парацетамол")) {
                if (p1 == null) {
                    p1 = p;
                } else if (!p.equals(p1)) {
                    p2 = p;
                    break;
                }
            }
        }

        if (p1 != null && p2 != null) {
            p1.printInfo();
            p2.printInfo();

            boolean sameReference = (p1.getMedicine() == p2.getMedicine());
            log("Чи посилаються обидва призначення на той самий об'єкт препарату (p1.getMedicine() == p2.getMedicine())? -> " + sameReference);
            log("System.identityHashCode(p1.getMedicine()) = " + System.identityHashCode(p1.getMedicine()));
            log("System.identityHashCode(p2.getMedicine()) = " + System.identityHashCode(p2.getMedicine()));
        }
    }
}
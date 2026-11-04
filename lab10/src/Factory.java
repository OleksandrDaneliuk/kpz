import java.util.ArrayList;
import java.util.List;

// --- Інтерфейс продукту (Виписний документ) ---
interface DischargeDocument {
    String getTitle();
    String getContent();
    void sign(String doctorSignature);
}

// --- Конкретні продукти ---
class TherapyEpicrisis implements DischargeDocument {
    private final String patientName;
    private boolean signed = false;

    public TherapyEpicrisis(String patientName) {
        this.patientName = patientName;
    }

    @Override
    public String getTitle() {
        return "Загальний епікриз (Терапія)";
    }

    @Override
    public String getContent() {
        return "Пацієнт: " + patientName + " | Перебіг захворювання: стабільний. Призначено амбулаторне спостереження.";
    }

    @Override
    public void sign(String doctorSignature) {
        this.signed = true;
    }

    @Override
    public String toString() {
        return "[" + getTitle() + "] " + getContent() + " (Підписано: " + signed + ")";
    }
}

class SurgeryEpicrisis implements DischargeDocument {
    private final String patientName;
    private boolean signed = false;

    public SurgeryEpicrisis(String patientName) {
        this.patientName = patientName;
    }

    @Override
    public String getTitle() {
        return "Епікриз з протоколом операції (Хірургія)";
    }

    @Override
    public String getContent() {
        return "Пацієнт: " + patientName + " | Операція прошла успішно. Шви знято. Протокол втручання додано.";
    }

    @Override
    public void sign(String doctorSignature) {
        this.signed = true;
    }

    @Override
    public String toString() {
        return "[" + getTitle() + "] " + getContent() + " (Підписано: " + signed + ")";
    }
}

class CardiologyEpicrisis implements DischargeDocument {
    private final String patientName;
    private boolean signed = false;

    public CardiologyEpicrisis(String patientName) {
        this.patientName = patientName;
    }

    @Override
    public String getTitle() {
        return "Епікриз з результатами ЕКГ (Кардіологія)";
    }

    @Override
    public String getContent() {
        return "Пацієнт: " + patientName + " | Динаміка ЕКГ позитивна. Синусовий ритм відновлено.";
    }

    @Override
    public void sign(String doctorSignature) {
        this.signed = true;
    }

    @Override
    public String toString() {
        return "[" + getTitle() + "] " + getContent() + " (Підписано: " + signed + ")";
    }
}

// Новий клас продукту для Педіатрії (без зміни існуючої логіки)
class PediatricEpicrisis implements DischargeDocument {
    private final String patientName;
    private boolean signed = false;

    public PediatricEpicrisis(String patientName) {
        this.patientName = patientName;
    }

    @Override
    public String getTitle() {
        return "Педіатричний епікриз з картою щеплень (Педіатрія)";
    }

    @Override
    public String getContent() {
        return "Пацієнт (дитина): " + patientName + " | Одужання повне. Вакцинаційний календар дотримано.";
    }

    @Override
    public void sign(String doctorSignature) {
        this.signed = true;
    }

    @Override
    public String toString() {
        return "[" + getTitle() + "] " + getContent() + " (Підписано: " + signed + ")";
    }
}

// --- Абстрактний Творець (Department) ---
abstract class Department {
    private final String name;
    private final List<DischargeDocument> archive = new ArrayList<>();

    public Department(String name) {
        this.name = name;
    }

    // Єдина процедура виписки (не змінюється при додаванні нових відділень)
    public DischargeDocument dischargePatient(String patientName, String doctorSignature) {
        System.out.println("\n--- Розпочато виписку у відділенні: " + name + " ---");

        // Step 1: Перевірка завершення лікування
        System.out.println("[1] Перевірка завершення лікування пацієнта: " + patientName);

        // Step 2: Фабричний метод створює конкретний документ
        DischargeDocument doc = createDischargeDocument(patientName);

        // Step 3: Підписати документ
        doc.sign(doctorSignature);
        System.out.println("[2] Документ сформовано та підписано лікарем: " + doctorSignature);

        // Step 4: Передати в архів
        archive.add(doc);
        System.out.println("[3] Передано до архіву відділення " + name);

        return doc;
    }

    // Factory Method
    protected abstract DischargeDocument createDischargeDocument(String patientName);

    public List<DischargeDocument> getArchive() {
        return archive;
    }
}

// --- Конкретні Творці ---
class TherapyDepartment extends Department {
    public TherapyDepartment() { super("Терапевтичне відділення"); }

    @Override
    protected DischargeDocument createDischargeDocument(String patientName) {
        return new TherapyEpicrisis(patientName);
    }
}

class SurgeryDepartment extends Department {
    public SurgeryDepartment() { super("Хірургічне відділення"); }

    @Override
    protected DischargeDocument createDischargeDocument(String patientName) {
        return new SurgeryEpicrisis(patientName);
    }
}

class CardiologyDepartment extends Department {
    public CardiologyDepartment() { super("Кардіологічне відділення"); }

    @Override
    protected DischargeDocument createDischargeDocument(String patientName) {
        return new CardiologyEpicrisis(patientName);
    }
}

// Додавання нового відділення розширенням класу
class PediatricDepartment extends Department {
    public PediatricDepartment() { super("Педіатричне відділення"); }

    @Override
    protected DischargeDocument createDischargeDocument(String patientName) {
        return new PediatricEpicrisis(patientName);
    }
}
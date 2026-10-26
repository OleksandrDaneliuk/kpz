import java.util.ArrayList;
import java.util.List;

class UltrasoundMachine {
    private final String id;
    private String currentPatient;
    private String profileSettings;

    public UltrasoundMachine(String id) {
        this.id = id;
        Logger.log("UltrasoundMachine", "«Дорога» операція: Фізична ініціалізація та первинне самотестування апарата " + id + "...");
        simulateDelay(1000);
        this.profileSettings = "Базовий";
    }

    public String getId() { return id; }

    public void setupForPatient(String patientName, String profile) {
        Logger.log("UltrasoundMachine", "Калібрування та застосування профілю '" + profile + "' для " + id + " під пацієнта " + patientName);
        simulateDelay(500);
        this.currentPatient = patientName;
        this.profileSettings = profile;
    }

    public void reset() {
        Logger.log("UltrasoundMachine", "Очищення даних пацієнта " + currentPatient + " та дезінфекція " + id);
        this.currentPatient = null;
        this.profileSettings = "Базовий";
    }

    private static void simulateDelay(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}

// Пул апаратів УЗД
class UltrasoundMachinePool {
    private final List<UltrasoundMachine> available = new ArrayList<>();
    private final List<UltrasoundMachine> inUse = new ArrayList<>();

    public UltrasoundMachinePool(int capacity) {
        Logger.log("UltrasoundPool", "Створення пулу УЗД обладнання ємністю " + capacity + " од.");
        for (int i = 1; i <= capacity; i++) {
            available.add(new UltrasoundMachine("УЗД-0" + i));
        }
    }

    public UltrasoundMachine acquireMachine(String departmentName, String patientName, String profile) {
        if (available.isEmpty()) {
            Logger.log("UltrasoundPool", "[" + departmentName + "] ПОМИЛКА: Немає вільних апаратів УЗД для пацієнта " + patientName + "! Запит відхилено.");
            return null;
        }

        UltrasoundMachine machine = available.remove(0);
        inUse.add(machine);
        Logger.log("UltrasoundPool", "[" + departmentName + "] Отримано з пулу: " + machine.getId());
        machine.setupForPatient(patientName, profile);
        return machine;
    }

    public void releaseMachine(UltrasoundMachine machine) {
        if (machine != null && inUse.remove(machine)) {
            machine.reset();
            available.add(machine);
            Logger.log("UltrasoundPool", "Апарат " + machine.getId() + " успішно повернуто до пулу.");
        }
    }
}
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class Logger {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void log(String source, String message) {
        String time = LocalTime.now().format(FORMATTER);
        System.out.printf("[%s] [%s] %s%n", time, source, message);
    }
}

enum BedState { FREE, OCCUPIED }

class Bed {
    private final String roomNumber;
    private final String department;
    private BedState state;
    private String patientName;

    public Bed(String roomNumber, String department) {
        this.roomNumber = roomNumber;
        this.department = department;
        this.state = BedState.FREE;
        this.patientName = null;
    }

    public String getRoomNumber() { return roomNumber; }
    public BedState getState() { return state; }

    public void occupy(String patientName) {
        this.state = BedState.OCCUPIED;
        this.patientName = patientName;
    }

    public void release() {
        this.state = BedState.FREE;
        this.patientName = null;
    }

    @Override
    public String toString() {
        return String.format("Палата №%s (%s) — %s",
                roomNumber, department, (state == BedState.FREE ? "ВІЛЬНЕ" : "ЗАЙНЯТЕ (" + patientName + ")"));
    }
}

class BedRegistry {
    private static volatile BedRegistry instance;
    private final Map<String, Bed> beds = new ConcurrentHashMap<>();

    private BedRegistry() {
        Logger.log("BedRegistry", "«Дорога» операція: Ініціалізація баз даних та завантаження конфігурації ліжкового фонду...");
        simulateDelay(1200);
        Logger.log("BedRegistry", "Реєстр успішно створено та ініціалізовано.");
    }

    public static BedRegistry getInstance() {
        if (instance == null) {
            synchronized (BedRegistry.class) {
                if (instance == null) {
                    instance = new BedRegistry();
                }
            }
        }
        return instance;
    }

    public void addBed(Bed bed) {
        beds.put(bed.getRoomNumber(), bed);
        Logger.log("BedRegistry", "Зареєстровано об'єкт: " + bed);
    }

    public Bed getBed(String roomNumber) {
        return beds.get(roomNumber);
    }

    public void printRegistryStatus() {
        Logger.log("BedRegistry", "--- Поточний звіт ліжкового фонду ---");
        for (Bed bed : beds.values()) {
            Logger.log("BedRegistry", "  " + bed);
        }
    }

    private static void simulateDelay(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
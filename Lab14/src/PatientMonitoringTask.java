import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PatientMonitoringTask {

    // Immutable-об'єкт події
    public record VitalSigns(
            String patientId,
            int heartRate,
            int systolicBp,
            int spo2
    ) {
        public boolean isCritical() {
            return heartRate < 50 || heartRate > 120
                    || systolicBp < 85 || systolicBp > 160
                    || spo2 < 92;
        }

        @Override
        public String toString() {
            return String.format("Пацієнт %s [Пульс: %d, Тиск: %d, SpO2: %d%%]",
                    patientId, heartRate, systolicBp, spo2);
        }
    }

    // Observer
    public interface PatientMonitorListener {
        void onVitalSignsUpdated(String time, VitalSigns signs);
    }

    // Subject / Publisher
    public static class PatientMonitor {
        private final String monitorId;
        private final String patientId;
        private final List<PatientMonitorListener> listeners = new CopyOnWriteArrayList<>();

        public PatientMonitor(String monitorId, String patientId) {
            this.monitorId = monitorId;
            this.patientId = patientId;
        }

        public void subscribe(String time, PatientMonitorListener listener) {
            listeners.add(listener);
            System.out.printf("[%s] [%s] Підписано отримувача: %s%n", time, monitorId, listener);
        }

        public void unsubscribe(String time, PatientMonitorListener listener) {
            listeners.remove(listener);
            System.out.printf("[%s] [%s] Відписано отримувача: %s%n", time, monitorId, listener);
        }

        public void recordMeasurements(String time, int heartRate, int systolicBp, int spo2) {
            VitalSigns signs = new VitalSigns(patientId, heartRate, systolicBp, spo2);
            System.out.printf("[%s] [%s] Зафіксовано показники -> %s%n", time, monitorId, signs);
            notifyListeners(time, signs);
        }

        private void notifyListeners(String time, VitalSigns signs) {
            for (PatientMonitorListener listener : listeners) {
                listener.onVitalSignsUpdated(time, signs);
            }
        }
    }

    // Конкретний підписник 1: Пост медсестри
    public static class NurseStation implements PatientMonitorListener {
        private final String stationName;

        public NurseStation(String stationName) {
            this.stationName = stationName;
        }

        @Override
        public void onVitalSignsUpdated(String time, VitalSigns signs) {
            if (signs.isCritical()) {
                System.out.printf("[%s] [%s] ТРИВОГА! КРИТИЧНІ ПОКАЗНИКИ: %s. Терміновий виклик у палату!%n",
                        time, stationName, signs);
            }
        }

        @Override
        public String toString() {
            return stationName;
        }
    }

    // Конкретний підписник 2: Електронна картка пацієнта
    public static class ElectronicMedicalRecord implements PatientMonitorListener {
        @Override
        public void onVitalSignsUpdated(String time, VitalSigns signs) {
            System.out.printf("[%s] [Електронна картка] Збережено в історію хвороби: %s%n", time, signs);
        }

        @Override
        public String toString() {
            return "Електронна картка";
        }
    }

    // Конкретний підписник 3: Черговий лікар
    public static class DutyDoctor implements PatientMonitorListener {
        private final String doctorName;

        public DutyDoctor(String doctorName) {
            this.doctorName = doctorName;
        }

        @Override
        public void onVitalSignsUpdated(String time, VitalSigns signs) {
            if (signs.isCritical()) {
                System.out.printf("[%s] [Черговий лікар: %s] СПОВІЩЕННЯ НА ПЕЙДЖЕР! Потрібен огляд: %s%n",
                        time, doctorName, signs);
            }
        }

        @Override
        public String toString() {
            return "Черговий лікар " + doctorName;
        }
    }

    public static void run() {
        System.out.println("=== ЗАВДАННЯ 1: МОНІТОРИНГ ПОКАЗНИКІВ ПАЦІЄНТА (OBSERVER) ===");

        PatientMonitor monitor = new PatientMonitor("Монітор-ВІТ-04", "Іваненко П.С.");
        NurseStation nurseStation = new NurseStation("Пост медсестри №2");
        ElectronicMedicalRecord emr = new ElectronicMedicalRecord();
        DutyDoctor doctorKovalenko = new DutyDoctor("Коваленко О.І.");
        DutyDoctor doctorMelnyk = new DutyDoctor("Мельник Т.В.");

        // 1. Підписуємо отримувачів
        monitor.subscribe("08:00", nurseStation);
        monitor.subscribe("08:00", emr);
        monitor.subscribe("08:00", doctorKovalenko);

        // 2. Нормальні показники -> картка робить запис, тривоги немає
        monitor.recordMeasurements("08:15", 74, 120, 98);

        // 3. Критична сатурація -> реагують усі по-різному
        monitor.recordMeasurements("08:42", 118, 135, 87);

        // 4. Зміна чергового лікаря та повторне критичне падіння показників
        monitor.unsubscribe("09:00", doctorKovalenko);
        monitor.subscribe("09:00", doctorMelnyk);
        monitor.recordMeasurements("09:10", 132, 165, 85);
    }
}
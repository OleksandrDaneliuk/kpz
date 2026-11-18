import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class DoctorPrescriptionsTask {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static void log(String source, String event) {
        String time = LocalTime.now().format(TIME_FMT);
        System.out.printf("[%s] [%-28s] %s%n", time, source, event);
    }

    // Інтерфейс команди (Command)
    public interface PrescriptionCommand {
        String getDescription();
        void execute(String nurseName);
        void undo(String doctorName);
    }

    // Отримувач (Receiver) — лист призначень пацієнта
    public static class PatientChart {
        private final String patientName;
        private final List<String> records = new ArrayList<>();

        public PatientChart(String patientName) {
            this.patientName = patientName;
        }

        public String getPatientName() {
            return patientName;
        }

        public void addMedication(String medication, String nurseName) {
            records.add(medication);
            log("PatientChart:" + patientName, "Фактично виконав '" + nurseName + "' -> додано препарат: " + medication);
        }

        public void removeMedication(String medication, String doctorName) {
            records.remove(medication);
            log("PatientChart:" + patientName, "Відкликано ініціатором '" + doctorName + "' -> скасовано препарат: " + medication);
        }

        public void changeDosage(String medication, String dosage, String nurseName) {
            records.add(medication + " (" + dosage + ")");
            log("PatientChart:" + patientName, "Фактично виконав '" + nurseName + "' -> змінено дозу " + medication + " на " + dosage);
        }

        public void revertDosage(String medication, String doctorName) {
            log("PatientChart:" + patientName, "Відкликано ініціатором '" + doctorName + "' -> повернуто попередню дозу для " + medication);
        }

        public void cancelProcedure(String procedure, String nurseName) {
            records.remove(procedure);
            log("PatientChart:" + patientName, "Фактично виконав '" + nurseName + "' -> скасовано процедуру: " + procedure);
        }

        public void restoreProcedure(String procedure, String doctorName) {
            log("PatientChart:" + patientName, "Відкликано ініціатором '" + doctorName + "' -> відновлено процедуру: " + procedure);
        }
    }

    // Конкретна команда 1: Додати препарат
    public static class AddMedicationCommand implements PrescriptionCommand {
        private final PatientChart chart;
        private final String medication;

        public AddMedicationCommand(PatientChart chart, String medication) {
            this.chart = chart;
            this.medication = medication;
        }

        @Override
        public String getDescription() {
            return "Додати препарат '" + medication + "' (пацієнт: " + chart.getPatientName() + ")";
        }

        @Override
        public void execute(String nurseName) {
            chart.addMedication(medication, nurseName);
        }

        @Override
        public void undo(String doctorName) {
            chart.removeMedication(medication, doctorName);
        }
    }

    // Конкретна команда 2: Змінити дозу
    public static class ChangeDosageCommand implements PrescriptionCommand {
        private final PatientChart chart;
        private final String medication;
        private final String newDosage;

        public ChangeDosageCommand(PatientChart chart, String medication, String newDosage) {
            this.chart = chart;
            this.medication = medication;
            this.newDosage = newDosage;
        }

        @Override
        public String getDescription() {
            return "Змінити дозу '" + medication + "' на " + newDosage + " (пацієнт: " + chart.getPatientName() + ")";
        }

        @Override
        public void execute(String nurseName) {
            chart.changeDosage(medication, newDosage, nurseName);
        }

        @Override
        public void undo(String doctorName) {
            chart.revertDosage(medication, doctorName);
        }
    }

    // Конкретна команда 3: Скасувати процедуру
    public static class CancelProcedureCommand implements PrescriptionCommand {
        private final PatientChart chart;
        private final String procedure;

        public CancelProcedureCommand(PatientChart chart, String procedure) {
            this.chart = chart;
            this.procedure = procedure;
        }

        @Override
        public String getDescription() {
            return "Скасувати процедуру '" + procedure + "' (пацієнт: " + chart.getPatientName() + ")";
        }

        @Override
        public void execute(String nurseName) {
            chart.cancelProcedure(procedure, nurseName);
        }

        @Override
        public void undo(String doctorName) {
            chart.restoreProcedure(procedure, doctorName);
        }
    }

    // Ініціатор (Invoker) — черга призначень
    public static class PrescriptionQueue {
        private final Deque<PrescriptionCommand> pendingQueue = new ArrayDeque<>();
        private final List<String> executionJournal = new ArrayList<>();

        public void enqueue(PrescriptionCommand command, String doctorName) {
            pendingQueue.addLast(command);
            log("PrescriptionQueue", "Отримано запит від '" + doctorName + "' -> " + command.getDescription());
        }

        public void revokeLast(String doctorName) {
            PrescriptionCommand last = pendingQueue.pollLast();
            if (last != null) {
                log("PrescriptionQueue", "Лікар '" + doctorName + "' відкликає останнє призначення -> " + last.getDescription());
                last.undo(doctorName);
            } else {
                log("PrescriptionQueue", "Черга порожня, немає дій для відкликання.");
            }
        }

        public void printPending() {
            log("PrescriptionQueue", "Очікують виконання (" + pendingQueue.size() + " шт.):");
            int idx = 1;
            for (PrescriptionCommand cmd : pendingQueue) {
                System.out.printf("   %d. %s%n", idx++, cmd.getDescription());
            }
        }

        public void executeAll(String nurseName) {
            log("Nurse:" + nurseName, "Починає виконання черги призначень (" + pendingQueue.size() + " шт.)...");
            while (!pendingQueue.isEmpty()) {
                PrescriptionCommand cmd = pendingQueue.pollFirst();
                cmd.execute(nurseName);
                String timestamp = LocalTime.now().format(TIME_FMT);
                executionJournal.add(String.format("[%s] Виконав: %s | Дія: %s", timestamp, nurseName, cmd.getDescription()));
            }
        }

        public void printJournal() {
            log("ExecutionJournal", "Журнал виконаних призначень (" + executionJournal.size() + " записів):");
            for (String record : executionJournal) {
                System.out.println("   " + record);
            }
        }
    }

    public static void run() {
        PatientChart kovalenkoChart = new PatientChart("Коваленко");
        PrescriptionQueue queue = new PrescriptionQueue();

        // 1. Лікар вносить 3 призначення пацієнту Коваленко
        queue.enqueue(new AddMedicationCommand(kovalenkoChart, "Цефтріаксон 1.0 г"), "Лікар Петренко");
        queue.enqueue(new ChangeDosageCommand(kovalenkoChart, "Ібупрофен", "400 мг"), "Лікар Петренко");
        queue.enqueue(new CancelProcedureCommand(kovalenkoChart, "Електрофорез"), "Лікар Петренко");
        System.out.println();
        queue.printPending();
        System.out.println();

        // 2. Лікар відкликає помилкове останнє призначення
        queue.revokeLast("Лікар Петренко");
        System.out.println();
        queue.printPending();
        System.out.println();

        // 3. Медсестра виконує всі призначення з черги
        queue.executeAll("Медсестра Іваненко");
        System.out.println();
        queue.printJournal();
    }
}
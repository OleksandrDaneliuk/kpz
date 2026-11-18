import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HotlineSupportTask {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static void log(String source, String event) {
        String time = LocalTime.now().format(TIME_FMT);
        System.out.printf("[%s] [%-28s] %s%n", time, source, event);
    }

    public enum Severity {
        GENERAL,
        TREATMENT,
        URGENT,
        LIFE_THREAT,
        UNSUPPORTED
    }

    // Об'єкт запиту
    public static class PatientCall {
        private final String patientName;
        private final Severity severity;
        private final String description;

        public PatientCall(String patientName, Severity severity, String description) {
            this.patientName = patientName;
            this.severity = severity;
            this.description = description;
        }

        public String getPatientName() {
            return patientName;
        }

        public Severity getSeverity() {
            return severity;
        }

        public String getDescription() {
            return description;
        }
    }

    public static abstract class SupportHandler {
        protected SupportHandler next;
        protected final String roleName;

        public SupportHandler(String roleName) {
            this.roleName = roleName;
        }

        public SupportHandler setNext(SupportHandler next) {
            this.next = next;
            return next;
        }

        public void handle(PatientCall call) {
            log(roleName, "Отримав звернення від '" + call.getPatientName() + "' [" + call.getSeverity() + ": " + call.getDescription() + "]");

            if (canHandle(call)) {
                process(call);
            } else if (next != null) {
                log(roleName, "Не може обробити -> передає далі до [" + next.roleName + "]");
                next.handle(call);
            } else {
                log("HotlineSystem", "Звернення від '" + call.getPatientName() + "' не обробив жоден рівень. Зверніться до реєстратури.");
            }
        }

        protected abstract boolean canHandle(PatientCall call);
        protected abstract void process(PatientCall call);
    }

    // Рівень 1: Черговий оператор
    public static class OperatorHandler extends SupportHandler {
        public OperatorHandler() {
            super("Черговий оператор");
        }

        @Override
        protected boolean canHandle(PatientCall call) {
            return call.getSeverity() == Severity.GENERAL;
        }

        @Override
        protected void process(PatientCall call) {
            log(roleName, "ФАКТИЧНО ОБРОБИВ звернення пацієнта '" + call.getPatientName() + "' (надано довідку).");
        }
    }

    // Рівень 2: Медсестра
    public static class NurseHandler extends SupportHandler {
        public NurseHandler() {
            super("Медсестра");
        }

        @Override
        protected boolean canHandle(PatientCall call) {
            return call.getSeverity() == Severity.TREATMENT;
        }

        @Override
        protected void process(PatientCall call) {
            log(roleName, "ФАКТИЧНО ОБРОБИВ звернення пацієнта '" + call.getPatientName() + "' (консультація щодо прийому ліків).");
        }
    }

    // Рівень 3: Черговий лікар
    public static class DutyDoctorHandler extends SupportHandler {
        public DutyDoctorHandler() {
            super("Черговий лікар");
        }

        @Override
        protected boolean canHandle(PatientCall call) {
            // Лікар бере термінові звернення, а також питання лікування, якщо медсестра відсутня в ланцюжку
            return call.getSeverity() == Severity.URGENT || call.getSeverity() == Severity.TREATMENT;
        }

        @Override
        protected void process(PatientCall call) {
            log(roleName, "ФАКТИЧНО ОБРОБИВ звернення пацієнта '" + call.getPatientName() + "' (лікарська консультація рівня " + call.getSeverity() + ").");
        }
    }

    // Рівень 4: Екстрена служба
    public static class EmergencyServiceHandler extends SupportHandler {
        public EmergencyServiceHandler() {
            super("Екстрена служба (103)");
        }

        @Override
        protected boolean canHandle(PatientCall call) {
            return call.getSeverity() == Severity.LIFE_THREAT;
        }

        @Override
        protected void process(PatientCall call) {
            log(roleName, "ФАКТИЧНО ОБРОБИВ звернення пацієнта '" + call.getPatientName() + "' (терміново направлено бригаду швидкої!).");
        }
    }

    public static void run() {
        // Оператор -> Медсестра -> Черговий лікар -> Екстрена служба
        SupportHandler operator = new OperatorHandler();
        SupportHandler nurse = new NurseHandler();
        SupportHandler doctor = new DutyDoctorHandler();
        SupportHandler emergency = new EmergencyServiceHandler();
        operator.setNext(nurse).setNext(doctor).setNext(emergency);

        operator.handle(new PatientCall("Сидоренко", Severity.GENERAL, "Графік прийому терапевта"));
        System.out.println();
        operator.handle(new PatientCall("Бондар", Severity.TREATMENT, "Як розводити порошок для ін'єкцій?"));
        System.out.println();
        operator.handle(new PatientCall("Ткачук", Severity.URGENT, "Температура 39.6, сильний озноб"));
        System.out.println();
        operator.handle(new PatientCall("Мельник", Severity.LIFE_THREAT, "Гострий біль за грудиною, задишка"));
        System.out.println();

        // 2. нічний режим (без медсестри)
        SupportHandler nightOperator = new OperatorHandler();
        SupportHandler nightDoctor = new DutyDoctorHandler();
        SupportHandler nightEmergency = new EmergencyServiceHandler();
        nightOperator.setNext(nightDoctor).setNext(nightEmergency);

        log("Config", "Перебудовано на НІЧНИЙ ланцюжок (без медсестри): Оператор -> Лікар -> 103");
        System.out.println();

        nightOperator.handle(new PatientCall("Кравчук", Severity.TREATMENT, "Чи можна прийняти знеболювальне вночі?"));
        System.out.println();

        nightOperator.handle(new PatientCall("Гнатюк", Severity.UNSUPPORTED, "Питання щодо паркування біля корпусу"));
    }
}
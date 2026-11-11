import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MedicalReportsTask {

    private static void log(String event) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.printf("[%s] [Bridge    ] %s%n", time, event);
    }


    public interface ReportFormat {
        String generateOutput(String reportTitle, String reportData);
    }


    public static class PrintFormat implements ReportFormat {
        @Override
        public String generateOutput(String reportTitle, String reportData) {
            return String.format("[ДРУК] >>> %s <<<\n%s\n--- Надіслано на друкарку ---", reportTitle, reportData);
        }
    }

    public static class PdfFormat implements ReportFormat {
        @Override
        public String generateOutput(String reportTitle, String reportData) {
            return String.format("[PDF-ДОКУМЕНТ] Header: %s | Content: %s | Sign: Digital_PDF_OK", reportTitle, reportData);
        }
    }

    public static class StateSystemFormat implements ReportFormat {
        @Override
        public String generateOutput(String reportTitle, String reportData) {
            return String.format("<xml><type>%s</type><payload>%s</payload><status>SENT_TO_GOV</status></xml>", reportTitle, reportData);
        }
    }


    public static class CsvFormat implements ReportFormat {
        @Override
        public String generateOutput(String reportTitle, String reportData) {
            return String.format("REPORT_NAME,DATA\n\"%s\",\"%s\"", reportTitle, reportData.replace("\n", "; "));
        }
    }


    public static abstract class MedicalReport {
        protected ReportFormat format;

        public MedicalReport(ReportFormat format) {
            this.format = format;
        }

        public void setFormat(ReportFormat format) {
            this.format = format;
        }

        public abstract void render();
    }


    public static class PatientDischargeReport extends MedicalReport {
        private final String patientName;
        private final String diagnosis;

        public PatientDischargeReport(ReportFormat format, String patientName, String diagnosis) {
            super(format);
            this.patientName = patientName;
            this.diagnosis = diagnosis;
        }

        @Override
        public void render() {
            String data = "Пацієнт: " + patientName + ", ДІАГНОЗ: " + diagnosis + ", Стан: Виписано";
            String output = format.generateOutput("Виписка пацієнта", data);
            log("Сформовано Виписку пацієнта:\n" + output);
        }
    }

    public static class HealthDeptStatReport extends MedicalReport {
        private final int totalPatients;
        private final int recoveredCount;

        public HealthDeptStatReport(ReportFormat format, int totalPatients, int recoveredCount) {
            super(format);
            this.totalPatients = totalPatients;
            this.recoveredCount = recoveredCount;
        }

        @Override
        public void render() {
            String data = "Всього пацієнтів: " + totalPatients + ", Одужало: " + recoveredCount;
            String output = format.generateOutput("Форма для Департаменту ОЗ", data);
            log("Сформовано Статистичну форму:\n" + output);
        }
    }

    public static class DepartmentMonthlyReport extends MedicalReport {
        private final String deptName;
        private final String month;

        public DepartmentMonthlyReport(ReportFormat format, String deptName, String month) {
            super(format);
            this.deptName = deptName;
            this.month = month;
        }

        @Override
        public void render() {
            String data = "Відділення: " + deptName + ", Період: " + month + ", Використано ресурсів: 94%";
            String output = format.generateOutput("Звіт відділення за місяць", data);
            log("Сформовано Звіт відділення:\n" + output);
        }
    }


    public static void run() {
        log("Медичні звіти (Bridge)");

        MedicalReport discharge = new PatientDischargeReport(new PrintFormat(), "Петренко О.В.", "Гіпертонія ІІ ст.");
        discharge.render();

        discharge.setFormat(new PdfFormat());
        discharge.render();

        MedicalReport statReport = new HealthDeptStatReport(new StateSystemFormat(), 450, 412);
        statReport.render();

        log("Додано новий клас CsvFormat без зміни існуючих звітів.");
        MedicalReport deptReport = new DepartmentMonthlyReport(new CsvFormat(), "Кардіологія", "Жовтень 2026");
        deptReport.render();
    }
}
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;

// Опис предметної області: Медична інформаційна система діагностичного центру.
// Завдання коду: Обробка діагностичних показників пацієнта — валідація фізіологічних параметрів, розрахунок індексу маси тіла (ІМТ) та ризику серцево-судинних захворювань, формування медичного звіту в форматі JSON, збереження запису в архівний журнал та надсилання термінового SMS-сповіщення лікарю при критичних показниках.

public class PatientData {
    private String patientId;
    private String fullName;
    private double weightKg;
    private double heightCm;
    private int systolicBloodPressure;
    private int diastolicBloodPressure;
    private String doctorPhoneNumber;

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }

    public double getHeightCm() { return heightCm; }
    public void setHeightCm(double heightCm) { this.heightCm = heightCm; }

    public int getSystolicBloodPressure() { return systolicBloodPressure; }
    public void setSystolicBloodPressure(int systolicBloodPressure) { this.systolicBloodPressure = systolicBloodPressure; }

    public int getDiastolicBloodPressure() { return diastolicBloodPressure; }
    public void setDiastolicBloodPressure(int diastolicBloodPressure) { this.diastolicBloodPressure = diastolicBloodPressure; }

    public String getDoctorPhoneNumber() { return doctorPhoneNumber; }
    public void setDoctorPhoneNumber(String doctorPhoneNumber) { this.doctorPhoneNumber = doctorPhoneNumber; }
}

public class MedicalReportManager {
    public void processPatientDiagnostics(PatientData patient) {
        // 1. Валідація вхідних медичних даних
        if (patient == null) {
            throw new IllegalArgumentException("Параметр patient не може бути null.");
        }

        if (patient.getWeightKg() <= 2 || patient.getWeightKg() > 300) {
            throw new IllegalArgumentException("Некоректне значення ваги пацієнта.");
        }

        if (patient.getHeightCm() <= 40 || patient.getHeightCm() > 250) {
            throw new IllegalArgumentException("Некоректне значення зросту пацієнта.");
        }

        if (patient.getSystolicBloodPressure() < 50 || patient.getDiastolicBloodPressure() < 30) {
            throw new IllegalArgumentException("Показники тиску знаходяться за межами допустимого діапазону.");
        }

        // 2. Розрахунок медичних показників та оцінка ризику
        double heightMeters = patient.getHeightCm() / 100.0;
        double bmi = patient.getWeightKg() / (heightMeters * heightMeters);

        String riskCategory;
        if (bmi > 30 || patient.getSystolicBloodPressure() >= 140) {
            riskCategory = "Високий ризик";
        } else if (bmi >= 25 || patient.getSystolicBloodPressure() >= 130) {
            riskCategory = "Помірний ризик";
        } else {
            riskCategory = "Низький ризик";
        }

        // 3. Формування JSON-звіту для медкартки
        String jsonReport = String.format("{\n" +
                        "  \"PatientId\": \"%s\",\n" +
                        "  \"Name\": \"%s\",\n" +
                        "  \"BMI\": %.2f,\n" +
                        "  \"BloodPressure\": \"%d/%d\",\n" +
                        "  \"Category\": \"%s\",\n" +
                        "  \"GeneratedAt\": \"%s\"\n" +
                        "}",
                patient.getPatientId(),
                patient.getFullName(),
                bmi,
                patient.getSystolicBloodPressure(), patient.getDiastolicBloodPressure(),
                riskCategory,
                Instant.now().toString()
        );

        // 4. Збереження у файл медогляду
        String filename = "medical_report_" + patient.getPatientId() + ".json";
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(jsonReport);
        } catch (IOException e) {
            System.err.println("Помилка збереження JSON звіту: " + e.getMessage());
        }

        // 5. Відправка SMS лікарю при критичному стані
        if ("Високий ризик".equals(riskCategory)) {
            try {
                String smsMessage = String.format("УВАГА: Пацієнт %s має критичні показники! ІМТ: %.1f, Тиск: %d/%d",
                        patient.getFullName(), bmi, patient.getSystolicBloodPressure(), patient.getDiastolicBloodPressure());
                System.out.println("Надсилання SMS на номер " + patient.getDoctorPhoneNumber() + " через http://sms-gateway.local/send");
                System.out.println("Повідомлення: " + smsMessage);
            } catch (Exception ex) {
                System.out.println("Не вдалося відправити SMS лікарю: " + ex.getMessage());
            }
        }
    }
}
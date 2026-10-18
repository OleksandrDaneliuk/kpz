package task1;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Locale;

// ===== Domain model =====

class PatientData {
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
    public void setSystolicBloodPressure(int v) { this.systolicBloodPressure = v; }
    public int getDiastolicBloodPressure() { return diastolicBloodPressure; }
    public void setDiastolicBloodPressure(int v) { this.diastolicBloodPressure = v; }
    public String getDoctorPhoneNumber() { return doctorPhoneNumber; }
    public void setDoctorPhoneNumber(String v) { this.doctorPhoneNumber = v; }
}

enum RiskCategory {
    LOW("Low risk"), MODERATE("Moderate risk"), HIGH("High risk");

    private final String label;
    RiskCategory(String label) { this.label = label; }
    public String getLabel() { return label; }
}

record DiagnosticResult(PatientData patient, double bmi, RiskCategory riskCategory, Instant generatedAt) { }

// ===== Abstractions =====

interface PatientValidator { void validate(PatientData patient); }
interface ReportFormatter { String format(DiagnosticResult result); }
interface ReportRepository { void save(String patientId, String content); }
interface DoctorNotifier { void notifyCritical(DiagnosticResult result); }

class ReportStorageException extends RuntimeException {
    ReportStorageException(String message, Throwable cause) { super(message, cause); }
}

// ===== Services (one reason to change each) =====

class PatientDataValidator implements PatientValidator {
    @Override
    public void validate(PatientData patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Parameter patient must not be null.");
        }
        validateWeight(patient.getWeightKg());
        validateHeight(patient.getHeightCm());
        validateBloodPressure(patient.getSystolicBloodPressure(), patient.getDiastolicBloodPressure());
    }

    private void validateWeight(double weightKg) {
        if (weightKg <= 2 || weightKg > 300) {
            throw new IllegalArgumentException("Invalid patient weight.");
        }
    }

    private void validateHeight(double heightCm) {
        if (heightCm <= 40 || heightCm > 250) {
            throw new IllegalArgumentException("Invalid patient height.");
        }
    }

    private void validateBloodPressure(int systolic, int diastolic) {
        if (systolic < 50 || diastolic < 30) {
            throw new IllegalArgumentException("Blood pressure is outside the allowed range.");
        }
    }
}

class BmiCalculator {
    public double calculate(PatientData patient) {
        double heightMeters = patient.getHeightCm() / 100.0;
        return patient.getWeightKg() / (heightMeters * heightMeters);
    }
}

class RiskClassifier {
    public RiskCategory classify(double bmi, int systolicPressure) {
        if (bmi > 30 || systolicPressure >= 140) {
            return RiskCategory.HIGH;
        } else if (bmi >= 25 || systolicPressure >= 130) {
            return RiskCategory.MODERATE;
        }
        return RiskCategory.LOW;
    }
}

class JsonReportFormatter implements ReportFormatter {
    @Override
    public String format(DiagnosticResult result) {
        PatientData p = result.patient();
        return String.format(Locale.ROOT,
                "{\n" +
                "  \"PatientId\": \"%s\",\n" +
                "  \"Name\": \"%s\",\n" +
                "  \"BMI\": %.2f,\n" +
                "  \"BloodPressure\": \"%d/%d\",\n" +
                "  \"Category\": \"%s\",\n" +
                "  \"GeneratedAt\": \"%s\"\n" +
                "}",
                escape(p.getPatientId()), escape(p.getFullName()), result.bmi(),
                p.getSystolicBloodPressure(), p.getDiastolicBloodPressure(),
                result.riskCategory().getLabel(), result.generatedAt());
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

class FileReportRepository implements ReportRepository {
    @Override
    public void save(String patientId, String content) {
        String filename = "medical_report_" + patientId.replaceAll("[^A-Za-z0-9_-]", "_") + ".json";
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
        } catch (IOException e) {
            throw new ReportStorageException("Failed to save JSON report: " + e.getMessage(), e);
        }
    }
}

class SmsDoctorNotifier implements DoctorNotifier {
    private static final String GATEWAY_URL = "http://sms-gateway.local/send";

    @Override
    public void notifyCritical(DiagnosticResult result) {
        PatientData p = result.patient();
        String message = String.format(Locale.ROOT,
                "ATTENTION: Patient %s has critical indicators! BMI: %.1f, Pressure: %d/%d",
                p.getFullName(), result.bmi(),
                p.getSystolicBloodPressure(), p.getDiastolicBloodPressure());
        System.out.println("Sending SMS to " + p.getDoctorPhoneNumber() + " via " + GATEWAY_URL);
        System.out.println("Message: " + message);
    }
}

// ===== Orchestrator =====

class PatientDiagnosticsService {
    private final PatientValidator validator;
    private final BmiCalculator bmiCalculator;
    private final RiskClassifier riskClassifier;
    private final ReportFormatter formatter;
    private final ReportRepository repository;
    private final DoctorNotifier notifier;

    PatientDiagnosticsService(PatientValidator validator, BmiCalculator bmiCalculator,
                              RiskClassifier riskClassifier, ReportFormatter formatter,
                              ReportRepository repository, DoctorNotifier notifier) {
        this.validator = validator;
        this.bmiCalculator = bmiCalculator;
        this.riskClassifier = riskClassifier;
        this.formatter = formatter;
        this.repository = repository;
        this.notifier = notifier;
    }

    public void process(PatientData patient) {
        validator.validate(patient);

        double bmi = bmiCalculator.calculate(patient);
        RiskCategory risk = riskClassifier.classify(bmi, patient.getSystolicBloodPressure());
        DiagnosticResult result = new DiagnosticResult(patient, bmi, risk, Instant.now());

        try {
            repository.save(patient.getPatientId(), formatter.format(result));
        } catch (ReportStorageException e) {
            System.err.println(e.getMessage());
        }

        if (risk == RiskCategory.HIGH) {
            notifier.notifyCritical(result);
        }
    }
}

// ===== Composition root =====

public class Main {
    public static void main(String[] args) {
        PatientDiagnosticsService service = new PatientDiagnosticsService(
                new PatientDataValidator(), new BmiCalculator(), new RiskClassifier(),
                new JsonReportFormatter(), new FileReportRepository(), new SmsDoctorNotifier());

        PatientData patient = new PatientData();
        patient.setPatientId("P-001");
        patient.setFullName("Ivan Petrenko");
        patient.setWeightKg(105);
        patient.setHeightCm(175);
        patient.setSystolicBloodPressure(150);
        patient.setDiastolicBloodPressure(95);
        patient.setDoctorPhoneNumber("+380501234567");

        service.process(patient);
    }
}

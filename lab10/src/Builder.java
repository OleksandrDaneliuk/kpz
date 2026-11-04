import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Незмінний продукт
class Referral {
    private final String patientName;
    private final List<String> tests;
    private final boolean urgent;
    private final boolean fastingRequired;
    private final boolean requiresContrast;
    private final String notes;
    private final String doctorSignature;

    private Referral(Builder builder) {
        this.patientName = builder.patientName;
        this.tests = Collections.unmodifiableList(new ArrayList<>(builder.tests));
        this.urgent = builder.urgent;
        this.fastingRequired = builder.fastingRequired;
        this.requiresContrast = builder.requiresContrast;
        this.notes = builder.notes;
        this.doctorSignature = builder.doctorSignature;
    }

    @Override
    public String toString() {
        return "НАПРАВЛЕННЯ НА ОБСТЕЖЕННЯ:\n" +
                "  Пацієнт: " + patientName + "\n" +
                "  Дослідження: " + tests + "\n" +
                "  Терміново: " + (urgent ? "ТАК" : "Ні") + "\n" +
                "  Натщесерце: " + (fastingRequired ? "ТАК" : "Ні") + "\n" +
                "  Потреба в контрасті: " + (requiresContrast ? "ТАК" : "Ні") + "\n" +
                "  Примітки: " + (notes != null ? notes : "відсутні") + "\n" +
                "  Підпис лікаря: " + doctorSignature;
    }

    // --- Будівельник (Fluent Builder) ---
    public static class Builder {
        private String patientName;
        private final List<String> tests = new ArrayList<>();
        private boolean urgent = false;
        private boolean fastingRequired = false;
        private boolean requiresContrast = false;
        private String notes;
        private String doctorSignature;

        public Builder setPatient(String patientName) {
            this.patientName = patientName;
            return this;
        }

        public Builder addTest(String testName) {
            if (testName != null && !testName.isBlank()) {
                this.tests.add(testName);
            }
            return this;
        }

        public Builder setUrgent(boolean urgent) {
            this.urgent = urgent;
            return this;
        }

        public Builder setFastingRequired(boolean fastingRequired) {
            this.fastingRequired = fastingRequired;
            return this;
        }

        public Builder setRequiresContrast(boolean requiresContrast) {
            this.requiresContrast = requiresContrast;
            return this;
        }

        public Builder setNotes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder setDoctorSignature(String doctorSignature) {
            this.doctorSignature = doctorSignature;
            return this;
        }

        public Referral build() {
            if (patientName == null || patientName.isBlank()) {
                throw new IllegalStateException("Неможливо сформувати направлення: не вказано пацієнта.");
            }
            if (tests.isEmpty()) {
                throw new IllegalStateException("Неможливо сформувати направлення: не вказано жодного дослідження.");
            }
            if (doctorSignature == null || doctorSignature.isBlank()) {
                throw new IllegalStateException("Неможливо сформувати направлення: відсутній підпис лікаря.");
            }
            return new Referral(this);
        }
    }
}

//Керує послідовністю створення типових направлень
class ReferralDirector {

    public Referral createPreOperativeReferral(Referral.Builder builder, String patientName, String doctorSignature) {
        return builder
                .setPatient(patientName)
                .addTest("Загальний аналіз крові")
                .addTest("Коагулограма")
                .addTest("Група крові та резус-фактор")
                .addTest("ЕКГ")
                .setFastingRequired(true)
                .setNotes("Передопераційна діагностика перед плановим втручанням")
                .setDoctorSignature(doctorSignature)
                .build();
    }

    public Referral createRoutineCheckupReferral(Referral.Builder builder, String patientName, String doctorSignature) {
        return builder
                .setPatient(patientName)
                .addTest("Загальний аналіз крові")
                .addTest("Загальний аналіз сечі")
                .addTest("Флюорографія")
                .setFastingRequired(true)
                .setDoctorSignature(doctorSignature)
                .build();
    }
}
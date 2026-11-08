/**
 * TASK 4: DECORATOR. Flags on a patient card.
 * Flags are added at runtime in any combination; the card keeps the same interface.
 * Adding a new flag means adding one new class, with no changes to existing ones.
 */
public final class Task4Decorator {

    private Task4Decorator() {
    }

    public interface PatientCard {
        String patientName();

        void display();

        void assignProcedure(String procedure);
    }

    public static class BasicPatientCard implements PatientCard {
        private final String name;

        public BasicPatientCard(String name) {
            this.name = name;
        }

        @Override
        public String patientName() {
            return name;
        }

        @Override
        public void display() {
            Log.log("BasicPatientCard", "displaying card of " + name);
        }

        @Override
        public void assignProcedure(String procedure) {
            Log.log("BasicPatientCard", "procedure '" + procedure + "' assigned to " + name);
        }
    }

    /** Base decorator: same interface, delegates everything to the wrapped card. */
    public abstract static class CardFlag implements PatientCard {
        protected final PatientCard wrapped;

        protected CardFlag(PatientCard wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public String patientName() {
            return wrapped.patientName();
        }

        @Override
        public void display() {
            wrapped.display();
        }

        @Override
        public void assignProcedure(String procedure) {
            wrapped.assignProcedure(procedure);
        }
    }

    public static class AllergyFlag extends CardFlag {
        private final String allergen;

        public AllergyFlag(PatientCard wrapped, String allergen) {
            super(wrapped);
            this.allergen = allergen;
        }

        @Override
        public void display() {
            super.display();
            Log.log("AllergyFlag", "WARNING: allergy to " + allergen);
        }
    }

    public static class FallRiskFlag extends CardFlag {
        public FallRiskFlag(PatientCard wrapped) {
            super(wrapped);
        }

        @Override
        public void display() {
            super.display();
            Log.log("FallRiskFlag", "WARNING: fall risk, bed rails up, assist when walking");
        }
    }

    public static class InfectionControlFlag extends CardFlag {
        public InfectionControlFlag(PatientCard wrapped) {
            super(wrapped);
        }

        @Override
        public void display() {
            super.display();
            Log.log("InfectionControlFlag", "WARNING: infection control, isolation precautions");
        }

        @Override
        public void assignProcedure(String procedure) {
            Log.log("InfectionControlFlag", "REMINDER before '" + procedure
                    + "': use protective equipment (gloves, mask, gown)");
            super.assignProcedure(procedure);
        }
    }

    public static class VipFlag extends CardFlag {
        public VipFlag(PatientCard wrapped) {
            super(wrapped);
        }

        @Override
        public void display() {
            super.display();
            Log.log("VipFlag", "NOTE: VIP patient, restricted access to the card, private ward");
        }
    }
}

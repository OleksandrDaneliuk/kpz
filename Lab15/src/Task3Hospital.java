import java.util.ArrayList;
import java.util.List;

/** Завдання 3. Стан пацієнта у стаціонарі — шаблон STATE. */
public final class Task3Hospital {
    private Task3Hospital() { }

    // ---------- State ----------
    public interface PatientState {
        String title();

        void prescribe(Inpatient p, String medication);

        void visit(Inpatient p, String visitor);

        void moveToTreatment(Inpatient p);

        void moveToIcu(Inpatient p);

        void startDischarge(Inpatient p);

        void completeDischarge(Inpatient p);
    }

    // ---------- Context ----------
    public static final class Inpatient {
        private final String name;
        private PatientState state;
        private final List<String> prescriptions = new ArrayList<String>();

        public Inpatient(String name) {
            this.name = name;
            this.state = new ReceptionState();
            Log.event(source(), "Госпіталізовано, стан: " + state.title());
        }

        // Усі операції лише делегують поточному стану — жодних if за статусом.
        public void prescribe(String med) { state.prescribe(this, med); }

        public void visit(String visitor) { state.visit(this, visitor); }

        public void moveToTreatment() { state.moveToTreatment(this); }

        public void moveToIcu() { state.moveToIcu(this); }

        public void startDischarge() { state.startDischarge(this); }

        public void completeDischarge() { state.completeDischarge(this); }

        public void setState(PatientState next) {
            Log.event(source(), "ПЕРЕХІД: «" + state.title() + "» -> «" + next.title() + "»");
            this.state = next;
        }

        public void addPrescription(String med) { prescriptions.add(med); }

        public String getName() { return name; }

        public String source() { return "Пацієнт " + name; }
    }

    // ---------- Базовий стан: усе заборонено за замовчуванням ----------
    public abstract static class AbstractState implements PatientState {
        protected void refuse(Inpatient p, String operation, String reason) {
            Log.event(p.source(), "ВІДМОВА: «" + operation + "» у стані «" + title() + "» — " + reason);
        }

        public void prescribe(Inpatient p, String med) {
            refuse(p, "призначення " + med, "призначення можливі лише на лікуванні та в реанімації");
        }

        public void visit(Inpatient p, String visitor) {
            refuse(p, "відвідування (" + visitor + ")", "відвідування в цьому стані недоступні");
        }

        public void moveToTreatment(Inpatient p) {
            refuse(p, "переведення на лікування", "перехід не передбачений");
        }

        public void moveToIcu(Inpatient p) {
            refuse(p, "переведення в реанімацію", "у реанімацію переводять лише з лікування");
        }

        public void startDischarge(Inpatient p) {
            refuse(p, "початок виписки", "виписка можлива лише зі стану «На лікуванні»");
        }

        public void completeDischarge(Inpatient p) {
            refuse(p, "завершення виписки", "спочатку має бути стан «На виписці»");
        }

        protected void allowVisit(Inpatient p, String visitor) {
            Log.event(p.source(), "Відвідування дозволено: " + visitor + " (стан «" + title() + "»)");
        }

        protected void allowPrescribe(Inpatient p, String med) {
            p.addPrescription(med);
            Log.event(p.source(), "Призначено «" + med + "» (стан «" + title() + "»)");
        }
    }

    public static final class ReceptionState extends AbstractState {
        public String title() { return "Приймальне відділення"; }

        @Override public void visit(Inpatient p, String v) { allowVisit(p, v); }

        @Override public void moveToTreatment(Inpatient p) { p.setState(new TreatmentState()); }
    }

    public static final class TreatmentState extends AbstractState {
        public String title() { return "На лікуванні"; }

        @Override public void prescribe(Inpatient p, String med) { allowPrescribe(p, med); }

        @Override public void visit(Inpatient p, String v) { allowVisit(p, v); }

        @Override public void moveToIcu(Inpatient p) { p.setState(new IntensiveCareState()); }

        @Override public void startDischarge(Inpatient p) { p.setState(new DischargingState()); }
    }

    public static final class IntensiveCareState extends AbstractState {
        public String title() { return "У реанімації"; }

        @Override public void prescribe(Inpatient p, String med) { allowPrescribe(p, med); }

        @Override public void visit(Inpatient p, String v) {
            refuse(p, "відвідування (" + v + ")", "відвідування в реанімації заборонені");
        }

        @Override public void moveToTreatment(Inpatient p) { p.setState(new TreatmentState()); }

        @Override public void startDischarge(Inpatient p) {
            refuse(p, "початок виписки",
                    "виписка можлива лише зі стану «На лікуванні»; спочатку переведіть пацієнта з реанімації");
        }
    }

    public static final class DischargingState extends AbstractState {
        public String title() { return "На виписці"; }

        @Override public void visit(Inpatient p, String v) { allowVisit(p, v); }

        @Override public void completeDischarge(Inpatient p) { p.setState(new DischargedState()); }
    }

    public static final class DischargedState extends AbstractState {
        public String title() { return "Виписаний"; }

        @Override public void prescribe(Inpatient p, String med) { refuse(p, "призначення " + med, "пацієнта вже виписано"); }

        @Override public void visit(Inpatient p, String v) { refuse(p, "відвідування", "пацієнта вже виписано"); }

        @Override public void moveToTreatment(Inpatient p) { refuse(p, "переведення на лікування", "пацієнта вже виписано"); }

        @Override public void moveToIcu(Inpatient p) { refuse(p, "переведення в реанімацію", "пацієнта вже виписано"); }

        @Override public void startDischarge(Inpatient p) { refuse(p, "початок виписки", "пацієнта вже виписано"); }

        @Override public void completeDischarge(Inpatient p) { refuse(p, "завершення виписки", "пацієнта вже виписано"); }
    }

    /** НОВИЙ стан: клас Inpatient не змінюється. */
    public static final class QuarantineState extends AbstractState {
        public String title() { return "Карантин"; }

        @Override public void prescribe(Inpatient p, String med) { allowPrescribe(p, med); }

        @Override public void visit(Inpatient p, String v) {
            refuse(p, "відвідування (" + v + ")", "відвідування на карантині заборонені");
        }

        @Override public void moveToTreatment(Inpatient p) { p.setState(new TreatmentState()); }
    }

    // ---------- Демонстраційний сценарій ----------
    public static void demo() {
        Log.event("Сценарій", "1) Повний шлях: приймальне -> лікування -> виписка");
        Inpatient ivan = new Inpatient("Іван");
        ivan.prescribe("антибіотик");      // ще рано — відмова
        ivan.moveToTreatment();
        ivan.prescribe("антибіотик");
        ivan.visit("дружина");
        ivan.startDischarge();
        ivan.completeDischarge();

        Log.event("Сценарій", "2) Реанімація: відвідування -> відмова; виписка -> відмова");
        Inpatient olena = new Inpatient("Олена");
        olena.moveToTreatment();
        olena.moveToIcu();
        olena.visit("син");
        olena.startDischarge();
        olena.prescribe("киснева підтримка"); // у реанімації дозволено

        Log.event("Сценарій", "3) Новий стан «Карантин» без змін класу Inpatient");
        Inpatient petro = new Inpatient("Петро");
        petro.setState(new QuarantineState());
        petro.visit("брат");
        petro.moveToTreatment();
    }
}

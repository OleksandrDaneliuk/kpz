import java.util.ArrayList;
import java.util.List;

class TreatmentPlan implements Prototype<TreatmentPlan> {
    private String planType;
    private List<String> tests;
    private List<String> procedures;
    private String diet;
    private String regime;
    private String patientName;

    public TreatmentPlan(String planType, List<String> tests, List<String> procedures, String diet, String regime) {
        this.planType = planType;
        Logger.log("TreatmentPlan", "«Дорога» операція: Формування базового медичного протоколу '" + planType + "'...");
        simulateDelay(1500);
        this.tests = new ArrayList<>(tests);
        this.procedures = new ArrayList<>(procedures);
        this.diet = diet;
        this.regime = regime;
        this.patientName = "Шаблон (без призначення)";
    }

    public TreatmentPlan() {

    }


    public void setPatientName(String patientName) { this.patientName = patientName; }
    public void addTest(String test) { this.tests.add(test); }
    public void addProcedure(String procedure) { this.procedures.add(procedure); }
    public void setDiet(String diet) { this.diet = diet; }

    @Override
    public TreatmentPlan clone() {
        TreatmentPlan cloned = new TreatmentPlan();
        cloned.planType = this.planType;
        cloned.tests = new ArrayList<>(this.tests);
        cloned.procedures = new ArrayList<>(this.procedures);
        cloned.diet = this.diet;
        cloned.regime = this.regime;
        cloned.patientName = this.patientName;
        return cloned;
    }

    public void printInfo() {
        Logger.log("TreatmentPlan", String.format("План [%s] для пацієнта: %s | Аналізи: %s | Процедури: %s | Дієта: %s | Режим: %s",
                planType, patientName, tests, procedures, diet, regime));
    }

    private static void simulateDelay(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
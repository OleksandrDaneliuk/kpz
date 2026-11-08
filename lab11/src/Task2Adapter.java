import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class Task2Adapter {

    private Task2Adapter() {
    }


    public record LabResult(String patientId, String indicator, double valueMmolPerL) {
        @Override
        public String toString() {
            return String.format(Locale.ROOT, "%s: %s = %.3f mmol/L", patientId, indicator, valueMmolPerL);
        }
    }


    public interface LabAnalyzer {
        String name();

        List<LabResult> fetchResults();
    }


    public static class VendorADriver {
        public String readResultString() {
            String raw = "PID=P-001;GLU=5400;CREA=88;UREA=4300";
            Log.log("VendorADriver", "returns raw string: " + raw);
            return raw;
        }
    }


    public static class VendorBDevice {
        public String dump() {
            String raw = "P-002|G:7200,U:5100,K:4200";
            Log.log("VendorBDevice", "returns raw string: " + raw);
            return raw;
        }
    }

    public static class VendorAAdapter implements LabAnalyzer {
        private static final Map<String, String> CODES =
                Map.of("GLU", "Glucose", "CREA", "Creatinine", "UREA", "Urea");
        private final VendorADriver driver;

        public VendorAAdapter(VendorADriver driver) {
            this.driver = driver;
        }

        @Override
        public String name() {
            return "VendorA analyzer";
        }

        @Override
        public List<LabResult> fetchResults() {
            Log.log("VendorAAdapter", "asking the adapted VendorADriver for data and converting it");
            String raw = driver.readResultString();
            String patientId = null;
            List<LabResult> results = new ArrayList<>();
            for (String part : raw.split(";")) {
                String[] kv = part.split("=");
                if (kv[0].equals("PID")) {
                    patientId = kv[1];
                } else {
                    results.add(new LabResult(patientId, CODES.get(kv[0]),
                            Double.parseDouble(kv[1]) / 1000.0));
                }
            }
            Log.log("VendorAAdapter", "converted " + results.size() + " indicators (umol/L -> mmol/L)");
            return results;
        }
    }

    public static class VendorBAdapter implements LabAnalyzer {
        private static final Map<String, String> CODES =
                Map.of("G", "Glucose", "U", "Urea", "K", "Potassium");
        private final VendorBDevice device;

        public VendorBAdapter(VendorBDevice device) {
            this.device = device;
        }

        @Override
        public String name() {
            return "VendorB analyzer";
        }

        @Override
        public List<LabResult> fetchResults() {
            Log.log("VendorBAdapter", "asking the adapted VendorBDevice for data and converting it");
            String raw = device.dump();
            String[] halves = raw.split("\\|");
            String patientId = halves[0];
            List<LabResult> results = new ArrayList<>();
            for (String pair : halves[1].split(",")) {
                String[] kv = pair.split(":");
                results.add(new LabResult(patientId, CODES.get(kv[0]),
                        Double.parseDouble(kv[1]) / 1000.0));
            }
            Log.log("VendorBAdapter", "converted " + results.size() + " indicators (umol/L -> mmol/L)");
            return results;
        }
    }

    /** Hospital system: depends only on the LabAnalyzer interface. */
    public static class HospitalLabSystem {
        private final Map<String, List<LabResult>> patientCards = new LinkedHashMap<>();

        public void importFrom(LabAnalyzer analyzer) {
            Log.log("HospitalLabSystem",
                    "requesting results from " + analyzer.name() + " via LabAnalyzer interface");
            for (LabResult r : analyzer.fetchResults()) {
                patientCards.computeIfAbsent(r.patientId(), k -> new ArrayList<>()).add(r);
                Log.log("HospitalLabSystem", "wrote to patient card -> " + r);
            }
        }

        public List<LabResult> cardOf(String patientId) {
            return patientCards.getOrDefault(patientId, List.of());
        }
    }
}
 
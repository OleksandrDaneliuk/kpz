import java.util.List;

/**
 * Runner: one method per situation (demo scenarios), then tests.
 * Tests re-run the same scenarios with console echo off and inspect the recorded log.
 * No external libraries: a tiny check() helper replaces JUnit.
 */
public class Main {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("##### DEMO #####");
        situation1Proxy();
        situation2Adapter();
        situation3Facade();

        System.out.println();
        System.out.println("##### TESTS #####");
        testSituation1Proxy();
        testSituation2Adapter();
        testSituation3Facade();
    }

    static void header(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }

    static void situation1Proxy() {
        header("Situation 1: Medical images (Proxy)");
        Task1Proxy.RemoteImageStorage storage = new Task1Proxy.RemoteImageStorage();
        Task1Proxy.PatientFile file = new Task1Proxy.PatientFile("Petrenko I.");
        file.addImage(new Task1Proxy.MedicalImageProxy("CT-1", "CT", storage));
        file.addImage(new Task1Proxy.MedicalImageProxy("CT-2", "CT", storage));
        file.addImage(new Task1Proxy.MedicalImageProxy("MRI-1", "MRI", storage));
        file.addImage(new Task1Proxy.MedicalImageProxy("MRI-2", "MRI", storage));

        Task1Proxy.Employee doctor = new Task1Proxy.Employee("Dr. Shevchenko", Task1Proxy.Role.DOCTOR);
        Task1Proxy.Employee receptionist =
                new Task1Proxy.Employee("Kovalenko", Task1Proxy.Role.RECEPTIONIST);

        file.openFile();                 // no downloads
        file.openImage(3, doctor);       // first open: one download
        file.openImage(3, doctor);       // second open: no download
        file.openImage(3, receptionist); // denied
    }

    static void situation2Adapter() {
        header("Situation 2: Lab analyzer (Adapter)");
        Task2Adapter.HospitalLabSystem hospital = new Task2Adapter.HospitalLabSystem();
        hospital.importFrom(new Task2Adapter.VendorAAdapter(new Task2Adapter.VendorADriver()));
        // second analyzer, different format: the hospital system is unchanged
        hospital.importFrom(new Task2Adapter.VendorBAdapter(new Task2Adapter.VendorBDevice()));
    }

    static void situation3Facade() {
        header("Situation 3: Hospitalization (Facade)");
        Task3Facade.BedRegistry beds = new Task3Facade.BedRegistry();
        Task3Facade.HospitalizationFacade facade = new Task3Facade.HospitalizationFacade(
                new Task3Facade.PatientRegistration(), beds, new Task3Facade.DoctorAssignment(),
                new Task3Facade.ExaminationPlanner(), new Task3Facade.KitchenNotifier());

        Task3Facade.AdmissionsDepartment admissions = new Task3Facade.AdmissionsDepartment(facade);
        Task3Facade.PlannedHospitalizationModule planned =
                new Task3Facade.PlannedHospitalizationModule(facade);

        admissions.admitEmergency("Bondar O.", "Ward 5");
        planned.admitPlanned("Melnyk T.", "Ward 7");

        Log.log("Runner", "transferring Bondar O. directly through BedRegistry (no facade)");
        beds.transfer("Bondar O.", "Ward 9");
    }


    // Tests

    static void testSituation1Proxy() {
        System.out.println("-- Task 1: Proxy");
        List<String> log = capture(Main::situation1Proxy);

        int opened = indexOf(log, "opened, 4 images attached (none loaded)");
        int firstRequest = indexOf(log, "requests image MRI-2");
        int firstDownload = indexOf(log, "[RemoteImageStorage]");

        check(opened >= 0, "card opens with 4 images attached");
        check(firstDownload > firstRequest && firstRequest > opened,
                "no download when the card opens, only after the first image open");
        check(count(log, "[RemoteImageStorage]") == 1, "image is downloaded exactly once");
        check(count(log, "already loaded") == 1, "second open reuses the loaded image");
        check(count(log, "displaying MRI image") == 2, "doctor sees the image on both opens");
        check(count(log, "ACCESS DENIED") == 1
                        && count(log, "Only doctors have access to medical images") == 1,
                "receptionist gets a clear denial message");
        check(count(log, "[RealMedicalImage#MRI-2] displaying MRI image (250 MB) to Kovalenko") == 0,
                "receptionist never reaches the real image");
        check(count(log, "[MedicalImageProxy#") >= 3 && count(log, "[RealMedicalImage#") >= 3,
                "log shows both the proxy and the real object acting");
    }

    static void testSituation2Adapter() {
        System.out.println("-- Task 2: Adapter");
        List<String> log = capture(Main::situation2Adapter);

        check(count(log, "[VendorADriver] returns raw string: PID=P-001;GLU=5400;CREA=88;UREA=4300") == 1,
                "raw vendor A string is logged");
        check(count(log, "[VendorBDevice] returns raw string: P-002|G:7200,U:5100,K:4200") == 1,
                "raw vendor B string is logged");
        check(count(log, "P-001: Glucose = 5.400 mmol/L") == 1, "vendor A glucose converted to mmol/L");
        check(count(log, "P-001: Creatinine = 0.088 mmol/L") == 1, "vendor A creatinine converted");
        check(count(log, "P-002: Potassium = 4.200 mmol/L") == 1, "vendor B potassium converted");
        check(count(log, "[HospitalLabSystem] wrote to patient card") == 6,
                "hospital system wrote all 6 results to patient cards");

        // direct (non-log) checks on the adapter output
        List<Task2Adapter.LabResult> a =
                new Task2Adapter.VendorAAdapter(new Task2Adapter.VendorADriver()).fetchResults();
        check(a.size() == 3 && a.get(0).patientId().equals("P-001")
                        && Math.abs(a.get(0).valueMmolPerL() - 5.4) < 1e-9,
                "adapter A returns hospital LabResult objects (5.4 mmol/L)");

        // a third, unknown analyzer plugs in without any change to HospitalLabSystem
        Task2Adapter.LabAnalyzer third = new Task2Adapter.LabAnalyzer() {
            @Override
            public String name() {
                return "VendorC analyzer";
            }

            @Override
            public List<Task2Adapter.LabResult> fetchResults() {
                return List.of(new Task2Adapter.LabResult("P-003", "Sodium", 140.0));
            }
        };
        Task2Adapter.HospitalLabSystem hospital = new Task2Adapter.HospitalLabSystem();
        Log.setEcho(false);
        hospital.importFrom(third);
        Log.setEcho(true);
        check(hospital.cardOf("P-003").size() == 1, "new analyzer connects without changing the hospital system");
    }

    static void testSituation3Facade() {
        System.out.println("-- Task 3: Facade");
        List<String> log = capture(Main::situation3Facade);

        check(count(log, "[HospitalizationFacade] hospitalize(") == 2,
                "two hospitalizations went through the same facade operation");
        check(count(log, "[PatientRegistration]") == 2, "registration called for both patients");
        check(count(log, "[DoctorAssignment]") == 2, "doctor assigned for both patients");
        check(count(log, "[ExaminationPlanner]") == 2, "examination plan built for both patients");
        check(count(log, "[KitchenNotifier]") == 2, "kitchen notified for both patients");
        check(indexOf(log, "diet meals for Melnyk T.") >= 0, "kitchen is notified about the planned patient");
        check(indexOf(log, "built PLANNED examination plan for Melnyk T.") >= 0,
                "planned patient gets a PLANNED examination plan");

        int reg = indexOf(log, "[PatientRegistration]");
        int bed = indexOf(log, "[BedRegistry]");
        int doc = indexOf(log, "[DoctorAssignment]");
        int plan = indexOf(log, "[ExaminationPlanner]");
        int kitchen = indexOf(log, "[KitchenNotifier]");
        check(reg < bed && bed < doc && doc < plan && plan < kitchen,
                "subsystems are called in the defined order");

        check(indexOf(log, "transferred Bondar O. to Ward 9") >= 0, "patient is transferred to another ward");
        check(count(log, "[PatientRegistration]") == 2 && count(log, "[HospitalizationFacade] hospitalize(") == 2,
                "transfer does not trigger a new hospitalization");
    }


    static List<String> capture(Runnable scenario) {
        Log.reset();
        Log.setEcho(false);
        try {
            scenario.run();
            return Log.lines();
        } finally {
            Log.setEcho(true);
        }
    }

    static int indexOf(List<String> lines, String part) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains(part)) {
                return i;
            }
        }
        return -1;
    }

    static int count(List<String> lines, String part) {
        int n = 0;
        for (String line : lines) {
            if (line.contains(part)) {
                n++;
            }
        }
        return n;
    }

    static void check(boolean condition, String name) {
        if (condition) {
            passed++;
            System.out.println("  PASS " + name);
        } else {
            failed++;
            System.out.println("  FAIL " + name);
        }
    }
}

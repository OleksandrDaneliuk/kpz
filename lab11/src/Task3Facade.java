public final class Task3Facade {

    private Task3Facade() {
    }

    public enum AdmissionType { EMERGENCY, PLANNED }

    public static class PatientRegistration {
        public void register(String patient) {
            Log.log("PatientRegistration", "registered patient " + patient);
        }
    }

    public static class BedRegistry {
        public void reserveBed(String patient, String ward) {
            Log.log("BedRegistry", "reserved a bed in " + ward + " for " + patient);
        }

        public void transfer(String patient, String newWard) {
            Log.log("BedRegistry", "transferred " + patient + " to " + newWard);
        }
    }

    public static class DoctorAssignment {
        public void assign(String patient) {
            Log.log("DoctorAssignment", "assigned attending doctor to " + patient);
        }
    }

    public static class ExaminationPlanner {
        public void plan(String patient, AdmissionType type) {
            Log.log("ExaminationPlanner", "built " + type + " examination plan for " + patient);
        }
    }

    public static class KitchenNotifier {
        public void notifyKitchen(String patient, String ward) {
            Log.log("KitchenNotifier", "kitchen notified: diet meals for " + patient + " in " + ward);
        }
    }

    public static class HospitalizationFacade {
        private final PatientRegistration registration;
        private final BedRegistry beds;
        private final DoctorAssignment doctors;
        private final ExaminationPlanner planner;
        private final KitchenNotifier kitchen;

        public HospitalizationFacade(PatientRegistration registration, BedRegistry beds,
                                     DoctorAssignment doctors, ExaminationPlanner planner,
                                     KitchenNotifier kitchen) {
            this.registration = registration;
            this.beds = beds;
            this.doctors = doctors;
            this.planner = planner;
            this.kitchen = kitchen;
        }

        public void hospitalize(String patient, String ward, AdmissionType type) {
            Log.log("HospitalizationFacade",
                    "hospitalize(" + patient + ", " + ward + ", " + type + ") started");
            registration.register(patient);
            beds.reserveBed(patient, ward);
            doctors.assign(patient);
            planner.plan(patient, type);
            kitchen.notifyKitchen(patient, ward); // always called, for every admission type
            Log.log("HospitalizationFacade", "hospitalization of " + patient + " completed");
        }
    }

    /** Client: holds only the facade, never the subsystems. */
    public static class AdmissionsDepartment {
        private final HospitalizationFacade facade;

        public AdmissionsDepartment(HospitalizationFacade facade) {
            this.facade = facade;
        }

        public void admitEmergency(String patient, String ward) {
            Log.log("AdmissionsDepartment", "emergency patient arrived: " + patient);
            facade.hospitalize(patient, ward, AdmissionType.EMERGENCY);
        }
    }

    public static class PlannedHospitalizationModule {
        private final HospitalizationFacade facade;

        public PlannedHospitalizationModule(HospitalizationFacade facade) {
            this.facade = facade;
        }

        public void admitPlanned(String patient, String ward) {
            Log.log("PlannedHospitalizationModule", "scheduled patient arrived: " + patient);
            facade.hospitalize(patient, ward, AdmissionType.PLANNED);
        }
    }
}

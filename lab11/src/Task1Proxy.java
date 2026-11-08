import java.util.ArrayList;
import java.util.List;

public final class Task1Proxy {

    private Task1Proxy() {
    }

    public enum Role { DOCTOR, NURSE, RECEPTIONIST }

    public record Employee(String name, Role role) {
        @Override
        public String toString() {
            return name + " (" + role + ")";
        }
    }

    public interface MedicalImage {
        String id();

        void open(Employee viewer);
    }

    public static class RemoteImageStorage {
        public int download(String imageId) {
            int sizeMb = 256;
            Log.log("RemoteImageStorage", "downloading " + imageId + " (" + sizeMb + " MB)");
            return sizeMb;
        }
    }

    public static class RealMedicalImage implements MedicalImage {
        private final String id;
        private final String type;
        private final int sizeMb;

        public RealMedicalImage(String id, String type, RemoteImageStorage storage) {
            this.id = id;
            this.type = type;
            Log.log("RealMedicalImage#" + id, "loading data from remote storage");
            this.sizeMb = storage.download(id);
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public void open(Employee viewer) {
            Log.log("RealMedicalImage#" + id,
                    "displaying " + type + " image (" + sizeMb + " MB) to " + viewer);
        }
    }

    public static class MedicalImageProxy implements MedicalImage {
        private final String id;
        private final String type;
        private final RemoteImageStorage storage;
        private RealMedicalImage real; // created on the first permitted open

        public MedicalImageProxy(String id, String type, RemoteImageStorage storage) {
            this.id = id;
            this.type = type;
            this.storage = storage;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public void open(Employee viewer) {
            String src = "MedicalImageProxy#" + id;
            if (viewer.role() != Role.DOCTOR) {
                Log.log(src, "ACCESS DENIED for " + viewer + ": Only doctors have access to medical images");
                return; // the real image is never created or loaded
            }
            if (real == null) {
                Log.log(src, "first open by " + viewer + ": creating the real image");
                real = new RealMedicalImage(id, type, storage);
            } else {
                Log.log(src, "already loaded, reusing the real image (no download)");
            }
            real.open(viewer);
        }
    }

    /** Patient file: works only with the MedicalImage interface. */
    public static class PatientFile {
        private final String patient;
        private final List<MedicalImage> images = new ArrayList<>();

        public PatientFile(String patient) {
            this.patient = patient;
        }

        public void addImage(MedicalImage image) {
            images.add(image);
        }

        public void openFile() {
            Log.log("PatientFile(" + patient + ")",
                    "opened, " + images.size() + " images attached (none loaded)");
        }

        public void openImage(int index, Employee viewer) {
            MedicalImage image = images.get(index);
            Log.log("PatientFile(" + patient + ")", viewer + " requests image " + image.id());
            image.open(viewer);
        }
    }
}

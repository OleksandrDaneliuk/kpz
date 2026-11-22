public class Main {
    public static void main(String[] args) {
        PatientMonitoringTask.run();
        System.out.println("\n" + "=".repeat(80) + "\n");
        OperatingRoomTask.run();
    }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("\n>>> ЗАВДАННЯ 1: Виписка пацієнтів (Factory Method) <<<");

        Department therapy = new TherapyDepartment();
        Department surgery = new SurgeryDepartment();
        Department cardiology = new CardiologyDepartment();

        // Виписка однією процедурою з різних відділень
        therapy.dischargePatient("Іваненко А.В.", "д-р Петренко");
        surgery.dischargePatient("Сидоренко О.М.", "д-р Коваль");
        cardiology.dischargePatient("Мельник Г.П.", "д-р Бондар");

        // Відкриття нового (педіатричного) відділення без зміни процедури виписки
        Department pediatrics = new PediatricDepartment();
        pediatrics.dischargePatient("Малюк Шевченко", "д-р Василенко");

        // Вивід журналу виписаних документів
        System.out.println("\nЖурнал виписаних документів:");
        System.out.println("- " + therapy.getArchive().get(0));
        System.out.println("- " + surgery.getArchive().get(0));
        System.out.println("- " + cardiology.getArchive().get(0));
        System.out.println("- " + pediatrics.getArchive().get(0));
    }
}
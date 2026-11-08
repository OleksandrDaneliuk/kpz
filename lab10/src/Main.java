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


        //BUILDER
        System.out.println("ЗАВДАННЯ 2: Направлення на обстеження (Builder)");


        Referral customReferral = new Referral.Builder()
                .setPatient("Коваленко С.П.")
                .addTest("Загальний аналіз крові")
                .addTest("УЗД черевної порожнини")
                .setUrgent(true)
                .setDoctorSignature("д-р Гриценко")
                .build();

        System.out.println(customReferral);

        //Передопераційне направлення
        ReferralDirector director = new ReferralDirector();
        Referral preOpReferral = director.createPreOperativeReferral(new Referral.Builder(), "Бойко В.В.", "д-р Сергієнко");
        System.out.println("\n" + preOpReferral);

        //Спроба сформувати направлення без жодного аналізу
        System.out.println("\nСпроба створення некоректного направлення:");
        try {
            new Referral.Builder()
                    .setPatient("Безкоровайний Т.Г.")
                    .setDoctorSignature("д-р Франко")
                    .build();
        } catch (IllegalStateException e) {
            System.out.println("У журналі помилок: \"" + e.getMessage() + "\"");
        }


        System.out.println("ЗАВДАННЯ 3: Abstract Factory");

        PatientRoom room101 = new PatientRoom(101);
        PatientRoom room102 = new PatientRoom(102);
        PatientRoom room103 = new PatientRoom(103);

        room101.prepareKit(new AdultMedicalKitFactory());
        room102.prepareKit(new PediatricMedicalKitFactory());
        room103.prepareKit(new NewbornMedicalKitFactory());
    }

}
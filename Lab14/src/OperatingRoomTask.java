public class OperatingRoomTask {

    // Mediator interface
    public interface SurgicalMediator {
        void notify(String time, SurgicalParticipant sender, String event);
    }

    // Abstract component
    public static abstract class SurgicalParticipant {
        protected final String roleName;
        protected SurgicalMediator mediator;

        protected SurgicalParticipant(String roleName) {
            this.roleName = roleName;
        }

        public void setMediator(SurgicalMediator mediator) {
            this.mediator = mediator;
        }

        public String getRoleName() {
            return roleName;
        }
    }

    // Component 1
    public static class ScrubNurse extends SurgicalParticipant {
        public ScrubNurse(String name) {
            super("Операційна медсестра (" + name + ")");
        }

        public void confirmInstrumentsReady(String time) {
            System.out.printf("[%s] [%s] Стерильні інструменти підготовлено та перевірено.%n", time, roleName);
            mediator.notify(time, this, "INSTRUMENTS_READY");
        }
    }

    // Component 2
    public static class Anesthesiologist extends SurgicalParticipant {
        public Anesthesiologist(String name) {
            super("Анестезіолог (" + name + ")");
        }

        public void startAnesthesia(String time) {
            System.out.printf("[%s] [%s] Інструменти готові -> Розпочинаю введення пацієнта в наркоз.%n", time, roleName);
            mediator.notify(time, this, "ANESTHESIA_STARTED");
        }
    }

    // Component 3
    public static class Surgeon extends SurgicalParticipant {
        public Surgeon(String name) {
            super("Хірург (" + name + ")");
        }

        public void requestBlood(String time, String bloodType) {
            System.out.printf("[%s] [%s] Потрібна резервна кров (%s) для операції.%n", time, roleName, bloodType);
            mediator.notify(time, this, "BLOOD_REQUESTED:" + bloodType);
        }

        public void finishSurgery(String time) {
            System.out.printf("[%s] [%s] Операцію успішно завершено, накладено шви.%n", time, roleName);
            mediator.notify(time, this, "SURGERY_COMPLETED");
        }
    }

    // Component 4
    public static class BloodBankLab extends SurgicalParticipant {
        public BloodBankLab() {
            super("Лабораторія крові");
        }

        public void prepareBlood(String time, String bloodType) {
            System.out.printf("[%s] [%s] Отримано запит -> Підібрано та доставлено кров групи %s.%n",
                    time, roleName, bloodType);
            mediator.notify(time, this, "BLOOD_READY");
        }
    }

    // Component 5
    public static class IntensiveCareUnit extends SurgicalParticipant {
        public IntensiveCareUnit() {
            super("Відділення реанімації");
        }

        public void preparePostOpBed(String time) {
            System.out.printf("[%s] [%s] Отримано сигнал про завершення -> Підготовлено ліжко та апарат ШВЛ для пацієнта.%n",
                    time, roleName);
        }
    }

    // Concrete Mediator
    public static class OperatingRoomCoordinator implements SurgicalMediator {
        private Anesthesiologist anesthesiologist;
        private BloodBankLab bloodBankLab;
        private IntensiveCareUnit icu;

        private boolean instrumentsReady = false;
        private boolean anesthesiaStarted = false;

        public void registerAnesthesiologist(Anesthesiologist anesthesiologist) {
            this.anesthesiologist = anesthesiologist;
            anesthesiologist.setMediator(this);
        }

        public void registerBloodBankLab(BloodBankLab bloodBankLab) {
            this.bloodBankLab = bloodBankLab;
            bloodBankLab.setMediator(this);
        }

        public void registerIcu(IntensiveCareUnit icu) {
            this.icu = icu;
            icu.setMediator(this);
        }

        public void registerParticipant(SurgicalParticipant participant) {
            participant.setMediator(this);
        }

        @Override
        public void notify(String time, SurgicalParticipant sender, String event) {
            if (event.equals("INSTRUMENTS_READY")) {
                instrumentsReady = true;
                System.out.printf("[%s] [Координатор оперблоку] Підтверджено готовність інструментів. Дозволяю наркоз.%n", time);
                if (anesthesiologist != null) {
                    anesthesiologist.startAnesthesia(time);
                }
            } else if (event.equals("ANESTHESIA_STARTED")) {
                anesthesiaStarted = true;
                System.out.printf("[%s] [Координатор оперблоку] Пацієнт у наркозі. Хірург може починати роботу.%n", time);
            } else if (event.startsWith("BLOOD_REQUESTED:")) {
                String bloodType = event.split(":")[1];
                System.out.printf("[%s] [Координатор оперблоку] Передаю запит на кров (%s) до лабораторії.%n", time, bloodType);
                if (bloodBankLab != null) {
                    bloodBankLab.prepareBlood(time, bloodType);
                }
            } else if (event.equals("BLOOD_READY")) {
                System.out.printf("[%s] [Координатор оперблоку] Кров в операційній, хірургічна бригада поінформована.%n", time);
            } else if (event.equals("SURGERY_COMPLETED")) {
                if (!instrumentsReady || !anesthesiaStarted) {
                    System.out.printf("[%s] [Координатор оперблоку] ПОМИЛКА ПРОТОКОЛУ: завершення без підготовки!%n", time);
                    return;
                }
                System.out.printf("[%s] [Координатор оперблоку] Операція завершена. Сповіщаю реанімацію про прийом пацієнта.%n", time);
                if (icu != null) {
                    icu.preparePostOpBed(time);
                }
            }
        }
    }

    // DEMO
    public static void run() {
        System.out.println("=== ЗАВДАННЯ 2: ОПЕРАЦІЙНИЙ БЛОК (MEDIATOR) ===");

        OperatingRoomCoordinator coordinator = new OperatingRoomCoordinator();

        ScrubNurse nurse = new ScrubNurse("Сидоренко М.");
        Anesthesiologist anesthesiologist = new Anesthesiologist("Бондаренко В.");
        Surgeon surgeon = new Surgeon("Д-р Петренко А.");
        BloodBankLab bloodLab = new BloodBankLab();
        IntensiveCareUnit icu = new IntensiveCareUnit();

        coordinator.registerParticipant(nurse);
        coordinator.registerParticipant(surgeon);
        coordinator.registerAnesthesiologist(anesthesiologist);
        coordinator.registerBloodBankLab(bloodLab);
        coordinator.registerIcu(icu);

        // 1. Медсестра підтверджує готовність -> координатор дозволяє наркоз анестезіологу
        nurse.confirmInstrumentsReady("10:00");

        // 2. Хірург запитує кров -> координатор передає запит лабораторії
        surgeon.requestBlood("10:25", "A(II) Rh+");

        // 3. Хірург завершує операцію -> координатор сповіщає реанімацію
        surgeon.finishSurgery("12:10");
    }
}
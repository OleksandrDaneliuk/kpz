import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HospitalStructureTask {

    private static void log(String event) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.printf("[%s] [Composite ] %s%n", time, event);
    }

    // Спільний інтерфейс (Component)
    public interface HospitalComponent {
        String getName();
        int getTotalBeds();
        int getFreeBeds();
        void printStructure(String indent);
    }

    // Лист (Leaf) — Ліжко
    public static class Bed implements HospitalComponent {
        private final String id;
        private final boolean isFree;

        public Bed(String id, boolean isFree) {
            this.id = id;
            this.isFree = isFree;
        }

        @Override
        public String getName() {
            return "Ліжко " + id;
        }

        @Override
        public int getTotalBeds() {
            return 1;
        }

        @Override
        public int getFreeBeds() {
            return isFree ? 1 : 0;
        }

        @Override
        public void printStructure(String indent) {
            System.out.printf("%s- Ліжко %s [%s]%n", indent, id, isFree ? "ВІЛЬНЕ" : "ЗАЙНЯТЕ");
        }
    }

    // Контейнер (Composite) — Підрозділи будь-якого рівня
    public static class HospitalUnit implements HospitalComponent {
        private final String name;
        private final String unitType;
        private final List<HospitalComponent> children = new ArrayList<>();

        public HospitalUnit(String name, String unitType) {
            this.name = name;
            this.unitType = unitType;
        }

        public void add(HospitalComponent component) {
            children.add(component);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getTotalBeds() {
            return children.stream().mapToInt(HospitalComponent::getTotalBeds).sum();
        }

        @Override
        public int getFreeBeds() {
            return children.stream().mapToInt(HospitalComponent::getFreeBeds).sum();
        }

        @Override
        public void printStructure(String indent) {
            int occupied = getTotalBeds() - getFreeBeds();
            System.out.printf("%s+ %s \"%s\" (Вільних: %d, Зайнятих: %d, Всього: %d)%n",
                    indent, unitType, name, getFreeBeds(), occupied, getTotalBeds());
            for (HospitalComponent child : children) {
                child.printStructure(indent + "  ");
            }
        }
    }

    public static void run() {
        HospitalUnit hospital = new HospitalUnit("Обласна лікарня №1", "Лікарня");
        HospitalUnit buildingA = new HospitalUnit("Терапевтичний корпус", "Корпус");
        HospitalUnit buildingB = new HospitalUnit("Хірургічний корпус", "Корпус");

        HospitalUnit cardiology = new HospitalUnit("Кардіологія", "Відділення");
        HospitalUnit surgery = new HospitalUnit("Загальна хірургія", "Відділення");
        HospitalUnit icuBlock = new HospitalUnit("Блок інтенсивної терапії", "Спеціалізований Блок");

        // Палати та ліжка
        HospitalUnit ward101 = new HospitalUnit("Палата 101", "Палата");
        ward101.add(new Bed("101-A", false));
        ward101.add(new Bed("101-B", true));
        cardiology.add(ward101);

        HospitalUnit icuWard1 = new HospitalUnit("Палата ІТ-1", "Палата");
        icuWard1.add(new Bed("IT-1", true));
        icuWard1.add(new Bed("IT-2", true));
        icuBlock.add(icuWard1);
        cardiology.add(icuBlock);

        HospitalUnit ward201 = new HospitalUnit("Палата 201", "Палата");
        ward201.add(new Bed("201-A", false));
        ward201.add(new Bed("201-B", false));
        surgery.add(ward201);

        buildingA.add(cardiology);
        buildingB.add(surgery);
        hospital.add(buildingA);
        hospital.add(buildingB);

        log("Структура лікарні побудована. Виведення дерева:");
        System.out.println();
        hospital.printStructure("");
        System.out.println();

        log(String.format("Вільні ліжка в усій лікарні (%s): %d з %d",
                hospital.getName(), hospital.getFreeBeds(), hospital.getTotalBeds()));

        log(String.format("Вільні ліжка в окремому відділенні (%s): %d з %d",
                cardiology.getName(), cardiology.getFreeBeds(), cardiology.getTotalBeds()));
    }
}
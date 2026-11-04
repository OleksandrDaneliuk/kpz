interface TonometerCuff {
    String getDescription();
}

interface IvCatheter {
    String getDescription();
}

interface SyringePump {
    String getDescription();
}


class AdultCuff implements TonometerCuff {
    @Override public String getDescription() { return "Манжета тонометра [Стандартна доросла, 22-32 см]"; }
}
class AdultCatheter implements IvCatheter {
    @Override public String getDescription() { return "Внутрішньовенний катетер [Дорослий 18G Green]"; }
}
class AdultSyringePump implements SyringePump {
    @Override public String getDescription() { return "Шприц-дозатор [Дорослий профіль: 0.1 - 99.9 мл/год]"; }
}



class PediatricCuff implements TonometerCuff {
    @Override public String getDescription() { return "Манжета тонометра [Дитяча, 14-21 см]"; }
}
class PediatricCatheter implements IvCatheter {
    @Override public String getDescription() { return "Внутрішньовенний катетер [Дитячий 22G Blue]"; }
}
class PediatricSyringePump implements SyringePump {
    @Override public String getDescription() { return "Шприц-дозатор [Дитячий профіль: точне мікродозування 0.01 мл/год]"; }
}



class NewbornCuff implements TonometerCuff {
    @Override public String getDescription() { return "Манжета тонометра [Неонатальна №1, 6-11 см]"; }
}
class NewbornCatheter implements IvCatheter {
    @Override public String getDescription() { return "Внутрішньовенний катетер [Неонатальний 24G Yellow з крильцями]"; }
}
class NewbornSyringePump implements SyringePump {
    @Override public String getDescription() { return "Шприц-дозатор [Неонатальний ультраточний профіль з захистом від болюсу]"; }
}

//AbstractFactory
interface MedicalKitFactory {
    TonometerCuff createCuff();
    IvCatheter createCatheter();
    SyringePump createPump();
    String getCategoryName();
}

//Concrete Abstract Factors
class AdultMedicalKitFactory implements MedicalKitFactory {
    @Override public TonometerCuff createCuff() { return new AdultCuff(); }
    @Override public IvCatheter createCatheter() { return new AdultCatheter(); }
    @Override public SyringePump createPump() { return new AdultSyringePump(); }
    @Override public String getCategoryName() { return "ДОРОСЛИЙ"; }
}

class PediatricMedicalKitFactory implements MedicalKitFactory {
    @Override public TonometerCuff createCuff() { return new PediatricCuff(); }
    @Override public IvCatheter createCatheter() { return new PediatricCatheter(); }
    @Override public SyringePump createPump() { return new PediatricSyringePump(); }
    @Override public String getCategoryName() { return "ДИТЯЧИЙ"; }
}

// New Factory
class NewbornMedicalKitFactory implements MedicalKitFactory {
    @Override public TonometerCuff createCuff() { return new NewbornCuff(); }
    @Override public IvCatheter createCatheter() { return new NewbornCatheter(); }
    @Override public SyringePump createPump() { return new NewbornSyringePump(); }
    @Override public String getCategoryName() { return "НОВОНАРОДЖЕНІ"; }
}


class PatientRoom {
    private final int roomNumber;

    public PatientRoom(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void prepareKit(MedicalKitFactory factory) {
        System.out.println("\n=== Підготовка палати №" + roomNumber + " (Категорія: " + factory.getCategoryName() + ") ===");

        TonometerCuff cuff = factory.createCuff();
        IvCatheter catheter = factory.createCatheter();
        SyringePump pump = factory.createPump();

        System.out.println("Видано узгоджений набір медвиробів:");
        System.out.println("1." + cuff.getDescription());
        System.out.println("2." + catheter.getDescription());
        System.out.println("3." + pump.getDescription());
        System.out.println("Усі вироби належать до однієї категорії: " + factory.getCategoryName());
    }
}
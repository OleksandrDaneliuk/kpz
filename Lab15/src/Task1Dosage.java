import java.util.Locale;

/** STRATEGY. */
public final class Task1Dosage {
    private Task1Dosage() { }

    // Домен
    public static final class Patient {
        public final String name;
        public final double weightKg;
        public final double heightCm;
        public final double gfr;      // швидкість клубочкової фільтрації, мл/хв
        public final int ageDays;

        public Patient(String name, double weightKg, double heightCm, double gfr, int ageDays) {
            this.name = name;
            this.weightKg = weightKg;
            this.heightCm = heightCm;
            this.gfr = gfr;
            this.ageDays = ageDays;
        }
    }

    public static final class Drug {
        public final String name;
        public final double mgPerKg;
        public final double mgPerM2;
        public final double fixedAdultMg;

        public Drug(String name, double mgPerKg, double mgPerM2, double fixedAdultMg) {
            this.name = name;
            this.mgPerKg = mgPerKg;
            this.mgPerM2 = mgPerM2;
            this.fixedAdultMg = fixedAdultMg;
        }
    }

    // Strategy
    public interface DosageStrategy {
        String name();

        double calculate(Patient patient, Drug drug);
    }

    public static final class WeightBasedDosage implements DosageStrategy {
        public String name() { return "За масою тіла"; }

        public double calculate(Patient p, Drug d) {
            return d.mgPerKg * p.weightKg;
        }
    }

    public static final class BodySurfaceAreaDosage implements DosageStrategy {
        public String name() { return "За площею поверхні тіла"; }

        public double calculate(Patient p, Drug d) {
            double bsa = Math.sqrt(p.heightCm * p.weightKg / 3600.0); // формула Мостеллера
            return d.mgPerM2 * bsa;
        }
    }

    public static final class FixedAdultDosage implements DosageStrategy {
        public String name() { return "Фіксована доза для дорослих"; }

        public double calculate(Patient p, Drug d) {
            return d.fixedAdultMg;
        }
    }

    public static final class RenalAdjustedDosage implements DosageStrategy {
        public String name() { return "З поправкою на функцію нирок"; }

        public double calculate(Patient p, Drug d) {
            return d.mgPerKg * p.weightKg * factor(p.gfr);
        }

        private double factor(double gfr) {
            if (gfr >= 90) return 1.0;
            if (gfr >= 60) return 0.75;
            if (gfr >= 30) return 0.5;
            return 0.25;
        }
    }

    public static final class NeonatalDosage implements DosageStrategy {
        private static final double MG_PER_KG = 10.0;
        private static final int MAX_AGE_DAYS = 28;

        public String name() { return "Для новонароджених"; }

        public double calculate(Patient p, Drug d) {
            if (p.ageDays > MAX_AGE_DAYS) {
                throw new IllegalArgumentException("Методика лише для віку до " + MAX_AGE_DAYS + " днів");
            }
            return MG_PER_KG * p.weightKg;
        }
    }

    //  Context
    public static final class DoseCalculator {
        private DosageStrategy strategy;

        public void setStrategy(DosageStrategy strategy) {
            this.strategy = strategy;
            Log.event("Лікар", "Обрано методику: " + strategy.name());
        }

        public double calculate(Patient patient, Drug drug) {
            double dose = strategy.calculate(patient, drug);
            Log.event("Розрахунок", String.format(Locale.US,
                    "Пацієнт %s, препарат %s, методика «%s» -> %.1f мг",
                    patient.name, drug.name, strategy.name(), dose));
            return dose;
        }
    }

    public static void demo() {
        Drug drug = new Drug("Парацетамол", 15, 500, 1000);
        Patient ivan = new Patient("Іван (70 кг)", 70, 175, 95, 40 * 365);
        DoseCalculator calc = new DoseCalculator();

        Log.event("Сценарій", "1) Один пацієнт, три методики");
        DosageStrategy[] strategies = {new WeightBasedDosage(), new BodySurfaceAreaDosage(), new FixedAdultDosage()};
        for (DosageStrategy s : strategies) {
            calc.setStrategy(s);
            calc.calculate(ivan, drug);
        }

        Log.event("Сценарій", "2) Знижена функція нирок -> змінюємо методику під час виконання");
        Patient maria = new Patient("Марія (70 кг, ШКФ 45)", 70, 165, 45, 55 * 365);
        calc.setStrategy(new WeightBasedDosage());
        calc.calculate(maria, drug);
        calc.setStrategy(new RenalAdjustedDosage());
        calc.calculate(maria, drug);

        Log.event("Сценарій", "3) Додаємо методику для новонароджених (один новий клас)");
        Patient baby = new Patient("Олег (3.2 кг, 5 днів)", 3.2, 50, 20, 5);
        calc.setStrategy(new NeonatalDosage());
        calc.calculate(baby, drug);
    }
}
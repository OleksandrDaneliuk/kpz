import java.util.HashSet;
import java.util.Set;

/** Завдання 2. Підготовка до обстежень — шаблон TEMPLATE METHOD. */
public final class Task2Examination {
    private Task2Examination() { }

    // ---------- Домен ----------
    public static final class Patient {
        public final String name;
        private final Set<String> referrals = new HashSet<String>();
        public boolean pregnant;
        public boolean pacemaker;
        public boolean bleedingDisorder;

        public Patient(String name) { this.name = name; }

        public Patient withReferral(String code) {
            referrals.add(code);
            return this;
        }

        public boolean hasReferral(String code) { return referrals.contains(code); }
    }

    public static final class ExaminationResult {
        public final boolean completed;
        public final String stoppedAt;     // етап зупинки (null, якщо завершено)
        public final String conclusion;

        public ExaminationResult(boolean completed, String stoppedAt, String conclusion) {
            this.completed = completed;
            this.stoppedAt = stoppedAt;
            this.conclusion = conclusion;
        }
    }

    // ---------- Template Method ----------
    public abstract static class Examination {

        /** ШАБЛОННИЙ МЕТОД: єдина послідовність етапів, final — підкласи її не змінять. */
        public final ExaminationResult perform(Patient p) {
            Log.event(title(), "Початок процедури для пацієнта " + p.name);

            Log.event(title(), "Етап 1/6: перевірка направлення");
            if (!checkReferral(p)) {
                return stop("перевірка направлення", "направлення на " + title() + " відсутнє");
            }
            Log.event(title(), "Етап 2/6: перевірка протипоказань");
            String reason = findContraindication(p);
            if (reason != null) {
                return stop("перевірка протипоказань", reason);
            }
            Log.event(title(), "Етап 3/6: підготовка пацієнта");
            prepare(p);

            Log.event(title(), "Етап 4/6 (необов'язковий, hook): контраст");
            administerContrast(p);

            Log.event(title(), "Етап 5/6: проведення обстеження");
            String findings = conduct(p);

            Log.event(title(), "Етап 6/6: оформлення висновку");
            String conclusion = writeConclusion(p, findings);
            return new ExaminationResult(true, null, conclusion);
        }

        // --- те, що визначає кожне обстеження ---
        public abstract String title();

        protected abstract String code();

        protected abstract String findContraindication(Patient p); // null = протипоказань немає

        protected abstract void prepare(Patient p);

        protected abstract String conduct(Patient p);

        // --- hook: поведінка за замовчуванням, можна перевизначити ---
        protected void administerContrast(Patient p) {
            Log.event(title(), "Контраст не потрібен — етап пропущено (поведінка за замовчуванням)");
        }

        // --- спільні етапи: живуть в одному місці ---
        protected final boolean checkReferral(Patient p) {
            boolean ok = p.hasReferral(code());
            if (ok) Log.event(title(), "Направлення є");
            return ok;
        }

        protected final String writeConclusion(Patient p, String findings) {
            String text = "Висновок [" + title() + "] для " + p.name + ": " + findings;
            Log.event(title(), "Висновок оформлено за єдиним зразком: " + text);
            return text;
        }

        private ExaminationResult stop(String stage, String reason) {
            Log.event(title(), "ЗУПИНКА на етапі «" + stage + "»: " + reason);
            return new ExaminationResult(false, stage, null);
        }
    }

    public static final class CtExamination extends Examination {
        public String title() { return "КТ"; }

        protected String code() { return "CT"; }

        protected String findContraindication(Patient p) {
            return p.pregnant ? "вагітність" : null;
        }

        protected void prepare(Patient p) {
            Log.event(title(), "Голодування 4 год, зняття металевих предметів");
        }

        @Override
        protected void administerContrast(Patient p) {
            Log.event(title(), "Внутрішньовенне введення контрасту");
        }

        protected String conduct(Patient p) {
            Log.event(title(), "Сканування з контрастом");
            return "патологій не виявлено";
        }
    }

    public static final class MriExamination extends Examination {
        public String title() { return "МРТ"; }

        protected String code() { return "MRI"; }

        protected String findContraindication(Patient p) {
            return p.pacemaker ? "кардіостимулятор (небезпечно в магнітному полі)" : null;
        }

        protected void prepare(Patient p) {
            Log.event(title(), "Видалення всіх металевих предметів, навушники від шуму");
        }

        protected String conduct(Patient p) {
            Log.event(title(), "Сканування в магнітному полі");
            return "без змін";
        }
    }

    public static final class EndoscopyExamination extends Examination {
        public String title() { return "Ендоскопія"; }

        protected String code() { return "ENDOSCOPY"; }

        protected String findContraindication(Patient p) {
            return p.bleedingDisorder ? "порушення згортання крові" : null;
        }

        protected void prepare(Patient p) {
            Log.event(title(), "Голодування 8 год, седація");
        }

        protected String conduct(Patient p) {
            Log.event(title(), "Введення ендоскопа, огляд");
            return "слизова без особливостей";
        }
    }

    /** НОВЕ обстеження: один клас, послідовність етапів не копіюється. */
    public static final class XRayExamination extends Examination {
        public String title() { return "Рентген"; }

        protected String code() { return "XRAY"; }

        protected String findContraindication(Patient p) {
            return p.pregnant ? "вагітність (іонізуюче випромінювання)" : null;
        }

        protected void prepare(Patient p) {
            Log.event(title(), "Зняття металевих предметів, свинцевий захист");
        }

        protected String conduct(Patient p) {
            Log.event(title(), "Знімок грудної клітки");
            return "легені чисті";
        }
    }

    // ---------- Демонстраційний сценарій ----------
    public static void demo() {
        Log.event("Сценарій", "1) КТ з контрастом — видно всі етапи");
        new CtExamination().perform(new Patient("Анна").withReferral("CT"));

        Log.event("Сценарій", "2) МРТ тією самою процедурою, пацієнт з кардіостимулятором");
        Patient petro = new Patient("Петро").withReferral("MRI");
        petro.pacemaker = true;
        ExaminationResult r = new MriExamination().perform(petro);
        Log.event("Сценарій", "Результат: завершено=" + r.completed + ", зупинка на етапі «" + r.stoppedAt + "»");

        Log.event("Сценарій", "3) Нове обстеження «рентген» — один клас");
        new XRayExamination().perform(new Patient("Дмитро").withReferral("XRAY"));
    }
}

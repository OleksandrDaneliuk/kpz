import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Log {
    private static final int START_MINUTES = 9 * 60;
    private static int minutes = START_MINUTES;
    private static boolean echo = true;
    private static final List<String> LINES = new ArrayList<>();

    private Log() {
    }

    public static void log(String source, String event) {
        minutes++;
        String line = String.format("[%02d:%02d] [%s] %s", minutes / 60, minutes % 60, source, event);
        LINES.add(line);
        if (echo) {
            System.out.println(line);
        }
    }

    public static void reset() {
        minutes = START_MINUTES;
        LINES.clear();
    }

    public static void setEcho(boolean value) {
        echo = value;
    }

    public static List<String> lines() {
        return Collections.unmodifiableList(new ArrayList<>(LINES));
    }
}

public final class Log {
    private static int seconds = 9 * 3600;

    private Log() { }

    public static void reset() {
        seconds = 9 * 3600;
    }

    public static void event(String source, String message) {
        seconds += 5;
        int h = seconds / 3600, m = (seconds % 3600) / 60, s = seconds % 60;
        System.out.println(String.format("[%02d:%02d:%02d] [%s] %s", h, m, s, source, message));
    }
}
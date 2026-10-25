public class ConsoleCommandParser {
    public boolean parseBoolean(String input) {
        if (input == null) return false;
        String trimmed = input.trim().toLowerCase();
        return trimmed.equals("так") || trimmed.equals("yes") || trimmed.equals("1") || trimmed.equals("t");
    }
}


import java.io.PrintStream;

public class Main {
    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, "UTF-8")); 

        section("ЗАВДАННЯ 1. Розрахунок дозування (Strategy)");
        Task1Dosage.demo();
    }

    private static void section(String title) {
        Log.reset();
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
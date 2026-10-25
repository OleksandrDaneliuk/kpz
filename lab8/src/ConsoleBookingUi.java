import java.util.Scanner;


public class ConsoleBookingUi {
    private final BookingApplicationController controller;
    private final ConsoleCommandParser parser;

    public ConsoleBookingUi(BookingApplicationController controller, ConsoleCommandParser parser) {
        this.controller = controller;
        this.parser = parser;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Бронювання номера в готелі (Thin CLI UI) ===");
        System.out.print("Введіть ПІБ гостя: ");
        String name = scanner.nextLine();

        System.out.print("Введіть номер телефону (+380...): ");
        String phone = scanner.nextLine();

        System.out.print("Введіть паспортні дані: ");
        String passport = scanner.nextLine();

        System.out.print("Введіть сезон (Low/High/Peak): ");
        String season = scanner.nextLine();

        System.out.print("Введіть кількість ночей: ");
        int nights = Integer.parseInt(scanner.nextLine());

        System.out.print("Включити сніданок? (так/ні): ");
        boolean breakfast = parser.parseBoolean(scanner.nextLine());

        System.out.print("Включити парковку? (так/ні): ");
        boolean parking = parser.parseBoolean(scanner.nextLine());


        CreateBookingRequestDto request = new CreateBookingRequestDto(
                name, phone, passport, 302, "Deluxe", 2000.0,
                nights, season, breakfast, parking
        );


        BookingResponseDto response = controller.createBooking(request);


        System.out.println("\nРезультат");
        System.out.println(response.confirmationText());
        System.out.println("Статус бронювання: " + response.status());
    }
}
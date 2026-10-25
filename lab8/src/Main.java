public class Main {
    public static void main(String[] args) {

        BookingRepository repository = new FileBookingRepository();
        NotificationService notificationService = new SmsNotificationService();
        BookingFactory factory = new BookingFactory(new StandardTouristTaxCalculator(), 500);

        BookingApplicationController controller =
                new BookingApplicationController(factory, repository, notificationService);
        ConsoleCommandParser parser = new ConsoleCommandParser();

        ConsoleBookingUi ui = new ConsoleBookingUi(controller, parser);

        ui.start();
    }
}

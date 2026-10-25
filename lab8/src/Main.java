public class Main {
    public static void main(String[] args) {

        BookingRepository repository = new FileBookingRepository();
        NotificationService notificationService = new SmsNotificationService();

        BookingApplicationController controller = new BookingApplicationController(repository, notificationService);
        ConsoleCommandParser parser = new ConsoleCommandParser();

        ConsoleBookingUi ui = new ConsoleBookingUi(controller, parser);

        ui.start();
    }
}
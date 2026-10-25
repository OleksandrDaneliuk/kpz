import java.util.Scanner;

public class HotelManager {
    public void createBookingFromConsole() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Бронювання номера в готелі ===");
        System.out.print("Введіть ПІБ гостя: ");
        String name = scanner.nextLine();
        System.out.print("Введіть сезон (Low/High/Peak): ");
        String season = scanner.nextLine();

        Guest guest = new Guest(10, name, "+380971112233", "AA123456");
        Room room = new Room(302, "Deluxe", 2000.0);

        Booking booking = Booking.create(501, guest, room, 3, season, true, true);

        System.out.println(booking.generateConfirmationText());

        System.out.println("[Файлова БД] Запис про бронювання #" + booking.getBookingId() + " додано до bookings.dat.");
        System.out.println("[SMS] Підтвердження надіслано на номер " + booking.getGuest().getPhone());
    }
}
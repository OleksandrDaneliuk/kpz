public class FileBookingRepository implements BookingRepository {
    @Override
    public void save(Booking booking) {
        // Імітація збереження у файлову БД
        System.out.println("[Файлова БД] Запис про бронювання #" + booking.getBookingId() + " додано до bookings.dat.");
    }
}

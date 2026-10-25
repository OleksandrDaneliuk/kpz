public class SmsNotificationService implements NotificationService {
    @Override
    public void sendBookingConfirmation(Booking booking) {
        // Імітація відправки SMS
        System.out.println("[SMS] Підтвердження надіслано на номер " + booking.getGuest().getPhone());
    }
}




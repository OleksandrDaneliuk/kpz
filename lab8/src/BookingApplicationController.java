public class BookingApplicationController {
    private final BookingFactory factory;
    private final BookingRepository repository;
    private final NotificationService notificationService;

    public BookingApplicationController(BookingFactory factory,
                                        BookingRepository repository,
                                        NotificationService notificationService) {
        this.factory = factory;
        this.repository = repository;
        this.notificationService = notificationService;
    }

    public BookingResponseDto createBooking(CreateBookingRequestDto request) {
        Booking booking = factory.create(request);
        repository.save(booking);
        notificationService.sendBookingConfirmation(booking);

        return new BookingResponseDto(
                booking.getBookingId(),
                booking.getGuest().getFullName(),
                booking.getRoom().getRoomNumber(),
                booking.getRoom().getCategory(),
                booking.getRoomTotal(),
                booking.getServicesTotal(),
                booking.getTouristTax(),
                booking.getGrandTotal(),
                booking.getStatus(),
                booking.generateConfirmationText());
    }
}

public class BookingApplicationController {
    private final BookingRepository repository;
    private final NotificationService notificationService;
    private int idCounter = 500;

    public BookingApplicationController(BookingRepository repository, NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    public BookingResponseDto createBooking(CreateBookingRequestDto request) {

        Guest guest = new Guest(10, request.guestName(), request.guestPhone(), request.passportNumber());
        Room room = new Room(request.roomNumber(), request.roomCategory(), request.roomBasePrice());

        int bookingId = ++idCounter;
        Booking booking = Booking.create(
                bookingId,
                guest,
                room,
                request.nights(),
                request.season(),
                request.includeBreakfast(),
                request.includeParking()
        );


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
                booking.generateConfirmationText()
        );
    }
}





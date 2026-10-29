import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BookingFactory {
    private static final int DEFAULT_GUEST_ID = 10;

    private final TouristTaxCalculator taxCalculator;
    private final AtomicInteger idCounter;

    public BookingFactory(TouristTaxCalculator taxCalculator, int lastUsedId) {
        this.taxCalculator = taxCalculator;
        this.idCounter = new AtomicInteger(lastUsedId);
    }

    public Booking create(CreateBookingRequestDto request) {
        Guest guest = new Guest(DEFAULT_GUEST_ID, request.guestName(),
                request.guestPhone(), request.passportNumber());
        Room room = new Room(request.roomNumber(), request.roomCategory(),
                request.roomBasePrice());

        List<Booking.ExtraService> services = new ArrayList<>();
        if (request.includeBreakfast()) {
            services.add(Booking.ExtraService.BREAKFAST);
        }
        if (request.includeParking()) {
            services.add(Booking.ExtraService.PARKING);
        }

        return Booking.create(
                idCounter.incrementAndGet(),
                guest,
                room,
                request.nights(),
                Season.fromString(request.season()),
                taxCalculator,
                services
        );
    }
}

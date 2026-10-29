import java.util.List;
import java.util.Objects;

public class Booking {
    public enum ExtraService {
        BREAKFAST(300.0),
        PARKING(150.0);

        private final double price;
        ExtraService(double price) { this.price = price; }
        public double getPrice() { return price; }
    }

    private final int bookingId;
    private final Guest guest;
    private final Room room;
    private final int nights;
    private final SeasonalPricingStrategy seasonStrategy;
    private final List<ExtraService> services;

    private final double roomTotal;
    private final double servicesTotal;
    private final double touristTax;
    private final double grandTotal;
    private String status;

    Booking(int bookingId, Guest guest, Room room, int nights,
                    SeasonalPricingStrategy seasonStrategy,
                    TouristTaxCalculator taxCalculator,
                    List<ExtraService> services) {
        if (bookingId <= 0) {
            throw new IllegalArgumentException("ID бронювання має бути додатним.");
        }
        if (nights <= 0) {
            throw new IllegalArgumentException("Кількість ночей повинна бути більшою за 0.");
        }
        this.bookingId = bookingId;
        this.guest = Objects.requireNonNull(guest, "Сутність Guest є обов'язковою.");
        this.room = Objects.requireNonNull(room, "Сутність Room є обов'язковою.");
        this.nights = nights;
        this.seasonStrategy = Objects.requireNonNull(seasonStrategy, "Стратегія сезону є обов'язковою.");


        this.services = List.copyOf(Objects.requireNonNull(services, "Cписок послуг є обов'язковим."));

        this.roomTotal = calculateRoomTotal();
        this.servicesTotal = calculateServicesTotal();
        this.touristTax = Objects.requireNonNull(taxCalculator, "Калькулятор збору є обов'язковим.")
                .calculateTax(roomTotal, servicesTotal);
        this.grandTotal = calculateGrandTotal();
        this.status = "Confirmed";
    }

    public static Booking create(int bookingId, Guest guest, Room room, int nights,
                                 SeasonalPricingStrategy seasonStrategy,
                                 TouristTaxCalculator taxCalculator,
                                 List<ExtraService> services) {
        return new Booking(bookingId, guest, room, nights, seasonStrategy, taxCalculator, services);
    }

    private double calculateRoomTotal() {
        return seasonStrategy.calculate(room.getBasePricePerNight(), nights);
    }

    private double calculateServicesTotal() {
        return services.stream()
                .mapToDouble(service -> service.getPrice() * nights)
                .sum();
    }

    private double calculateGrandTotal() {
        return roomTotal + servicesTotal + touristTax;
    }

    public String generateConfirmationText() {
        return "Бронювання #" + bookingId + " для " + guest.getFullName() +
                "\nКімната: " + room.getRoomNumber() + " (" + room.getCategory() + ")" +
                "\nПроживання: " + roomTotal + " грн" +
                "\nПослуги: " + servicesTotal + " грн" +
                "\nТуристичний збір: " + touristTax + " грн" +
                "\nВсього: " + grandTotal + " грн" +
                "\nСтатус: " + status;
    }

    public int getBookingId() { return bookingId; }
    public Guest getGuest() { return guest; }
    public Room getRoom() { return room; }
    public int getNights() { return nights; }
    public SeasonalPricingStrategy getSeasonStrategy() { return seasonStrategy; }
    public List<ExtraService> getServices() { return services; }
    public double getRoomTotal() { return roomTotal; }
    public double getServicesTotal() { return servicesTotal; }
    public double getTouristTax() { return touristTax; }
    public double getGrandTotal() { return grandTotal; }
    public String getStatus() { return status; }
}
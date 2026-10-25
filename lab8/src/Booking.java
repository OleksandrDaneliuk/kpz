import java.util.Objects;

public class Booking {
    private final int bookingId;
    private final Guest guest;
    private final Room room;
    private final int nights;
    private final SeasonalPricingStrategy seasonStrategy;
    private final boolean includeBreakfast;
    private final boolean includeParking;

    private final double roomTotal;
    private final double servicesTotal;
    private final double touristTax;
    private final double grandTotal;
    private String status;

    public Booking(int bookingId, Guest guest, Room room, int nights,
                   SeasonalPricingStrategy seasonStrategy,
                   TouristTaxCalculator taxCalculator,
                   boolean includeBreakfast, boolean includeParking) {
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
        this.includeBreakfast = includeBreakfast;
        this.includeParking = includeParking;

        this.roomTotal = calculateRoomTotal();
        this.servicesTotal = calculateServicesTotal();
        this.touristTax = Objects.requireNonNull(taxCalculator, "Калькулятор збору є обов'язковим.")
                .calculateTax(roomTotal, servicesTotal);
        this.grandTotal = calculateGrandTotal();
        this.status = "Confirmed";
    }

    private double calculateRoomTotal() {
        return room.getBasePricePerNight() * nights * seasonStrategy.getMultiplier();
    }

    private double calculateServicesTotal() {
        double services = 0.0;
        if (includeBreakfast) {
            services += 300.0 * nights;
        }
        if (includeParking) {
            services += 150.0 * nights;
        }
        return services;
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
    public boolean isIncludeBreakfast() { return includeBreakfast; }
    public boolean isIncludeParking() { return includeParking; }
    public double getRoomTotal() { return roomTotal; }
    public double getServicesTotal() { return servicesTotal; }
    public double getTouristTax() { return touristTax; }
    public double getGrandTotal() { return grandTotal; }
    public String getStatus() { return status; }
}

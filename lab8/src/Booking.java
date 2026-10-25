import java.util.Objects;

public class Booking {
    private final int bookingId;
    private final Guest guest;
    private final Room room;
    private final int nights;
    private final String season; // "Low", "High", "Peak"
    private final boolean includeBreakfast;
    private final boolean includeParking;

    private final double roomTotal;
    private final double servicesTotal;
    private final double touristTax;
    private final double grandTotal;
    private String status;

    private Booking(int bookingId, Guest guest, Room room, int nights, String season,
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
        this.season = season != null ? season : "Low";
        this.includeBreakfast = includeBreakfast;
        this.includeParking = includeParking;

        this.roomTotal = calculateRoomTotal();
        this.servicesTotal = calculateServicesTotal();
        this.touristTax = calculateTouristTax();
        this.grandTotal = calculateGrandTotal();
        this.status = "Confirmed";
    }

    public static Booking create(int bookingId, Guest guest, Room room, int nights,
                                 String season, boolean includeBreakfast, boolean includeParking) {
        return new Booking(bookingId, guest, room, nights, season, includeBreakfast, includeParking);
    }


    private double getSeasonalMultiplier() {
        if ("Peak".equalsIgnoreCase(season)) {
            return 1.5;
        } else if ("High".equalsIgnoreCase(season)) {
            return 1.25;
        } else if ("Low".equalsIgnoreCase(season)) {
            return 0.9;
        }
        return 1.0;
    }

    private double calculateRoomTotal() {
        return room.getBasePricePerNight() * nights * getSeasonalMultiplier();
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

    private double calculateTouristTax() {
        return (roomTotal + servicesTotal) * 0.01;
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
                "\nВсього: " + grandTotal + " грн";
    }


    public int getBookingId() { return bookingId; }
    public Guest getGuest() { return guest; }
    public Room getRoom() { return room; }
    public int getNights() { return nights; }
    public String getSeason() { return season; }
    public boolean isIncludeBreakfast() { return includeBreakfast; }
    public boolean isIncludeParking() { return includeParking; }
    public double getRoomTotal() { return roomTotal; }
    public double getServicesTotal() { return servicesTotal; }
    public double getTouristTax() { return touristTax; }
    public double getGrandTotal() { return grandTotal; }
    public String getStatus() { return status; }
}

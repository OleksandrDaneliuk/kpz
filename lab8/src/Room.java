public class Room {
    private final int roomNumber;
    private final String category; // "Standard", "Deluxe", "Suite"
    private final double basePricePerNight;

    public Room(int roomNumber, String category, double basePricePerNight) {
        if (roomNumber <= 0) {
            throw new IllegalArgumentException("Номер кімнати повинен бути більшим за 0.");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Категорія номера не може бути порожньою.");
        }
        if (basePricePerNight <= 0) {
            throw new IllegalArgumentException("Базова ціна за ніч повинна бути більшою за 0.");
        }

        this.roomNumber = roomNumber;
        this.category = category;
        this.basePricePerNight = basePricePerNight;
    }

    public int getRoomNumber() { return roomNumber; }
    public String getCategory() { return category; }
    public double getBasePricePerNight() { return basePricePerNight; }
}

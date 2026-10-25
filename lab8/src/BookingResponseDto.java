public record BookingResponseDto(
        int bookingId,
        String guestName,
        int roomNumber,
        String roomCategory,
        double roomTotal,
        double servicesTotal,
        double touristTax,
        double grandTotal,
        String status,
        String confirmationText
) {}





public record CreateBookingRequestDto(
        String guestName,
        String guestPhone,
        String passportNumber,
        int roomNumber,
        String roomCategory,
        double roomBasePrice,
        int nights,
        String season,
        boolean includeBreakfast,
        boolean includeParking
) {}




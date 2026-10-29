public record Stay(String category, double basePrice, SeasonalPricingStrategy season) {
    public double total(int nights) {
        return season.calculate(basePrice, nights);
    }
}

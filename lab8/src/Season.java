public enum Season implements SeasonalPricingStrategy {
    LOW  { public double calculate(double b, int n) { return b * n * 0.90; } },
    HIGH { public double calculate(double b, int n) { return b * n * 1.25; } },
    PEAK { public double calculate(double b, int n) { return b * n * 1.50; } };

    public static Season fromString(String season) {
        if (season == null) {
            throw new IllegalArgumentException("Не може бути null.");
        }
        try {
            return Season.valueOf(season.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Невідомий сезон: " + season);
        }
    }
}


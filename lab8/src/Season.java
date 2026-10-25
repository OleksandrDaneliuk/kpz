public enum Season implements SeasonalPricingStrategy {
    LOW(0.9),
    HIGH(1.25),
    PEAK(1.5),
    REGULAR(1.0);

    private final double multiplier;

    Season(double multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public double getMultiplier() {
        return multiplier;
    }

    public static Season fromString(String name) {
        if (name == null) return REGULAR;
        for (Season season : values()) {
            if (season.name().equalsIgnoreCase(name)) {
                return season;
            }
        }
        return REGULAR;
    }
}


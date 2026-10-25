public class StandardTouristTaxCalculator implements TouristTaxCalculator {
    private static final double TAX_RATE = 0.01;

    @Override
    public double calculateTax(double roomTotal, double servicesTotal) {
        return (roomTotal + servicesTotal) * TAX_RATE;
    }
}

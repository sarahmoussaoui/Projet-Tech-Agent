package part2_intercontainers;

public class ProductEvaluator {
    // Weights for each criterion (can be adjusted based on buyer preferences)
    private static final double PRICE_WEIGHT = 0.3;
    private static final double DELIVERY_TIME_WEIGHT = 0.2;
    private static final double QUALITY_WEIGHT = 0.2;
    private static final double USER_RATINGS_WEIGHT = 0.2;
    private static final double STOCK_WEIGHT = 0.1;

    public static double evaluateProduct(double price, double deliveryTime, 
                                       double quality, double userRatings, 
                                       int stockAvailability) {
        // Normalize all values to 0-1 range (higher is better)
        double normalizedPrice = 1 - normalize(price, 100, 200); // Lower price is better
        double normalizedDelivery = 1 - normalize(deliveryTime, 1, 10); // Lower delivery time is better
        double normalizedQuality = normalize(quality, 1, 10); // Higher quality is better
        double normalizedRatings = normalize(userRatings, 1, 5); // Higher ratings are better
        double normalizedStock = normalize(stockAvailability, 0, 100); // Higher stock is better

        // Calculate weighted score
        return (PRICE_WEIGHT * normalizedPrice) +
               (DELIVERY_TIME_WEIGHT * normalizedDelivery) +
               (QUALITY_WEIGHT * normalizedQuality) +
               (USER_RATINGS_WEIGHT * normalizedRatings) +
               (STOCK_WEIGHT * normalizedStock);
    }

    private static double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }
}
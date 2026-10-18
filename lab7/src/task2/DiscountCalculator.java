package task2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum CustomerCategory {
    REGULAR,
    VIP,
    WHOLESALE,
    EMPLOYEE,
    PARTNER
}

class Order {
    private String orderId;
    private BigDecimal totalAmount;
    private int itemCount;
    private boolean isFirstOrder;

    public Order(String orderId, BigDecimal totalAmount, int itemCount, boolean isFirstOrder) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.itemCount = itemCount;
        this.isFirstOrder = isFirstOrder;
    }

    public String getOrderId() { return orderId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public int getItemCount() { return itemCount; }
    public boolean isFirstOrder() { return isFirstOrder; }
}



interface DiscountStrategy {
    CustomerCategory getCategory();
    BigDecimal calculate(Order order);
}

class RegularDiscountStrategy implements DiscountStrategy {
    @Override
    public CustomerCategory getCategory() {
        return CustomerCategory.REGULAR;
    }

    @Override
    public BigDecimal calculate(Order order) {
        BigDecimal amount = order.getTotalAmount();
        if (order.isFirstOrder()) {
            return amount.multiply(new BigDecimal("0.10"));
        } else if (amount.compareTo(new BigDecimal("100.00")) >= 0) {
            return amount.multiply(new BigDecimal("0.05"));
        }
        return BigDecimal.ZERO;
    }
}

class VipDiscountStrategy implements DiscountStrategy {
    @Override
    public CustomerCategory getCategory() {
        return CustomerCategory.VIP;
    }

    @Override
    public BigDecimal calculate(Order order) {
        BigDecimal amount = order.getTotalAmount();
        BigDecimal discount = amount.multiply(new BigDecimal("0.15"));
        if (order.getItemCount() > 5) {
            discount = discount.add(amount.multiply(new BigDecimal("0.05")));
        }
        return discount;
    }
}

class WholesaleDiscountStrategy implements DiscountStrategy {
    @Override
    public CustomerCategory getCategory() {
        return CustomerCategory.WHOLESALE;
    }

    @Override
    public BigDecimal calculate(Order order) {
        BigDecimal amount = order.getTotalAmount();
        if (amount.compareTo(new BigDecimal("1000.00")) >= 0) {
            return amount.multiply(new BigDecimal("0.20"));
        }
        return amount.multiply(new BigDecimal("0.10"));
    }
}

class EmployeeDiscountStrategy implements DiscountStrategy {
    @Override
    public CustomerCategory getCategory() {
        return CustomerCategory.EMPLOYEE;
    }

    @Override
    public BigDecimal calculate(Order order) {
        return order.getTotalAmount().multiply(new BigDecimal("0.30"));
    }
}

class PartnerDiscountStrategy implements DiscountStrategy {
    @Override
    public CustomerCategory getCategory() {
        return CustomerCategory.PARTNER;
    }

    @Override
    public BigDecimal calculate(Order order) {
        return order.getTotalAmount().multiply(new BigDecimal("0.25"));
    }
}


public class DiscountCalculator {
    private final Map<CustomerCategory, DiscountStrategy> strategies = new HashMap<>();

    public DiscountCalculator(List<DiscountStrategy> strategyList) {
        for (DiscountStrategy strategy : strategyList) {
            strategies.put(strategy.getCategory(), strategy);
        }
    }

    public BigDecimal calculateDiscount(Order order, CustomerCategory category) {
        DiscountStrategy strategy = strategies.get(category);
        if (strategy == null) {
            throw new IllegalArgumentException("Невідома категорія клієнта: " + category);
        }

        BigDecimal discount = strategy.calculate(order).setScale(2, RoundingMode.HALF_UP);
        System.out.println("[DISCOUNT] Замовлення " + order.getOrderId() + ": Категорія=" + category + ", Розрахована знижка=$" + discount);
        return discount;
    }
}
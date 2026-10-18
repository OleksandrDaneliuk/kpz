package variant2_discountcalculation;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Опис предметної області: Модуль дисконтної системи e-commerce платформи.
// Завдання коду: Розрахунок підсумкової знижки на замовлення залежно від типу клієнта та промо-акції.
//
//

enum CustomerCategory {
    REGULAR,
    VIP,
    WHOLESALE,
    EMPLOYEE
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

public class DiscountCalculator {

    public BigDecimal calculateDiscount(Order order, CustomerCategory category) {
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal amount = order.getTotalAmount();

        if (category == CustomerCategory.REGULAR) {
            // Для звичайних клієнтів: 10% для першого замовлення, або 5% при замовленні від $100
            if (order.isFirstOrder()) {
                discount = amount.multiply(new BigDecimal("0.10"));
            } else if (amount.compareTo(new BigDecimal("100.00")) >= 0) {
                discount = amount.multiply(new BigDecimal("0.05"));
            }
        } else if (category == CustomerCategory.VIP) {
            // VIP: базово 15%, плюс додатково 5% якщо в кошику більше 5 товарів
            discount = amount.multiply(new BigDecimal("0.15"));
            if (order.getItemCount() > 5) {
                discount = discount.add(amount.multiply(new BigDecimal("0.05")));
            }
        } else if (category == CustomerCategory.WHOLESALE) {
            // Оптові покупці: 20% при замовленні від $1000, інакше 10%
            if (amount.compareTo(new BigDecimal("1000.00")) >= 0) {
                discount = amount.multiply(new BigDecimal("0.20"));
            } else {
                discount = amount.multiply(new BigDecimal("0.10"));
            }
        } else if (category == CustomerCategory.EMPLOYEE) {
            // Співробітники компанії: фіксована знижка 30% на все
            discount = amount.multiply(new BigDecimal("0.30"));
        } else {
            throw new IllegalArgumentException("Невідома категорія клієнта: " + category);
        }

        discount = discount.setScale(2, RoundingMode.HALF_UP);
        System.out.println("[DISCOUNT] Замовлення " + order.getOrderId() + ": Категорія=" + category + ", Розрахована знижка=$" + discount);
        return discount;
    }
}
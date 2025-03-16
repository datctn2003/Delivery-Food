package phdhtl.khoa63.foodapp.Admin;

import java.util.ArrayList;
import java.util.List;
import phdhtl.khoa63.foodapp.Domain.Foods;

public class Order {
    private String orderId;
    private String userId;
    private List<Foods> orderedItems;
    private double totalAmount;
    private String paymentMethod;
    private String status;

    // Constructor mặc định
    public Order() {
        this.orderedItems = new ArrayList<>(); // Khởi tạo danh sách tránh lỗi NullPointerException
    }

    // Constructor đầy đủ tham số
    public Order(String orderId, String userId, List<Foods> orderedItems, double totalAmount, String paymentMethod, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderedItems = (orderedItems != null) ? orderedItems : new ArrayList<>();
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    // Getter methods
    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public List<Foods> getOrderedItems() { return orderedItems; }
    public double getTotalAmount() { return totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getStatus() { return status; }

    // Setter methods
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setOrderedItems(List<Foods> orderedItems) {
        this.orderedItems = (orderedItems != null) ? orderedItems : new ArrayList<>();
    }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setStatus(String status) { this.status = status; }
}

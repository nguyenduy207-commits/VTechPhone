package vtechphone.service;
import vtechphone.entity.Order;
import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    void createOrder(Order order);
    void updateOrder(Order order);
    Order getOrderById(Integer id);
    List<Order> getOrdersByUserId(Integer userId);
    List<Order> getAllOrders();
    
    // doanh thu
    BigDecimal getTotalRevenue();
    long getCompletedOrdersCount();
    List<Object[]> getMonthlyRevenue();
}
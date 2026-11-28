package vtechphone.dao;
import vtechphone.entity.Order;
import java.math.BigDecimal;
import java.util.List;

public interface OrderDAO {
    void create(Order order);
    void update(Order order);
    Order findById(Integer id);
    List<Order> findByUserId(Integer userId);
    List<Order> findAll();
    
    //Lấy tổng doanh thu và số lượng đơn hàng đã hoàn thành
    BigDecimal getTotalRevenue();
    long countCompletedOrders();
    List<Object[]> getMonthlyRevenue();
}
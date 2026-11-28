package vtechphone.service;
import vtechphone.dao.OrderDAO;
import vtechphone.dao.OrderDAOImpl;
import vtechphone.entity.Order;
import java.math.BigDecimal;
import java.util.List;

public class OrderServiceImpl implements OrderService {
    OrderDAO orderDAO = new OrderDAOImpl();

    @Override
    public void createOrder(Order order) { orderDAO.create(order); }
    @Override
    public void updateOrder(Order order) { orderDAO.update(order); }
    @Override
    public Order getOrderById(Integer id) { return orderDAO.findById(id); }
    @Override
    public List<Order> getOrdersByUserId(Integer userId) { return orderDAO.findByUserId(userId); }
    @Override
    public List<Order> getAllOrders() { return orderDAO.findAll(); }
    
    // doanh thu
    @Override
    public BigDecimal getTotalRevenue() { return orderDAO.getTotalRevenue(); }
    @Override
    public long getCompletedOrdersCount() { return orderDAO.countCompletedOrders(); }
	@Override
	public List<Object[]> getMonthlyRevenue() {
		// TODO Auto-generated method stub
		return orderDAO.getMonthlyRevenue();
	}
}
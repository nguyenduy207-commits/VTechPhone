package vtechphone.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vtechphone.entity.Order;
import vtechphone.entity.OrderItem;
import vtechphone.entity.Product;

import java.math.BigDecimal;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {

	@Override
	public void create(Order order) {
		EntityManager em = VTechPhonePU.getEntityManager();
		try {
			em.getTransaction().begin();

			// Tính tổng tiền từ items và cập nhật stock
			BigDecimal goodsTotal = BigDecimal.ZERO;
			for (OrderItem item : order.getItems()) {
				Product managedProduct = em.find(Product.class, item.getProduct().getProductId());
				if (managedProduct == null) {
					throw new IllegalStateException(
							"Sản phẩm ID " + item.getProduct().getProductId() + " không tồn tại!");
				}
				if (managedProduct.getStock() < item.getQuantity()) {
					throw new IllegalStateException("Sản phẩm " + managedProduct.getName() + " không đủ hàng!");
				}
				managedProduct.setStock(managedProduct.getStock() - item.getQuantity());
				item.setProduct(managedProduct);
				item.setUnitPrice(managedProduct.getPrice());
				goodsTotal = goodsTotal.add(managedProduct.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
			}
			BigDecimal shippingFee = goodsTotal.multiply(new BigDecimal("0.01")).setScale(0,
					java.math.RoundingMode.HALF_UP);
			order.setTotalAmount(goodsTotal.add(shippingFee));
			em.persist(order);
			em.getTransaction().commit();
		} catch (RuntimeException ex) {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			throw ex;
		} finally {
			em.close();
		}
	}

	@Override
	public void update(Order order) {
		EntityManager em = VTechPhonePU.getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(order);
			em.getTransaction().commit();
		} catch (RuntimeException ex) {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			throw ex;
		} finally {
			em.close();
		}
	}

	@Override
	public Order findById(Integer id) {
		EntityManager em = VTechPhonePU.getEntityManager();
		try {
			return em.find(Order.class, id);
		} finally {
			em.close();
		}
	}

	@Override
	public List<Order> findByUserId(Integer userId) {
		EntityManager em = VTechPhonePU.getEntityManager();
		try {
			return em.createQuery(
					"SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product WHERE o.customer.userId = :uid ORDER BY o.orderDate DESC",
					Order.class).setParameter("uid", userId).getResultList();
		} finally {
			em.close();
		}
	}

	@Override
	public List<Order> findAll() {
		EntityManager em = VTechPhonePU.getEntityManager();
		try {
			return em.createQuery("SELECT o FROM Order o JOIN FETCH o.customer ORDER BY o.orderDate DESC", Order.class)
					.getResultList();
		} finally {
			em.close();
		}
	}

	@Override
	public BigDecimal getTotalRevenue() {
		EntityManager em = VTechPhonePU.getEntityManager();
		try {
			String jpql = "SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status";
			TypedQuery<BigDecimal> query = em.createQuery(jpql, BigDecimal.class);
			query.setParameter("status", "COMPLETED");
			BigDecimal result = query.getSingleResult();
			return result != null ? result : BigDecimal.ZERO;
		} finally {
			em.close();
		}
	}

	// Hàm đếm số lượng đơn hàng đã hoàn thành
	@Override
	public long countCompletedOrders() {
		EntityManager em = VTechPhonePU.getEntityManager();
		try {
			String jpql = "SELECT COUNT(o) FROM Order o WHERE o.status = :status";
			TypedQuery<Long> query = em.createQuery(jpql, Long.class);
			query.setParameter("status", "COMPLETED");
			return query.getSingleResult();
		} finally {
			em.close();
		}
	}

	@Override
	public List<Object[]> getMonthlyRevenue() {
		EntityManager em = VTechPhonePU.getEntityManager();
		try {
			// SQL Server: dùng TOP và ORDER BY DESC để lấy 12 tháng gần nhất
			String sql = """
					SELECT TOP 12
					    FORMAT(order_date, 'yyyy-MM') AS month,
					    SUM(total_amount) AS revenue
					FROM Orders
					WHERE status = 'COMPLETED'
					GROUP BY FORMAT(order_date, 'yyyy-MM')
					ORDER BY month DESC
					""";
			return em.createNativeQuery(sql).getResultList();
		} finally {
			em.close();
		}
	}
}
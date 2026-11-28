package vtechphone.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "OrderItems")
@IdClass(OrderItemId.class)
public class OrderItem {
	@Id
	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;

	@Id
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@Column(name = "quantity")
	private Integer quantity;

	@Column(name = "unit_price")
	private BigDecimal unitPrice;
}

// Class ID phụ trợ cho composite key
class OrderItemId implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private Integer order;
	private Integer product;

	public OrderItemId() {
	}

	public OrderItemId(Integer order, Integer product) {
		this.order = order;
		this.product = product;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Integer getProduct() {
		return product;
	}

	public void setProduct(Integer product) {
		this.product = product;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof OrderItemId that))
			return false;
		return java.util.Objects.equals(order, that.order) && java.util.Objects.equals(product, that.product);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(order, product);
	}
}
package vtechphone.dao;
import vtechphone.entity.CartItem;
import java.util.List;

public interface CartDAO {
    CartItem addToCart(int userId, int productId, int quantity);
    void updateQuantity(Long cartItemId, int quantity);
    void removeFromCart(Long cartItemId);
    void clearCart(int userId);
    List<CartItem> findCartItemsByUserId(int userId);
    CartItem findByUserIdAndProductId(int userId, int productId);
    
    // 🔥 Mới thêm 2 hàm để ShopServlet gọi trực tiếp theo userId + productId
    void updateQuantity(int userId, int productId, int quantity);
    void removeFromCart(int userId, int productId);
}
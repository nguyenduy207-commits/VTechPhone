package vtechphone.service;
import vtechphone.entity.CartItem;
import java.util.List;

public interface CartService {
    CartItem addToCart(int userId, int productId, int quantity);
    void updateQuantity(Long cartItemId, int quantity);
    void removeFromCart(Long cartItemId);
    List<CartItem> getCartItemsByUserId(int userId);
    void clearCart(int userId);
    
    // 🔥 Mới thêm 2 hàm để khớp với ShopServlet
    void updateQuantity(int userId, int productId, int quantity);
    void removeFromCart(int userId, int productId);
}
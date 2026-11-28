package vtechphone.service;
import vtechphone.dao.CartDAO;
import vtechphone.dao.CartDAOImpl;
import vtechphone.entity.CartItem;
import java.util.List;

public class CartServiceImpl implements CartService {
    private CartDAO cartDAO = new CartDAOImpl();

    @Override
    public CartItem addToCart(int userId, int productId, int quantity) {
        return cartDAO.addToCart(userId, productId, quantity);
    }

    @Override
    public void updateQuantity(Long cartItemId, int quantity) {
        cartDAO.updateQuantity(cartItemId, quantity);
    }

    @Override
    public void removeFromCart(Long cartItemId) {
        cartDAO.removeFromCart(cartItemId);
    }

    @Override
    public List<CartItem> getCartItemsByUserId(int userId) {
        return cartDAO.findCartItemsByUserId(userId);
    }

    @Override
    public void clearCart(int userId) {
        cartDAO.clearCart(userId);
    }

    // 🔥 Triển khai 2 hàm tiện ích mới
    @Override
    public void updateQuantity(int userId, int productId, int quantity) {
        cartDAO.updateQuantity(userId, productId, quantity);
    }

    @Override
    public void removeFromCart(int userId, int productId) {
        cartDAO.removeFromCart(userId, productId);
    }
}
package vtechphone.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import vtechphone.entity.CartItem;
import vtechphone.entity.Product;
import vtechphone.entity.User;
import java.util.List;

public class CartDAOImpl implements CartDAO {

    @Override
    public CartItem addToCart(int userId, int productId, int quantity) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            em.getTransaction().begin();
            CartItem existing = findByUserIdAndProductId(userId, productId);
            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + quantity);
                em.merge(existing);
                em.getTransaction().commit();
                return existing;
            } else {
                CartItem newItem = new CartItem();
                User u = new User(); u.setUserId(userId);
                Product p = new Product(); p.setProductId(productId);
                newItem.setUser(u);
                newItem.setProduct(p);
                newItem.setQuantity(quantity);
                em.persist(newItem);
                em.getTransaction().commit();
                return newItem;
            }
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public void updateQuantity(Long cartItemId, int quantity) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            em.getTransaction().begin();
            CartItem item = em.find(CartItem.class, cartItemId);
            if (item != null) {
                item.setQuantity(quantity);
                em.merge(item);
            }
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public void removeFromCart(Long cartItemId) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            em.getTransaction().begin();
            CartItem item = em.find(CartItem.class, cartItemId);
            if (item != null) em.remove(item);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public void clearCart(int userId) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM CartItem c WHERE c.user.userId = :uid")
              .setParameter("uid", userId)
              .executeUpdate();
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public List<CartItem> findCartItemsByUserId(int userId) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM CartItem c JOIN FETCH c.product p JOIN FETCH p.brand JOIN FETCH p.category WHERE c.user.userId = :uid", CartItem.class)
                     .setParameter("uid", userId)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public CartItem findByUserIdAndProductId(int userId, int productId) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM CartItem c WHERE c.user.userId = :uid AND c.product.productId = :pid", CartItem.class)
                     .setParameter("uid", userId)
                     .setParameter("pid", productId)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    // 🔥 Triển khai 2 hàm tiện ích mới
    @Override
    public void updateQuantity(int userId, int productId, int quantity) {
        CartItem item = findByUserIdAndProductId(userId, productId);
        if (item != null) {
            updateQuantity(item.getId(), quantity);
        }
    }

    @Override
    public void removeFromCart(int userId, int productId) {
        CartItem item = findByUserIdAndProductId(userId, productId);
        if (item != null) {
            removeFromCart(item.getId());
        }
    }
}
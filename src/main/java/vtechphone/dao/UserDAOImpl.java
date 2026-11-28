package vtechphone.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import vtechphone.entity.User;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public User create(User entity) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public void update(User entity) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    // 🔥 QUAN TRỌNG: Hàm xóa User
    @Override
    public void delete(User entity) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.contains(entity) ? entity : em.merge(entity));
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public List<User> findAll() {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            return em.createQuery("SELECT o FROM User o ORDER BY o.userId ASC", User.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public User findById(Integer id) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public User findByEmail(String email) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            var jpql = "SELECT o FROM User o WHERE o.email = ?1";
            var query = em.createQuery(jpql, User.class);
            query.setParameter(1, email);
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public User findByPhone(String phone) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            var jpql = "SELECT o FROM User o WHERE o.phone = ?1";
            var query = em.createQuery(jpql, User.class);
            query.setParameter(1, phone);
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public User findByEmailOrPhone(String username) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            var jpql = "SELECT o FROM User o WHERE o.email = ?1 OR o.phone = ?1";
            var query = em.createQuery(jpql, User.class);
            query.setParameter(1, username);
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }
}
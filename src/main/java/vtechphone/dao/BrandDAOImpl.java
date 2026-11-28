package vtechphone.dao;

import jakarta.persistence.EntityManager;
import vtechphone.entity.Brand;
import java.util.List;

public class BrandDAOImpl implements BrandDAO {

    @Override
    public Brand create(Brand entity) {
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
    public void update(Brand entity) {
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

    @Override
    public void delete(Brand entity) {
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
    public List<Brand> findAll() {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            return em.createQuery("SELECT b FROM Brand b ORDER BY b.brandId ASC", Brand.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Brand findById(Integer id) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            return em.find(Brand.class, id);
        } finally {
            em.close();
        }
    }
}
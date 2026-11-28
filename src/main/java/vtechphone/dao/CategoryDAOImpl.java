package vtechphone.dao;
import jakarta.persistence.EntityManager;
import vtechphone.entity.Category;
import java.util.List;

public class CategoryDAOImpl implements CategoryDAO {
    @Override
    public Category create(Category entity) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try { em.getTransaction().begin(); em.persist(entity); em.getTransaction().commit(); return entity; } 
        catch(RuntimeException ex) { if(em.getTransaction().isActive()) em.getTransaction().rollback(); throw ex; } 
        finally { em.close(); }
    }
    @Override public void update(Category entity) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try { em.getTransaction().begin(); em.merge(entity); em.getTransaction().commit(); } 
        catch(RuntimeException ex) { if(em.getTransaction().isActive()) em.getTransaction().rollback(); throw ex; } 
        finally { em.close(); }
    }
    @Override public void delete(Category entity) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try { em.getTransaction().begin(); em.remove(em.contains(entity) ? entity : em.merge(entity)); em.getTransaction().commit(); } 
        catch(RuntimeException ex) { if(em.getTransaction().isActive()) em.getTransaction().rollback(); throw ex; } 
        finally { em.close(); }
    }
    @Override public List<Category> findAll() {
        EntityManager em = VTechPhonePU.getEntityManager();
        try { return em.createQuery("SELECT c FROM Category c ORDER BY c.categoryId ASC", Category.class).getResultList(); } 
        finally { em.close(); }
    }
    @Override public Category findById(Integer id) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try { return em.find(Category.class, id); } 
        finally { em.close(); }
    }
}
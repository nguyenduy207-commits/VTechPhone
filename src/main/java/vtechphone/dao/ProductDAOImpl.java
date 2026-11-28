package vtechphone.dao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import vtechphone.entity.Brand;
import vtechphone.entity.Category;
import vtechphone.entity.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {

    @Override
    public Product create(Product entity) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally { em.close(); }
    }

    @Override
    public void update(Product entity) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally { em.close(); }
    }

    @Override
    public void delete(Product entity) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.contains(entity) ? entity : em.merge(entity));
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally { em.close(); }
    }

    @Override
    public List<Product> findAll() {
        return findAll(1, 100); // Default lấy 100 sản phẩm nếu không truyền param
    }

    @Override
    public List<Product> findAll(int page, int size) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            int offset = (page - 1) * size;
            // 🔥 Dùng DISTINCT và JOIN FETCH để lấy dữ liệu chính xác, an toàn với phân trang
            return em.createQuery("SELECT DISTINCT p FROM Product p JOIN FETCH p.brand JOIN FETCH p.category ORDER BY p.productId ASC", Product.class)
                    .setFirstResult(offset)
                    .setMaxResults(size)
                    .getResultList();
        } finally { em.close(); }
    }

    @Override
    public Product findById(Integer id) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p JOIN FETCH p.brand JOIN FETCH p.category WHERE p.productId = :id", Product.class)
                     .setParameter("id", id).getSingleResult();
        } catch (Exception ex) {
            return null;
        } finally { em.close(); }
    }

    @Override
    public List<Product> search(String keyword, Integer categoryId, Integer brandId, Double minPrice, Double maxPrice, int page, int size) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            int offset = (page - 1) * size;
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Product> query = cb.createQuery(Product.class);
            Root<Product> root = query.from(Product.class);
            root.fetch("brand"); root.fetch("category");
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
            }
            if (categoryId != null) {
                Category c = new Category(); c.setCategoryId(categoryId);
                predicates.add(cb.equal(root.get("category"), c));
            }
            if (brandId != null) {
                Brand b = new Brand(); b.setBrandId(brandId);
                predicates.add(cb.equal(root.get("brand"), b));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            query.where(predicates.toArray(new Predicate[0]));
            return em.createQuery(query)
                    .setFirstResult(offset)
                    .setMaxResults(size)
                    .getResultList();
        } finally { em.close(); }
    }

    @Override
    public long countAll() {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(p) FROM Product p", Long.class).getSingleResult();
        } finally { em.close(); }
    }

    @Override
    public long countSearch(String keyword, Integer categoryId, Integer brandId, Double minPrice, Double maxPrice) {
        EntityManager em = VTechPhonePU.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> query = cb.createQuery(Long.class);
            Root<Product> root = query.from(Product.class);
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
            }
            if (categoryId != null) {
                Category c = new Category(); c.setCategoryId(categoryId);
                predicates.add(cb.equal(root.get("category"), c));
            }
            if (brandId != null) {
                Brand b = new Brand(); b.setBrandId(brandId);
                predicates.add(cb.equal(root.get("brand"), b));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            query.select(cb.count(root));
            query.where(predicates.toArray(new Predicate[0]));
            return em.createQuery(query).getSingleResult();
        } finally { em.close(); }
    }
}
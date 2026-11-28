package vtechphone.dao;
import vtechphone.entity.Category;
import java.util.List;

public interface CategoryDAO {
    Category create(Category entity);
    void update(Category entity);
    void delete(Category entity);
    List<Category> findAll();
    Category findById(Integer id);
}
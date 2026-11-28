package vtechphone.service;
import vtechphone.entity.Category;
import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);
    void updateCategory(Category category);
    void deleteCategory(Integer id);
    List<Category> getAllCategories();
    Category getCategoryById(Integer id);
}
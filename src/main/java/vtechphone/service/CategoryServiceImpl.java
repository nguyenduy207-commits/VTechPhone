package vtechphone.service;

import vtechphone.dao.CategoryDAO;
import vtechphone.dao.CategoryDAOImpl;
import vtechphone.entity.Category;
import java.util.List;

public class CategoryServiceImpl implements CategoryService {
    private CategoryDAO categoryDAO = new CategoryDAOImpl();

    @Override
    public Category createCategory(Category category) {
        return categoryDAO.create(category);
    }

    @Override
    public void updateCategory(Category category) {
        categoryDAO.update(category);
    }

    @Override
    public void deleteCategory(Integer id) {
        Category category = categoryDAO.findById(id);
        if (category != null) {
            categoryDAO.delete(category);
        }
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    @Override
    public Category getCategoryById(Integer id) {
        return categoryDAO.findById(id);
    }
}
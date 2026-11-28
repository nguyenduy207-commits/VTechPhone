package vtechphone.service;

import vtechphone.dao.ProductDAO;
import vtechphone.dao.ProductDAOImpl;
import vtechphone.entity.Product;
import java.util.List;

public class ProductServiceImpl implements ProductService {
    ProductDAO productDAO = new ProductDAOImpl();

    @Override
    public Product createProduct(Product product) {
        if (product.getStock() == null) product.setStock(0);
        return productDAO.create(product);
    }

    @Override
    public void updateProduct(Product product) {
        productDAO.update(product);
    }

    @Override
    public void deleteProduct(Integer id) {
        Product p = productDAO.findById(id);
        if (p != null) productDAO.delete(p);
    }

    @Override
    public List<Product> getAllProducts() {
        return productDAO.findAll(1, 100); // fallback
    }

    @Override
    public List<Product> getAllProducts(int page, int size) {
        return productDAO.findAll(page, size);
    }

    @Override
    public Product getProductById(Integer id) {
        return productDAO.findById(id);
    }

    @Override
    public List<Product> searchProducts(String keyword, Integer categoryId, Integer brandId, Double minPrice, Double maxPrice) {
        return productDAO.search(keyword, categoryId, brandId, minPrice, maxPrice, 1, 100);
    }

    @Override
    public List<Product> searchProducts(String keyword, Integer categoryId, Integer brandId, Double minPrice, Double maxPrice, int page, int size) {
        return productDAO.search(keyword, categoryId, brandId, minPrice, maxPrice, page, size);
    }

    @Override
    public long countAllProducts() {
        return productDAO.countAll();
    }

    @Override
    public long countSearchProducts(String keyword, Integer categoryId, Integer brandId, Double minPrice, Double maxPrice) {
        return productDAO.countSearch(keyword, categoryId, brandId, minPrice, maxPrice);
    }
}
package vtechphone.service;
import vtechphone.entity.Product;
import java.util.List;

public interface ProductService {
    Product createProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(Integer id);
    
    List<Product> getAllProducts(); // giữ nguyên
    List<Product> getAllProducts(int page, int size); // Hàm mới
    Product getProductById(Integer id);
    
    List<Product> searchProducts(String keyword, Integer categoryId, Integer brandId, Double minPrice, Double maxPrice);
    List<Product> searchProducts(String keyword, Integer categoryId, Integer brandId, Double minPrice, Double maxPrice, int page, int size);
    
    long countAllProducts();
    long countSearchProducts(String keyword, Integer categoryId, Integer brandId, Double minPrice, Double maxPrice);
}
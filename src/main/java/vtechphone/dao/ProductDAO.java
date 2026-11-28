package vtechphone.dao;
import vtechphone.entity.Product;
import java.util.List;

public interface ProductDAO {
    Product create(Product entity);
    void update(Product entity);
    void delete(Product entity);
    
    // Các hàm lấy danh sách có phân trang
    List<Product> findAll(int page, int size);
    List<Product> findAll(); // Giữ lại để tương thích nếu cần
    Product findById(Integer id);
    
    // Tìm kiếm và lọc sản phẩm có phân trang
    List<Product> search(String keyword, Integer categoryId, Integer brandId, Double minPrice, Double maxPrice, int page, int size);
    // Hàm đếm tổng số sản phẩm để tính số trang
    long countAll();
    long countSearch(String keyword, Integer categoryId, Integer brandId, Double minPrice, Double maxPrice);
}
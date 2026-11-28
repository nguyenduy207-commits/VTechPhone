package vtechphone.dao;
import vtechphone.entity.Brand;
import java.util.List;

public interface BrandDAO {
    Brand create(Brand entity);
    void update(Brand entity);
    void delete(Brand entity);
    List<Brand> findAll();
    Brand findById(Integer id);
}
package vtechphone.service;
import vtechphone.entity.Brand;
import java.util.List;

public interface BrandService {
    Brand createBrand(Brand brand);
    void updateBrand(Brand brand);
    void deleteBrand(Integer id);
    List<Brand> getAllBrands();
    Brand getBrandById(Integer id);
}
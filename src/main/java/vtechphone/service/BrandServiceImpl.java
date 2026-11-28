package vtechphone.service;

import vtechphone.dao.BrandDAO;
import vtechphone.dao.BrandDAOImpl;
import vtechphone.entity.Brand;
import java.util.List;

public class BrandServiceImpl implements BrandService {
    BrandDAO brandDAO = new BrandDAOImpl();

    @Override
    public Brand createBrand(Brand brand) {
        return brandDAO.create(brand);
    }

    @Override
    public void updateBrand(Brand brand) {
        brandDAO.update(brand);
    }

    @Override
    public void deleteBrand(Integer id) {
        Brand b = brandDAO.findById(id);
        if (b != null) brandDAO.delete(b);
    }

    @Override
    public List<Brand> getAllBrands() {
        return brandDAO.findAll();
    }

    @Override
    public Brand getBrandById(Integer id) {
        return brandDAO.findById(id);
    }
}
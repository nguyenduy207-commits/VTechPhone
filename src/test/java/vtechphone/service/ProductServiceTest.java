package vtechphone.service;

import java.lang.reflect.Field;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

import vtechphone.dao.ProductDAO;
import vtechphone.entity.Product;

public class ProductServiceTest {
    private ProductServiceImpl service;
    private ProductDAO dao;

    @BeforeMethod
    public void setUp() throws Exception {
        service = new ProductServiceImpl();
        dao = mock(ProductDAO.class);
        Field field = ProductServiceImpl.class.getDeclaredField("productDAO");
        field.setAccessible(true);
        field.set(service, dao);
    }

    @Test
    public void createShouldSetNullStockToZero() {
        Product product = new Product();
        product.setStock(null);
        when(dao.create(product)).thenReturn(product);

        Product result = service.createProduct(product);

        Assert.assertEquals(result.getStock().intValue(), 0);
        verify(dao).create(product);
    }

    @Test
    public void createShouldKeepExistingStock() {
        Product product = new Product();
        product.setStock(15);
        when(dao.create(product)).thenReturn(product);

        service.createProduct(product);

        Assert.assertEquals(product.getStock().intValue(), 15);
    }

    @Test
    public void getProductByIdShouldReturnProduct() {
        Product product = new Product();
        when(dao.findById(5)).thenReturn(product);
        Assert.assertSame(service.getProductById(5), product);
    }

    @Test
    public void getAllProductsShouldUseRequestedPageAndSize() {
        when(dao.findAll(2, 10)).thenReturn(List.of(new Product()));
        Assert.assertEquals(service.getAllProducts(2, 10).size(), 1);
        verify(dao).findAll(2, 10);
    }

    @Test
    public void searchShouldPassFiltersToDao() {
        when(dao.search("iphone", 1, 2, 100.0, 500.0, 1, 20))
                .thenReturn(List.of(new Product()));

        List<Product> result = service.searchProducts(
                "iphone", 1, 2, 100.0, 500.0, 1, 20);

        Assert.assertEquals(result.size(), 1);
        verify(dao).search("iphone", 1, 2, 100.0, 500.0, 1, 20);
    }

    @Test
    public void deleteShouldDeleteOnlyWhenProductExists() {
        Product product = new Product();
        when(dao.findById(9)).thenReturn(product);

        service.deleteProduct(9);

        verify(dao).delete(product);
    }

    @Test
    public void deleteShouldNotCallDaoDeleteWhenMissing() {
        when(dao.findById(9)).thenReturn(null);
        service.deleteProduct(9);
        verify(dao, never()).delete(any());
    }

    @Test
    public void countAllShouldReturnDaoCount() {
        when(dao.countAll()).thenReturn(25L);
        Assert.assertEquals(service.countAllProducts(), 25L);
    }
}

package no.kristiania.product;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateProductTest {
 private static ProductCategoryDao productCategoryDao;


    @BeforeAll
    static void initCategory() throws SQLException {
        productCategoryDao = new ProductCategoryDao(TestData.createDataSource());
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryName("Random category");
        ProductCategory productCategoryTwo = new ProductCategory();
        productCategoryTwo.setCategoryName("Another one");
        productCategoryDao.save(productCategory);
        productCategoryDao.save(productCategoryTwo);
    }


    @Test
    void shouldListProduct() throws SQLException {
        ProductDao productDao = new ProductDao(TestData.createDataSource());
        
        assertThat(productDao.listAll())
                .extracting(Product::getProductDescription)
                .contains("A somewhat pricey car");
    }

    @Test
    void shouldCreateNewProduct() throws SQLException {
        ProductDao productDao = new ProductDao(TestData.createDataSource());
        
        Product product = new Product();
        product.setProductName("BMW");
        product.setProductDescription("A somewhat pricey car");
        product.setProductPrice(100);
        product.setProductCategoryId(productCategoryDao.retrieveProductCategoryByName(TestData.pickOne("Random category","Another one")));
        productDao.save(product);
    }
}

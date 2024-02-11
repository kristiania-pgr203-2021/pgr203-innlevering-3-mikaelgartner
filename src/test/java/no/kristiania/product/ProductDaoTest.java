package no.kristiania.product;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;

public class ProductDaoTest {
    private static ProductCategoryDao productCategoryDao = null;
    private final ProductDao dao = new ProductDao(TestData.createDataSource());

    @BeforeAll
    static void initCategory() throws SQLException {
        productCategoryDao = new ProductCategoryDao(TestData.createDataSource());
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryName("electric");
        ProductCategory productCategoryTwo = new ProductCategory();
        productCategoryTwo.setCategoryName("diesel");
        ProductCategory productCategoryThree = new ProductCategory();
        productCategoryThree.setCategoryName("petrol");

        productCategoryDao.save(productCategory);
        productCategoryDao.save(productCategoryTwo);
        productCategoryDao.save(productCategoryThree);
    }


    @Test
    void shouldRetrieveSavedProduct() throws SQLException {
        Product product = exampleProduct();
        product.setProductCategoryId(productCategoryDao.retrieveProductCategoryByName("electric"));
        dao.save(product);
        assertThat(dao.retrieve(product.getId()))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(product);
    }

    @Test
    void shouldListProducts() throws SQLException {
        Product product = exampleProduct();
        product.setProductCategoryId(productCategoryDao.retrieveProductCategoryByName("electric"));
        dao.save(product);

        Product anotherProduct = exampleProduct();
        anotherProduct.setProductCategoryId(productCategoryDao.retrieveProductCategoryByName("diesel"));
        dao.save(anotherProduct);


        assertThat(dao.listAll())
                .extracting(Product::getId)
                .contains(product.getId(), anotherProduct.getId());
    }

/*
    @Test
    void shouldListProductsByCategory() throws SQLException {
        Product matchingProduct = exampleProduct();
        matchingProduct.setCategoryId(categoryDao.retrieveCategoryIdByName("electric"));
        dao.save(matchingProduct);

        Product anotherMatchingProduct = exampleProduct();
        anotherMatchingProduct.setCategoryId(categoryDao.retrieveCategoryIdByName("electric"));
        dao.save(anotherMatchingProduct);

        Product nonMatchingProduct = exampleProduct();
        nonMatchingProduct.setCategoryId(categoryDao.retrieveCategoryIdByName("petrol"));
        dao.save(nonMatchingProduct);


        assertThat(dao.listProductsByCategory(matchingProduct.getCategoryId()))
                .extracting(Product::getId)
                .contains(matchingProduct.getCategoryId(), anotherMatchingProduct.getCategoryId())
                .doesNotContain(nonMatchingProduct.getCategoryId());
    }

 */

    private Product exampleProduct() {
        Product product = new Product();
        product.setProductName(TestData.pickOne("BMW", "Ford", "Hyundai", "Ferrari", "Lotus", "Volvo"));
        product.setProductDescription(TestData.pickOne("Extremely reliable car", "the one with the best mileage", "top seller across the globe"));
        product.setProductPrice(Integer.parseInt(TestData.pickOne("500000", "200000", "600000", "7500000", "3500000")));
        return product;
    }
}

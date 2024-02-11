package no.kristiania.product;


import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductCategoryDaoTest {

    private final ProductCategoryDao dao = new ProductCategoryDao(TestData.createDataSource());

    @Test
    void shouldListSavedRoles() throws SQLException {
        String productCategoryName = "Category-" + UUID.randomUUID();
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryName(productCategoryName);
        dao.save(productCategory);


        String categoryNameTwo = "Category-" + UUID.randomUUID();
        ProductCategory productCategoryTwo = new ProductCategory();
        productCategoryTwo.setCategoryName(categoryNameTwo);
        dao.save(productCategoryTwo);


        assertThat(dao.listAll())
                .extracting(ProductCategory::getCategoryName)
                .contains(productCategory.getCategoryName(), productCategoryTwo.getCategoryName());
    }

    @Test
    void shouldRetrieveSavedCategory() throws SQLException {
        ProductCategory productCategory = exampleCategory();

        assertThat(dao.retrieve(productCategory.getId()))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(productCategory);
    }

    @Test
    void shouldRetrieveCategoryIdByName() throws SQLException {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryName("BMW");
        dao.save(productCategory);

       Long categoryId = dao.retrieveProductCategoryByName(productCategory.getCategoryName());

        assertThat(dao.retrieve(categoryId))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(productCategory);
    }

    private ProductCategory exampleCategory() throws SQLException {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryName("Lotus");
        dao.save(productCategory);
        return productCategory;
    }

}

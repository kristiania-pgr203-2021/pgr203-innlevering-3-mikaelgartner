package no.kristiania.product;


import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class ProductCategoryDao extends AbstractDao<ProductCategory> {
    public ProductCategoryDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected ProductCategory rowToObject(ResultSet rs) throws SQLException {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryName(rs.getString("category_name"));
        productCategory.setId(rs.getLong("id"));
        return productCategory;
    }

    public void save(ProductCategory productCategory) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into category (category_name) values (?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, productCategory.getCategoryName());
                statement.executeUpdate();
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    productCategory.setId(rs.getLong("id"));
                }
            }
        }
    }

    protected Long retrieveProductCategoryByName(String name) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM category WHERE category_name = ?")) {
                statement.setString(1, name);
                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();
                    return rs.getLong(1);
                }
            }
        }
    }

    @Override
    public List<ProductCategory> listAll() throws SQLException {
        return super.listAll("SELECT * FROM category");
    }

    
    public ProductCategory retrieve(long id) throws SQLException {
        return super.retrieve("SELECT * FROM category WHERE id =  ?", id);
    }
}

package no.kristiania.product;

import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;

public class ProductsServer {
    private static final Logger logger = LoggerFactory.getLogger(ProductsServer.class);

    public static void main(String[] args) throws IOException, SQLException {


        logger.info("Started server for you");
        DataSource dataSource = createDataSource();
        ProductCategoryDao productCategoryDao = new ProductCategoryDao(dataSource);
        ProductDao productDao = new ProductDao(dataSource);
        Product product = new Product();
        ProductCategory productCategory = new ProductCategory();

        Scanner scanner = new Scanner(System.in);


        showAlternatives();
        String input = scanner.nextLine();
        while (!input.equals("!q")) {

            switch (input) {
                case "01":
                    String result;
                    System.out.println("Enter a product name:");
                    result = scanner.nextLine();
                    product.setProductName(result);
                    System.out.println("Add description to product:");
                    result = scanner.nextLine();
                    product.setProductDescription(result);
                    System.out.println("Enter a product price:");
                    result = scanner.nextLine();
                    product.setProductPrice(Integer.parseInt(result));

                    chooseCategory(productDao, product, productCategoryDao, result, scanner);
                    input = showAlternatives(scanner);
                    break;
                case "02":
                    System.out.println("Enter a category name:");
                    result = scanner.nextLine();
                    productCategory.setCategoryName(result);
                    productCategoryDao.save(productCategory);
                    System.out.println("Saved category successfully " + result);
                    input = showAlternatives(scanner);
                    break;
                case "03":
                    List<ProductCategory> categories = productCategoryDao.listAll();
                    for (ProductCategory c : categories) System.out.println(c.getCategoryName());
                    System.out.println("--------------------");
                    System.out.println();
                    input = showAlternatives(scanner);
                    break;
                case "04":
                    List<Product> productList = productDao.listAll();
                    for (Product p : productList) {
                        System.out.println(p.getProductName());
                    }
                    System.out.println("--------------------");
                    System.out.println(" ");
                    input = showAlternatives(scanner);
                    break;
                case "05":
                    String userInput;
                    System.out.println("Choose a category: ");
                    List<ProductCategory> productCategoryList = productCategoryDao.listAll();
                    for (ProductCategory c : productCategoryList) System.out.println(c.getCategoryName());
                    System.out.println("--------------------");
                    userInput = scanner.nextLine();
                    List<Product> matchingProducts = productDao.listProductsByCategory(productCategoryDao.retrieveProductCategoryByName(userInput));
                    for (Product p : matchingProducts) System.out.println(p.getProductName());
                    System.out.println("--------------------");
                    input = showAlternatives(scanner);
                    break;
                default:
                    input = showAlternatives(scanner);
                    break;
            }
        }
    }

    private static void chooseCategory(ProductDao productDao, Product product, ProductCategoryDao productCategoryDao, String input, Scanner sc) throws SQLException {

        if (productCategoryDao.listAll().isEmpty()) {
            addCategory(product, sc, productCategoryDao, productDao);
        } else {
            System.out.println("Choose category from the list below");
            List<ProductCategory> productCategory = productCategoryDao.listAll();
            for (ProductCategory c : productCategory) System.out.println(c.getCategoryName());
            input = sc.nextLine();
            product.setProductCategoryId(productCategoryDao.retrieveProductCategoryByName(input));
            productDao.save(product);
            logger.info("Saved product: " + product.getProductName());
            System.out.println("--------------------");
            System.out.println(" ");
        }
    }

    private static void addCategory(Product product, Scanner scanner, ProductCategoryDao productCategoryDao, ProductDao productDao) throws SQLException {
        String result;

        System.out.println("Enter a category name:");
        result = scanner.nextLine();
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryName(result);
        productCategoryDao.save(productCategory);
        product.setProductCategoryId(productCategoryDao.retrieveProductCategoryByName(result));
        productDao.save(product);
        logger.info("Saved product: " + product.getProductName());
        System.out.println("--------------------");
    }

    private static String showAlternatives(Scanner scanner) {
        System.out.println("01 Add a product");
        System.out.println("02 Add categories");
        System.out.println("03 List all categories");
        System.out.println("04 List all the products");
        System.out.println("05 List all the products by category");
        System.out.println("Write !q to quit");
        return scanner.nextLine();
    }

    private static void showAlternatives() {
        System.out.println("01 Add a product");
        System.out.println("02 Add categories");
        System.out.println("03 List all the categories");
        System.out.println("04 List all the products");
        System.out.println("05 List all the products by category");
        System.out.println("Write !q to quit");
    }

    private static DataSource createDataSource() throws IOException {
        InputStream input = new FileInputStream("src/main/resources/conf/application.properties");
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        Properties env = new Properties();

        env.load(input);

        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUser(env.getProperty("db.user"));
        dataSource.setPassword(env.getProperty("db.password"));
        logger.info("Authorizing...");

        boolean connectionEstablished = Flyway.configure().dataSource(dataSource).load().migrate().warnings.isEmpty();
        if (connectionEstablished) logger.info("Connected...");
        return dataSource;
    }
}

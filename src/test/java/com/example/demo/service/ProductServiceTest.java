package com.example.demo.service;

import com.example.demo.DTO.Product;
import com.example.demo.DTO.ProductShort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import com.example.demo.service.impl.ProductService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    private RestClient mockRestClient;

    // Make sure this points to your implementation class!
    private ProductService productService;

    @BeforeEach
    void setUp() {
        // 2. Create the deep-stubbed RestClient mock
        mockRestClient = Mockito.mock(RestClient.class, Mockito.RETURNS_DEEP_STUBS);

        // 3. Create the real service using the new constructor
        ProductService realService = new ProductService(mockRestClient);

        // 4. Manually wrap the real service in a Spy
        productService = Mockito.spy(realService);
    }

    // --- TESTS FOR BUSINESS LOGIC & FILTERING ---

    @Test
    void toSummary_withShortDescription_returnsExactDescription() {
        Product p = createDummyProizvod(1, "Laptop", "Tech", 1000.0, "Short desc");

        ProductShort summary = productService.toSummary(p);

        assertEquals("Short desc", summary.description());
        assertEquals("Laptop", summary.title());
        assertEquals("thumb.jpg", summary.picture()); // Validates mapping thumbnail -> picture
    }

    @Test
    void toSummary_withLongDescription_truncatesTo100Characters() {
        // Create a description that is 150 characters long
        String longDesc = "a".repeat(150);
        Product p = createDummyProizvod(1, "Laptop", "Tech", 1000.0, longDesc);

        ProductShort summary = productService.toSummary(p);

        assertEquals(100, summary.description().length());
        assertTrue(summary.description().endsWith("..."));
    }

    @Test
    void fetchProductsByName_withValidSearch_filtersCorrectly() {
        Product p1 = createDummyProizvod(1, "Apple iPhone", "Tech", 900.0, "Desc");
        Product p2 = createDummyProizvod(2, "Samsung Galaxy", "Tech", 800.0, "Desc");

        // We tell the Spy: "When fetchAllProducts() is called inside this class,
        // don't make an HTTP request. Just return this list."
        Mockito.doReturn(List.of(p1, p2)).when(productService).fetchAllProducts();

        List<Product> result = productService.fetchProductsByName("apple");

        assertEquals(1, result.size());
        assertEquals("Apple iPhone", result.get(0).title());
    }

    @Test
    void fetchProductsByName_withBlankSearch_returnsAll() {
        Product p1 = createDummyProizvod(1, "Apple iPhone", "Tech", 900.0, "Desc");

        Mockito.doReturn(List.of(p1)).when(productService).fetchAllProducts();

        List<Product> result = productService.fetchProductsByName("");

        assertEquals(1, result.size());
    }

    @Test
    void fetchProductsByPriceAndCategory_filtersCorrectly() {
        Product p1 = createDummyProizvod(1, "Cheap Phone", "Tech", 150.0, "Desc");
        Product p2 = createDummyProizvod(2, "Expensive Phone", "Tech", 1200.0, "Desc");
        Product p3 = createDummyProizvod(3, "Shoes", "Clothing", 100.0, "Desc");

        Mockito.doReturn(List.of(p1, p2, p3)).when(productService).fetchAllProducts();

        // Test filtering by exact category and price range
        List<Product> resultTech = productService.fetchProductsByPriceAndCategory("Tech", 100.0, 500.0);
        assertEquals(1, resultTech.size());
        assertEquals("Cheap Phone", resultTech.get(0).title());

        // Test the "all" category bypass
        List<Product> resultAll = productService.fetchProductsByPriceAndCategory("all", 0.0, 2000.0);
        assertEquals(3, resultAll.size());
    }

    // --- TESTS FOR HTTP CALLS ---

    @Test
    void fetchProduct_simulatesHttpCall() {
        // 1. Arrange: Create the deep-stub mock
        RestClient mockRestClient = Mockito.mock(RestClient.class, Mockito.RETURNS_DEEP_STUBS);

        // NO MORE REFLECTION! Just pass the mock into the constructor.
        ProductService serviceToTest = new ProductService(mockRestClient);

        Product expectedProduct = createDummyProizvod(99, "Remote API Product", "Tech", 50.0, "Desc");

        // 2. Mock Behavior
        Mockito.when(mockRestClient.get()
                        .uri("/products/99")
                        .retrieve()
                        .onStatus(Mockito.any(), Mockito.any())
                        .body(Product.class))
                .thenReturn(expectedProduct);

        // 3. Act & Assert
        Product result = serviceToTest.fetchProduct(99);

        assertEquals(99, result.id());
        assertEquals("Remote API Product", result.title());
    }


    // --- HELPER METHOD ---

    private Product createDummyProizvod(Integer id, String title, String category, Double price, String desc) {
        return new Product(
                id, title, desc, category, price,
                0.0, 5.0, 100, null, "Test Brand",
                "SKU-123", 1, null, "1 Year",
                "Ships fast", "In Stock", null,
                "30 days", 1, null, null, "thumb.jpg"
        );
    }
}
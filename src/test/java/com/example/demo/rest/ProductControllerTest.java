package com.example.demo.rest;

import com.example.demo.config.SecurityConfig;
import com.example.demo.DTO.Product;
import com.example.demo.DTO.ProductShort;
import com.example.demo.config.JwtAuthFilter;
import com.example.demo.service.ProductService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, ProductControllerTest.TestSecurityConfig.class})
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService proizvodService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @TestConfiguration
    @EnableMethodSecurity // Ensures @PreAuthorize works in this test
    static class TestSecurityConfig {
        // You don't need to put anything here, it just forces the context to load
    }

    @BeforeEach
    void setUpFilterChain() throws Exception {
        // This prevents the mocked JWT filter from returning a premature 200 OK.
        // It tells the mock to pass the request down the chain to Spring Security and the Controller.
        Mockito.doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any());

        // Note: If your JwtAuthFilter implements Filter directly instead of
        // extending OncePerRequestFilter, change `doFilterInternal` to `doFilter`.
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getProducts_asAdmin_returnsOk() throws Exception {
        Product p = createDummyProizvod(1);
        ProductShort ps = createDummySkraceno();

        Mockito.when(proizvodService.fetchAllProducts()).thenReturn(List.of(p));
        Mockito.when(proizvodService.toSummary(p)).thenReturn(ps);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getProducts_asUser_returnsForbidden() throws Exception {
        mockMvc.perform(get("/products"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getProduct_withValidId_returnsOk() throws Exception {
        Product p = createDummyProizvod(1);
        Mockito.when(proizvodService.fetchProduct(1)).thenReturn(p);

        mockMvc.perform(get("/product").param("id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getProduct_unauthenticated_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/product").param("id", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getProductsByPriceCategory_returnsFilteredList() throws Exception {
        Mockito.when(proizvodService.fetchProductsByPriceAndCategory(
                anyString(), anyDouble(), anyDouble()
        )).thenReturn(List.of(createDummyProizvod(1)));

        mockMvc.perform(get("/products/filter")
                        .param("lowerPrice", "10.0")
                        .param("higherPrice", "50.0")
                        .param("category", "Tech"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getProductsByName_asUser_returnsOk() throws Exception {
        Mockito.when(proizvodService.fetchProductsByName("laptop"))
                .thenReturn(List.of(createDummyProizvod(1)));

        mockMvc.perform(get("/products/search")
                        .param("searchText", "laptop"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getProductsByName_asAdmin_returnsForbidden() throws Exception {
        mockMvc.perform(get("/products/search")
                        .param("searchText", "laptop"))
                .andExpect(status().isForbidden());
    }

    // --- HELPER METHODS ---

    private Product createDummyProizvod(Integer id) {
        return new Product(
                id, "Test Title", "Test Description", "test-category", 99.99,
                0.0, 5.0, 100, null, "Test Brand",
                "SKU-123", 1, null, "1 Year",
                "Ships fast", "In Stock", null,
                "30 days", 1, null, null, "thumb.jpg"
        );
    }

    private ProductShort createDummySkraceno() {
        // Adjust these arguments to match your actual ProizvodSkraceno record definition
        return new ProductShort("thumb.jpg", "Test Title", 99.99, "Test Description");
    }
}
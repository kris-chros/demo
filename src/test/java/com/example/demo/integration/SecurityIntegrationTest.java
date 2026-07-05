package com.example.demo.integration;

import com.example.demo.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // We mock the service so the controller doesn't try to make real calls
    // while we are testing the security layer.
    @MockitoBean
    private ProductService proizvodService;

    // --- ADMIN ACCESS TESTS ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_canAccessProducts() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_cannotAccessProductsSearch() throws Exception {
        // Specifically testing your rule: Admin should be forbidden from filtering
        mockMvc.perform(get("/products/search")
                        .param("searchText", "mas")).andExpect(status().isForbidden());
    }

    // --- USER ACCESS TESTS ---

    @Test
    @WithMockUser(roles = "USER")
    void user_canAccessProductById() throws Exception {
        mockMvc.perform(get("/product").param("id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void user_cannotAccessProducts() throws Exception {
        // Specifically testing your rule: User should be forbidden from listing all products
        mockMvc.perform(get("/products"))
                .andExpect(status().isForbidden());
    }
}

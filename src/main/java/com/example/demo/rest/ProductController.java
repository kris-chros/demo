package com.example.demo.rest;

import com.example.demo.DTO.Product;
import com.example.demo.DTO.ProductShort;
import com.example.demo.service.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("/")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/products") // endpoint za listu proizvoda
    public List<ProductShort> getProducts() {
        List<Product> proizvodi = productService.fetchAllProducts();
        return proizvodi.stream().map(productService::toSummary).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/product") // endpoint za jedan proizvod
    public Product getProduct(@RequestParam Integer id) {
        return productService.fetchProduct(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/products/filter") // endpoint za pretraživanje po kategoriji i cijeni
    public List<Product> getProductsByPriceCategory(
            @RequestParam(required = false) Double lowerPrice,
            @RequestParam(required = false) Double higherPrice,
            @RequestParam(required = false) String category) {

        // Provide default fallback values immediately before passing to the cached service
        Double min = (lowerPrice != null) ? lowerPrice : 0.0;
        Double max = (higherPrice != null) ? higherPrice : Double.MAX_VALUE;
        String cat = (category != null && !category.isBlank()) ? category.toLowerCase() : "all";

        return productService.fetchProductsByPriceAndCategory(cat, min, max);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/products/search") // endpoint za pretraživanje po nazivu
    public List<Product> getProductsByName(@RequestParam String searchText) {
        return productService.fetchProductsByName(searchText);
    }
}

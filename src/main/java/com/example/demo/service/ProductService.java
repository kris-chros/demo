package com.example.demo.service;

import com.example.demo.DTO.Product;
import com.example.demo.DTO.ProductShort;

import java.util.List;

public interface ProductService {
    ProductShort toSummary(Product product);
    List<Product> fetchAllProducts();
    Product fetchProduct(Integer id);
    List<Product> fetchProductsByPriceAndCategory(String category, Double lowerPrice, Double higherPrice);
    List<Product> fetchProductsByName(String searchWord);
}

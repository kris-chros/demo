package com.example.demo.service.impl;

import com.example.demo.DTO.DummyJSONResponse;
import com.example.demo.DTO.Product;
import com.example.demo.DTO.ProductShort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.example.demo.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService implements com.example.demo.service.ProductService {

    private final RestClient restClient;

    public ProductService(RestClient dummyJsonClient) {
        this.restClient = dummyJsonClient;
    }

    @Override
    public ProductShort toSummary(Product product) { // skraćeni proizvod
        String rawDesc = product.description();
        String truncatedDesc = "";

        if (rawDesc != null) {
            truncatedDesc = rawDesc.length() <= 100
                    ? rawDesc
                    : rawDesc.substring(0, 97) + "..."; // 97 chars + 3 dots = 100 total
        }

        return new ProductShort(
                product.thumbnail(),
                product.title(),
                product.price(),
                truncatedDesc
        );
    }

    @Override
    @Cacheable(value = "productsCache") // cacheing dohvat proizvoda za ponovnu uporabu
    public List<Product> fetchAllProducts() {
        DummyJSONResponse responseP = restClient.get()
                .uri("/products")
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new NotFoundException("Products not found.");
                })
                .body(DummyJSONResponse.class);
        return responseP.products();
    }

    @Override
    @Cacheable(value = "singleProductCache", key = "#id")
    public Product fetchProduct(Integer id){ // dohvat jednog proizvoda koji se onda cacheira po id-u
        return restClient.get()
                .uri("/products/" + id)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new NotFoundException("Product with ID " + id + " does not exist.");
                })
                .body(Product.class);
    }

    @Override
    @Cacheable(value = "filteredProductsCache", key = "#category + ':' + #lowerPrice + ':' + #higherPrice")
    public List<Product> fetchProductsByPriceAndCategory(String category, Double lowerPrice, Double higherPrice) {
        // dohvat proizvoda filtriranih po cijeni i kategoriji
        return fetchAllProducts().stream()
                .filter(p -> p.price() >= lowerPrice && p.price() <= higherPrice)
                .filter(p -> "all".equals(category) || p.category().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> fetchProductsByName(String searchWord) { // dohvat proizvoda po imenu
        List<Product> sviProizvodi = fetchAllProducts();
        if (searchWord == null || searchWord.isBlank()){
            return sviProizvodi;
        }
        return sviProizvodi.stream()
                .filter(p -> p.title().toLowerCase().contains(searchWord.toLowerCase()))
                .toList();
    }
}
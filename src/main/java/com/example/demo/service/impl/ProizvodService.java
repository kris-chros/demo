package com.example.demo.service.impl;

import com.example.demo.DTO.DummyJSONResponse;
import com.example.demo.DTO.Proizvod;
import com.example.demo.DTO.ProizvodSkraceno;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.example.demo.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProizvodService implements com.example.demo.service.ProizvodService {

    private final RestClient restClient;

    public ProizvodService() {
        // Initialize RestClient with the base URL of DummyJSON
        this.restClient = RestClient.builder()
                .baseUrl("https://dummyjson.com")
                .build();
    }

    @Override
    public ProizvodSkraceno toSummary(Proizvod proizvod) {
        String rawDesc = proizvod.description();
        String truncatedDesc = "";

        if (rawDesc != null) {
            truncatedDesc = rawDesc.length() <= 100
                    ? rawDesc
                    : rawDesc.substring(0, 97) + "..."; // 97 chars + 3 dots = 100 total
        }

        return new ProizvodSkraceno(
                proizvod.thumbnail(), // Mapping 'thumbnail' to 'picture'
                proizvod.title(),
                proizvod.price(),
                truncatedDesc
        );
    }

    @Override
    @Cacheable(value = "productsCache") // cacheing dohvat proizvoda za ponovnu uporabu
    public List<Proizvod> fetchAllProducts() {
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
    public Proizvod fetchProduct(Integer id){ // dohvat jednog proizvoda koji se onda cacheira po id-u
        return restClient.get()
                .uri("/products/" + id)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new NotFoundException("Product with ID " + id + " does not exist.");
                })
                .body(Proizvod.class);
    }

    @Override
    @Cacheable(value = "filteredProductsCache", key = "#category + ':' + #lowerPrice + ':' + #higherPrice")
    public List<Proizvod> fetchProductsByPriceAndCategory(String category, Double lowerPrice, Double higherPrice) {
        return fetchAllProducts().stream()
                .filter(p -> p.price() >= lowerPrice && p.price() <= higherPrice)
                .filter(p -> "all".equals(category) || p.category().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<Proizvod> fetchProductsByName(String searchWord) {
        List<Proizvod> sviProizvodi = fetchAllProducts();
        if (searchWord == null || searchWord.isBlank()){
            return sviProizvodi;
        }
        return sviProizvodi.stream()
                .filter(p -> p.title().toLowerCase().contains(searchWord.toLowerCase()))
                .toList();
    }
}
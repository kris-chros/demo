package com.example.demo.rest;

import com.example.demo.DTO.Proizvod;
import com.example.demo.DTO.ProizvodSkraceno;
import com.example.demo.service.ProizvodService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class ProizvodController {

    private final ProizvodService proizvodService;

    public ProizvodController(ProizvodService proizvodService) {
        this.proizvodService = proizvodService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/products") //endpoint za listu proizvoda
    public List<ProizvodSkraceno> getProducts() {
        List<Proizvod> proizvodi = proizvodService.fetchAllProducts();
        return proizvodi.stream().map(proizvodService::toSummary).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/product") //endpoint za jedan proizvod
    public Proizvod getProduct(@RequestParam Integer id) {
        return proizvodService.fetchProduct(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/products/filter")
    public List<Proizvod> getProductsByPriceCategory(
            @RequestParam(required = false) Double lowerPrice,
            @RequestParam(required = false) Double higherPrice,
            @RequestParam(required = false) String category) {

        // Provide default fallback values immediately before passing to the cached service
        Double min = (lowerPrice != null) ? lowerPrice : 0.0;
        Double max = (higherPrice != null) ? higherPrice : Double.MAX_VALUE;
        String cat = (category != null && !category.isBlank()) ? category.toLowerCase() : "all";

        return proizvodService.fetchProductsByPriceAndCategory(cat, min, max);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/products/search") //endpoint za pretraživanje po nazivu
    public List<Proizvod> getProductsByName(@RequestParam String searchText) {
        return proizvodService.fetchProductsByName(searchText);
    }
}

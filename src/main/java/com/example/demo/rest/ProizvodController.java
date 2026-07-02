package com.example.demo.rest;

import com.example.demo.DTO.Proizvod;
import com.example.demo.service.ProizvodService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class ProizvodController {

    private final ProizvodService proizvodService;

    // Spring automatically injects our service here
    public ProizvodController(ProizvodService proizvodService) {
        this.proizvodService = proizvodService;
    }

    @GetMapping("/products")
    public List<Proizvod> getProducts() {
        // Your middleware could filter, log, or transform the data here
        return proizvodService.fetchAllProducts();
    }
}

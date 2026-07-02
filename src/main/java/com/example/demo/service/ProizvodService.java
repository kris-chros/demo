package com.example.demo.service;

import com.example.demo.DTO.Proizvod;
import com.example.demo.DTO.ProizvodSkraceno;

import java.util.List;
import java.util.Optional;

public interface ProizvodService {
    public ProizvodSkraceno toSummary(Proizvod proizvod);
    public List<Proizvod> fetchAllProducts();
    public Proizvod fetchProduct(Integer id);
    public List<Proizvod> fetchProductsByPriceAndCategory(String category, Double lowerPrice, Double higherPrice);
    public List<Proizvod> fetchProductsByName(String searchWord);
}

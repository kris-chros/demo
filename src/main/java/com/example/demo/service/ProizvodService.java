package com.example.demo.service;

import com.example.demo.DTO.Proizvod;

import java.util.List;

public interface ProizvodService {
    public List<Proizvod> fetchAllProducts();
    public Proizvod fetchProduct(Integer id);
    public List<Proizvod> fetchProductsByPriceAndCategory(String category, Double lowerPrice, Double higherPrice);
    public List<Proizvod> fetchProductsByName(String searchWord);
}

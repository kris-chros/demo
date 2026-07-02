package com.example.demo.DTO;

import java.util.List;

public record Proizvod(
        Integer id,
        String title,
        String description,
        String category,
        Double price,
        Double discountPercentage,
        Double rating,
        Integer stock,
        List<String> tags,
        String brand,
        String sku,
        Integer weight,
        Dimensions dimensions,
        String warrantyInformation,
        String shippingInformation,
        String availabilityStatus,
        List<Review> reviews,
        String returnPolicy,
        Integer minimumOrderQuantity,
        Meta meta,
        List<String> images,
        String thumbnail
    ) {
        @Override
        public Integer id() {
            return id;
        }

        @Override
        public String title() {
            return title;
        }

        @Override
        public String description() {
            return description;
        }

        @Override
        public String category() {
            return category;
        }

        @Override
        public Double price() {
            return price;
        }

        @Override
        public String thumbnail() {
            return thumbnail;
        }
    }
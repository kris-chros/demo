package com.example.demo.DTO;

public record Proizvod(
        Integer id,
        String title,
        String description,
        Double price,
        String category,
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
    public Double price() {
        return price;
    }

    @Override
    public String category() {
        return category;
    }

    @Override
    public String thumbnail() {
        return thumbnail;
    }
}

package com.example.demo.DTO;

public record Review(
        Integer rating,
        String comment,
        String date,
        String reviewerName,
        String reviewerEmail
) {}

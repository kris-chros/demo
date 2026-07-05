package com.example.demo.DTO;

import java.util.List;

// The root wrapper object DummyJSON returns
public record DummyJSONResponse(
        List<Product> products,
        int total,
        int skip,
        int limit
) {}

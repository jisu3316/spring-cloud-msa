package com.example.catalogservice.service;

import com.example.catalogservice.doamin.CatalogEntity;

public interface CatalogService {
    Iterable<CatalogEntity> getAllCatalogs();
}

package com.tech.scaffolding.techscaffoldings.microservices.items.repository;

import com.tech.scaffolding.techscaffoldings.microservices.items.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> findAll();
    Item findById(Long id);
    Item save(Item item);
    void deleteById(Long id);
}

package com.tech.scaffolding.techscaffoldings.microservices.items.repository;

import com.tech.scaffolding.techscaffoldings.microservices.items.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}

package dev.aj.spring_6.repositories;

import dev.aj.spring_6.model.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}

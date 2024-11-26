package com.baseshelf.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryJpaRepository extends JpaRepository<Category, Long> {
}

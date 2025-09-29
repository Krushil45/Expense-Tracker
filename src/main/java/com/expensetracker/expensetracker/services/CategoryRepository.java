package com.expensetracker.expensetracker.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.expensetracker.expensetracker.models.Category;

import jakarta.validation.constraints.NotBlank;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	List<Category> findByCreatedBy(Long userId);


    // Query method for filtering categories by name with pagination
	Page<Category> findByCreatedByAndNameContainingIgnoreCase(Long createdBy, String name, Pageable pageable);

	Page<Category> findByCreatedBy(Long createdBy, Pageable pageable);


	boolean existsByName(@NotBlank(message = "Category name is mandatory") String name);
}

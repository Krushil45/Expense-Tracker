package com.expensetracker.expensetracker.services;

import com.expensetracker.expensetracker.models.Category;
import com.expensetracker.expensetracker.models.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Method to retrieve categories with optional name filtering and pagination
    public Page<Category> getCategories(String name, Pageable pageable, Long userId) {
        if (name != null && !name.isEmpty()) {
            return categoryRepository.findByCreatedByAndNameContainingIgnoreCase(userId, name, pageable);
        } else {
            return categoryRepository.findByCreatedBy(userId, pageable);
        }
    }


    public void save(Category category, user loggedInUser) {
        // Check if category with same name already exists
        if (categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Category with this name already exists.");
        }

        if (loggedInUser != null) {
            category.setCreatedBy(loggedInUser.getId());
        }

        categoryRepository.save(category);
    }


    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    // Method to retrieve all categories without pagination
    public List<Category> getCategoriesByUser(Long userId) {
        return categoryRepository.findByCreatedBy(userId);
    }

    
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
   
}

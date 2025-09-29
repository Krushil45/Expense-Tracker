package com.expensetracker.expensetracker.controllers;

import com.expensetracker.expensetracker.models.Category;
import com.expensetracker.expensetracker.services.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Endpoint to get categories with pagination and optional filtering by name
    @GetMapping("/categories")
    public String getCategories(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(required = false) String name,
                                Model model, HttpServletRequest request) {

        com.expensetracker.expensetracker.models.user loggedInUser =
                (com.expensetracker.expensetracker.models.user) request.getSession().getAttribute("loggedInUser");

        Pageable pageable = PageRequest.of(page, size);

        // Call service with user ID to fetch only that user's categories
        Page<Category> categories = categoryService.getCategories(name, pageable, loggedInUser.getId());

        model.addAttribute("requestURI", request.getRequestURI());
        model.addAttribute("categories", categories);
        return "categories";
    }

//  Creating a new category
    @GetMapping("/categories/add")
    public String showCreateCategoryForm(Model model,HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Add New Category - Expense Tracker");
        return "create-new-category";
    }

    @PostMapping("/categories/add")
    public String saveTransaction(@Valid @ModelAttribute("category") Category category,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes,
                                  HttpServletRequest request,
                                  Model model) {
        if (result.hasErrors()) {
            System.out.println("Validation errors: " + result.getAllErrors());
            model.addAttribute("requestURI", request.getRequestURI());
            return "create-new-category";
        }

        com.expensetracker.expensetracker.models.user loggedInUser =
                (com.expensetracker.expensetracker.models.user) request.getSession().getAttribute("loggedInUser");

        categoryService.save(category, loggedInUser);

        redirectAttributes.addFlashAttribute("successMessage", "Category added successfully!");
        return "redirect:/categories";
    }


    @GetMapping("/categories/edit/{id}")
    public String showCategoryEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        Category category = categoryService.getCategoryById(id);
        System.out.println("Searching for category with ID: " + id);
        if (category != null) {
            model.addAttribute("requestURI", request.getRequestURI());
            model.addAttribute("category", category);
            model.addAttribute("pageTitle", "Edit Category - Expense Tracker");
            return "edit-category"; // The edit page template
        } else {
            return "redirect:/categories"; // Redirect to list if transaction not found
        }
    }

    @PostMapping("/categories/edit/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute("category") Category category,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("requestURI", request.getRequestURI());
            return "edit-category";
        }

        category.setId(id);
        
        com.expensetracker.expensetracker.models.user loggedInUser =
                (com.expensetracker.expensetracker.models.user) request.getSession().getAttribute("loggedInUser");

        try {
            categoryService.save(category, loggedInUser);
            redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully!");
            return "redirect:/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("requestURI", request.getRequestURI());
            return "edit-category";
        }
    }

    
    @DeleteMapping("/category/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted successfully.");
    }



}
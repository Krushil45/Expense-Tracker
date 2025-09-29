package com.expensetracker.expensetracker.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.expensetracker.expensetracker.models.Category;
import com.expensetracker.expensetracker.models.Transaction;
import com.expensetracker.expensetracker.models.user;
import com.expensetracker.expensetracker.services.CategoryService;
import com.expensetracker.expensetracker.services.TransactionRepository;
import com.expensetracker.expensetracker.services.TransactionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/transactions")
public class TransactionsController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CategoryService categoryService;

    // Method to list all transactions
    @GetMapping
    public String listTransactions(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String description,
                                   @RequestParam(required = false) BigDecimal amount,
                                   @RequestParam(required = false) String amountFilter, // "=", "<=", ">="
                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                   Model model, HttpServletRequest request, HttpSession session) {

        // Retrieve logged-in user from session
        user loggedInUser = (user) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            model.addAttribute("errorMessage", "Please log in to view your transactions.");
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, size);
        // Fetch transactions for the logged-in user
        Page<Transaction> transactions = transactionService.findTransactionsByUser(loggedInUser.getId(), description, amount, amountFilter, startDate, endDate, pageable);
        
        model.addAttribute("requestURI", request.getRequestURI());
        model.addAttribute("transactions", transactions);
        model.addAttribute("description", description);
        model.addAttribute("amount", amount);
        model.addAttribute("amountFilter", amountFilter);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "transactions"; // View that displays the list of transactions
    }


    // Method to show the form to add a new transaction
    @GetMapping("/add")
    public String showTransactionForm(Model model, HttpServletRequest request, HttpSession session) {
        user loggedInUser = (user) session.getAttribute("loggedInUser");
        Long userId = loggedInUser.getId();

        List<Category> categories = categoryService.getCategoriesByUser(userId); // Fetch only user-specific categories
        model.addAttribute("categories", categories);
        model.addAttribute("requestURI", request.getRequestURI());
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("pageTitle", "Add New Transaction - Expense Tracker");

        return "add-transaction";
    }


    // Method to save the new transaction
    @PostMapping("/add")
    public String saveTransaction(@Valid @ModelAttribute("transaction") Transaction transaction,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) { // Inject the HttpSession to access the logged-in user
        if (result.hasErrors()) {
            return "add-transaction";
        }

        // Retrieve the logged-in user from the session
        user loggedInUser = (user) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            // Set the createdBy field to the logged-in user's ID
            transaction.setCreatedBy(loggedInUser.getId());
        } else {
            // Handle the case where the user is not logged in
            redirectAttributes.addFlashAttribute("errorMessage", "User not logged in.");
            return "redirect:/login"; // Redirect to the login page if the user is not logged in
        }

        // Save the transaction
        transactionRepository.save(transaction);
        redirectAttributes.addFlashAttribute("successMessage", "Transaction added successfully!");
        return "redirect:/transactions"; // Redirect to the transaction list
    }


    @GetMapping("/view/{id}")
    @ResponseBody
    public ResponseEntity<Transaction> viewTransaction(@PathVariable Long id) {
        // Fetch the transaction by ID from the repository or service layer
        Transaction transaction = transactionService.getTransactionById(id);

        // Check if transaction exists and return JSON response
        if (transaction != null) {
            return ResponseEntity.ok(transaction);
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if transaction not found
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpServletRequest request, HttpSession session) {
        Transaction transaction = transactionService.getTransactionById(id);
        model.addAttribute("requestURI", request.getRequestURI());

        if (transaction != null) {
            user loggedInUser = (user) session.getAttribute("loggedInUser");
            Long userId = loggedInUser.getId();

            // Fetch only the categories created by the logged-in user
            List<Category> categories = categoryService.getCategoriesByUser(userId);
            model.addAttribute("categories", categories);
            model.addAttribute("transaction", transaction);
            model.addAttribute("pageTitle", "Edit Transaction - Expense Tracker");

            return "edit-transaction";
        } else {
            return "redirect:/transactions";
        }
    }


    @PostMapping("/edit/{id}")
    public String updateTransaction(@PathVariable Long id,
                                    @Valid @ModelAttribute("transaction") Transaction transaction,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes,
                                    HttpServletRequest request,
                                    HttpSession session) {

        if (result.hasErrors()) {
            return "edit-transaction"; // Return to the form if there are validation errors
        }

        // Fetch the transaction by its ID
        Transaction existingTransaction = transactionService.getTransactionById(id);

        if (existingTransaction != null) {
            // Retain the original createdBy if not set (i.e., if the transaction is being updated)
            if (existingTransaction.getCreatedBy() != null) {
                transaction.setCreatedBy(existingTransaction.getCreatedBy());
            } else {
                // If createdBy is not already set (in case it's a new transaction or it was cleared),
                // set the createdBy to the logged-in user's ID
                user loggedInUser = (user) session.getAttribute("loggedInUser");
                if (loggedInUser != null) {
                    transaction.setCreatedBy(loggedInUser.getId());
                }
            }

            // Set the transaction ID to ensure we are updating the correct record
            transaction.setId(id);

            // Save the updated transaction
            transactionRepository.save(transaction);

            redirectAttributes.addFlashAttribute("successMessage", "Transaction updated successfully!");
            return "redirect:/transactions"; // Redirect back to the transaction list
        } else {
            return "redirect:/transactions"; // Redirect to list if transaction not found
        }
    }


    @DeleteMapping("/delete/{id}") // Corrected endpoint
    public ResponseEntity<String> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok("Transaction deleted successfully.");
    }



}

package com.expensetracker.expensetracker.controllers;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.expensetracker.expensetracker.models.user;
import com.expensetracker.expensetracker.services.TransactionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {
    @Autowired
    private TransactionService transactionService;
    // This method maps the root URL ("/") to the dashboard view
    @GetMapping("/")
    public String loginPage() {
    	return "login";
    }
    
    @GetMapping("/logout")
	 public String logout(HttpSession session) {
	     session.invalidate(); // Destroys the session
	     return "redirect:/";  // Redirects to the home page
	 }
    
    @GetMapping("/index")
    public String showDashboard(Model model,HttpServletRequest request) {
        // Returns the name of the HTML template (index.html) inside /templates
        model.addAttribute("pageTitle", "Dashboard - Expense Tracker");
        model.addAttribute("requestURI", request.getRequestURI()); // Add request URI to the model
        return "index";
    }

    @GetMapping("/reports")
    public String showReports(Model model, HttpServletRequest request) {
        // Returns the name of the HTML template (index.html) inside /templates
        model.addAttribute("requestURI", request.getRequestURI()); // Add request URI to the model
        model.addAttribute("pageTitle", "Reports - Expense Tracker");
        return "reports";
    }

    // Method to retrieve total expenses for monthly, weekly, and yearly via AJAX
    @GetMapping("/total-expenses")
    @ResponseBody
    public ResponseEntity<Map<String, Double>> getTotalExpenses(HttpSession session) {
        user loggedInUser = (user) session.getAttribute("loggedInUser");
        Long userId = loggedInUser.getId();  // Retrieve userId from the logged-in user
        Map<String, Double> expenses = transactionService.getTotalExpenses(userId); // Pass to service
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/total-incomes")
    @ResponseBody
    public ResponseEntity<Map<String, Double>> getTotalIncomes(HttpSession session) {
        user loggedInUser = (user) session.getAttribute("loggedInUser");
        Long userId = loggedInUser.getId(); // Get the user ID
        Map<String, Double> incomes = transactionService.getTotalIncomes(userId);
        return ResponseEntity.ok(incomes);
    }

    @GetMapping("/total-transactions")
    @ResponseBody
    public ResponseEntity<Map<String, Double>> getTotalTransactions(HttpSession session) {
        user loggedInUser = (user) session.getAttribute("loggedInUser");
        Long userId = loggedInUser.getId();
        Map<String, Double> data = transactionService.getTotalTransactions(userId);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/total-incomes-by-month")
    @ResponseBody
    public ResponseEntity<Map<String, Double>> getTotalIncomesByMonth(HttpSession session) {
        user loggedInUser = (user) session.getAttribute("loggedInUser");
        Map<String, Double> data = transactionService.getTotalIncomesByMonth(loggedInUser);
        return ResponseEntity.ok(data);
    }


    @GetMapping("/total-expenses-by-month")
    @ResponseBody
    public ResponseEntity<Map<String, Double>> getTotalExpensesByMonth(HttpSession session) {
    	user loggedInUser = (user) session.getAttribute("loggedInUser");
        Long userId = loggedInUser.getId();
        Map<String, Double> data = transactionService.getTotalExpensesByMonth(userId);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/total-transactions-by-month")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> getTotalTransactionsByMonth(HttpSession session) {
        user loggedInUser = (user) session.getAttribute("loggedInUser");

        Long userId = loggedInUser.getId();
        Map<String, Integer> data = transactionService.getTotalTransactionsByMonth(userId);
        return ResponseEntity.ok(data);
    }






}

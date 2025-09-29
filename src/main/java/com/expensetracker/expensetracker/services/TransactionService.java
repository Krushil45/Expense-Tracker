package com.expensetracker.expensetracker.services;

import com.expensetracker.expensetracker.models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.expensetracker.expensetracker.models.user;


import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Page<Transaction> findTransactionsByUser(Long userId, String description, BigDecimal amount, String amountFilter,
            LocalDate startDate, LocalDate endDate, Pageable pageable) {
return transactionRepository.findFilteredTransactionsByUser(userId, description, amount, amountFilter, startDate, endDate, pageable);
}

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    // Fetch total expenses for month, week, year
    public Map<String, Double> getTotalExpenses(Long userId) {
        Map<String, Double> totalExpenses = new HashMap<>();
        LocalDate currentDate = LocalDate.now();

        LocalDate firstDayOfMonth = currentDate.with(TemporalAdjusters.firstDayOfMonth());
        double monthlyExpense = transactionRepository.sumExpensesBetweenDates(userId, firstDayOfMonth, currentDate);
        totalExpenses.put("monthly", monthlyExpense);

        LocalDate startOfWeek = currentDate.with(DayOfWeek.MONDAY);
        double weeklyExpense = transactionRepository.sumExpensesBetweenDates(userId, startOfWeek, currentDate);
        totalExpenses.put("weekly", weeklyExpense);

        LocalDate firstDayOfYear = currentDate.with(TemporalAdjusters.firstDayOfYear());
        double yearlyExpense = transactionRepository.sumExpensesBetweenDates(userId, firstDayOfYear, currentDate);
        totalExpenses.put("yearly", yearlyExpense);

        return totalExpenses;
    }



    // Method to retrieve total incomes for the current month, week, and year
    public Map<String, Double> getTotalIncomes(Long userId) {
        Map<String, Double> totalIncomes = new HashMap<>();
        LocalDate currentDate = LocalDate.now();

        LocalDate firstDayOfMonth = currentDate.with(TemporalAdjusters.firstDayOfMonth());
        double monthlyIncome = transactionRepository.sumIncomesBetweenDates(userId, firstDayOfMonth, currentDate);
        totalIncomes.put("monthly", monthlyIncome);

        LocalDate startOfWeek = currentDate.with(DayOfWeek.MONDAY);
        double weeklyIncome = transactionRepository.sumIncomesBetweenDates(userId, startOfWeek, currentDate);
        totalIncomes.put("weekly", weeklyIncome);

        LocalDate firstDayOfYear = currentDate.with(TemporalAdjusters.firstDayOfYear());
        double yearlyIncome = transactionRepository.sumIncomesBetweenDates(userId, firstDayOfYear, currentDate);
        totalIncomes.put("yearly", yearlyIncome);

        return totalIncomes;
    }


    // Method to retrieve total incomes for the current month, week, and year
    public Map<String, Double> getTotalTransactions(Long userId) {
        Map<String, Double> totalIncomes = new HashMap<>();

        LocalDate currentDate = LocalDate.now();

        // Monthly
        LocalDate firstDayOfMonth = currentDate.with(TemporalAdjusters.firstDayOfMonth());
        double monthlyIncome = transactionRepository.countTransactionsBetweenDates(userId, firstDayOfMonth, currentDate);
        totalIncomes.put("monthly", monthlyIncome);

        // Weekly (starts Monday)
        LocalDate startOfWeek = currentDate.with(DayOfWeek.MONDAY);
        double weeklyIncome = transactionRepository.countTransactionsBetweenDates(userId, startOfWeek, currentDate);
        totalIncomes.put("weekly", weeklyIncome);

        // Yearly
        LocalDate firstDayOfYear = currentDate.with(TemporalAdjusters.firstDayOfYear());
        double yearlyIncome = transactionRepository.countTransactionsBetweenDates(userId, firstDayOfYear, currentDate);
        totalIncomes.put("yearly", yearlyIncome);

        return totalIncomes;
    }


    public Map<String, Double> getTotalIncomesByMonth(user user) {
        Map<String, Double> totalIncomesByMonth = new HashMap<>();

        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();

        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(currentYear, month);
            LocalDate firstDayOfMonth = yearMonth.atDay(1);
            LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

            // ✅ Use user.getId() here
            Double totalIncome = transactionRepository.countIncomesBetweenDates(
                user.getId(), firstDayOfMonth, lastDayOfMonth);

            totalIncomesByMonth.put(yearMonth.getMonth().name(), totalIncome != null ? totalIncome : 0.0);
        }

        return totalIncomesByMonth;
    }



    public Map<String, Double> getTotalExpensesByMonth(Long userId) {
        Map<String, Double> totalExpensesByMonth = new HashMap<>();

        // Get the current year
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();

        // Loop through each month of the current year
        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(currentYear, month);
            LocalDate firstDayOfMonth = yearMonth.atDay(1); // First day of the month
            LocalDate lastDayOfMonth = yearMonth.atEndOfMonth(); // Last day of the month

            // Calculate total expenses for the month (add userId filter)
            Double totalExpense = transactionRepository.countExpensesBetweenDates(userId, firstDayOfMonth, lastDayOfMonth);

            // If no expenses found (null), set totalExpense to 0.0
            totalExpensesByMonth.put(yearMonth.getMonth().name(), totalExpense != null ? totalExpense : 0.0);
        }

        return totalExpensesByMonth;
    }

    //
    // Method to retrieve total transactions count by month for the current year
    public Map<String, Integer> getTotalTransactionsByMonth(Long userId) {
        Map<String, Integer> totalTransactionsByMonth = new HashMap<>();

        // Get the current year
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();

        // Loop through each month of the current year
        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(currentYear, month);
            LocalDate firstDayOfMonth = yearMonth.atDay(1); // First day of the month
            LocalDate lastDayOfMonth = yearMonth.atEndOfMonth(); // Last day of the month

            // Calculate total transactions for the month (add userId filter)
            int totalTransactions = transactionRepository.countTransactionsMonth(userId, firstDayOfMonth, lastDayOfMonth);
            totalTransactionsByMonth.put(yearMonth.getMonth().name(), totalTransactions);
        }

        return totalTransactionsByMonth;
    }


    public void deleteTransaction(Long id) {
    	transactionRepository.deleteById(id);
    }
   

}

package com.expensetracker.expensetracker.services;

import com.expensetracker.expensetracker.models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.expensetracker.expensetracker.models.user;


import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	@Query("SELECT t FROM Transaction t WHERE " +
		       "t.createdBy = :userId AND " +
		       "(:description IS NULL OR t.description LIKE %:description%) AND " +
		       "(:amount IS NULL OR " +
		       " ( :amountFilter = '=' AND t.amount = :amount ) OR " +
		       " ( :amountFilter = '<=' AND t.amount <= :amount ) OR " +
		       " ( :amountFilter = '>=' AND t.amount >= :amount )) AND " +
		       "(:startDate IS NULL OR t.date >= :startDate) AND " +
		       "(:endDate IS NULL OR t.date <= :endDate)")
		Page<Transaction> findFilteredTransactionsByUser(@Param("userId") Long userId,
		                                                  @Param("description") String description,
		                                                  @Param("amount") BigDecimal amount,
		                                                  @Param("amountFilter") String amountFilter,
		                                                  @Param("startDate") LocalDate startDate,
		                                                  @Param("endDate") LocalDate endDate,
		                                                  Pageable pageable);


	@Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
		       "WHERE t.transactionType = 'EXPENSE' AND t.createdBy = :userId " +
		       "AND t.date BETWEEN :startDate AND :endDate")
		double sumExpensesBetweenDates(@Param("userId") Long userId,
		                               @Param("startDate") LocalDate startDate,
		                               @Param("endDate") LocalDate endDate);

    // For incomes
	@Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
		       "WHERE t.transactionType = 'INCOME' AND t.createdBy = :userId " +
		       "AND t.date BETWEEN :startDate AND :endDate")
		double sumIncomesBetweenDates(@Param("userId") Long userId,
		                              @Param("startDate") LocalDate startDate,
		                              @Param("endDate") LocalDate endDate);


    // Custom query to count transactions between two dates
	@Query("SELECT COUNT(t) FROM Transaction t WHERE t.createdBy = :userId AND t.date BETWEEN :startDate AND :endDate")
	Long countTransactionsBetweenDates(@Param("userId") Long userId,
	                                   @Param("startDate") LocalDate startDate,
	                                   @Param("endDate") LocalDate endDate);

	@Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
		       "WHERE t.transactionType = 'INCOME' AND t.createdBy = :userId " +
		       "AND t.date BETWEEN :startDate AND :endDate")
		Double countIncomesBetweenDates(@Param("userId") Long userId,
		                                @Param("startDate") LocalDate startDate,
		                                @Param("endDate") LocalDate endDate);





    // Custom query to count total expenses between two dates
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.createdBy = :userId AND t.transactionType = 'EXPENSE' AND t.date >= :startDate AND t.date <= :endDate")
    Double countExpensesBetweenDates(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    // Custom query to count total transactions between two dates
 // Custom query to count total transactions between two dates
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.createdBy = :userId AND t.date >= :startDate AND t.date <= :endDate")
    int countTransactionsMonth(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

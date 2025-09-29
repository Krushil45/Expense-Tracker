package com.expensetracker.expensetracker.services;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.expensetracker.expensetracker.models.user;

@Repository
public interface userRepository extends JpaRepository<user, Long> {
	 Optional<user> findByEmail(String email);
	 Optional<user> findByEmailAndPassword(String email, String password);

}

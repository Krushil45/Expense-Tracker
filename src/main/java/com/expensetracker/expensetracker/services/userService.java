package com.expensetracker.expensetracker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expensetracker.expensetracker.models.user;

@Service
public class userService {
	@Autowired
	userRepository repository;

	 public user registerUser(user user) {
	        // Check if email already exists
	        if (repository.findByEmail(user.getEmail()).isPresent()) {
	            throw new RuntimeException("Email already exists!"); // Exception will be handled in Controller
	        }
	        return repository.save(user);
	    }
}

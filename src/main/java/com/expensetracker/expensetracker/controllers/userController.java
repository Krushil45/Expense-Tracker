package com.expensetracker.expensetracker.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.expensetracker.models.user;
import com.expensetracker.expensetracker.services.userRepository;
import com.expensetracker.expensetracker.services.userService;

import jakarta.servlet.http.HttpSession;

@RestController
public class userController {
	@Autowired
	userService service;
	@Autowired
	userRepository userRepository;

	@PostMapping("/signup")
	public ResponseEntity<?> signupUser(@RequestBody user user) {
		try {
			user savedUser = service.registerUser(user);
			return ResponseEntity.ok(savedUser);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // Send error response
		}
	}

//login
	 @PostMapping("/login")
	    public String login(@RequestParam String email, @RequestParam String password, HttpSession session) {
	        Optional<user> optionalUser = userRepository.findByEmailAndPassword(email, password);
	        if (optionalUser.isPresent()) {
	            session.setAttribute("loggedInUser", optionalUser.get());
	            return "success";
	        } else {
	            return "Invalid username or password";
	        }
	    }
	 
	 @GetMapping("/current-user")
	 @ResponseBody
	 public Map<String, String> getCurrentUser(HttpSession session) {
	     user loggedInUser = (user) session.getAttribute("loggedInUser");
	     Map<String, String> response = new HashMap<>();
	     if (loggedInUser != null) {
	         response.put("name", loggedInUser.getFullName()); // or getName()
	     } else {
	         response.put("name", "Guest");
	     }
	     return response;
	 }
	 
	 

	 
	 
	 
	 
}

package com.example.adminDashboardProject.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.adminDashboardProject.entity.User;
import com.example.adminDashboardProject.entity.UserType;
import com.example.adminDashboardProject.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
	
	@Autowired
	private UserRepository userRepo;

	@PostMapping("/login")
	public ResponseEntity<?> verifyUser(@RequestBody User user) {
		User availableUser = userRepo.findByUsername(user.getUsername());
		
		// Check if user exists, is admin, and password matches
		if (availableUser == null || 
			!availableUser.getUserType().equals(UserType.ADMIN) || 
			!user.getPassword().equals(availableUser.getPassword())) {
			
			// .build() creates the actual ResponseEntity object
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials or insufficient permissions");
		}
		
		Map<String, String> response = new HashMap<>();
		response.put("username", user.getUsername());
		response.put("status", "success");
		
		return ResponseEntity.ok(response);
	}
}

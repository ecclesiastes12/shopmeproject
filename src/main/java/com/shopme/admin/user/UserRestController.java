package com.shopme.admin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {

	@Autowired
	UserService service;
	
	@PostMapping("/users/check_email")
	public String checkDuplicateEmail(@Param("email") String email) {
		//same as if else statement 
		/*
		 * if(service.isEmailUnique(email)) { return "OK"; }else { return "Duplicated";
		 * }
		 */
		return service.isEmailUnique(email) ? "OK" : "Duplicated";
	}
}

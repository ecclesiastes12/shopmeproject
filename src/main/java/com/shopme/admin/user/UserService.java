package com.shopme.admin.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
public class UserService {

	@Autowired
	UserRepository userRepo;
	
	@Autowired
	RoleRepository roleRepo;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	//list all users
	public List<User> listAll(){
		return (List<User>) userRepo.findAll();
	}
	
	//list all roles
	public List<Role> listRoles(){
		return (List<Role>) roleRepo.findAll();
	}

	//saves user
	public void save(User user) {
		encodePassword(user); //method call
		userRepo.save(user);
	}
	
	//encrypts the password
	public void encodePassword(User user) {
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
	} 
	
	//checks if email is unique
	public boolean isEmailUnique(String email) {
		User userByEmail = userRepo.getUserEmail(email);
		return userByEmail == null;
	}
}

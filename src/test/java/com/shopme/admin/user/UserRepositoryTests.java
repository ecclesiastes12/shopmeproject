package com.shopme.admin.user;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

 
	@Autowired
	UserRepository repo;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void testCreateNewUserWithOneRole()  {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User userPeter = new User("peter@gmail.com","peter12","Peter","Mensah");
		userPeter.addRole(roleAdmin);
		
		User savedUser = repo.save(userPeter);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateNewUserWithTwoRoles() {
		User userJames = new User("james@gmail.com","james12","James","Osei");
		Role roleEditor = new Role(3);
		Role roleAssistant = new Role(5);
		userJames.addRole(roleEditor);
		userJames.addRole(roleAssistant);
		
		User savedUser = repo.save(userJames);
		
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAllUsers() {
		Iterable<User> listUsers = repo.findAll();
		listUsers.forEach(user -> System.out.println(user));
	}
	
	@Test
	public void testGetUserById() {
		User userPeet = repo.findById(1).get();
		System.out.println(userPeet);
		assertThat(userPeet).isNotNull();
	}
	
	@Test
	public void testUpdateUserDetails() {
		User userPeet = repo.findById(1).get();
		userPeet.setEnabled(true);
		userPeet.setEmail("peter24@gmail.com");
		
		repo.save(userPeet);
	}
	
	@Test
	public void testUpdateUserRoles() {
		User userJames = repo.findById(2).get();
		Role roleEditor = new Role(3);
		Role roleSalesperson = new Role(2);
		
		userJames.getRoles().remove(roleEditor);
		userJames.addRole(roleSalesperson);
		
		repo.save(userJames);
	}
	
	@Test
	public void testDeleteUser() {
		Integer userId = 2;
		repo.deleteById(userId);
	}
	
	@Test
	public void testGetUserByEmail() {
		 String email = "peter24@gmail.com";
		 User user = repo.getUserEmail(email);
		 
		 assertThat(user).isNotNull();
	}
	
}

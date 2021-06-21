package com.shopme.admin.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.shopme.common.entity.User;

public interface UserRepository extends CrudRepository<User, Integer>{

	/*NB :email refers to the parameter value in @Param("email") */
	
	@Query("SELECT u FROM User u WHERE u.email = :email")
	public User getUserEmail(@Param("email") String email);
	
}

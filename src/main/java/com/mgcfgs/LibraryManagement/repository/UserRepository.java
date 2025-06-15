package com.mgcfgs.LibraryManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mgcfgs.LibraryManagement.model.RegisterUser;


@Repository
public interface UserRepository extends JpaRepository<RegisterUser, Long> {
    // Automatically finds by email
    RegisterUser findByEmail(String email);

    // Custom query for login (optional)
    @Query("SELECT u FROM RegisterUser u WHERE u.email = :email AND u.password = :password")
    RegisterUser registerUser(@Param("email") String email, @Param("password") String password);

    
}
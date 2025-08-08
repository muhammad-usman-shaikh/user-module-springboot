package com.usman.auth.user_module_springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.usman.auth.user_module_springboot.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}

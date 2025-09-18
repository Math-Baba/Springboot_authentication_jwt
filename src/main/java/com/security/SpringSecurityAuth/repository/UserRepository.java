package com.security.SpringSecurityAuth.repository;

import com.security.SpringSecurityAuth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}

package com.springboot.kakaologintest.data.repository;

import com.springboot.kakaologintest.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
     User findByEmail(String email);
     User findByPersonalEmail(String personalEmail);

}

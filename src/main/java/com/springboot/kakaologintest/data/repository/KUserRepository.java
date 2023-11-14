package com.springboot.kakaologintest.data.repository;

import com.springboot.kakaologintest.data.entity.K_User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KUserRepository extends JpaRepository<K_User,Long> {
}

package com.springboot.kakaologintest.data.repository;


import com.springboot.kakaologintest.data.entity.UniversityType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniversityTypeRepository extends JpaRepository<UniversityType, Long> {
    UniversityType getByMajor(String Major);
}

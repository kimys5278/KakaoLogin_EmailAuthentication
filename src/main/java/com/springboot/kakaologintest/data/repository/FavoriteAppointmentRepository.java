package com.springboot.kakaologintest.data.repository;


import com.springboot.kakaologintest.data.entity.FavoriteAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteAppointmentRepository extends JpaRepository<FavoriteAppointment,Long> {
}

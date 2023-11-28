package com.springboot.kakaologintest.data.repository;


import com.springboot.kakaologintest.data.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}

package com.springboot.kakaologintest.data.service;

import com.springboot.kakaologintest.data.dto.response.AllAppointmentDto;
import org.springframework.data.domain.Page;

public interface MyPageService {
    Page<AllAppointmentDto> getOwnTopThree(String token);
    Page<AllAppointmentDto> getOwnDetail(int page, int size, String token);
    Page<AllAppointmentDto> getParticipatingTopThree(String token);
    Page<AllAppointmentDto> getParticipatingDetail(int page, int size, String token);
    Page<AllAppointmentDto> getAllFavorites(int page, int size, String token);
}

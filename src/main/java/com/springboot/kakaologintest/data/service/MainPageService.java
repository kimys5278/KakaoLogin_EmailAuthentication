package com.springboot.kakaologintest.data.service;

import com.springboot.kakaologintest.data.dto.response.AllAppointmentDto;
import com.springboot.kakaologintest.data.dto.response.MainPageResponseDto;
import com.springboot.kakaologintest.data.dto.response.TopFiveAppointmentResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MainPageService {
        Page<AllAppointmentDto> getAllAppointment(int page, int size, List<String> collegeList, String sort);
        Page<MainPageResponseDto> getRightAwayAppointment();
        Page<TopFiveAppointmentResponseDto> getTopFiveAppointment();
        Page<MainPageResponseDto> getFavoriteAppointment(String token);
    }



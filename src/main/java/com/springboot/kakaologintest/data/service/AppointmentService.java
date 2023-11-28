package com.springboot.kakaologintest.data.service;


import com.springboot.kakaologintest.data.dto.AppointmentDto;
import com.springboot.kakaologintest.data.dto.UpdateAppointmentDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AppointmentService {
    AppointmentDto getAppointment(Long pid,
                                  HttpServletRequest servletRequest,
                                  HttpServletResponse servletResponse);
    void saveAppointment(AppointmentDto postDto,HttpServletRequest servletRequest,
                  HttpServletResponse servletResponse);
    void updateAppointment(UpdateAppointmentDto updateAppointmentDto,
                           HttpServletRequest servletRequest,
                           HttpServletResponse servletResponse);
    void deleteAppointment(Long pid,
                    HttpServletRequest servletRequest,
                    HttpServletResponse servletResponse) throws Exception;

}

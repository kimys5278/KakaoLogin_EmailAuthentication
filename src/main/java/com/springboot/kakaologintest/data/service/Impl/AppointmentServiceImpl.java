package com.springboot.kakaologintest.data.service.Impl;

import com.springboot.kakaologintest.data.dto.AppointmentDto;
import com.springboot.kakaologintest.data.dto.UpdateAppointmentDto;
import com.springboot.kakaologintest.data.entity.Appointment;
import com.springboot.kakaologintest.data.repository.AppointmentRepository;
import com.springboot.kakaologintest.data.service.AppointmentService;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository){
        this.appointmentRepository = appointmentRepository;
    }
    @Override
    public AppointmentDto getAppointment(Long pid, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {

        ModelMapper mapper = new ModelMapper();
        AppointmentDto appointmentDto = new AppointmentDto();

        Appointment appointment = appointmentRepository.findById(pid)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found with PID: " + pid));

        return appointmentDto;
    }

    @Override
    public void saveAppointment(AppointmentDto postDto, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {

    }

    @Override
    public void updateAppointment(UpdateAppointmentDto updateAppointmentDto, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {

    }

    @Override
    public void deleteAppointment(Long pid, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws Exception {

    }
}

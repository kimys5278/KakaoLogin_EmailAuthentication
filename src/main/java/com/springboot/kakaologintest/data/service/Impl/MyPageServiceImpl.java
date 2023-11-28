package com.springboot.kakaologintest.data.service.Impl;

import com.querydsl.jpa.impl.JPAQuery;
import com.springboot.kakaologintest.data.dto.response.AllAppointmentDto;
import com.springboot.kakaologintest.data.entity.*;
import com.springboot.kakaologintest.data.repository.AppointmentRepository;
import com.springboot.kakaologintest.data.repository.UserRepository;
import com.springboot.kakaologintest.data.service.MyPageService;
import com.springboot.kakaologintest.data.service.exception.UserNotFoundException;
import com.springboot.kakaologintest.kakaoinfo.KakaoProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MyPageServiceImpl implements MyPageService {
    private final UserRepository userRepository;
    private  UserServiceImpl userService;
    private final AppointmentRepository appointmentRepository;
    @PersistenceContext
    EntityManager entityManager;

    public MyPageServiceImpl(UserRepository userRepository, UserServiceImpl userService, AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.appointmentRepository = appointmentRepository;
    }

    private User findUser(String token){
        // 카카오 프로필 정보 가져오기
        KakaoProfile profile = userService.findProfile(token);

        User user = userRepository.findByKakaoId(profile.getId());
        if (user == null) {
            throw new UserNotFoundException("유저를 찾을 수 없습니다.");
        }
        return user;
    }

    @Override
    public Page<AllAppointmentDto> getOwnTopThree(String token) {
        User findUser = findUser(token);

        JPAQuery<Appointment> jpaQuery = new JPAQuery<>(entityManager);
        List<Appointment> appointmentList;

        appointmentList = jpaQuery.from(QAppointment.appointment)
                .where(QAppointment.appointment.pid.eq(findUser.getUid()))
                .orderBy(QAppointment.appointment.createAt.desc())
                .limit(3)
                .fetch();

        List<AllAppointmentDto> responseDto = appointmentList.stream()
                .map(appointment -> {
                    return AllAppointmentDto.builder()
                            .uid(appointment.getUid())
                            .title(appointment.getTitle())
                            .maxPeople(appointment.getMaxPeople())
                            .minPeople(appointment.getMinPeople())
                            .favoriteCount(appointment.getFavoriteCount())
                            .build();
                }).collect(Collectors.toList());

        return new PageImpl<>(responseDto);
    }

    @Override
    public Page<AllAppointmentDto> getOwnDetail(int page, int size, String token) {
        User findUser = findUser(token);

        JPAQuery<Appointment> jpaQuery = new JPAQuery<>(entityManager);
        List<Appointment> appointmentList = jpaQuery.from(QAppointment.appointment)
                .where(QAppointment.appointment.pid.eq(findUser.getUid()))
                .orderBy(QAppointment.appointment.createAt.desc())
                .offset(page * size) // 한 페이지 당 8개
                .limit(size)
                .fetch();

        List<AllAppointmentDto> responseDto = appointmentList.stream()
                .map(appointment -> {
                    return AllAppointmentDto.builder()
                            .uid(appointment.getUid())
                            .title(appointment.getTitle())
                            .maxPeople(appointment.getMaxPeople())
                            .minPeople(appointment.getMinPeople())
                            .favoriteCount(appointment.getFavoriteCount())
                            .build();
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responseDto);
    }

    @Override
    public Page<AllAppointmentDto> getParticipatingTopThree(String token) {
        User findUser = findUser(token);

        JPAQuery<Member> jpaQuery = new JPAQuery<>(entityManager);
        List<Member> memberList = jpaQuery.from(QMember.member)
                .where(QMember.member.user().uid.eq(findUser.getUid())) // '내가 참여하고 있는 약속들' 조건 추가
                .orderBy(QMember.member.createAt.desc())
                .limit(3)
                .fetch();

        List<AllAppointmentDto> responseDto = memberList.stream()
                .map(member -> {
                    Appointment appointment = appointmentRepository.getById(member.getAppointment().getUid());
                    // 내가 속해 있는 약속
                    return new AllAppointmentDto().builder()
                            .uid(appointment.getUid())
                            .title(appointment.getTitle())
                            .maxPeople(appointment.getMaxPeople())
                            .minPeople(appointment.getMinPeople())
                            .favoriteCount(appointment.getFavoriteCount())
                            .build();
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responseDto);
    }

    @Override
    public Page<AllAppointmentDto> getParticipatingDetail(int page, int size, String token) {
        User findUser = findUser(token);

        JPAQuery<Member> jpaQuery = new JPAQuery<>(entityManager);
        List<Member> memberList = jpaQuery.from(QMember.member)
                .where(QMember.member.user().uid.eq(findUser.getUid()))
                .orderBy(QMember.member.createAt.desc())
                .offset(page * size)
                .limit(size)
                .fetch();

        List<AllAppointmentDto> responseDto = memberList.stream()
                .map(member -> {
                    Appointment appointment = appointmentRepository.getById(member.getAppointment().getUid());
                    return new AllAppointmentDto().builder()
                            .uid(appointment.getUid())
                            .title(appointment.getTitle())
                            .maxPeople(appointment.getMaxPeople())
                            .minPeople(appointment.getMinPeople())
                            .favoriteCount(appointment.getFavoriteCount())
                            .build();
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responseDto);
    }

    @Override
    public Page<AllAppointmentDto> getAllFavorites(int page, int size, String token) {
        User findUser = findUser(token);

        JPAQuery<FavoriteAppointment> jpaQuery = new JPAQuery<>(entityManager);
        List<FavoriteAppointment> list = jpaQuery.from(QFavoriteAppointment.favoriteAppointment)
                .where(QFavoriteAppointment.favoriteAppointment.user().eq(findUser))
                .orderBy(QFavoriteAppointment.favoriteAppointment.createAt.desc())
                .offset(page * size)
                .limit(size)
                .fetch();

        List<AllAppointmentDto> responseDto = list.stream()
                .map(favoriteAppointment -> {
                    Appointment appointment = appointmentRepository.getById(favoriteAppointment.getAppointmentId());

                    return new AllAppointmentDto().builder()
                            .uid(appointment.getUid())
                            .title(appointment.getTitle())
                            .maxPeople(appointment.getMaxPeople())
                            .minPeople(appointment.getMinPeople())
                            .favoriteCount(appointment.getFavoriteCount())
                            .build();
                }).collect(Collectors.toList());
        return new PageImpl<>(responseDto);
    }
}

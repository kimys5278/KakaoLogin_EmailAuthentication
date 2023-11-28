package com.springboot.kakaologintest.data.service.Impl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.springboot.kakaologintest.data.dto.response.AllAppointmentDto;
import com.springboot.kakaologintest.data.dto.response.MainPageResponseDto;
import com.springboot.kakaologintest.data.dto.response.TopFiveAppointmentResponseDto;
import com.springboot.kakaologintest.data.entity.*;
import com.springboot.kakaologintest.data.repository.AppointmentRepository;
import com.springboot.kakaologintest.data.repository.ConfirmationRepository;
import com.springboot.kakaologintest.data.repository.UniversityTypeRepository;
import com.springboot.kakaologintest.data.repository.UserRepository;
import com.springboot.kakaologintest.data.service.MainPageService;
import com.springboot.kakaologintest.data.service.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MainPageServiceImpl implements MainPageService {

    private final UserRepository userRepository;
    private final ConfirmationRepository confirmationRepository;
    private final AppointmentRepository appointmentRepository;
    private final UniversityTypeRepository universityTypeRepository;
    private final UserServiceImpl userServiceImpl;
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public MainPageServiceImpl(ConfirmationRepository confirmationRepository, UserRepository userRepository, AppointmentRepository appointmentRepository, UniversityTypeRepository universityTypeRepository, UserServiceImpl userServiceImpl) {
        this.userRepository = userRepository;

        this.appointmentRepository = appointmentRepository;
        this.universityTypeRepository = universityTypeRepository;
        this.confirmationRepository = confirmationRepository;
        this.userServiceImpl = userServiceImpl;
    }

    /**
     * @return 현재 year+'년'+month+'월'+day+'일'
     */
    private String createCompareTime(){
        LocalDate localDateTime = LocalDateTime.now().toLocalDate();
        String year = String.valueOf(localDateTime.getYear());
        String month = String.valueOf(localDateTime.getMonthValue());
        String day = String.valueOf(localDateTime.getDayOfMonth());

        return year+'년'+month+'월'+day+'일';
    }

    // 약속의 인원 초과 여부 확인 메서드
    public long validateNumberOfMember(Appointment appointment){
        JPAQuery<Long> memberCountSubQuery = new JPAQuery<>(entityManager);

        memberCountSubQuery.select(QMember.member.count())
                .from(QMember.member)
                .where(QMember.member.appointment().eq(QAppointment.appointment)).fetchOne();

        long validateMax = memberCountSubQuery.clone()
                .where(QMember.member.appointment().eq(appointment))
                .fetchOne();

        return validateMax;
    }

    public int validateExpireDate(Appointment appointment){
        return appointment.getExpiredDate().compareTo(createCompareTime());
    }

    @Override
    public Page<AllAppointmentDto> getAllAppointment(int page, int size, List<String> collegeList, String sort) {
        log.info("[getAllAppointment] 모든 약속들 반환");
        JPAQuery<Appointment> appointmentJPAQuery = new JPAQuery<>(entityManager);

        List<Appointment> appointments;

        BooleanExpression booleanExpression = Expressions.asBoolean(true).isTrue();

        if(!collegeList.isEmpty()){ // 필터에 대학 종류가 있을 경우
            log.info("[getAllAppointment] collegeList Filtering : {}", collegeList);
            BooleanExpression collegeBooleanExpression = Expressions.asBoolean(false).isTrue();
            for(String college : collegeList){
                collegeBooleanExpression = collegeBooleanExpression.or( // 대학이 일치하는 지 확인 조건 추가
                        QAppointment.appointment.collegeCategory.eq(college));
            }
            booleanExpression = booleanExpression.and(collegeBooleanExpression);
        }

        OrderSpecifier<?> orderBy;

        switch (sort.toUpperCase()){
            case "RECOMMEND": // 추천순
                log.info("[getAllAppointment] 조회수 내림차순 정렬");
                orderBy = QAppointment.appointment.viewCount.desc();
                break;
            case "HOT": // 인기순
                log.info("[getAllAppointment] 즐겨찾기 내림차순 정렬");
                orderBy = QAppointment.appointment.favoriteCount.desc();
                break;
            case "NEW": // 최신순
                log.info("[getAllAppointment] 생성일자 내림차순 정렬");
                orderBy = QAppointment.appointment.createAt.desc();
                break;
            case "EXPIRATION": // 당일 만료순
                log.info("[getAllAppointment] 만료일이 당일인 약속 반환");
                booleanExpression = booleanExpression.and(QAppointment.appointment.expiredDate.eq(createCompareTime()));
                orderBy = QAppointment.appointment.viewCount.desc();
                break;
            default:
                throw new IllegalArgumentException("Invalid Sort Parameter");
        }
        appointments = appointmentJPAQuery.from(QAppointment.appointment)
                .where(booleanExpression)
                .orderBy(orderBy)
                .offset(page * size) // default 값
                .limit(size)
                .fetch();

        long totalElements = appointmentJPAQuery.clone().fetchCount(); // 총 페이지 수 반환을 위한 클론

        List<AllAppointmentDto> responseDto = appointments.stream()
                .map(appointment -> {
                    long validateMax = validateNumberOfMember(appointment); // 인원수 제한 검사
                    int validateDate = validateExpireDate(appointment);
                    if (validateMax >= Integer.parseInt(appointment.getMaxPeople()) || validateDate < 0) {
                        return null;
                    }
                    return new AllAppointmentDto().builder()
                            .uid(appointment.getUid())
                            .title(appointment.getTitle())
                            .maxPeople(appointment.getMaxPeople())
                            .minPeople(appointment.getMinPeople())
                            .favoriteCount(appointment.getFavoriteCount())
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new PageImpl<>(responseDto, PageRequest.of(page, size),totalElements);
    }

    @Override
    public Page<MainPageResponseDto> getRightAwayAppointment() {
        log.info("[getRightAwayAppointment] 당일 만료일 상위 4개 약속 반환");
        JPAQuery<Appointment> appointmentJPAQuery = new JPAQuery<>(entityManager);
        OrderSpecifier<?> orderBy;
        List<Appointment> appointmentList;

        orderBy = QAppointment.appointment.viewCount.desc();

        appointmentList = appointmentJPAQuery
                .from(QAppointment.appointment)
                .orderBy(orderBy)
                .limit(4)
                .where(QAppointment.appointment.expiredDate.eq(createCompareTime()))
                .fetch();

        List<MainPageResponseDto> responseDto = appointmentList.stream()
                .map(appointment -> {
                    long validateMax = validateNumberOfMember(appointment); // 인원수 제한 검사
                    if (validateMax >= Integer.parseInt(appointment.getMaxPeople())) {
                        return null;
                    }
                    return MainPageResponseDto.builder()
                            .title(appointment.getTitle())
                            .uid(appointment.getUid())
                            .build();
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responseDto);
    }

    @Override
    public Page<TopFiveAppointmentResponseDto> getTopFiveAppointment() {
        log.info("[getTopFiveAppointment] 상위 조회수");

        JPAQuery<Appointment> jpaQuery = new JPAQuery<>(entityManager);
        List<Appointment> appointmentList = jpaQuery.from(QAppointment.appointment)
                .orderBy(QAppointment.appointment.viewCount.desc())
                .limit(5)
                .fetch();

        List<TopFiveAppointmentResponseDto> responseDto = appointmentList.stream()
                .map(appointment -> {
                    long validateMax = validateNumberOfMember(appointment); // 인원수 제한 검사
                    int validateDate = validateExpireDate(appointment);
                    if (validateMax >= Integer.parseInt(appointment.getMaxPeople()) || validateDate < 0) {
                        return null;
                    }
                    User master = userRepository.getByUid(appointment.getPid());
                    UniversityType universityType = universityTypeRepository.getByMajor(master.getMajor());
                    return TopFiveAppointmentResponseDto.builder()
                            .userName(master.getNickname())
                            .university(universityType.getUniversity())
                            .favoriteCount(appointment.getFavoriteCount())
                            .uid(appointment.getUid())
                            .build();
                }).collect(Collectors.toList());

        return new PageImpl<>(responseDto);
    }

    @Override
    public Page<MainPageResponseDto> getFavoriteAppointment(String token) {
        log.info("[getFavoriteAppointment] test");
        System.out.println("email : "+ userServiceImpl.getUsername(token));
        User findUser = userRepository.getByEmail(userServiceImpl.getUsername(token));

        if(findUser == null){
            throw new UserNotFoundException("유저를 찾을 수 없습니다.");
        }
        log.info("[getFavoriteAppointment] user : {}", findUser.getUid());

        JPAQuery<FavoriteAppointment> jpaQuery = new JPAQuery<>(entityManager);
        List<FavoriteAppointment> appointments;

        BooleanExpression booleanExpression = Expressions.asBoolean(true).isTrue();
        booleanExpression = booleanExpression.and(QFavoriteAppointment.favoriteAppointment.user().eq(findUser));

        appointments = jpaQuery.from(QFavoriteAppointment.favoriteAppointment)
                .where(booleanExpression)
                .orderBy(QFavoriteAppointment.favoriteAppointment.createAt.desc())
                .limit(4)
                .fetch();

        List<MainPageResponseDto> responseDto = appointments.stream()
                .map(favoriteAppointment -> {
                    Appointment appointment = appointmentRepository.getById(favoriteAppointment.getAppointmentId());
                    long validateMax = validateNumberOfMember(appointment); // 인원수 제한 검사
                    int validateDate = validateExpireDate(appointment);
                    if (validateMax >= Integer.parseInt(appointment.getMaxPeople()) || validateDate < 0) {
                        return null;
                    }
                    return MainPageResponseDto.builder()
                            .title(appointment.getTitle())
                            .uid(appointment.getUid())
                            .build();
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responseDto);
    }
}

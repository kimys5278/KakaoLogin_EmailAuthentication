package com.springboot.kakaologintest.data.repository;

import com.springboot.kakaologintest.data.entity.EmailConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationRepository extends JpaRepository<EmailConfirmation,Long> {
    // 인증번호로 이메일 확인 객체 찾기
    EmailConfirmation findByConfirmationCode(String confirmationCode);
    EmailConfirmation findByPersonalEmail(String personalEmail);
}


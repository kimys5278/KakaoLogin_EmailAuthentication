package com.springboot.kakaologintest.controller;

import com.springboot.kakaologintest.data.service.MailService;
import com.springboot.kakaologintest.data.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController("/api")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;
    //이메일로 인증번호 전송
    @PostMapping("/emailConfirm")
    public String emailConfirm(@RequestParam String email) throws Exception {

        String confirmMessage = mailService.sendSimpleMessage(email);

        return confirmMessage;
    }
    // 인증번호만을 사용하여 인증 처리
    @PostMapping("/verifyEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam String confirmationCode) {
        boolean isVerified = mailService.verifyEmail(confirmationCode);
        if (isVerified) {
            return ResponseEntity.ok("인증이 완료되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증번호가 일치하지 않습니다.");
        }
    }


}
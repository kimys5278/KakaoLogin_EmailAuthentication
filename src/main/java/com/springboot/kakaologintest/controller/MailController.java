package com.springboot.kakaologintest.controller;

import com.springboot.kakaologintest.data.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/v1/email")
public class MailController {
    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    //이메일로 인증번호 전송
    @PostMapping("/emailConfirm")
    public String emailConfirm(@RequestParam String email) throws Exception {

        String confirmMessage = mailService.sendSimpleMessage(email);

        return confirmMessage;
    }
    // 인증번호만을 사용하여 인증 처리
    @PostMapping("/verifyEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam String confirmationCode,HttpServletRequest request) {
        boolean isVerified = mailService.verifyEmail(confirmationCode,request);
        if (isVerified) {
            return ResponseEntity.ok("인증이 완료되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증번호가 일치하지 않습니다.");
        }
    }
//    @PostMapping("/emailConfirmAndVerify")
//    public ResponseEntity<String> emailConfirmAndVerify(@RequestParam String email, @RequestParam(required = false) String confirmationCode) throws Exception {
//        // 먼저 이메일 전송 처리
//        if (confirmationCode == null) {
//            String confirmMessage = mailService.sendSimpleMessage(email);
//            return ResponseEntity.ok("인증번호가 전송되었습니다: " + confirmMessage);
//        } else {
//            // 이메일 인증번호 검증 처리
//            boolean isVerified = mailService.verifyEmail(confirmationCode);
//            if (isVerified) {
//                return ResponseEntity.ok("인증이 완료되었습니다.");
//            } else {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증번호가 일치하지 않습니다.");
//            }
//        }
//    }


}
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

    @PostMapping("/emailConfirm")
    public String emailConfirm(@RequestParam String email) throws Exception {

        String confirm = mailService.sendSimpleMessage(email);

        return confirm;
    }
}

package com.springboot.kakaologintest.data.service;

import org.springframework.mail.SimpleMailMessage;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

public interface MailService {
    String sendSimpleMessage(String to ) throws Exception ;

    boolean verifyEmail(String confirmationCode, HttpServletRequest request);


}

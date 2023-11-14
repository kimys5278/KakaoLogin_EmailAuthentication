package com.springboot.kakaologintest.data.service;

import org.springframework.mail.SimpleMailMessage;

import javax.mail.internet.MimeMessage;

public interface MailService {
    String sendSimpleMessage(String to)throws Exception;
    boolean verifyEmail(String confirmationCode);


}
